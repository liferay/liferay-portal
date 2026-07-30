#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

function main {
	local mode=${1:-full}

	cd resources

	if [[ ${mode} == "fast" ]]
	then
		go generate ./...

		mkdir -p build

		CGO_ENABLED=0 GOOS=linux go build -o build/manager .

		return
	fi

	go mod download

	go mod verify

	go generate ./...

	CGO_ENABLED=0 go build -a --ldflags="-s -w" -o manager main.go
}

main "${@}"