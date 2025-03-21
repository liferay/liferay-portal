<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.search.Document" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletDisplayContext" %>

<%@ page import="java.util.List" %>

<portlet:defineObjects />

<%
SearchResultsPortletDisplayContext searchResultsPortletDisplayContext = (SearchResultsPortletDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

if (searchResultsPortletDisplayContext.isRenderNothing()) {
	return;
}

SearchResultsPortletInstanceConfiguration searchResultsPortletInstanceConfiguration = searchResultsPortletDisplayContext.getSearchResultsPortletInstanceConfiguration();

List<SearchResultSummaryDisplayContext> searchResultSummaryDisplayContexts = searchResultsPortletDisplayContext.getSearchResultSummaryDisplayContexts();

SearchContainer<Document> searchContainer = searchResultsPortletDisplayContext.getSearchContainer();
%>

<c:choose>
	<c:when test="<%= searchResultSummaryDisplayContexts.isEmpty() && searchResultsPortletDisplayContext.isShowEmptyResultMessage() %>">
		<div class="sheet">
			<liferay-frontend:empty-result-message
				description='<%= LanguageUtil.format(request, "no-results-were-found-that-matched-the-keywords-x", "<strong>" + HtmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()) + "</strong>", false) %>'
				title='<%= LanguageUtil.format(request, "no-results-were-found", false) %>'
			/>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ddm:template-renderer
			className="<%= SearchResultSummaryDisplayContext.class.getName() %>"
			contextObjects='<%=
				HashMapBuilder.<String, Object>put(
					"namespace", liferayPortletResponse.getNamespace()
				).put(
					"searchContainer", searchContainer
				).put(
					"searchResultsPortletDisplayContext", searchResultsPortletDisplayContext
				).put(
					"userClassName", User.class.getName()
				).build()
			%>'
			displayStyle="<%= searchResultsPortletInstanceConfiguration.displayStyle() %>"
			displayStyleGroupId="<%= searchResultsPortletDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= searchResultSummaryDisplayContexts %>"
		/>

		<c:if test="<%= searchResultsPortletDisplayContext.isShowPagination() %>">
			<aui:form action="#" useNamespace="<%= false %>">
				<liferay-ui:search-paginator
					id="<%= liferayPortletResponse.getNamespace() %>"
					markupView="lexicon"
					searchContainer="<%= searchContainer %>"
				/>
			</aui:form>
		</c:if>
	</c:otherwise>
</c:choose>