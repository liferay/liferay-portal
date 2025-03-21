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
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys" %><%@
page import="com.liferay.layout.seo.model.LayoutSEOEntry" %><%@
page import="com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag" %><%@
page import="com.liferay.layout.seo.model.LayoutSEOSite" %><%@
page import="com.liferay.layout.seo.service.LayoutSEOEntryLocalServiceUtil" %><%@
page import="com.liferay.layout.seo.web.internal.constants.LayoutSEOWebKeys" %><%@
page import="com.liferay.layout.seo.web.internal.display.context.LayoutsSEODisplayContext" %><%@
page import="com.liferay.layout.seo.web.internal.display.context.OpenGraphSettingsDisplayContext" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.exception.SitemapChangeFrequencyException" %><%@
page import="com.liferay.portal.kernel.exception.SitemapIncludeException" %><%@
page import="com.liferay.portal.kernel.exception.SitemapPagePriorityException" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Layout" %><%@
page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.UnicodeProperties" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.util.PropsValues" %><%@
page import="com.liferay.redirect.configuration.CrawlerUserAgentsConfiguration" %>

<%@ page import="jakarta.portlet.PortletRequest" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.List" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%@ include file="/init-ext.jsp" %>