<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AuditConfigurationDisplayContext auditConfigurationDisplayContext = (AuditConfigurationDisplayContext)request.getAttribute(AuditConfigurationDisplayContext.class.getName());
PersistentAuditMessageProcessorConfigurationDisplayContext persistentAuditMessageProcessorConfigurationDisplayContext = (PersistentAuditMessageProcessorConfigurationDisplayContext)request.getAttribute(PersistentAuditMessageProcessorConfigurationDisplayContext.class.getName());
%>

<aui:input disabled="<%= auditConfigurationDisplayContext.isEnabledOverridden() %>" helpMessage="<%= auditConfigurationDisplayContext.getEnabledHelpMessage() %>" name="enabled" type="checkbox" value="<%= auditConfigurationDisplayContext.isEnabled() %>" />

<h3 class="sheet-subtitle"><liferay-ui:message key="database-processor" /></h3>

<aui:input disabled="<%= persistentAuditMessageProcessorConfigurationDisplayContext.isEnabledOverridden() %>" helpMessage="<%= persistentAuditMessageProcessorConfigurationDisplayContext.getEnabledHelpMessage() %>" label="enable-database-processor" name="persistentAuditMessageProcessorEnabled" type="checkbox" value="<%= persistentAuditMessageProcessorConfigurationDisplayContext.isEnabled() %>" />

<aui:input disabled="<%= persistentAuditMessageProcessorConfigurationDisplayContext.isBufferSizeOverridden() %>" helpMessage="<%= persistentAuditMessageProcessorConfigurationDisplayContext.getBufferSizeHelpMessage() %>" label="buffer-size" min="0" name="persistentAuditMessageProcessorBufferSize" type="number" value="<%= persistentAuditMessageProcessorConfigurationDisplayContext.getBufferSize() %>" />

<aui:input disabled="<%= persistentAuditMessageProcessorConfigurationDisplayContext.isFlushIntervalOverridden() %>" helpMessage="<%= persistentAuditMessageProcessorConfigurationDisplayContext.getFlushIntervalHelpMessage() %>" label="flush-interval-in-milliseconds" min="1" name="persistentAuditMessageProcessorFlushInterval" type="number" value="<%= persistentAuditMessageProcessorConfigurationDisplayContext.getFlushInterval() %>" />