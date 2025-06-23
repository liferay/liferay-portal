FROM golang:1.24 AS builder

WORKDIR /workspace

COPY resources .

RUN go mod download && \
	go mod verify && \
	CGO_ENABLED=0 go build -a -ldflags="-s -w" -o manager main.go

FROM gcr.io/distroless/static:nonroot

ENTRYPOINT ["/manager"]

COPY --from=builder /workspace/manager /

USER 65532:65532