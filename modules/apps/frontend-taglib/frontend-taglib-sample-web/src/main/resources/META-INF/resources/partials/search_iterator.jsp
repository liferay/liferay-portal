<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchIteratorDisplayContext searchIteratorDisplayContext = (SearchIteratorDisplayContext)request.getAttribute(SampleWebKeys.SEARCH_ITERATOR_DISPLAY_CONTEXT);
%>

<clay:container-fluid>
	<liferay-ui:search-container
		rowChecker='<%= new com.liferay.portal.kernel.dao.search.RowChecker((RenderResponse)request.getAttribute("jakarta.portlet.response")) %>'
		searchContainer="<%= searchIteratorDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="java.lang.String"
			modelVar="domain"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="name value"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="description"
				value="description value"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>