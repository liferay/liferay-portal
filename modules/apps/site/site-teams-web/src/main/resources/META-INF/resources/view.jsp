<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SiteTeamsDisplayContext siteTeamsDisplayContext = new SiteTeamsDisplayContext(request, renderRequest, renderResponse);

SiteTeamsManagementToolbarDisplayContext siteTeamsManagementToolbarDisplayContext = new SiteTeamsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, siteTeamsDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= siteTeamsManagementToolbarDisplayContext %>"
	propsTransformer="{SiteTeamsManagementToolbarPropsTransformer} from site-teams-web"
/>

<portlet:actionURL name="deleteTeams" var="deleteTeamsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteTeamsURL %>" cssClass="container-fluid" name="fm">
	<liferay-ui:search-container
		searchContainer="<%= siteTeamsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Team"
			escapedModel="<%= true %>"
			keyPropertyName="teamId"
			modelVar="team"
		>

			<%
			PortletURL rowURL = null;

			if (TeamPermissionUtil.contains(permissionChecker, team, ActionKeys.ASSIGN_MEMBERS)) {
				rowURL = PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCPath(
					"/edit_team_assignments.jsp"
				).setParameter(
					"teamId", team.getTeamId()
				).buildPortletURL();
			}

			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", siteTeamsManagementToolbarDisplayContext.getAvailableActions(team)
				).build());
			%>

			<c:choose>
				<c:when test="<%= siteTeamsDisplayContext.isDescriptiveView() %>">
					<liferay-ui:search-container-column-icon
						icon="users"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5">
							<aui:a href="<%= (rowURL != null) ? rowURL.toString() : null %>"><%= team.getName() %></aui:a>
						</div>

						<div class="h6 text-default">
							<span><%= team.getDescription() %></span>
						</div>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-jsp
						path="/team_action.jsp"
					/>
				</c:when>
				<c:when test="<%= siteTeamsDisplayContext.isListView() %>">
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small table-cell-minw-200 table-title"
						href="<%= rowURL %>"
						name="name"
						property="name"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-300"
						href="<%= rowURL %>"
						name="description"
						property="description"
					/>

					<liferay-ui:search-container-column-jsp
						path="/team_action.jsp"
					/>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= siteTeamsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>