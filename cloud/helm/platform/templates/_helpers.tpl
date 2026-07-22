{{- define "liferay-platform.chartSource" -}}
{{- if .path -}}
path: {{ .path }}
{{- else -}}
chart: {{ .chart }}
{{- end }}
repoURL: {{ .repoURL }}
targetRevision: {{ .targetRevision | quote }}
{{- end -}}

{{- define "liferay-platform.labels" -}}
liferay.com/project: liferay-cloud-native
{{- end -}}

{{- define "liferay-platform.operatorApplication" -}}
{{- $root := index . 0 -}}
{{- $app := index . 1 -}}
{{- if $app.enabled -}}
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
    annotations:
        argocd.argoproj.io/sync-wave: {{ $app.syncWave | quote }}
    finalizers:
        -   resources-finalizer.argocd.argoproj.io
    labels:
        {{- include "liferay-platform.labels" $root | nindent 8 }}
        app.kubernetes.io/name: {{ $app.name }}
    name: {{ $app.name }}
    namespace: {{ $root.Release.Namespace }}
spec:
    destination:
        namespace: {{ $app.namespace }}
        server: https://kubernetes.default.svc
    project: default
    source:
        {{- include "liferay-platform.chartSource" $app | nindent 8 }}
        {{- with $app.values }}
        helm:
            valuesObject:
                {{- toYaml . | nindent 16 }}
        {{- end }}
    syncPolicy:
        automated:
            prune: true
            selfHeal: true
        {{- with $app.namespaceLabels }}
        managedNamespaceMetadata:
            labels:
                {{- toYaml . | nindent 16 }}
        {{- end }}
        retry:
            backoff:
                duration: 30s
                factor: 2
                maxDuration: 10m
            limit: 10
        syncOptions:
            -   CreateNamespace=true
            -   ServerSideApply=true
{{- end -}}
{{- end -}}