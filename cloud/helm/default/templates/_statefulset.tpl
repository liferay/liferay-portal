{{- define "liferay.statefulset" -}}
{{- $backendPort := 8080 -}}
{{- range .statefulset.service.ports -}}
    {{- if eq .name "http" -}}{{- $backendPort = .port -}}{{- end -}}
{{- end -}}
{{- $suffix := ternary "" (printf "-%s" .name) (eq .name "") }}
apiVersion: apps/v1
kind: StatefulSet
metadata:
    {{- with .statefulset.annotations }}
    annotations:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
    name: {{ include "liferay.name" .root }}{{ $suffix }}
    namespace: {{ include "liferay.namespace" .root }}
spec:
    replicas: {{ .statefulset.replicaCount }}
    selector:
        matchLabels:
            app: {{ include "liferay.name" .root }}{{ $suffix }}
            {{- include "liferay.selectorLabels" .root | nindent 12 }}
    serviceName: {{ include "liferay.name" .root }}{{ $suffix }}
    template:
        metadata:
            annotations:
                checksum/config: {{ include (print .root.Template.BasePath "/configmap.yaml") .root | sha256sum }}
            labels:
                app: {{ include "liferay.name" .root }}{{ $suffix }}
                {{- include "liferay.labels" .root | nindent 16 }}
        spec:
            {{- with .statefulset.affinity }}
            affinity:
                {{- toYaml . | nindent 16 }}
            {{- end }}
            containers:
                -   #
                    {{- if or .statefulset.env .statefulset.customEnv }}
                    env:
                        {{- with .statefulset.env }}
                        {{- toYaml . | nindent 22 }}
                        {{- end }}
                        {{- range $k, $v := .statefulset.customEnv }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
                    {{- end }}
                    {{- if or .statefulset.envFrom .statefulset.customEnvFrom }}
                    envFrom:
                        {{- with .statefulset.envFrom }}
                        {{- toYaml . | nindent 22 }}
                        {{- end }}
                        {{- range $k, $v := .statefulset.customEnvFrom }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
                    {{- end }}
                    image: {{ printf "%s:%s" .statefulset.image.repository (.statefulset.image.tag | toString) }}
                    imagePullPolicy: {{ .statefulset.image.pullPolicy }}
                    {{- with .statefulset.livenessProbe }}
                    livenessProbe:
                        {{- toYaml . | nindent 22 }}
                    {{- end }}
                    name: {{ include "liferay.name" .root }}{{ $suffix }}
                    {{- if or .statefulset.ports .statefulset.customPorts }}
                    ports:
                        {{- with .statefulset.ports }}
                        {{- toYaml . | nindent 22 }}
                        {{- end }}
                        {{- range $k, $v := .statefulset.customPorts }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
                    {{- end }}
                    {{- with .statefulset.readinessProbe }}
                    readinessProbe:
                        {{- toYaml . | nindent 22 }}
                    {{- end }}
                    {{- with .statefulset.resources }}
                    resources:
                        {{- toYaml . | nindent 22 }}
                    {{- end }}
                    {{- with .statefulset.securityContext }}
                    securityContext:
                        {{- toYaml . | nindent 22 }}
                    {{- end }}
                    {{- with .statefulset.startupProbe }}
                    startupProbe:
                        {{- toYaml . | nindent 22 }}
                    {{- end }}
                    {{- if or .statefulset.volumeMounts .statefulset.customVolumeMounts}}
                    volumeMounts:
                        {{- with .statefulset.volumeMounts }}
                        {{- toYaml . | nindent 22 }}
                        {{- end }}
                        {{- range $k, $v := .statefulset.customVolumeMounts }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
                    {{- end }}
            {{- if or .statefulset.pullSecrets .statefulset.customPullSecrets}}
            imagePullSecrets:
                {{- with .statefulset.pullSecrets }}
                {{- toYaml . | nindent 16 }}
                {{- end }}
                {{- range $k, $v := .statefulset.customPullSecrets }}
                {{- toYaml $v | nindent 16 }}
                {{- end }}
            {{- end }}
            {{- if or .statefulset.initContainers .statefulset.customInitContainers }}
            {{- $statefulset := merge .statefulset (dict "liferayname" (include "liferay.name" .root)) }}
            initContainers:
                {{- range .statefulset.initContainers }}
                {{- if .containerTemplate }}
                {{- tpl .containerTemplate $statefulset | nindent 16 }}
                {{- else }}
                -   #
                    {{- toYaml . | nindent 18 }}
                {{- end }}
                {{- end }}
                {{- range $k, $v := .statefulset.customInitContainers }}
                {{- range $entry := $v }}
                {{- if $entry.containerTemplate }}
                {{- tpl $entry.containerTemplate $statefulset | nindent 16 }}
                {{- else }}
                -   #
                    {{- toYaml $entry | nindent 18 }}
                {{- end }}
                {{- end }}
                {{- end }}
            {{- end }}
            {{- with .statefulset.nodeSelector }}
            nodeSelector:
                {{- toYaml . | nindent 16 }}
            {{- end }}
            {{- with .statefulset.schedulingGates }}
            schedulingGates:
                {{- toYaml . | nindent 16 }}
            {{- end }}
            {{- with .statefulset.podSecurityContext }}
            securityContext:
                {{- toYaml . | nindent 16 }}
            {{- end }}
            serviceAccountName: {{ include "liferay.serviceAccountName" .root }}
            {{- with .statefulset.tolerations }}
            tolerations:
            {{- toYaml . | nindent 12 }}
            {{- end }}
            {{- if or .statefulset.volumes .statefulset.customVolumes }}
            volumes:
                {{- with .statefulset.volumes }}
                {{- toYaml . | nindent 16 }}
                {{- end }}
                {{- if and .statefulset.overlay .statefulset.overlay.enabled }}
                -   name: {{ .statefulset.overlay.bucketName }}
                    persistentVolumeClaim:
                        claimName: {{ .statefulset.overlay.bucketName }}
                {{- end }}
                {{- range $k, $v := .statefulset.customVolumes }}
                {{- toYaml $v | nindent 16 }}
                {{- end }}
            {{- end }}
    {{- with .statefulset.updateStrategy }}
    updateStrategy:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    {{- if or .statefulset.volumeClaimTemplates .statefulset.customVolumeClaimTemplates }}
    volumeClaimTemplates:
        {{- with .statefulset.volumeClaimTemplates }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
        {{- range $k, $v := .statefulset.customVolumeClaimTemplates }}
        {{- toYaml $v | nindent 8 }}
        {{- end }}
    {{- end }}
{{- if and .statefulset.network .statefulset.network.enabled }}
---
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
    {{- with .statefulset.network.annotations }}
    annotations:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
    name: {{ include "liferay.name" .root }}-httproute
    namespace: {{ include "liferay.namespace" .root }}
spec:
    {{- with .statefulset.network.hostnames }}
    hostnames:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    parentRefs:
        -   group: gateway.networking.k8s.io
            kind: Gateway
            name: {{ .statefulset.network.gatewayName }}
            sectionName: {{ .statefulset.network.endpointRef }}
        {{- with .statefulset.network.extraParentRefs }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
    rules:
        -   backendRefs:
                -   name: {{ include "liferay.name" .root }}{{ $suffix }}
                    port: {{ $backendPort }}
            matches:
                -   path:
                        type: PathPrefix
                        value: /
            {{- with .statefulset.network.timeouts }}
            timeouts:
                backendRequest: {{ .backendRequest }}
                request: {{ .request }}
            {{- end }}
        {{- with .statefulset.network.extraRules }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
{{- if and .statefulset.network.forceHttpsRedirect (ne .statefulset.network.endpointRef "http") }}
---
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
    name: {{ include "liferay.name" .root }}-https-redirect
    namespace: {{ include "liferay.namespace" .root }}
spec:
    {{- with .statefulset.network.hostnames }}
    hostnames:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    parentRefs:
        -   group: gateway.networking.k8s.io
            kind: Gateway
            name: {{ .statefulset.network.gatewayName }}
            sectionName: http
    rules:
        -   filters:
            -   requestRedirect:
                    scheme: https
                    statusCode: 301
                type: RequestRedirect
{{- end }}
{{- end }}
---
apiVersion: gateway.envoyproxy.io/v1alpha1
kind: BackendTrafficPolicy
metadata:
    name: liferay-hash-policy
    namespace: {{ .Release.Namespace }}
spec:
    hashPolicies:
        -   cookie:
                name: JSESSIONID
    loadBalancer:
        ringHash:
            minimumRingSize: 4096
        type: RingHash
    targetRefs:
        -   group: ""
            kind: Service
            name: {{ include "liferay.name" .root }}{{ $suffix }}
---
apiVersion: v1
kind: Service
metadata:
    {{- with .statefulset.service.annotations }}
    annotations:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
    name: {{ include "liferay.name" .root }}{{ $suffix }}
    namespace: {{ include "liferay.namespace" .root }}
spec:
    {{- if or .statefulset.service.ports .statefulset.customServicePorts }}
    ports:
    {{- with .statefulset.service.ports }}
        {{- toYaml . | nindent 8 }}
    {{- end }}
    {{- range $k, $v := .statefulset.customServicePorts }}
        {{- toYaml $v | nindent 8 }}
    {{- end }}
    {{- end }}
    selector:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.selectorLabels" .root | nindent 8 }}
    type: {{ .statefulset.service.type }}
---
apiVersion: v1
kind: Service
metadata:
    {{- with .statefulset.service.annotations }}
    annotations:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
    name: {{ include "liferay.name" .root }}{{ $suffix }}-headless
    namespace: {{ include "liferay.namespace" .root }}
spec:
    clusterIP: None
    {{- with .statefulset.service.ports }}
    ports:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    selector:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.selectorLabels" .root | nindent 8 }}
    type: ClusterIP
{{- end -}}