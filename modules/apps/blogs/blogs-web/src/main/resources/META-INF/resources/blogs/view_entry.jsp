<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/blogs/init.jsp" %>

<liferay-util:dynamic-include key="com.liferay.blogs.web#/blogs/view_entry.jsp#pre" />

<%
BlogsViewEntryDisplayContext blogsViewEntryDisplayContext = new BlogsViewEntryDisplayContext(liferayPortletRequest, liferayPortletResponse);

request.setAttribute("view_entry_content.jsp-entry", blogsViewEntryDisplayContext.getBlogsEntry());

request.setAttribute("view_entry_content.jsp-assetEntry", blogsViewEntryDisplayContext.getBlogsEntryAssetEntry());

request.setAttribute("view_entry_content.jsp-ratingsEntry", blogsViewEntryDisplayContext.getBlogsEntryRatingsEntry());
request.setAttribute("view_entry_content.jsp-ratingsStats", blogsViewEntryDisplayContext.getBlogsEntryRatingsStats());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(blogsViewEntryDisplayContext.getRedirect());

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	renderResponse.setTitle(blogsViewEntryDisplayContext.getBlogsEntryTitle());
}

PortalUtil.setPageDescription(blogsViewEntryDisplayContext.getBlogsEntryDescription(), request);
PortalUtil.setPageTitle(blogsViewEntryDisplayContext.getBlogsEntryTitle(), request);

List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(BlogsEntry.class.getName(), blogsViewEntryDisplayContext.getBlogsEntryId());

assetHelper.addLayoutTags(request, assetTags);

PortalUtil.setPageKeywords(ListUtil.toString(assetTags, AssetTag.NAME_ACCESSOR), request);

if (request.getAttribute(WebKeys.LAYOUT_ASSET_ENTRY) == null) {
	request.setAttribute(WebKeys.LAYOUT_ASSET_ENTRY, blogsViewEntryDisplayContext.getBlogsEntryAssetEntry());
}

LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(request, blogsViewEntryDisplayContext.getBlogsEntryAssetEntryId());

PortalUtil.addPortletBreadcrumbEntry(request, blogsViewEntryDisplayContext.getBlogsEntryTitle(), currentURL);
%>

<portlet:actionURL name="/blogs/edit_entry" var="editEntryURL" />

<aui:form action="<%= editEntryURL %>" method="post" name="fm1" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveEntry();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="entryId" type="hidden" value="<%= String.valueOf(blogsViewEntryDisplayContext.getBlogsEntryId()) %>" />

	<div class="widget-mode-detail" data-analytics-asset-id="<%= String.valueOf(blogsViewEntryDisplayContext.getBlogsEntryId()) %>" data-analytics-asset-title="<%= HtmlUtil.escapeAttribute(blogsViewEntryDisplayContext.getBlogsEntryTitle()) %>" data-analytics-asset-type="blog" data-analytics-external-reference-code="<%= blogsViewEntryDisplayContext.getBlogEntryExternalReferenceCode() %>">
		<liferay-util:include page="/blogs/view_entry_content_detail.jsp" servletContext="<%= application %>" />
	</div>
</aui:form>

<clay:container-fluid>
	<c:if test="<%= blogsViewEntryDisplayContext.isBlogsEntryPreviousAndNextNavigationEnabled() %>">
		<clay:row>
			<clay:col
				cssClass="entry-navigation mx-md-auto"
				md="10"
			>
				<h2>
					<strong><liferay-ui:message key="more-blog-entries" /></strong>
				</h2>

				<div class="card-page widget-mode-card">

					<%
					request.setAttribute("view_entry_related.jsp-blogs_entry", blogsViewEntryDisplayContext.getPreviousBlogsEntry());
					%>

					<liferay-util:include page="/blogs/view_entry_related.jsp" servletContext="<%= application %>" />

					<%
					request.setAttribute("view_entry_related.jsp-blogs_entry", blogsViewEntryDisplayContext.getNextBlogsEntry());
					%>

					<liferay-util:include page="/blogs/view_entry_related.jsp" servletContext="<%= application %>" />
				</div>
			</clay:col>
		</clay:row>
	</c:if>

	<clay:row>
		<clay:col
			cssClass="offset-md-2"
			md="8"
		>
			<c:if test="<%= blogsViewEntryDisplayContext.isCommentsEnabled() %>">
				<c:if test="<%= blogsViewEntryDisplayContext.isTrackbackEnabled() %>">
					<aui:input inlineLabel="left" name="trackbackURL" type="resource" value="<%= blogsViewEntryDisplayContext.getTrackbackURL() %>" />
				</c:if>

				<liferay-comment:discussion
					className="<%= BlogsEntry.class.getName() %>"
					classPK="<%= blogsViewEntryDisplayContext.getBlogsEntryId() %>"
					discussion="<%= blogsViewEntryDisplayContext.getDiscussion() %>"
					formName="fm2"
					ratingsEnabled="<%= blogsViewEntryDisplayContext.isCommentRatingsEnabled() %>"
					redirect="<%= currentURL %>"
					userId="<%= blogsViewEntryDisplayContext.getBlogsEntryUserId() %>"
				/>
			</c:if>
		</clay:col>
	</clay:row>
</clay:container-fluid>

<liferay-util:dynamic-include key="com.liferay.blogs.web#/blogs/view_entry.jsp#post" />