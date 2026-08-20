<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/navigation/init.jsp" %>

<%
KBArticleNavigationFragmentDisplayContext kbArticleNavigationFragmentDisplayContext = (KBArticleNavigationFragmentDisplayContext)request.getAttribute(KBArticleNavigationFragmentDisplayContext.class.getName());

int level = GetterUtil.getInteger(request.getAttribute("view_navigation_kb_articles.jsp-level"));
long parentResourcePrimKey = (long)request.getAttribute("view_navigation_kb_articles.jsp-parentResourcePrimKey");

for (KBArticle kbArticle : kbArticleNavigationFragmentDisplayContext.getKBArticles(parentResourcePrimKey, level)) {
%>

	<ul class="list-unstyled <%= (level > 0) ? "pl-4" : StringPool.BLANK %>">
		<li class="<%= kbArticleNavigationFragmentDisplayContext.getKBArticleCssClass(kbArticle, level) %> text-truncate">
			<c:choose>
				<c:when test="<%= kbArticleNavigationFragmentDisplayContext.isSelected(kbArticle) %>">
					<span class="sr-only"><liferay-ui:message key="current-article" /></span>

					<%= HtmlUtil.escape(kbArticle.getTitle()) %>
				</c:when>
				<c:otherwise>
					<a href="<%= kbArticleNavigationFragmentDisplayContext.getKBArticleFriendlyURL(kbArticle) %>"><%= HtmlUtil.escape(kbArticle.getTitle()) %></a>
				</c:otherwise>
			</c:choose>

			<c:if test="<%= kbArticleNavigationFragmentDisplayContext.isFurtherExpansionRequired(kbArticle, level) %>">

				<%
				request.setAttribute("view_navigation_kb_articles.jsp-level", level + 1);
				request.setAttribute("view_navigation_kb_articles.jsp-parentResourcePrimKey", kbArticle.getResourcePrimKey());
				%>

				<liferay-util:include page="/navigation/view_navigation_kb_articles.jsp" servletContext="<%= application %>" />
			</c:if>
		</li>
	</ul>

<%
}
%>