<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
FileSystemAuditMessageProcessorConfigurationDisplayContext fileSystemAuditMessageProcessorConfigurationDisplayContext = (FileSystemAuditMessageProcessorConfigurationDisplayContext)request.getAttribute(FileSystemAuditMessageProcessorConfigurationDisplayContext.class.getName());
%>

<aui:input disabled="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.isEnabledOverridden() %>" helpMessage="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.getEnabledHelpMessage() %>" label="enable-file-system-processor" name="enabled" type="checkbox" value="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.isEnabled() %>" />

<aui:input disabled="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.isGenerateChecksumOverridden() %>" helpMessage="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.getGenerateChecksumHelpMessage() %>" label="generate-checksum" name="generateChecksum" type="checkbox" value="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.isGenerateChecksum() %>" />

<aui:input disabled="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.isOutputDirectoryOverridden() %>" helpMessage="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.getOutputDirectoryHelpMessage() %>" label="output-directory" name="outputDirectory" type="text" value="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.getOutputDirectory() %>" />

<aui:select disabled="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.isOutputFormatOverridden() %>" helpMessage="<%= fileSystemAuditMessageProcessorConfigurationDisplayContext.getOutputFormatHelpMessage() %>" label="output-format" name="outputFormat">
	<aui:option label="CSV" selected='<%= "CSV".equals(fileSystemAuditMessageProcessorConfigurationDisplayContext.getOutputFormat()) %>' value="CSV" />
	<aui:option label="NDJSON" selected='<%= "NDJSON".equals(fileSystemAuditMessageProcessorConfigurationDisplayContext.getOutputFormat()) %>' value="NDJSON" />
</aui:select>