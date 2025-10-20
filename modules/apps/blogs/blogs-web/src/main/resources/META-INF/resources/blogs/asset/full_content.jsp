<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:html-top
	outputKey="com.liferay.blogs.web#/blogs/asset/full_content.jsp"
>
	<aui:link hashedFile="<%= true %>" href="blogs-web/blogs/css/common_main.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<liferay-util:dynamic-include key="com.liferay.blogs.web#/blogs/asset/full_content.jsp#pre" />

<%
BlogsEntry entry = (BlogsEntry)request.getAttribute(WebKeys.BLOGS_ENTRY);

String entryTitle = BlogsEntryUtil.getDisplayTitle(resourceBundle, entry);
%>

<div class="portlet-blogs">
	<div class="widget-mode-simple" data-analytics-asset-id="<%= String.valueOf(entry.getEntryId()) %>" data-analytics-asset-title="<%= HtmlUtil.escapeAttribute(entryTitle) %>" data-analytics-asset-type="blog">
		<div class="widget-mode-simple-entry">
			<div class="widget-content" id="<portlet:namespace /><%= entry.getEntryId() %>">
				<liferay-util:include page="/blogs/entry_cover_image_caption.jsp" servletContext="<%= application %>">
					<liferay-util:param name="coverImageCaption" value="<%= entry.getCoverImageCaption() %>" />
					<liferay-util:param name="coverImageURL" value="<%= entry.getCoverImageURL(themeDisplay) %>" />
				</liferay-util:include>

				<%= entry.getContent() %>
			</div>

			<liferay-expando:custom-attributes-available
				className="<%= BlogsEntry.class.getName() %>"
			>
				<liferay-expando:custom-attribute-list
					className="<%= BlogsEntry.class.getName() %>"
					classPK="<%= (entry != null) ? entry.getEntryId() : 0 %>"
					editable="<%= false %>"
					label="<%= true %>"
				/>
			</liferay-expando:custom-attributes-available>
		</div>
	</div>
</div>

<liferay-util:dynamic-include key="com.liferay.blogs.web#/blogs/asset/full_content.jsp#post" />