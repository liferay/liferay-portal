{{- define "liferay-client-extension.podSpec" -}}
{{- $descriptor := include "liferay-client-extension.descriptor" . | fromJson -}}
{{- $extInit := include "liferay-client-extension.requiresExtInit" . -}}
{{- $kind := include "liferay-client-extension.workloadKind" . -}}
{{- with .Values.workload.affinity }}
affinity:
    {{- toYaml . | nindent 4 }}
{{- end }}
containers:
    -   env:
            {{- if $extInit }}
            -   name: LIFERAY_ROUTES_CLIENT_EXTENSION
                value: /etc/liferay/lxc/ext-init-metadata
            {{- end }}
            -   name: LIFERAY_ROUTES_DXP
                value: /etc/liferay/lxc/dxp-metadata
            {{- $batchOAuthApplicationERC := get $descriptor "oauthApplicationHeadlessServerERC" }}
            {{- if and (eq (get $descriptor "type") "batch") $batchOAuthApplicationERC (not (hasKey (.Values.workload.env | default dict) "LIFERAY_BATCH_OAUTH_APP_ERC")) }}
            -   name: LIFERAY_BATCH_OAUTH_APP_ERC
                value: {{ $batchOAuthApplicationERC | quote }}
            {{- end }}
            {{- range $name, $value := .Values.workload.env }}
            {{- if not (has $name (list "LIFERAY_ROUTES_CLIENT_EXTENSION" "LIFERAY_ROUTES_DXP")) }}
            -   name: {{ $name }}
                value: {{ $value | quote }}
            {{- end }}
            {{- end }}
        image: {{ include "liferay-client-extension.image" . | quote }}
        imagePullPolicy: {{ .Values.image.pullPolicy }}
        {{- with .Values.workload.livenessProbe }}
        livenessProbe:
            {{- toYaml . | nindent 12 }}
        {{- end }}
        name: client-extension
        {{- if eq $kind "Deployment" }}
        ports:
            -   containerPort: {{ .Values.workload.containerPort }}
                name: http
                protocol: TCP
        {{- end }}
        {{- with .Values.workload.readinessProbe }}
        readinessProbe:
            {{- toYaml . | nindent 12 }}
        {{- end }}
        {{- with .Values.workload.resources }}
        resources:
            {{- toYaml . | nindent 12 }}
        {{- end }}
        {{- with .Values.workload.securityContext }}
        securityContext:
            {{- toYaml . | nindent 12 }}
        {{- end }}
        {{- with .Values.workload.startupProbe }}
        startupProbe:
            {{- toYaml . | nindent 12 }}
        {{- end }}
        volumeMounts:
            -   mountPath: /etc/caddy.d
                name: caddy-d
            -   mountPath: /etc/liferay/lxc/dxp-metadata
                name: lxc-dxp-metadata
                readOnly: true
            {{- if $extInit }}
            -   mountPath: /etc/liferay/lxc/ext-init-metadata
                name: lxc-ext-init-metadata
                readOnly: true
            {{- end }}
            {{- with .Values.workload.volumeMounts }}
            {{- toYaml . | nindent 12 }}
            {{- end }}
{{- with .Values.imagePullSecrets }}
imagePullSecrets:
    {{- toYaml . | nindent 4 }}
{{- end }}
{{- with .Values.workload.nodeSelector }}
nodeSelector:
    {{- toYaml . | nindent 4 }}
{{- end }}
{{- if ne $kind "Deployment" }}
restartPolicy: Never
{{- end }}
{{- with .Values.workload.podSecurityContext }}
securityContext:
    {{- toYaml . | nindent 4 }}
{{- end }}
{{- with include "liferay-client-extension.serviceAccountName" . }}
serviceAccountName: {{ . }}
{{- end }}
{{- with .Values.workload.tolerations }}
tolerations:
    {{- toYaml . | nindent 4 }}
{{- end }}
volumes:
    -   emptyDir: {}
        name: caddy-d
    -   configMap:
            name: {{ include "liferay-client-extension.dxpMetadataName" . }}
        name: lxc-dxp-metadata
    {{- if $extInit }}
    -   configMap:
            name: {{ include "liferay-client-extension.extInitName" . }}
        name: lxc-ext-init-metadata
    {{- end }}
    {{- with .Values.workload.volumes }}
    {{- toYaml . | nindent 4 }}
    {{- end }}
{{- end }}

{{- define "liferay-client-extension.podTemplateMetadata" -}}
{{- with .Values.podAnnotations }}
annotations:
    {{- toYaml . | nindent 4 }}
{{- end }}
labels:
    {{- include "liferay-client-extension.selectorLabels" . | nindent 4 }}
    {{- with .Values.podLabels }}
    {{- toYaml . | nindent 4 }}
    {{- end }}
{{- end }}