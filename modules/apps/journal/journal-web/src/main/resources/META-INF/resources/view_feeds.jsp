<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalFeedsDisplayContext journalFeedsDisplayContext = new JournalFeedsDisplayContext(renderRequest, renderResponse);

JournalFeedsManagementToolbarDisplayContext journalFeedsManagementToolbarDisplayContext = new JournalFeedsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalFeedsDisplayContext);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(journalFeedsDisplayContext.getRedirect());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(LanguageUtil.get(request, "feeds"));
%>

<clay:navigation-bar
	navigationItems='<%= journalDisplayContext.getNavigationItems("feeds") %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= journalFeedsManagementToolbarDisplayContext %>"
	propsTransformer="{FeedsManagementToolbarPropsTransformer} from journal-web"
/>

<portlet:actionURL name="/journal/delete_feeds" var="deleteFeedsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteFeedsURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<liferay-ui:search-container
		id="feeds"
		searchContainer="<%= journalFeedsDisplayContext.getFeedsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.journal.model.JournalFeed"
			escapedModel="<%= true %>"
			keyPropertyName="feedId"
			modelVar="feed"
		>

			<%
			String editURL = StringPool.BLANK;

			if (JournalFeedPermission.contains(permissionChecker, feed, ActionKeys.UPDATE)) {
				editURL = PortletURLBuilder.createRenderURL(
					liferayPortletResponse
				).setMVCPath(
					"/edit_feed.jsp"
				).setRedirect(
					currentURL
				).setParameter(
					"feedId", feed.getFeedId()
				).setParameter(
					"groupId", feed.getGroupId()
				).buildString();
			}

			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", journalFeedsManagementToolbarDisplayContext.getAvailableActions(feed)
				).build());
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(journalFeedsDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="rss"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5">
							<aui:a href="<%= editURL %>">
								<%= feed.getName() %>
							</aui:a>
						</div>

						<div class="h6 text-default">
							<%= feed.getDescription() %>
						</div>

						<div class="h6 text-default">
							<strong><liferay-ui:message key="id" /></strong>: <%= feed.getId() %>
						</div>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>

						<%
						JournalFeedActionDropdownItemsProvider journalFeedActionDropdownItemsProvider = new JournalFeedActionDropdownItemsProvider(feed, liferayPortletRequest, liferayPortletResponse);
						%>

						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= journalFeedActionDropdownItemsProvider.getActionDropdownItems() %>"
							propsTransformer="{FeedElementsDefaultPropsTransformer} from journal-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(journalFeedsDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						name="id"
						property="feedId"
					/>

					<liferay-ui:search-container-column-text
						href="<%= editURL %>"
						name="name"
						property="name"
						truncate="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						name="description"
						property="description"
						truncate="<%= true %>"
					/>

					<liferay-ui:search-container-column-text>

						<%
						JournalFeedActionDropdownItemsProvider journalFeedActionDropdownItemsProvider = new JournalFeedActionDropdownItemsProvider(feed, liferayPortletRequest, liferayPortletResponse);
						%>

						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= journalFeedActionDropdownItemsProvider.getActionDropdownItems() %>"
							propsTransformer="{FeedElementsDefaultPropsTransformer} from journal-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= journalFeedsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>