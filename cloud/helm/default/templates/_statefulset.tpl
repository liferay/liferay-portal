{{- define "liferay.statefulset" -}}
{{- $backendPort := 8080 -}}
{{- range .statefulset.service.ports -}}
    {{- if eq .name "http" -}}{{- $backendPort = .port -}}{{- end -}}
{{- end -}}
{{- $suffix := ternary "" (printf "-%s" .name) (eq .name "") }}
{{- $licensing := .statefulset.licensing | default dict }}
{{- $licenseSecretName := $licensing.secretName | default (printf "%s-entitlements" (include "liferay.name" .root)) }}
{{- $licenseVolumeName := "liferay-license" }}
{{- $marketplace := .statefulset.marketplace | default dict }}
{{- $marketplaceClaimName := printf "%s-marketplace" (include "liferay.name" .root) }}
{{- $marketplaceVolumeName := "liferay-marketplace" }}
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
    {{- $statefulset := merge (dict "liferayname" (include "liferay.name" .root)) .statefulset }}
    {{- if not .statefulset.autoscaling.enabled }}
    replicas: {{ .statefulset.replicaCount }}
    {{- end }}
    selector:
        matchLabels:
            app: {{ include "liferay.name" .root }}{{ $suffix }}
            {{- include "liferay.selectorLabels" .root | nindent 12 }}
    serviceName: {{ include "liferay.name" .root }}{{ $suffix }}
    template:
        metadata:
            annotations:
                checksum/config: {{ include (print .root.Template.BasePath "/configmap.yaml") .root | sha256sum }}
                checksum/init-scripts: {{ include (print .root.Template.BasePath "/liferay-init-scripts-cm.yaml") .root | sha256sum }}
                checksum/network: {{ include (print .root.Template.BasePath "/liferay-network-cm.yaml") .root | sha256sum }}
                {{- with .statefulset.annotations }}
                {{- toYaml . | nindent 16 }}
                {{- end }}
            labels:
                app: {{ include "liferay.name" .root }}{{ $suffix }}
                {{- include "liferay.labels" .root | nindent 16 }}
                {{- with .statefulset.podLabels }}
                {{- toYaml . | nindent 16 }}
                {{- end }}
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
                        {{- if and $v (gt (len $v) 0) }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
                        {{- end }}
                    {{- end }}
                    {{- $networkEnvFrom := and (eq .name "") .statefulset.network .statefulset.network.enabled .statefulset.network.hostnames }}
                    {{- if or .statefulset.envFrom .statefulset.customEnvFrom $networkEnvFrom }}
                    envFrom:
                        {{- if $networkEnvFrom }}
                        {{- list (dict "configMapRef" (dict "name" (printf "%s-network" (include "liferay.name" .root)))) | toYaml | nindent 22 }}
                        {{- end }}
                        {{- with .statefulset.envFrom }}
                        {{- toYaml . | nindent 22 }}
                        {{- end }}
                        {{- range $k, $v := .statefulset.customEnvFrom }}
                        {{- if and $v (gt (len $v) 0) }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
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
                        {{- if and $v (gt (len $v) 0) }}
                        {{- toYaml $v | nindent 22 }}
                        {{- end }}
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
            initContainers:
                {{- range .statefulset.initContainers }}
                {{- if .containerTemplate }}
                {{- $rendered := tpl .containerTemplate $statefulset | trim }}
                {{- if $rendered }}
                {{- $rendered | nindent 16 }}
                {{- end }}
                {{- else }}
                -   #
                    {{- toYaml . | nindent 18 }}
                {{- end }}
                {{- end }}
                {{- range $k, $v := .statefulset.customInitContainers }}
                {{- range $entry := $v }}
                {{- if $entry.containerTemplate }}
                {{- $rendered := tpl $entry.containerTemplate $statefulset | trim }}
                {{- if $rendered }}
                {{- $rendered | nindent 16 }}
                {{- end }}
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
            {{- if or .statefulset.volumes .statefulset.customVolumes $licensing.enabled $marketplace.enabled }}
            volumes:
                {{- with .statefulset.volumes }}
                {{- toYaml . | nindent 16 }}
                {{- end }}
                {{- range $k, $v := .statefulset.customVolumes }}
                {{- toYaml $v | nindent 16 }}
                {{- end }}
                {{- if $licensing.enabled }}
                {{- list (dict "name" $licenseVolumeName "secret" (dict "items" (list (dict "key" "license.xml" "path" "license.xml")) "optional" true "secretName" $licenseSecretName)) | toYaml | nindent 16 }}
                {{- end }}
                {{- if $marketplace.enabled }}
                {{- list (dict "name" $marketplaceVolumeName "persistentVolumeClaim" (dict "claimName" $marketplaceClaimName)) | toYaml | nindent 16 }}
                {{- end }}
            {{- end }}
    {{- with .statefulset.updateStrategy }}
    updateStrategy:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    {{- if or .statefulset.volumeClaimTemplates .statefulset.customVolumeClaimTemplates }}
    {{- $defaultStorageClassName := .statefulset.persistence.defaultStorageClassName }}
    volumeClaimTemplates:
        {{- range .statefulset.volumeClaimTemplates }}
        {{- $volumeClaimTemplate := . }}
        {{- if and $defaultStorageClassName (not (hasKey .spec "storageClassName")) }}
        {{- $volumeClaimTemplate = merge (deepCopy .) (dict "spec" (dict "storageClassName" $defaultStorageClassName)) }}
        {{- end }}
        {{- list $volumeClaimTemplate | toYaml | nindent 8 }}
        {{- end }}
        {{- range $k, $v := .statefulset.customVolumeClaimTemplates }}
        {{- toYaml $v | nindent 8 }}
        {{- end }}
    {{- end }}
{{- if and .statefulset.network .statefulset.network.enabled }}
{{- $perHost := and .statefulset.network.perHostnameRoutes (gt (len .statefulset.network.hostnames) 0) }}
---
apiVersion: gateway.envoyproxy.io/v1alpha1
kind: BackendTrafficPolicy
metadata:
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
    name: {{ include "liferay.name" .root }}-hash-policy
    namespace: {{ include "liferay.namespace" .root }}
spec:
    loadBalancer:
        consistentHash:
            cookie:
                name:
                    JSESSIONID
            type: Cookie
        type: ConsistentHash
    targetRefs:
        {{- if $perHost }}
        {{- range $hostname := .statefulset.network.hostnames }}
        {{- $slug := include "liferay.hostnameSlug" $hostname }}
        -   group: gateway.networking.k8s.io
            kind: HTTPRoute
            name: {{ include "liferay.name" $.root }}-httproute-{{ $slug }}
        {{- end }}
        {{- else }}
        -   group: gateway.networking.k8s.io
            kind: HTTPRoute
            name: {{ include "liferay.name" .root }}-httproute
        {{- end }}
{{- with .statefulset.network.securityPolicyAuthorizationSpec }}
---
apiVersion: gateway.envoyproxy.io/v1alpha1
kind: SecurityPolicy
metadata:
    labels:
        app: {{ include "liferay.name" $.root }}{{ $suffix }}
        {{- include "liferay.labels" $.root | nindent 8 }}
    name: {{ include "liferay.name" $.root }}-securitypolicy
    namespace: {{ include "liferay.namespace" $.root }}
spec:
    authorization:
        {{- toYaml . | nindent 8 }}
    targetRefs:
        {{- if $perHost }}
        {{- range $hostname := $.statefulset.network.hostnames }}
        {{- $slug := include "liferay.hostnameSlug" $hostname }}
        -   group: gateway.networking.k8s.io
            kind: HTTPRoute
            name: {{ include "liferay.name" $.root }}-httproute-{{ $slug }}
        {{- end }}
        {{- else }}
        -   group: gateway.networking.k8s.io
            kind: HTTPRoute
            name: {{ include "liferay.name" $.root }}-httproute
        {{- end }}
{{- end }}
{{- if .statefulset.network.gatewayName }}
{{- if $perHost }}
{{- $ctx := . }}
{{- range $hostname := .statefulset.network.hostnames }}
{{- $slug := include "liferay.hostnameSlug" $hostname }}
---
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
    {{- with $ctx.statefulset.network.annotations }}
    annotations:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    labels:
        app: {{ include "liferay.name" $ctx.root }}{{ $suffix }}
        {{- include "liferay.labels" $ctx.root | nindent 8 }}
    name: {{ include "liferay.name" $ctx.root }}-httproute-{{ $slug }}
    namespace: {{ include "liferay.namespace" $ctx.root }}
spec:
    hostnames:
        -   {{ $hostname | quote }}
    parentRefs:
        -   group: gateway.networking.k8s.io
            kind: Gateway
            name: {{ $ctx.statefulset.network.gatewayName }}
            sectionName: {{ printf "%s-%s" $ctx.statefulset.network.endpointRef $slug }}
    rules:
        -   backendRefs:
                -   name: {{ include "liferay.name" $ctx.root }}{{ $suffix }}
                    port: {{ $backendPort }}
            matches:
                -   path:
                        type: PathPrefix
                        value: /
            {{- with $ctx.statefulset.network.timeouts }}
            timeouts:
                backendRequest: {{ .backendRequest }}
                request: {{ .request }}
            {{- end }}
{{- end }}
{{- else }}
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
{{- end }}
{{- if and .statefulset.network.forceHttpsRedirect (ne .statefulset.network.endpointRef "http") }}
---
apiVersion: gateway.networking.k8s.io/v1
kind: HTTPRoute
metadata:
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
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
{{- end }}
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
{{- if and .statefulset.networkPolicy .statefulset.networkPolicy.enabled }}
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
    {{- with .statefulset.networkPolicy.annotations }}
    annotations:
        {{- toYaml . | nindent 8 }}
    {{- end }}
    labels:
        app: {{ include "liferay.name" .root }}{{ $suffix }}
        {{- include "liferay.labels" .root | nindent 8 }}
    name: {{ include "liferay.name" .root }}{{ $suffix }}
    namespace: {{ include "liferay.namespace" .root }}
spec:
    egress:
        -   ports:
                -   port: 53
                    protocol: UDP
                -   port: 53
                    protocol: TCP
            to:
                -   namespaceSelector:
                        matchLabels:
                            kubernetes.io/metadata.name: kube-system
                    podSelector:
                        matchLabels:
                            k8s-app: kube-dns
        -   to:
                -   podSelector:
                        matchLabels:
                            {{- include "liferay.selectorLabels" .root | nindent 28 }}
        {{- if .statefulset.networkPolicy.cluster.kubernetesEndpointCidrs }}
        {{- range (splitList "," .statefulset.networkPolicy.cluster.kubernetesEndpointCidrs) }}
        -   ports:
                -   port: 443
                    protocol: TCP
            to:
                -   ipBlock:
                        cidr: {{ . | trim }}
        {{- end }}
        {{- end }}
        {{- with .statefulset.networkPolicy.extraEgress }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
    ingress:
        {{- if and .statefulset.network .statefulset.network.enabled .statefulset.networkPolicy.allowGatewayIngress }}
        -   from:
                -   namespaceSelector:
                        matchLabels:
                            kubernetes.io/metadata.name: {{ .statefulset.networkPolicy.gatewayNamespace | quote }}
                    podSelector:
                        matchLabels:
                            app.kubernetes.io/managed-by: envoy-gateway
                            app.kubernetes.io/name: {{ .statefulset.networkPolicy.gatewayPodLabel | quote }}
                            gateway.envoyproxy.io/owning-gateway-namespace: {{ include "liferay.namespace" .root | quote }}
            ports:
                -   port: http
                    protocol: TCP
        {{- end }}
        -   from:
                -   podSelector:
                        matchLabels:
                            {{- include "liferay.selectorLabels" .root | nindent 28 }}
            ports:
                -   port: cluster
                    protocol: TCP
                -   port: http
                    protocol: TCP
        {{- with .statefulset.networkPolicy.extraIngress }}
        {{- toYaml . | nindent 8 }}
        {{- end }}
    podSelector:
        matchLabels:
            app: {{ include "liferay.name" .root }}{{ $suffix }}
            {{- include "liferay.selectorLabels" .root | nindent 12 }}
    policyTypes:
        -   Egress
        -   Ingress
{{- end }}
{{- end -}}