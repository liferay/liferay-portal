<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AuditConfigurationDisplayContext auditConfigurationDisplayContext = (AuditConfigurationDisplayContext)request.getAttribute(AuditConfigurationDisplayContext.class.getName());
%>

<c:if test="<%= auditConfigurationDisplayContext.isSystemScope() %>">
	<aui:input disabled="<%= auditConfigurationDisplayContext.isEnabledOverridden() %>" helpMessage="<%= auditConfigurationDisplayContext.getEnabledHelpMessage() %>" name="enabled" type="checkbox" value="<%= auditConfigurationDisplayContext.isEnabled() %>" />
</c:if>

<c:if test="<%= auditConfigurationDisplayContext.isAuditMessageMaxQueueSizeVisible() %>">
	<aui:input disabled="<%= auditConfigurationDisplayContext.isAuditMessageMaxQueueSizeOverridden() %>" helpMessage="<%= auditConfigurationDisplayContext.getAuditMessageMaxQueueSizeHelpMessage() %>" label="audit-message-max-queue-size" name="auditMessageMaxQueueSize" type="number" value="<%= auditConfigurationDisplayContext.getAuditMessageMaxQueueSize() %>" />
</c:if>

<aui:input disabled="<%= auditConfigurationDisplayContext.isPseudonymizationEnabledOverridden() %>" helpMessage="<%= auditConfigurationDisplayContext.getPseudonymizationEnabledHelpMessage() %>" label="pseudonymization-enabled" name="pseudonymizationEnabled" type="checkbox" value="<%= auditConfigurationDisplayContext.isPseudonymizationEnabled() %>" />

<aui:script>
	(function () {
		const form = document.getElementById('<portlet:namespace />fm');

		const pseudonymizationEnabledCheckbox = document.getElementById(
			'<portlet:namespace />pseudonymizationEnabled'
		);

		form.addEventListener('submit', (event) => {
			if (
				pseudonymizationEnabledCheckbox &&
				!pseudonymizationEnabledCheckbox.checked &&
				pseudonymizationEnabledCheckbox.defaultChecked
			) {
				event.preventDefault();
				event.stopImmediatePropagation();

				Liferay.Util.openConfirmModal({
					message:
						'<%= UnicodeLanguageUtil.get(request, "disable-pseudonymization-warning") %>',
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							submitForm(form);
						}
					},
				});
			}
		});
	})();
</aui:script>