#!/usr/bin/env sh

set -eu

certificate_directory="src/main/resources/certs"
mkdir -p "$certificate_directory"

openssl req \
  -nodes \
  -new \
  -x509 \
  -sha256 \
  -days 365 \
  -subj "/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,IP:127.0.0.1" \
  -keyout "$certificate_directory/server.key" \
  -out "$certificate_directory/server.cert"

echo "Created development certificate files in $certificate_directory"
