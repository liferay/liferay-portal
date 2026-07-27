FROM golang:1.24 AS builder

WORKDIR /workspace

COPY go_build.sh .
COPY resources resources

RUN ./go_build.sh

FROM gcr.io/distroless/static:nonroot

ENTRYPOINT ["/manager"]

COPY --from=builder /workspace/resources/manager /

USER 65532:65532