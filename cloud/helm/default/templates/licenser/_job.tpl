{{- define "liferay.licenser.jobSpec" -}}
containers:
    -   command: [
            "/bin/bash",
            "-c",
            "/opt/scripts/update-license.sh"
        ]
        env:
            -   name: LICENSE_SECRET_NAME
                value: {{ include "liferay.licenseSecretName" . }}
            -   name: LIFERAY_SUBSCRIPTION_AUTH_TOKEN
                value: {{ .Values.subscription.authToken }}
            -   name: LIFERAY_SUBSCRIPTION_HOST
                value: {{ .Values.subscription.host }}
            -   name: LIFERAY_SUBSCRIPTION_ID
                value: {{ .Values.subscription.id }}
            -   name: LIFERAY_SUBSCRIPTION_PATH
                value: {{ .Values.subscription.path }}
        image: alpine/k8s:1.30.14
        imagePullPolicy: IfNotPresent
        name: "{{ include "liferay.name" . }}-licenser"
        volumeMounts:
            -   mountPath: /opt/scripts
                name: config
restartPolicy: Never
serviceAccountName: "{{ include "liferay.name" . }}-licenser-serviceaccount"
volumes:
    -   configMap:
            defaultMode: 0777
            name: "{{ include "liferay.name" . }}-licenser-config"
        name: config
{{- end -}}
