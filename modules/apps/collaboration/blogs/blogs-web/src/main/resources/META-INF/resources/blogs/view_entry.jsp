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

<%@ include file="/blogs/init.jsp" %>

<liferay-util:dynamic-include key="com.liferay.blogs.web#/blogs/view_entry.jsp#pre" />

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setParameter("mvcRenderCommandName", "/blogs/view");

	redirect = portletURL.toString();
}

BlogsEntry entry = (BlogsEntry)request.getAttribute(WebKeys.BLOGS_ENTRY);

long entryId = ParamUtil.getLong(request, "entryId", entry.getEntryId());

AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(BlogsEntry.class.getName(), entry.getEntryId());

AssetEntryServiceUtil.incrementViewCounter(assetEntry);

AssetUtil.addLayoutTags(request, AssetTagLocalServiceUtil.getTags(BlogsEntry.class.getName(), entry.getEntryId()));

RatingsEntry ratingsEntry = RatingsEntryLocalServiceUtil.fetchEntry(themeDisplay.getUserId(), BlogsEntry.class.getName(), entry.getEntryId());
RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.fetchStats(BlogsEntry.class.getName(), entry.getEntryId());

request.setAttribute(WebKeys.LAYOUT_ASSET_ENTRY, assetEntry);

request.setAttribute("view_entry_content.jsp-entry", entry);

request.setAttribute("view_entry_content.jsp-assetEntry", assetEntry);

request.setAttribute("view_entry_content.jsp-ratingsEntry", ratingsEntry);
request.setAttribute("view_entry_content.jsp-ratingsStats", ratingsStats);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	renderResponse.setTitle(entry.getTitle());
}
%>

<portlet:actionURL name="/blogs/edit_entry" var="editEntryURL" />

<aui:form action="<%= editEntryURL %>" method="post" name="fm1" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveEntry();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="entryId" type="hidden" value="<%= String.valueOf(entryId) %>" />

	<liferay-util:include page="/blogs/view_entry_content.jsp" servletContext="<%= application %>" />
</aui:form>

<div class="container-fluid">
	<c:if test="<%= PropsValues.BLOGS_ENTRY_PREVIOUS_AND_NEXT_NAVIGATION_ENABLED %>">

		<%
		BlogsEntry[] prevAndNext = BlogsEntryServiceUtil.getEntriesPrevAndNext(entryId);

		BlogsEntry previousEntry = prevAndNext[0];
		BlogsEntry nextEntry = prevAndNext[2];
		%>

		<c:if test="<%= (previousEntry != null) || (nextEntry != null) %>">
			<div class="row">
				<div class="col-md-8 col-md-offset-2 entry-navigation">
					<h2><strong><liferay-ui:message key="more-blog-entries" /></strong></h2>

					<div class="row">
						<c:if test="<%= previousEntry != null %>">
							<aui:col cssClass="entry-navigation-item" md="4" sm="6">
								<portlet:renderURL var="previousEntryURL">
									<portlet:param name="mvcRenderCommandName" value="/blogs/view_entry" />
									<portlet:param name="urlTitle" value="<%= previousEntry.getUrlTitle() %>" />
								</portlet:renderURL>

								<liferay-util:html-top
									outputKey="blogs_previous_entry_link"
								>
									<link href="<%= previousEntryURL.toString() %>" rel="prev" />
								</liferay-util:html-top>

								<%
								String smallImageURL = previousEntry.getSmallImageURL(themeDisplay);
								%>

								<c:if test="<%= Validator.isNotNull(smallImageURL) %>">
									<aui:a href="<%= previousEntryURL %>" title="<%= HtmlUtil.escape(previousEntry.getTitle()) %>">
										<span class="small-image visible-lg-block visible-md-block" style="background-image: url(<%= HtmlUtil.escape(smallImageURL) %>)"></span>
									</aui:a>
								</c:if>

								<div class="entry-info text-muted">
									<small>
										<strong><%= HtmlUtil.escape(previousEntry.getUserName()) %></strong>
										<span> - </span>
										<span class="hide-accessible"><liferay-ui:message key="published-date" /></span>
										<%= dateFormatDate.format(previousEntry.getDisplayDate()) %>
									</small>
								</div>

								<div class="entry-content">
									<h4>
										<aui:a href="<%= previousEntryURL %>" title="<%= HtmlUtil.escape(previousEntry.getTitle()) %>"><%= HtmlUtil.escape(previousEntry.getTitle()) %></aui:a>
									</h4>

									<p class="entry-content-body visible-lg-block">
										<%= StringUtil.shorten(HtmlUtil.stripHtml(previousEntry.getContent()), 100) %>
									</p>
								</div>
							</aui:col>
						</c:if>

						<c:if test="<%= nextEntry != null %>">
							<aui:col cssClass="entry-navigation-item" md="4" sm="6">
								<portlet:renderURL var="nextEntryURL">
									<portlet:param name="mvcRenderCommandName" value="/blogs/view_entry" />
									<portlet:param name="urlTitle" value="<%= nextEntry.getUrlTitle() %>" />
								</portlet:renderURL>

								<liferay-util:html-top
									outputKey="blogs_next_entry_link"
								>
									<link href="<%= nextEntryURL.toString() %>" rel="next" />
								</liferay-util:html-top>

								<%
								String smallImageURL = nextEntry.getSmallImageURL(themeDisplay);
								%>

								<c:if test="<%= Validator.isNotNull(smallImageURL) %>">
									<aui:a href="<%= nextEntryURL %>" title="<%= HtmlUtil.escape(nextEntry.getTitle()) %>">
										<span class="small-image visible-lg-block visible-md-block" style="background-image: url(<%= HtmlUtil.escape(smallImageURL) %>)"></span>
									</aui:a>
								</c:if>

								<div class="entry-info text-muted">
									<small>
										<strong><%= HtmlUtil.escape(nextEntry.getUserName()) %></strong>
										<span> - </span>
										<span class="hide-accessible"><liferay-ui:message key="published-date" /></span>
										<%= dateFormatDate.format(nextEntry.getDisplayDate()) %>
									</small>
								</div>

								<div class="entry-content">
									<h4>
										<aui:a href="<%= nextEntryURL %>" title="<%= HtmlUtil.escape(nextEntry.getTitle()) %>"><%= HtmlUtil.escape(nextEntry.getTitle()) %></aui:a>
									</h4>

									<p class="visible-lg-block">
										<%= StringUtil.shorten(HtmlUtil.stripHtml(nextEntry.getContent()), 100) %>
									</p>
								</div>
							</aui:col>
						</c:if>
					</div>
				</div>
			</div>
		</c:if>
	</c:if>

	<div class="row">
		<div class="col-md-8 col-md-offset-2">
			<c:if test="<%= blogsPortletInstanceConfiguration.enableComments() %>">

				<%
				Discussion discussion = CommentManagerUtil.getDiscussion(user.getUserId(), scopeGroupId, BlogsEntry.class.getName(), entry.getEntryId(), new ServiceContextFunction(request));
				%>

				<c:if test="<%= discussion != null %>">
					<h2>
						<strong><liferay-ui:message arguments="<%= CommentManagerUtil.getCommentsCount(BlogsEntry.class.getName(), entry.getEntryId()) %>" key="x-comments" /></strong>
					</h2>

					<c:if test="<%= PropsValues.BLOGS_TRACKBACK_ENABLED && entry.isAllowTrackbacks() %>">
						<aui:input inlineLabel="left" name="trackbackURL" type="resource" value='<%= PortalUtil.getLayoutFullURL(themeDisplay.getLayout(), themeDisplay, false) + Portal.FRIENDLY_URL_SEPARATOR + "blogs/trackback/" + entry.getUrlTitle() %>' />
					</c:if>

					<liferay-ui:discussion
						className="<%= BlogsEntry.class.getName() %>"
						classPK="<%= entry.getEntryId() %>"
						discussion="<%= discussion %>"
						formName="fm2"
						ratingsEnabled="<%= blogsPortletInstanceConfiguration.enableCommentRatings() %>"
						redirect="<%= currentURL %>"
						userId="<%= entry.getUserId() %>"
					/>
				</c:if>
			</c:if>
		</div>
	</div>
</div>

<%
PortalUtil.setPageTitle(entry.getTitle(), request);
PortalUtil.setPageSubtitle(entry.getSubtitle(), request);
PortalUtil.setPageDescription(entry.getDescription(), request);

List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(BlogsEntry.class.getName(), entry.getEntryId());

PortalUtil.setPageKeywords(ListUtil.toString(assetTags, AssetTag.NAME_ACCESSOR), request);

PortalUtil.addPortletBreadcrumbEntry(request, entry.getTitle(), currentURL);
%>

<liferay-util:dynamic-include key="com.liferay.blogs.web#/blogs/view_entry.jsp#post" />