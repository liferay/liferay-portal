<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/site-navigation" prefix="liferay-site-navigation" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.item.selector.constants.ItemSelectorPortletKeys" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.theme.NavItem" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.site.navigation.constants.SiteNavigationConstants" %><%@
page import="com.liferay.site.navigation.menu.web.internal.constants.SiteNavigationMenuWebKeys" %><%@
page import="com.liferay.site.navigation.menu.web.internal.display.context.SiteNavigationMenuConfigurationDisplayContext" %><%@
page import="com.liferay.site.navigation.menu.web.internal.display.context.SiteNavigationMenuDisplayContext" %><%@
page import="com.liferay.site.navigation.model.SiteNavigationMenu" %><%@
page import="com.liferay.site.navigation.model.SiteNavigationMenuItem" %><%@
page import="com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil" %><%@
page import="com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil" %><%@
page import="com.liferay.site.navigation.type.SiteNavigationMenuItemType" %><%@
page import="com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
String portletResource = ParamUtil.getString(request, "portletResource");

SiteNavigationMenuDisplayContext siteNavigationMenuDisplayContext = new SiteNavigationMenuDisplayContext(request);
%>

<%@ include file="/init-ext.jsp" %>