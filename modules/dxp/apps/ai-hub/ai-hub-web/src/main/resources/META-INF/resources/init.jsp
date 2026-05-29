<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.ai.hub.web.internal.constants.AIHubFDSNames" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.EditAgentDefinitionDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.EditChatbotDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.EditConfigurationDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.EditContentRetrieverDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.EditGuardrailDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.EditInstructionDefinitionDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.HomeDashboardDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.ViewAgentDefinitionsDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.ViewChatbotsDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.ViewContentRetrieversDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.ViewGuardrailsDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.ViewInstructionDefinitionsDisplayContext" %><%@
page import="com.liferay.ai.hub.web.internal.display.context.ViewIssueReportsDisplayContext" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.workflow.constants.WorkflowPortletKeys" %>

<liferay-frontend:defineObjects />

<liferay-util:html-top>
	<aui:link hashedFile="<%= true %>" href="ai-hub-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<liferay-theme:defineObjects />

<portlet:defineObjects />