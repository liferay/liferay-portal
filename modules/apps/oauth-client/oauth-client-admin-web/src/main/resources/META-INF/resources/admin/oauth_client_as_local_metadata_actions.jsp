<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

OAuthClientASLocalMetadata oAuthClientASLocalMetadata = (OAuthClientASLocalMetadata)row.getObject();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>
	<portlet:renderURL var="editURL">
		<portlet:param name="mvcRenderCommandName" value="/oauth_client_admin/update_oauth_client_as_local_metadata" />
		<portlet:param name="localWellKnownURIOIC" value="<%= oAuthClientASLocalMetadata.getLocalWellKnownURIOIC() %>" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<liferay-ui:icon
		message="edit"
		url="<%= editURL.toString() %>"
	/>

	<portlet:actionURL name="/oauth_client_admin/delete_oauth_client_as_local_metadata" var="deleteURL">
		<portlet:param name="localWellKnownURIOIC" value="<%= oAuthClientASLocalMetadata.getLocalWellKnownURIOIC() %>" />
	</portlet:actionURL>

	<liferay-ui:icon-delete
		message="delete"
		url="<%= deleteURL.toString() %>"
	/>
</liferay-ui:icon-menu>