<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.suggestions.display.context.SuggestionDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.suggestions.display.context.SuggestionsPortletDisplayContext" %>

<%
SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = (SuggestionsPortletDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>

<div class="search-suggested-spelling">
	<c:if test="<%= suggestionsPortletDisplayContext.hasSpellCheckSuggestion() %>">
		<ul class="list-inline suggested-keywords">
			<li class="label label-default">
				<liferay-ui:message key="did-you-mean" />:
			</li>
			<li>

				<%
				SuggestionDisplayContext suggestionDisplayContext = suggestionsPortletDisplayContext.getSpellCheckSuggestion();
				%>

				<a href="<%= suggestionDisplayContext.getURL() %>">
					<%= suggestionDisplayContext.getSuggestedKeywordsFormatted() %>
				</a>
			</li>
		</ul>
	</c:if>

	<c:if test="<%= suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions() %>">
		<ul class="list-inline related-queries">
			<li class="label label-default">
				<liferay-ui:message key="related-queries" />:
			</li>

			<%
			for (SuggestionDisplayContext suggestionDisplayContext : suggestionsPortletDisplayContext.getRelatedQueriesSuggestions()) {
			%>

				<li>
					<a href="<%= suggestionDisplayContext.getURL() %>">
						<%= suggestionDisplayContext.getSuggestedKeywordsFormatted() %>
					</a>
				</li>

			<%
			}
			%>

		</ul>
	</c:if>
</div>