<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/asset_categories_error/init.jsp" %>

<liferay-ui:error exception="<%= AssetCategoryException.class %>">

	<%
	AssetCategoryException ace = (AssetCategoryException)errorException;

	AssetVocabulary vocabulary = ace.getVocabulary();

	String vocabularyTitle = StringPool.BLANK;

	if (vocabulary != null) {
		vocabularyTitle = vocabulary.getTitle(locale);
	}
	%>

	<c:choose>
		<c:when test="<%= ace.getType() == AssetCategoryException.AT_LEAST_ONE_CATEGORY %>">
			<liferay-ui:message arguments="<%= HtmlUtil.escape(vocabularyTitle) %>" key="please-select-at-least-one-category-for-x" translateArguments="<%= false %>" />
		</c:when>
		<c:when test="<%= ace.getType() == AssetCategoryException.TOO_MANY_CATEGORIES %>">
			<liferay-ui:message arguments="<%= HtmlUtil.escape(vocabularyTitle) %>" key="you-cannot-select-more-than-one-category-for-x" translateArguments="<%= false %>" />
		</c:when>
	</c:choose>
</liferay-ui:error>