<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend-data-set" prefix="frontend-data-set" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.mcp.server.web.internal.display.context.EditDataMaskDisplayContext" %><%@
page import="com.liferay.mcp.server.web.internal.display.context.EditProfileDisplayContext" %><%@
page import="com.liferay.mcp.server.web.internal.display.context.EditPromptDisplayContext" %><%@
page import="com.liferay.mcp.server.web.internal.display.context.MCPServerNavigationDisplayContext" %><%@
page import="com.liferay.mcp.server.web.internal.display.context.ViewDataMasksDisplayContext" %><%@
page import="com.liferay.mcp.server.web.internal.display.context.ViewProfilesDisplayContext" %><%@
page import="com.liferay.mcp.server.web.internal.display.context.ViewPromptsDisplayContext" %><%@
page import="com.liferay.petra.string.CharPool" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
MCPServerNavigationDisplayContext mcpServerNavigationDisplayContext = new MCPServerNavigationDisplayContext(request, liferayPortletResponse);
%>