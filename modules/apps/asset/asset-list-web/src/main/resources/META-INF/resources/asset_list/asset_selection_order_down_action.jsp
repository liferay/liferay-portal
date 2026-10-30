<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchContainer<?> searchContainer = (SearchContainer<?>)request.getAttribute("liferay-ui:search:searchContainer");

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

AssetListEntryAssetEntryRel assetListEntryAssetEntryRel = (AssetListEntryAssetEntryRel)row.getObject();

int position = assetListEntryAssetEntryRel.getPosition();

boolean last = position == (searchContainer.getTotal() - 1);
%>

<c:choose>
	<c:when test="<%= ((position > 0) && !last) || ((position == 0) && (searchContainer.getTotal() > 1)) %>">
		<portlet:actionURL name="/asset_list/move_asset_entry_selection" var="moveAssetEntrySelectionDownURL">
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="assetListEntryId" value="<%= String.valueOf(assetListEntryAssetEntryRel.getAssetListEntryId()) %>" />
			<portlet:param name="audienceEntryERC" value="<%= assetListEntryAssetEntryRel.getAudienceEntryERC() %>" />
			<portlet:param name="segmentsEntryId" value="<%= String.valueOf(assetListEntryAssetEntryRel.getSegmentsEntryId()) %>" />
			<portlet:param name="position" value="<%= String.valueOf(position) %>" />
			<portlet:param name="newPosition" value="<%= String.valueOf(position + 1) %>" />
		</portlet:actionURL>

		<clay:link
			aria-label='<%= LanguageUtil.get(request, "down") %>'
			cssClass="lfr-portal-tooltip"
			href="<%= moveAssetEntrySelectionDownURL %>"
			icon="angle-down"
			title='<%= LanguageUtil.get(request, "down") %>'
		/>
	</c:when>
	<c:when test="<%= last && (searchContainer.getTotal() > 1) %>">
		<portlet:actionURL name="/asset_list/move_asset_entry_selection" var="moveAssetEntrySelectionUpURL">
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="assetListEntryId" value="<%= String.valueOf(assetListEntryAssetEntryRel.getAssetListEntryId()) %>" />
			<portlet:param name="audienceEntryERC" value="<%= assetListEntryAssetEntryRel.getAudienceEntryERC() %>" />
			<portlet:param name="segmentsEntryId" value="<%= String.valueOf(assetListEntryAssetEntryRel.getSegmentsEntryId()) %>" />
			<portlet:param name="position" value="<%= String.valueOf(position) %>" />
			<portlet:param name="newPosition" value="<%= String.valueOf(position - 1) %>" />
		</portlet:actionURL>

		<clay:link
			aria-label='<%= LanguageUtil.get(request, "up") %>'
			cssClass="lfr-portal-tooltip"
			href="<%= moveAssetEntrySelectionUpURL %>"
			icon="angle-up"
			title='<%= LanguageUtil.get(request, "up") %>'
		/>
	</c:when>
</c:choose>