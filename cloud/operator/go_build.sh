#!/usr/bin/env bash

cd resources/ && \
	go mod download && \
	go mod verify && \
	go generate ./... && \
	CGO_ENABLED=0 go build -a -ldflags="-s -w" -o manager main.go