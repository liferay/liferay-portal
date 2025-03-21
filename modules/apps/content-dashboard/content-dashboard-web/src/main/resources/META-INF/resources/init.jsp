<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/sharing" prefix="liferay-sharing" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/user" prefix="liferay-user" %>

<%@ page import="com.liferay.asset.kernel.model.AssetVocabulary" %><%@
page import="com.liferay.content.dashboard.item.ContentDashboardItemVersion" %><%@
page import="com.liferay.content.dashboard.item.action.ContentDashboardItemAction" %><%@
page import="com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype" %><%@
page import="com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminConfigurationDisplayContext" %><%@
page import="com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminDisplayContext" %><%@
page import="com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminManagementToolbarDisplayContext" %><%@
page import="com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminSharingDisplayContext" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.servlet.SessionMessages" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<%@ page import="java.util.Collections" %><%@
page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%@ include file="/init-ext.jsp" %>