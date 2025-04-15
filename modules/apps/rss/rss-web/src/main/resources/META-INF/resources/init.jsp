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
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.rss.web.internal.configuration.RSSPortletInstanceConfiguration" %><%@
page import="com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration" %><%@
page import="com.liferay.rss.web.internal.display.context.RSSDisplayContext" %><%@
page import="com.liferay.rss.web.internal.util.RSSFeed" %><%@
page import="com.liferay.rss.web.internal.util.RSSFeedEntry" %>

<%@ page import="com.rometools.rome.feed.synd.SyndEntry" %><%@
page import="com.rometools.rome.feed.synd.SyndFeed" %><%@
page import="com.rometools.rome.feed.synd.SyndImage" %>

<%@ page import="jakarta.portlet.ValidatorException" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.Enumeration" %><%@
page import="java.util.List" %><%@
page import="java.util.Map" %><%@
page import="java.util.Objects" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
RSSWebCacheConfiguration rssWebCacheConfiguration = (RSSWebCacheConfiguration)request.getAttribute(RSSWebCacheConfiguration.class.getName());

RSSDisplayContext rssDisplayContext = new RSSDisplayContext(request, rssWebCacheConfiguration);

RSSPortletInstanceConfiguration rssPortletInstanceConfiguration = rssDisplayContext.getRSSPortletInstanceConfiguration();

Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<%@ include file="/init-ext.jsp" %>