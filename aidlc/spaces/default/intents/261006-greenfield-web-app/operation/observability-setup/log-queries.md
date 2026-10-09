# Log Queries — 社内お知らせ掲示板

## 方針

集中ログ基盤（CloudWatch Logs Insights / ELK 等）は本スコープ外（ローカル実行）。ログはアプリのコンソール標準出力に出る。ここでは、開発者が手元のコンソール/ログファイルから必要な情報を見つけるための簡易な検索指針を示す。 [observability-requirements]

## 出力されるログの種類

| レベル | 出所 | 例 |
|--------|------|-----|
| DEBUG | `AnnouncementService` | `create: 登録開始` / `create: 登録完了 id=12` / `findAll: 取得完了 count=5` |
| WARN | `AnnouncementService` | `create: 入力検証に失敗 reason=タイトルは必須です。` / `delete: お知らせが見つかりません id=99` |

いずれも入力値（本文・タイトル等）は含まない [NFR-SEC.8]。

## 手元での検索例（コンソール出力をパイプ）

```
# サービス層のトレースだけを見る
<アプリ出力> | Select-String "AnnouncementService"

# 検証失敗・未検出などの業務例外（WARN）だけを見る
<アプリ出力> | Select-String "WARN"

# 特定IDの操作を追う（例: id=12）
<アプリ出力> | Select-String "id=12"
```

- アプリをファイル出力する場合は `./mvnw spring-boot:run > app.log 2>&1` のようにリダイレクトし、`Select-String`（PowerShell）/ `grep`（bash）で検索する。

## スコープ外

- ログの長期保持・インデックス化・保持ポリシー・集中管理は本スコープ外。将来運用移行時に Actuator / ログ集約を検討。

<!-- 2026-10-08 サマリ再確認・承認を受けて再保存（最小構成方針で確定）。 -->

<!-- authorize 36e68233 -->
