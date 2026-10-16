<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/dynamic_include/init.jsp" %>

<%
BlogsGroupServiceConfiguration blogsGroupServiceConfiguration = ConfigurationProviderUtil.getConfiguration(BlogsGroupServiceConfiguration.class, new GroupServiceSettingsLocator(themeDisplay.getSiteGroupId(), BlogsConstants.SERVICE_NAME));
%>

<div class="btn-group">
	<c:if test="<%= BlogsUtil.isRSSFeedsEnabled(blogsGroupServiceConfiguration) && ((themeDisplay.getScopeGroupId() == 0) || GroupPermissionUtil.contains(themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(), ActionKeys.VIEW)) %>">

		<%
		String rssURL = RSSUtil.getURL(StringBundler.concat(themeDisplay.getPathMain(), "/blogs/rss?plid=", String.valueOf(themeDisplay.getPlid()), "&groupId=", String.valueOf(themeDisplay.getScopeGroupId())), GetterUtil.getInteger(blogsGroupServiceConfiguration.rssDelta()), blogsGroupServiceConfiguration.rssDisplayStyle(), blogsGroupServiceConfiguration.rssFeedType(), null);
		%>

		<div class="btn-group-item">
			<clay:link
				borderless="<%= true %>"
				displayType="secondary"
				href="<%= rssURL %>"
				icon="rss-full"
				label="rss"
				small="<%= true %>"
				type="button"
			/>
		</div>

		<liferay-util:html-top>
			<link href="<%= HtmlUtil.escapeAttribute(rssURL) %>" rel="alternate" title="RSS" type="application/rss+xml" />
		</liferay-util:html-top>
	</c:if>

	<%
	BlogsGroupServiceSettings blogsGroupServiceSettings = BlogsGroupServiceSettings.getInstance(scopeGroupId);
	%>

	<c:if test="<%= (blogsGroupServiceSettings.isEmailEntryAddedEnabled() || blogsGroupServiceSettings.isEmailEntryUpdatedEnabled()) && BlogsPermission.contains(permissionChecker, scopeGroupId, ActionKeys.SUBSCRIBE) %>">
		<div class="btn-group-item">
			<c:choose>
				<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(company.getCompanyId(), user.getUserId(), BlogsEntry.class.getName(), scopeGroupId) %>">
					<portlet:actionURL name="/blogs/edit_entry" var="unsubscribeURL">
						<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<clay:link
						displayType="secondary"
						href="<%= unsubscribeURL %>"
						label="unsubscribe"
						small="<%= true %>"
						type="button"
					/>
				</c:when>
				<c:otherwise>
					<portlet:actionURL name="/blogs/edit_entry" var="subscribeURL">
						<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:actionURL>

					<clay:link
						displayType="secondary"
						href="<%= subscribeURL %>"
						label="subscribe"
						small="<%= true %>"
						type="button"
					/>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>

	<c:if test="<%= BlogsPermission.contains(permissionChecker, scopeGroupId, ActionKeys.ADD_ENTRY) %>">
		<div class="btn-group-item">
			<clay:link
				displayType="primary"
				href='<%=
					PortletURLBuilder.create(
						PortalUtil.getControlPanelPortletURL(request, themeDisplay.getScopeGroup(), BlogsPortletKeys.BLOGS_ADMIN, 0, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
					).setMVCRenderCommandName(
						"/blogs/edit_entry"
					).setRedirect(
						currentURL
					).setPortletResource(
						portletDisplay.getId()
					).buildString()
				%>'
				label="new-entry"
				small="<%= true %>"
				type="button"
			/>
		</div>
	</c:if>
</div>