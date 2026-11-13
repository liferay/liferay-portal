<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PersistentAuditMessageProcessorCompanyConfigurationDisplayContext persistentAuditMessageProcessorCompanyConfigurationDisplayContext = (PersistentAuditMessageProcessorCompanyConfigurationDisplayContext)request.getAttribute(PersistentAuditMessageProcessorCompanyConfigurationDisplayContext.class.getName());
%>

<aui:input disabled="<%= persistentAuditMessageProcessorCompanyConfigurationDisplayContext.isEnabledInputDisabled() %>" helpMessage="<%= persistentAuditMessageProcessorCompanyConfigurationDisplayContext.getEnabledHelpMessage() %>" label="enable-database-processor" name="enabled" type="checkbox" value="<%= persistentAuditMessageProcessorCompanyConfigurationDisplayContext.isEnabled() %>" />