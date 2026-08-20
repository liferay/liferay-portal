<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AuditPortalSettingsConfigurationDisplayContext auditPortalSettingsConfigurationDisplayContext = (AuditPortalSettingsConfigurationDisplayContext)request.getAttribute(AuditPortalSettingsConfigurationDisplayContext.class.getName());
%>

<aui:input disabled="<%= auditPortalSettingsConfigurationDisplayContext.isEnabledOverridden() %>" helpMessage="<%= auditPortalSettingsConfigurationDisplayContext.getEnabledHelpMessage() %>" name="enabled" type="checkbox" value="<%= auditPortalSettingsConfigurationDisplayContext.isEnabled() %>" />

<h3 class="sheet-subtitle"><liferay-ui:message key="database-processor" /></h3>

<aui:input disabled="<%= auditPortalSettingsConfigurationDisplayContext.isPersistentAuditMessageProcessorEnabledOverridden() %>" helpMessage="<%= auditPortalSettingsConfigurationDisplayContext.getPersistentAuditMessageProcessorEnabledHelpMessage() %>" label="enable-database-processor" name="persistentAuditMessageProcessorEnabled" type="checkbox" value="<%= auditPortalSettingsConfigurationDisplayContext.isPersistentAuditMessageProcessorEnabled() %>" />

<aui:input disabled="<%= auditPortalSettingsConfigurationDisplayContext.isPersistentAuditMessageProcessorBufferSizeOverridden() %>" helpMessage="<%= auditPortalSettingsConfigurationDisplayContext.getPersistentAuditMessageProcessorBufferSizeHelpMessage() %>" label="buffer-size" min="0" name="persistentAuditMessageProcessorBufferSize" type="number" value="<%= auditPortalSettingsConfigurationDisplayContext.getPersistentAuditMessageProcessorBufferSize() %>" />

<aui:input disabled="<%= auditPortalSettingsConfigurationDisplayContext.isPersistentAuditMessageProcessorFlushIntervalOverridden() %>" helpMessage="<%= auditPortalSettingsConfigurationDisplayContext.getPersistentAuditMessageProcessorFlushIntervalHelpMessage() %>" label="flush-interval-in-milliseconds" min="1" name="persistentAuditMessageProcessorFlushInterval" type="number" value="<%= auditPortalSettingsConfigurationDisplayContext.getPersistentAuditMessageProcessorFlushInterval() %>" />