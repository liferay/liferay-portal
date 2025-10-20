<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

CommerceWishList commerceWishList = (CommerceWishList)row.getObject();
%>

<c:if test="<%= !commerceWishList.getDefaultWishList() %>">
	<portlet:actionURL name="/commerce_wish_list_content/edit_commerce_wish_list" var="deleteURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="commerceWishListId" value="<%= String.valueOf(commerceWishList.getCommerceWishListId()) %>" />
	</portlet:actionURL>

	<clay:link
		aria-label='<%= LanguageUtil.get(request, "delete") %>'
		href="<%= deleteURL.toString() %>"
		icon="times"
	/>
</c:if>