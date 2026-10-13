#!/usr/bin/env bash
# 社内お知らせ掲示板 — EC2 デプロイ/更新スクリプト（EC2 側で実行）
# 前提:
#   - setup-server.sh を一度実行済み（JDK17 / Nginx 導入、ディレクトリ作成済み）
#   - このスクリプトと同じディレクトリに announcement.service と nginx-announcement.conf がある
#   - 配置する成果物を事前に次の場所へ転送しておく:
#       /tmp/app.jar        … backend の実行可能 jar
#       /tmp/web/           … frontend の dist 配下一式
# 使い方: sudo bash deploy.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "==> バックエンド jar を配置"
if [ ! -f /tmp/app.jar ]; then
  echo "ERROR: /tmp/app.jar がありません。ローカルから scp で転送してください。" >&2
  exit 1
fi
sudo mv /tmp/app.jar /opt/announcement/app.jar
sudo chown announcement:announcement /opt/announcement/app.jar

echo "==> systemd サービス定義を配置"
sudo cp "${SCRIPT_DIR}/announcement.service" /etc/systemd/system/announcement.service
sudo systemctl daemon-reload
sudo systemctl enable announcement
sudo systemctl restart announcement

echo "==> フロント静的ファイルを配置"
if [ ! -d /tmp/web ]; then
  echo "ERROR: /tmp/web がありません。frontend/dist/* を /tmp/web/ へ scp してください。" >&2
  exit 1
fi
sudo rm -rf /var/www/announcement/*
sudo cp -r /tmp/web/* /var/www/announcement/
sudo chown -R nginx:nginx /var/www/announcement

echo "==> Nginx 設定を配置して反映"
sudo cp "${SCRIPT_DIR}/nginx-announcement.conf" /etc/nginx/conf.d/announcement.conf
sudo nginx -t
sudo systemctl reload nginx

echo "==> 稼働確認"
sleep 3
sudo systemctl --no-pager --full status announcement | head -n 5 || true
echo "---- backend ローカル疎通 ----"
curl -s http://localhost:8080/api/announcements | head -c 200 || true
echo ""
echo "==> 完了。ブラウザで http://<EC2のIP>/ を開いて確認してください。"
