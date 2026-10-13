#!/usr/bin/env bash
# 社内お知らせ掲示板 — EC2 サーバ初期セットアップ（Amazon Linux 2023 想定）
# EC2 に SSH 接続した上で、このスクリプトを一度だけ実行する。
#   例: scp で転送 → sudo bash setup-server.sh
# 役割: JDK17 + Nginx の導入、配置ディレクトリ作成、実行専用ユーザー作成、Nginx 起動。
set -euo pipefail

echo "==> パッケージ更新と JDK17 / Nginx の導入"
sudo dnf update -y
sudo dnf install -y java-17-amazon-corretto-headless nginx

echo "==> 配置ディレクトリの作成"
sudo mkdir -p /opt/announcement/data        # バックエンド jar と H2 データ
sudo mkdir -p /var/www/announcement          # フロント静的ファイル

echo "==> 実行専用ユーザー（権限最小化）の作成"
if ! id announcement >/dev/null 2>&1; then
  sudo useradd -r -s /sbin/nologin announcement
fi
sudo chown -R announcement:announcement /opt/announcement

echo "==> Nginx の起動と自動起動設定"
sudo systemctl enable --now nginx

echo "==> 完了。次は deploy.sh で jar と dist を配置してください。"
echo "    （systemd 定義 announcement.service と nginx-announcement.conf の配置も deploy.sh が行います）"
