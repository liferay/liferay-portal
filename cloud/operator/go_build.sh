#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	cd resources

	go mod download

	go mod verify

	go generate ./...

	CGO_ENABLED=0 go build -a --ldflags="-s -w" -o manager main.go
}

main "${@}"