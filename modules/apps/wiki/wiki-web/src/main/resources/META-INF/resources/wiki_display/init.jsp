<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ page import="com.liferay.wiki.configuration.WikiGroupServiceConfiguration" %><%@
page import="com.liferay.wiki.exception.NoSuchNodeException" %><%@
page import="com.liferay.wiki.web.internal.display.context.helper.WikiRequestHelper" %>

<%
WikiRequestHelper wikiRequestHelper = new WikiRequestHelper(request);

WikiGroupServiceConfiguration wikiGroupServiceConfiguration = wikiRequestHelper.getWikiGroupServiceConfiguration();

long nodeId = GetterUtil.getLong(portletPreferences.getValue("nodeId", StringPool.BLANK));
String title = GetterUtil.getString(portletPreferences.getValue("title", wikiGroupServiceConfiguration.frontPageName()));
%>

<%@ include file="/wiki_display/init-ext.jsp" %>