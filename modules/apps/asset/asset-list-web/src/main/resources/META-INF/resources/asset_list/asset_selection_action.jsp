<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

AssetListEntryAssetEntryRel assetListEntryAssetEntryRel = (AssetListEntryAssetEntryRel)row.getObject();
%>

<portlet:actionURL name="/asset_list/delete_asset_entry_selection" var="deleteAssetEntrySelectionURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="assetListEntryId" value="<%= String.valueOf(assetListEntryAssetEntryRel.getAssetListEntryId()) %>" />
	<portlet:param name="audienceEntryERC" value="<%= assetListEntryAssetEntryRel.getAudienceEntryERC() %>" />
	<portlet:param name="segmentsEntryId" value="<%= String.valueOf(assetListEntryAssetEntryRel.getSegmentsEntryId()) %>" />
	<portlet:param name="position" value="<%= String.valueOf(assetListEntryAssetEntryRel.getPosition()) %>" />
</portlet:actionURL>

<clay:link
	aria-label='<%= LanguageUtil.get(request, "remove") %>'
	cssClass="lfr-portal-tooltip"
	href="<%= deleteAssetEntrySelectionURL %>"
	icon="times-circle"
	title='<%= LanguageUtil.get(request, "remove") %>'
/>