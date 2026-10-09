**Collaborator:** aidlc-devsecops-agent

# セキュリティ/DevSecOps観点の貢献 — Practices Discovery

## セキュリティ上の見解

- 認証なし・社内ネットワーク前提・お試し用途という前提のもとでは、MVPスコープとして認証省略は許容範囲。
- ただし以下の基本的な対策は、認証がなくても実装すべき：
  - 入力値のサニタイズ（XSS対策：本文表示時のエスケープ）
  - SQLインジェクション対策（Spring Data JPA / パラメータ化クエリの使用で自動的に担保）
- 機微情報・個人情報は扱わない前提（Feasibilityで確認済み）のため、暗号化・データ保護要件は最小限。

## 申し送り（将来の露出時）

- 社内ネットワーク外に公開する場合は、認証・HTTPS・CSRF対策を必ず追加すること。

## Assumptions & Open Questions

- [assumption] XSS対策はReactのデフォルトエスケープで大部分カバーされる想定。
