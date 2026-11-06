<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/message_boards/init.jsp" %>

<%
String navigation = "banned-users";

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/message_boards_admin/view_banned_users"
).buildPortletURL();
%>

<%@ include file="/message_boards/nav.jspf" %>

<%
MBBannedUsersManagementToolbarDisplayContext mbBannedUsersManagementToolbarDisplayContext = new MBBannedUsersManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse);

int totalBannedUsers = MBBanLocalServiceUtil.getBansCount(scopeGroupId);
%>

<clay:management-toolbar
	actionDropdownItems="<%= mbBannedUsersManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps="<%= mbBannedUsersManagementToolbarDisplayContext.getAdditionalProps() %>"
	disabled="<%= totalBannedUsers == 0 %>"
	itemsTotal="<%= totalBannedUsers %>"
	propsTransformer="{BanUsersManagementToolbarPropsTransformer} from message-boards-web"
	searchContainerId="mbBanUsers"
	showCreationMenu="<%= false %>"
	showInfoButton="<%= false %>"
	showSearch="<%= false %>"
/>

<clay:container-fluid>
	<aui:form action="<%= portletURL %>" method="get" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<liferay-ui:search-container
			emptyResultsMessage="there-are-no-banned-users"
			headerNames="banned-user,banned-by,ban-date"
			id="mbBanUsers"
			iteratorURL="<%= portletURL %>"
			rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
			total="<%= totalBannedUsers %>"
		>
			<liferay-ui:search-container-results
				results="<%= MBBanLocalServiceUtil.getBans(scopeGroupId, searchContainer.getStart(), searchContainer.getEnd()) %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.message.boards.model.MBBan"
				keyPropertyName="banUserId"
				modelVar="ban"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(mbBannedUsersManagementToolbarDisplayContext.getAvailableActions(ban))
					).build());
				%>

				<liferay-ui:search-container-column-user
					showDetails="<%= false %>"
					userId="<%= ban.getBanUserId() %>"
				/>

				<liferay-ui:search-container-column-text
					colspan="<%= 2 %>"
				>

					<%
					Date createDate = ban.getCreateDate();

					String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true);
					%>

					<span class="text-default">
						<liferay-ui:message arguments="<%= new String[] {HtmlUtil.escape(PortalUtil.getUserName(ban.getUserId(), StringPool.BLANK)), modifiedDateDescription} %>" key="banned-by-x-x-ago" />
					</span>

					<h2 class="h5">

						<%
						User bannedUser = UserLocalServiceUtil.fetchUser(ban.getBanUserId());
						%>

						<c:choose>
							<c:when test="<%= (bannedUser != null) && bannedUser.isActive() %>">
								<aui:a href="<%= bannedUser.getDisplayURL(themeDisplay) %>">
									<%= HtmlUtil.escape(PortalUtil.getUserName(ban.getBanUserId(), StringPool.BLANK)) %>
								</aui:a>
							</c:when>
							<c:otherwise>
								<%= HtmlUtil.escape(PortalUtil.getUserName(ban.getBanUserId(), StringPool.BLANK)) %>
							</c:otherwise>
						</c:choose>
					</h2>

					<span class="text-default">
						<liferay-ui:message key="unban-date" />

						<%= dateTimeFormat.format(com.liferay.message.boards.util.MBUtil.getUnbanDate(ban, PropsValues.MESSAGE_BOARDS_EXPIRE_BAN_INTERVAL)) %>
					</span>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-jsp
					path="/message_boards/ban_user_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= mbBannedUsersManagementToolbarDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<%
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, TextFormatter.format("banned-users", TextFormatter.O)), portletURL.toString());

PortalUtil.setPageSubtitle(LanguageUtil.get(request, "banned-users"), request);
%>