<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/navigation/init.jsp" %>

<%
KBArticleNavigationFragmentDisplayContext kbArticleNavigationFragmentDisplayContext = (KBArticleNavigationFragmentDisplayContext)request.getAttribute(KBArticleNavigationFragmentDisplayContext.class.getName());
%>

<nav class="font-weight-semi-bold kb-article-navigation text-3">

	<%
	request.setAttribute("view_navigation_kb_articles.jsp-level", 0);
	request.setAttribute("view_navigation_kb_articles.jsp-parentResourcePrimKey", kbArticleNavigationFragmentDisplayContext.getKBArticleRootResourcePrimKey());
	%>

	<liferay-util:include page="/navigation/view_navigation_kb_articles.jsp" servletContext="<%= application %>" />
</nav>