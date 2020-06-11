<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetRenderer<?> assetRenderer = (AssetRenderer<?>)request.getAttribute(WebKeys.ASSET_RENDERER);

BlogsEntry entry = (BlogsEntry)request.getAttribute(WebKeys.BLOGS_ENTRY);

Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletDisplay.getId());

String coverImageURL = entry.getCoverImageURL(themeDisplay);
%>

<liferay-util:html-top
	outputKey="blogs_common_main_css"
>
	<link href="<%= PortalUtil.getStaticResourceURL(request, application.getContextPath() + "/blogs/css/common_main.css", portlet.getTimestamp()) %>" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<c:if test="<%= entry.isSmallImage() && Validator.isNull(coverImageURL) %>">
	<div class="asset-small-image">
		<img alt="" class="asset-small-image img-thumbnail" src="<%= HtmlUtil.escape(entry.getSmallImageURL(themeDisplay)) %>" width="150" />
	</div>
</c:if>

<div class="portlet-blogs">
	<div class="entry-body">
		<c:if test="<%= Validator.isNotNull(coverImageURL) %>">
			<div class="cover-image-container" style="background-image: url(<%= coverImageURL %>)"></div>
		</c:if>

		<%= HtmlUtil.escape(assetRenderer.getSummary(renderRequest, renderResponse)) %>
	</div>
</div>