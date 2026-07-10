<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/info/item/renderer/init.jsp" %>

<%
AssetCategory assetCategory = (AssetCategory)request.getAttribute(AssetCategoriesAdminWebKeys.ASSET_CATEGORY);
%>

<div class="asset-summary">
	<%= HtmlUtil.escape(assetCategory.getTitle(locale)) %>
</div>