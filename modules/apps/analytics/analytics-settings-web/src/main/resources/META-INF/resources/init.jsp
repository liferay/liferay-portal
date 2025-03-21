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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.analytics.settings.configuration.AnalyticsConfiguration" %><%@
page import="com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.AnalyticsSettingsDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.ChannelDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.ChannelManagementToolbarDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.FieldDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.FieldManagementToolbarDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.GroupDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.GroupManagementToolbarDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.OrganizationDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.OrganizationManagementToolbarDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.UserGroupDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.display.context.UserGroupManagementToolbarDisplayContext" %><%@
page import="com.liferay.analytics.settings.web.internal.search.ChannelSearch" %><%@
page import="com.liferay.analytics.settings.web.internal.user.AnalyticsUsersManager" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.servlet.SessionErrors" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.SetUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntriesUtil" %>

<%@ page import="jakarta.portlet.PortletURL" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Set" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<liferay-util:html-top>
	<aui:link href='<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathProxy() + application.getContextPath() + "/css/main.css") %>' rel="stylesheet" type="text/css" />
</liferay-util:html-top>