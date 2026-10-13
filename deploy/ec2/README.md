# EC2 デプロイ用ファイル

社内お知らせ掲示板を **AWS EC2 単一ホスト構成**（Nginx + Spring Boot jar + H2）でデプロイするための設定ファイルとスクリプトです。詳しい背景・AWS 側の手順（EC2 作成、セキュリティグループ、TLS 等）は `../../docs/AWS-DEPLOYMENT.md` を参照してください。ここはその手順で使う**実ファイル**を置く場所です。

## ファイル一覧

| ファイル | 役割 | 配置先（EC2） |
|----------|------|----------------|
| `setup-server.sh` | 初回のみ。JDK17 + Nginx 導入、ディレクトリ・ユーザー作成 | 実行するだけ |
| `deploy.sh` | jar / dist の配置、systemd・Nginx 設定の反映、再起動 | 実行するだけ |
| `announcement.service` | バックエンドの systemd サービス定義 | `/etc/systemd/system/`（deploy.sh が配置） |
| `nginx-announcement.conf` | Nginx 設定（静的配信 + `/api` 中継） | `/etc/nginx/conf.d/`（deploy.sh が配置） |

## 使い方（概要）

### 1. ローカルで成果物をビルド

```powershell
# バックエンド jar
cd backend; ./mvnw.cmd -DskipTests package

# フロント dist（API ベースURLを EC2 の公開URL/ドメインに設定）
cd ../frontend
$env:VITE_API_BASE = "http://<EC2のIP>"   # or https://<ドメイン>
npm run build
```

### 2. ファイルを EC2 へ転送

```powershell
# このデプロイ用ファイル一式
scp -i .\announcement-key.pem -r deploy\ec2 ec2-user@<IP>:/home/ec2-user/

# 成果物（deploy.sh が参照する固定パスへ）
scp -i .\announcement-key.pem backend\target\announcement-board-0.0.1-SNAPSHOT.jar ec2-user@<IP>:/tmp/app.jar
scp -i .\announcement-key.pem -r frontend\dist\* ec2-user@<IP>:/tmp/web/
```

### 3. EC2 で実行

```bash
cd ~/ec2
# 初回のみ
sudo bash setup-server.sh
# 配置＋再起動（更新時は 1→2→これ、を繰り返す）
sudo bash deploy.sh
```

### 4. 確認

ブラウザで `http://<EC2のIP>/` を開く。サンプルお知らせ13件（`data.sql`）が表示されれば成功。

## 注意

- セキュリティグループで **8080 は公開しない**（Nginx 経由のみ）。SSH(22)/HTTP(80) は自分のIP等に限定。
- 同一オリジン構成のため **CORS 設定は不要**。フロントを別オリジンにする場合のみ `announcement.service` の `APP_CORS_ALLOWED_ORIGIN` を設定。
- H2 データは `/opt/announcement/data/` に永続化。再デプロイでは消えない（`deploy.sh` は data を触らない）。
- 本番公開前に TLS・アクセス制限・H2 コンソール無効化など、`../../docs/AWS-DEPLOYMENT.md` §8 と `README.md` の公開前チェックリストを実施。
- `*.sh` は LF 改行・bash 実行前提です（Amazon Linux 2023 上で `sudo bash <script>` 実行）。
