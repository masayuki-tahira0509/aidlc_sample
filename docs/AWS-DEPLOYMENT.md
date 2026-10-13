# AWS デプロイ手順書（EC2 構成）— 社内お知らせ掲示板

このドキュメントは、本アプリ（React フロント + Spring Boot バックエンド + H2）を **AWS EC2** にデプロイするための手順書です。コンテナ（Docker）は使わず、EC2 上で jar を直接実行し、フロントの静的ファイルも同じ EC2 の Nginx で配信する **単一ホスト構成** を主軸にします。

> **前提と注意**
> - 本アプリは当初 **ローカル実行のみ** を想定し、AI-DLC のデプロイ系ステージはスコープ外でした（`team.md`）。本手順は「AWS に出す場合」の新規手順です。
> - 本アプリは **認証・認可を持ちません**。インターネットに公開する場合は、`README.md` の「公開前チェックリスト」（認証・TLS・CORS 見直し）を**必ず**実施してください。社内限定にするなら §8 のアクセス制限を検討してください。
> - 実デプロイは AWS 課金が発生します。各コマンドは**自分の AWS アカウント・認証情報**で、内容を理解したうえで実行してください。

---

## 0. この資料の内容（アジェンダ）

1. 構成の全体像（EC2 単一ホスト）
2. 前提ツールと事前準備
3. EC2 インスタンスの作成（コンソール／CLI）
4. 成果物のビルド（バックエンド jar・フロント dist）
5. サーバのセットアップ（JDK・Nginx）
6. バックエンドの配置と systemd 常駐化
7. フロントの配置と Nginx 設定（静的配信 + API リバースプロキシ）
8. アクセス制限・セキュリティ（TLS・CORS・認証）
9. 更新デプロイの手順（2回目以降）
10. 片付け（コスト停止）
11. 代替構成の選択肢

---

## 1. 構成の全体像（EC2 単一ホスト）

```
[ブラウザ]
   │  HTTPS/HTTP
   ▼
[EC2（Amazon Linux 2023）]
   ├── Nginx (:80 / :443)
   │     ├── /            → フロント静的ファイル（/var/www/announcement）
   │     └── /api/        → リバースプロキシ → localhost:8080
   └── Spring Boot (java -jar, :8080, systemd 常駐)
          └── H2 ファイルDB（/opt/announcement/data/）
```

ポイント:
- **1台の EC2 に全部載せる**最小構成。Nginx がフロント配信と `/api` の中継を担う。
- フロントとバックエンドが**同一オリジン**（同じドメイン）になるため、**CORS 設定が不要**になる（ブラウザから見ると同じホスト）。これは EC2 単一ホスト構成の利点。
- H2 は EC2 のディスク（EBS）に永続化されるので、App Runner のような揮発はない。ただし単一ノード・バックアップは手動（§8 補足）。

> フロントを S3+CloudFront に分離したい場合は §11 を参照。その場合は CORS 設定が必要になります。

---

> **実ファイルの場所**: 本手順で使う systemd 定義・Nginx 設定・セットアップ/デプロイスクリプトは、リポジトリの **`deploy/ec2/`** に実ファイルとして用意済みです。手順書からコピペせず、そのまま EC2 へ転送して使えます（使い方は `deploy/ec2/README.md`）。本手順書は背景と AWS 側の操作（EC2 作成・SG・TLS 等）を説明します。

## 2. 前提ツールと事前準備

| ツール | 用途 | 確認 |
|--------|------|------|
| AWS アカウント | デプロイ先 | — |
| AWS CLI v2 | インスタンス操作（任意。コンソールでも可） | `aws --version` |
| SSH クライアント | EC2 への接続 | Windows 10+ は標準 `ssh` |
| JDK 11+ / Maven Wrapper | ローカルで jar をビルド | `java -version` |
| Node.js 18+ / npm | ローカルでフロントをビルド | `node -v` |

認証設定（CLI を使う場合、**あなたの環境で実施**）:

```powershell
aws configure   # Access Key / Secret / リージョン(例 ap-northeast-1) / json
```

本手順はリージョン東京 `ap-northeast-1` を例にします。

---

## 3. EC2 インスタンスの作成

### 3.1 コンソールでの作成（分かりやすい）

1. EC2 → インスタンスを起動
2. **AMI**: Amazon Linux 2023（64bit x86）
3. **インスタンスタイプ**: `t3.small`（メモリ 2GB 目安。Spring Boot + Nginx が無理なく動く。最小で試すなら `t3.micro` でも可だがメモリ注意）
4. **キーペア**: 新規作成しダウンロード（`announcement-key.pem`）。SSH 接続に使う
5. **ネットワーク／セキュリティグループ**（インバウンド）:
   - SSH (22): **自分のIPのみ**許可（`マイIP`）
   - HTTP (80): 公開範囲に応じて（社内なら会社IP、検証なら一時的に自分のIP）
   - HTTPS (443): 同上（TLS を張る場合）
   - ※ 8080 は**開けない**（Nginx 経由で中継するため、バックエンドは外部公開しない）
6. **ストレージ**: 既定（8〜16GB gp3）で可
7. 起動

### 3.2 接続確認

```powershell
# .pem の権限は Windows でも SSH が要求する場合あり。ユーザー名は Amazon Linux 2023 は ec2-user
ssh -i .\announcement-key.pem ec2-user@<EC2のパブリックIP>
```

---

## 4. 成果物のビルド（ローカル）

EC2 に送るファイルを手元で作ります。

```powershell
# バックエンド jar
cd backend
./mvnw.cmd -DskipTests package
# 成果物: backend/target/announcement-board-0.0.1-SNAPSHOT.jar

# フロント（同一オリジン構成なので API ベースはパス /api に向ける）
cd ../frontend
npm install
$env:VITE_API_BASE = ""      # 空にすると api.ts は http://localhost:8080 を既定にするため、
                              # 同一オリジン運用では下記のどちらかで /api に合わせる（§7 の Nginx と整合）
npm run build
# 成果物: frontend/dist/
```

> **重要（API ベースURLの扱い）**: フロントは `src/api.ts` で `VITE_API_BASE` を使い、既定が `http://localhost:8080` です。EC2 の同一オリジン構成では、API を**同じホストの `/api`** に向けたいので、ビルド時に `VITE_API_BASE` を **EC2 の公開URL**（例 `http://<EC2のIP>` や `https://<ドメイン>`）に設定します。Nginx が `/api/` を 8080 に中継するため、`VITE_API_BASE = https://<ドメイン>` とすれば `https://<ドメイン>/api/announcements` を呼ぶ形になり、CORS も不要です。
>
> 例:
> ```powershell
> $env:VITE_API_BASE = "https://announcement.example.com"   # or http://<EC2のIP>
> npm run build
> ```

---

## 5. サーバのセットアップ（JDK・Nginx）

EC2 に SSH 接続して実行します。

```bash
# JDK 17（Amazon Corretto）と Nginx を導入
sudo dnf update -y
sudo dnf install -y java-17-amazon-corretto-headless nginx

# 配置用ディレクトリ
sudo mkdir -p /opt/announcement/data
sudo mkdir -p /var/www/announcement

# Nginx 起動＆自動起動
sudo systemctl enable --now nginx
```

---

## 6. バックエンドの配置と systemd 常駐化

### 6.1 jar を EC2 に転送（ローカルで実行）

```powershell
scp -i .\announcement-key.pem backend\target\announcement-board-0.0.1-SNAPSHOT.jar ec2-user@<EC2のIP>:/tmp/app.jar
```

### 6.2 配置と専用ユーザー（EC2 側）

```bash
sudo mv /tmp/app.jar /opt/announcement/app.jar
# 実行専用ユーザー（権限最小化）
sudo useradd -r -s /sbin/nologin announcement || true
sudo chown -R announcement:announcement /opt/announcement
```

### 6.3 systemd サービス定義（EC2 側）

`/etc/systemd/system/announcement.service` を作成:

```ini
[Unit]
Description=Announcement Board Backend (Spring Boot)
After=network.target

[Service]
User=announcement
WorkingDirectory=/opt/announcement
# H2 のファイルは WorkingDirectory 配下の ./data に作られる
ExecStart=/usr/bin/java -jar /opt/announcement/app.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=5
# 同一オリジン運用では CORS は不要だが、別オリジンにする場合はここで指定:
# Environment=APP_CORS_ALLOWED_ORIGIN=https://<フロントのドメイン>

[Install]
WantedBy=multi-user.target
```

起動:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now announcement
sudo systemctl status announcement        # active (running) を確認
# ローカル疎通（EC2 内から）
curl http://localhost:8080/api/announcements
```

> 起動時、`data.sql` によりサンプルお知らせ13件が自動投入されます（DB が空のときのみ）。H2 ファイルは `/opt/announcement/data/` に作成され、インスタンスの EBS に永続化されます。

---

## 7. フロントの配置と Nginx 設定

### 7.1 dist を EC2 に転送（ローカル）

```powershell
# dist 配下を EC2 の一時領域へ
scp -i .\announcement-key.pem -r frontend\dist\* ec2-user@<EC2のIP>:/tmp/web/
```

EC2 側で配置:

```bash
sudo rm -rf /var/www/announcement/*
sudo cp -r /tmp/web/* /var/www/announcement/
sudo chown -R nginx:nginx /var/www/announcement
```

### 7.2 Nginx 設定（EC2 側）

`/etc/nginx/conf.d/announcement.conf` を作成:

```nginx
server {
    listen 80;
    server_name _;      # ドメインがあれば announcement.example.com

    root /var/www/announcement;
    index index.html;

    # フロント（SPA）。見つからないパスは index.html へ
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API はバックエンド(8080)へリバースプロキシ
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

デフォルトの `server` ブロックと競合する場合は `/etc/nginx/nginx.conf` 内の既定 `server` を調整。反映:

```bash
sudo nginx -t          # 構文チェック
sudo systemctl reload nginx
```

### 7.3 疎通確認

- ブラウザで `http://<EC2のIP>/` を開く → お知らせ一覧（サンプル13件）が表示される
- 作成・編集・削除が動く
- 注意: フロントのビルド時 `VITE_API_BASE` を `http://<EC2のIP>`（または独自ドメイン）にしていること。同一オリジンなので CORS 設定は不要。

---

## 8. アクセス制限・セキュリティ

本アプリは認証なしのため、公開範囲に応じて必ず対策してください。

- **TLS（HTTPS）**: 独自ドメインがあるなら Let's Encrypt（certbot）か ACM+ALB で HTTPS 化。
  ```bash
  # 独自ドメインを EC2 に向けた後（Amazon Linux 2023 の例）
  sudo dnf install -y certbot python3-certbot-nginx
  sudo certbot --nginx -d announcement.example.com
  ```
- **アクセス元の制限**: セキュリティグループで HTTP/HTTPS を**社内IP等に限定**。全世界公開は認証なしのため非推奨。
- **8080 を公開しない**: セキュリティグループで 8080 を開けない（Nginx 経由のみ）。
- **CORS**: 同一オリジン構成なら不要。フロントを別オリジン（S3+CloudFront 等）にする場合のみ、`APP_CORS_ALLOWED_ORIGIN` をそのドメインに設定（§6.3 のコメント参照）。
- **H2 コンソール**: 本番では無効化推奨（`application.properties` の `spring.h2.console.enabled=false`）。外部公開は厳禁。
- **データのバックアップ**: H2 ファイル（`/opt/announcement/data/`）を定期的に退避（cron で S3 へコピー等）。EBS スナップショットでも可。
- **公開前チェックリスト**: `README.md` の該当節を実施。

---

## 9. 更新デプロイの手順（2回目以降）

コードを更新したときの再デプロイ:

```powershell
# ローカル: 再ビルド
cd backend; ./mvnw.cmd -DskipTests package
cd ../frontend; $env:VITE_API_BASE="https://<ドメイン or IP>"; npm run build

# 転送
scp -i .\announcement-key.pem backend\target\announcement-board-0.0.1-SNAPSHOT.jar ec2-user@<IP>:/tmp/app.jar
scp -i .\announcement-key.pem -r frontend\dist\* ec2-user@<IP>:/tmp/web/
```

```bash
# EC2: 差し替えて再起動
sudo mv /tmp/app.jar /opt/announcement/app.jar
sudo chown announcement:announcement /opt/announcement/app.jar
sudo systemctl restart announcement

sudo rm -rf /var/www/announcement/* && sudo cp -r /tmp/web/* /var/www/announcement/
sudo chown -R nginx:nginx /var/www/announcement
sudo systemctl reload nginx
```

> H2 データは `/opt/announcement/data/` に残るため、再デプロイで消えません（`data.sql` は空のときのみ投入なので重複もしません）。

---

## 10. 片付け（コスト停止）

- **一時停止**（課金を抑えるが EBS 料金は残る）: EC2 インスタンスを「停止」
- **完全削除**: インスタンスを「終了（Terminate）」。関連する EBS・Elastic IP も削除。キーペア・セキュリティグループは不要なら削除。

```powershell
aws ec2 describe-instances --query "Reservations[].Instances[].InstanceId" --output text
aws ec2 terminate-instances --instance-ids <INSTANCE_ID>
```

> Elastic IP を割り当てた場合、未使用だと課金されるので解放を忘れずに。

---

## 11. 代替構成の選択肢

| 構成 | 概要 | 向き |
|------|------|------|
| **EC2 単一ホスト（本手順の主軸）** | 1台に Nginx + jar + H2。同一オリジンで CORS 不要 | 最小・学習・社内小規模 |
| **EC2（バックエンド）+ S3+CloudFront（フロント）** | フロントを CDN 配信。CORS 設定が必要 | フロントを速く配りたい |
| **EC2 + RDS** | H2 を RDS(PostgreSQL)へ移行。EC2 障害時もデータ保全 | 本番・データ保全重視 |
| **Auto Scaling + ALB + 複数 EC2 + RDS** | 冗長化・スケール | 可用性が要る本番 |

RDS 移行や TLS/独自ドメインの詳細、IaC（CloudFormation/Terraform でこの EC2 構成をコード化）が必要なら、別途対応できます。

---

## まとめ（最短ルート）

1. `aws configure`（CLI を使う場合）
2. EC2（Amazon Linux 2023, t3.small）を作成。SG は 22/80 を自分のIPに限定、8080 は開けない（§3）
3. ローカルで jar と dist をビルド（§4。フロントは `VITE_API_BASE` に EC2 のURL）
4. EC2 に JDK17 + Nginx を導入（§5）
5. jar を配置して systemd で常駐化（§6）
6. dist を配置して Nginx を設定（静的配信 + `/api` 中継、§7）
7. `http://<EC2のIP>/` で疎通確認（§7.3）
8. 公開するなら TLS・アクセス制限（§8）

> この手順書はドキュメントです。実際の AWS 操作・課金は発生しません。実デプロイを一緒に進める場合は、各コマンドを承認を取りながら実行します（systemd / Nginx 設定ファイルの実ファイル化や、IaC テンプレート作成も対応できます）。
