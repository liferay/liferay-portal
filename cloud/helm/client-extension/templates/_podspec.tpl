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

{{- define "liferay-client-extension.podSpec" -}}
{{- $extInit := include "liferay-client-extension.requiresExtInit" . -}}
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
            {{- range $name, $value := .Values.workload.env }}
            {{- if not (has $name (list "LIFERAY_ROUTES_CLIENT_EXTENSION" "LIFERAY_ROUTES_DXP")) }}
            -   name: {{ $name }}
                value: {{ $value | quote }}
            {{- end }}
            {{- end }}
        image: "{{ required "image.repository is required" .Values.image.repository }}:{{ .Values.image.tag }}"
        imagePullPolicy: {{ .Values.image.pullPolicy }}
        {{- with .Values.workload.livenessProbe }}
        livenessProbe:
            {{- toYaml . | nindent 12 }}
        {{- end }}
        name: client-extension
        {{- if eq .Values.workload.kind "Deployment" }}
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
            -   mountPath: /etc/liferay/lxc/dxp-metadata
                name: lxc-dxp-metadata
                readOnly: true
            {{- if $extInit }}
            -   mountPath: /etc/liferay/lxc/ext-init-metadata
                name: lxc-ext-init-metadata
                readOnly: true
            {{- end }}
            {{- if .Values.artifact.reference }}
            {{- range .Values.artifact.mounts }}
            -   mountPath: {{ .mountPath }}
                name: artifact
                readOnly: true
                {{- with .subPath }}
                subPath: {{ . }}
                {{- end }}
            {{- end }}
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
{{- if ne .Values.workload.kind "Deployment" }}
restartPolicy: Never
{{- end }}
{{- with .Values.workload.podSecurityContext }}
securityContext:
    {{- toYaml . | nindent 4 }}
{{- end }}
{{- with .Values.workload.tolerations }}
tolerations:
    {{- toYaml . | nindent 4 }}
{{- end }}
volumes:
    {{- with .Values.artifact.reference }}
    -   image:
            pullPolicy: {{ $.Values.artifact.pullPolicy }}
            reference: {{ . }}
        name: artifact
    {{- end }}
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