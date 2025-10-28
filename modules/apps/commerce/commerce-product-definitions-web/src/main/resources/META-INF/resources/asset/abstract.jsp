<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetRenderer<?> assetRenderer = (AssetRenderer<?>)request.getAttribute(WebKeys.ASSET_RENDERER);
%>

<liferay-util:html-top
	outputKey="com.liferay.commerce.product.definitions.web#/asset/abstract.jsp"
>
	<aui:link hashedFile="<%= true %>" href="commerce-product-definitions-web/css/main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<div class="portlet-commerce-product-definitions">
	<div class="entry-body">
		<%= assetRenderer.getSummary(renderRequest, renderResponse) %>
	</div>
</div>