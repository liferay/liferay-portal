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

<aui:input disabled="<%= auditConfigurationDisplayContext.isEnabledOverridden() %>" helpMessage="<%= auditConfigurationDisplayContext.getEnabledHelpMessage() %>" name="enabled" type="checkbox" value="<%= auditConfigurationDisplayContext.isEnabled() %>" />

<c:if test="<%= auditConfigurationDisplayContext.isAuditMessageMaxQueueSizeVisible() %>">
	<aui:input disabled="<%= auditConfigurationDisplayContext.isAuditMessageMaxQueueSizeOverridden() %>" helpMessage="<%= auditConfigurationDisplayContext.getAuditMessageMaxQueueSizeHelpMessage() %>" name="auditMessageMaxQueueSize" type="number" value="<%= auditConfigurationDisplayContext.getAuditMessageMaxQueueSize() %>" />
</c:if>