<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.akismet.web.internal.security.permission.MBResourcePermission" %><%@
page import="com.liferay.message.boards.constants.MBCategoryConstants" %><%@
page import="com.liferay.message.boards.model.MBCategory" %><%@
page import="com.liferay.message.boards.model.MBMessage" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %>

<%
MBMessage message = (MBMessage)request.getAttribute("edit_message.jsp-message");

boolean spam = false;

if (message.getStatus() == WorkflowConstants.STATUS_DENIED) {
	spam = true;
}

String currentURL = PortalUtil.getCurrentURL(request);
%>

<liferay-theme:defineObjects />

<portlet:defineObjects />