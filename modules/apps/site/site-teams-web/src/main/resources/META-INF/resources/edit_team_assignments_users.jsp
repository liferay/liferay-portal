<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditSiteTeamAssignmentsUsersDisplayContext editSiteTeamAssignmentsUsersDisplayContext = new EditSiteTeamAssignmentsUsersDisplayContext(request, renderRequest, renderResponse);

EditSiteTeamAssignmentsUsersManagementToolbarDisplayContext editSiteTeamAssignmentsUsersManagementToolbarDisplayContext = new EditSiteTeamAssignmentsUsersManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, editSiteTeamAssignmentsUsersDisplayContext);
%>

<clay:navigation-bar
	navigationItems="<%= editSiteTeamAssignmentsUsersDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= editSiteTeamAssignmentsUsersManagementToolbarDisplayContext %>"
	propsTransformer="{EditTeamAssignmentsUsersManagementToolbarPropsTransformer} from site-teams-web"
/>

<portlet:actionURL name="deleteTeamUsers" var="deleteTeamUsersURL" />

<aui:form action="<%= deleteTeamUsersURL %>" cssClass="container-fluid portlet-site-teams-users" method="post" name="fm">
	<aui:input name="tabs1" type="hidden" value="<%= editSiteTeamAssignmentsUsersDisplayContext.getTabs1() %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="teamId" type="hidden" value="<%= String.valueOf(editSiteTeamAssignmentsUsersDisplayContext.getTeamId()) %>" />

	<clay:alert
		dismissible="<%= true %>"
		displayType="info"
		message="inherited-memberships-from-an-organization-or-a-user-group-are-managed-at-their-source-and-cannot-be-removed-here"
	/>

	<liferay-ui:search-container
		id="users"
		searchContainer="<%= editSiteTeamAssignmentsUsersDisplayContext.getUserSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			escapedModel="<%= true %>"
			keyProperty="userId"
			modelVar="user2"
			rowIdProperty="screenName"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", editSiteTeamAssignmentsUsersManagementToolbarDisplayContext.getAvailableActions(editSiteTeamAssignmentsUsersDisplayContext.getTeamId(), user2.getUserId())
				).build());
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(editSiteTeamAssignmentsUsersDisplayContext.getDisplayStyle(), "icon") %>'>
					<liferay-ui:search-container-column-text>
						<clay:user-card
							propsTransformer="{UserDropdownDefaultPropsTransformer} from site-teams-web"
							userCard="<%= new UserUserCard(editSiteTeamAssignmentsUsersDisplayContext.isInheritedMember(user2.getUserId()), renderRequest, renderResponse, editSiteTeamAssignmentsUsersDisplayContext.getRowChecker(), editSiteTeamAssignmentsUsersDisplayContext.getTeamId(), user2) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(editSiteTeamAssignmentsUsersDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-text>
						<liferay-user:user-portrait
							userId="<%= user2.getUserId() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5"><%= user2.getFullName() %></div>

						<div class="h6 text-default">
							<span><%= user2.getScreenName() %></span>
						</div>

						<c:choose>
							<c:when test="<%= editSiteTeamAssignmentsUsersDisplayContext.isInheritedMember(user2.getUserId()) %>">
								<div>
									<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(editSiteTeamAssignmentsUsersDisplayContext.getMembershipLabel(user2.getUserId())) %>">
										<clay:label
											displayType="info"
											label='<%= LanguageUtil.get(request, "inherited") %>'
										/>
									</span>
								</div>
							</c:when>
							<c:otherwise>
								<div>
									<clay:label
										displayType="secondary"
										label='<%= LanguageUtil.get(request, "direct") %>'
									/>
								</div>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>

						<%
						UserActionDropdownItemsProvider userActionDropdownItemsProvider = new UserActionDropdownItemsProvider(user2, editSiteTeamAssignmentsUsersDisplayContext.getTeamId(), renderRequest, renderResponse);
						%>

						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= userActionDropdownItemsProvider.getActionDropdownItems() %>"
							propsTransformer="{UserDropdownDefaultPropsTransformer} from site-teams-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="name"
						property="fullName"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="screen-name"
						property="screenName"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200"
						name="membership"
					>
						<c:choose>
							<c:when test="<%= editSiteTeamAssignmentsUsersDisplayContext.isInheritedMember(user2.getUserId()) %>">
								<span class="lfr-portal-tooltip" title="<%= HtmlUtil.escape(editSiteTeamAssignmentsUsersDisplayContext.getMembershipLabel(user2.getUserId())) %>">
									<clay:label
										displayType="info"
										label='<%= LanguageUtil.get(request, "inherited") %>'
									/>
								</span>
							</c:when>
							<c:otherwise>
								<clay:label
									displayType="secondary"
									label='<%= LanguageUtil.get(request, "direct") %>'
								/>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>

						<%
						UserActionDropdownItemsProvider userActionDropdownItemsProvider = new UserActionDropdownItemsProvider(user2, editSiteTeamAssignmentsUsersDisplayContext.getTeamId(), renderRequest, renderResponse);
						%>

						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= userActionDropdownItemsProvider.getActionDropdownItems() %>"
							propsTransformer="{UserDropdownDefaultPropsTransformer} from site-teams-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= editSiteTeamAssignmentsUsersDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="addTeamUsers" var="addTeamUsersURL" />

<aui:form action="<%= addTeamUsersURL %>" cssClass="hide" name="addTeamUsersFm">
	<aui:input name="tabs1" type="hidden" value="<%= editSiteTeamAssignmentsUsersDisplayContext.getTabs1() %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="teamId" type="hidden" value="<%= String.valueOf(editSiteTeamAssignmentsUsersDisplayContext.getTeamId()) %>" />
</aui:form>