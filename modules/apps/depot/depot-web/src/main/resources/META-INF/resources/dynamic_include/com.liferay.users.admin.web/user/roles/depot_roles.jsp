<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotAdminRolesDisplayContext depotAdminRolesDisplayContext = (DepotAdminRolesDisplayContext)request.getAttribute(DepotAdminRolesDisplayContext.class.getName());
%>

<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="/depot/update_roles" />

<clay:content-row
	containerElement="div"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><%= depotAdminRolesDisplayContext.getLabel() %></span>
	</clay:content-col>

	<c:if test="<%= depotAdminRolesDisplayContext.isSelectable() %>">
		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", depotAdminRolesDisplayContext.getLabelKey()) %>'
				cssClass="heading-end modify-link"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectDepotRoleLink" %>'
				label='<%= LanguageUtil.get(request, "select") %>'
				small="<%= true %>"
			/>
		</clay:content-col>
	</c:if>
</clay:content-row>

<liferay-util:buffer
	var="removeDepotRoleIcon"
>
	<liferay-ui:icon
		icon="times-circle"
		markupView="lexicon"
		message="remove"
	/>
</liferay-util:buffer>

<aui:input name="addDepotGroupRolesGroupIds" type="hidden" />
<aui:input name="addDepotGroupRolesRoleIds" type="hidden" />
<aui:input name="deleteDepotGroupRolesGroupIds" type="hidden" />
<aui:input name="deleteDepotGroupRolesRoleIds" type="hidden" />

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-depot-roles"
	curParam="depotRolesCur"
	emptyResultsMessage="<%= depotAdminRolesDisplayContext.getEmptyResultsMessageKey() %>"
	headerNames="<%= depotAdminRolesDisplayContext.getHeaderNames() %>"
	id="depotRolesSearchContainer"
	iteratorURL="<%= currentURLObj %>"
	total="<%= depotAdminRolesDisplayContext.getUserGroupRolesCount() %>"
>
	<liferay-ui:search-container-results
		results="<%= depotAdminRolesDisplayContext.getUserGroupRoles(searchContainer.getStart(), searchContainer.getResultEnd()) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.UserGroupRole"
		escapedModel="<%= true %>"
		keyProperty="roleId"
		modelVar="userGroupRole"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="title"
		>

			<%
			Role role = userGroupRole.getRole();
			%>

			<liferay-ui:icon
				iconCssClass="<%= role.getIconCssClass() %>"
				label="<%= true %>"
				message="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="<%= depotAdminRolesDisplayContext.getAssetLibraryLabelKey() %>"
		>
			<liferay-staging:descriptive-name
				group="<%= userGroupRole.getGroup() %>"
			/>
		</liferay-ui:search-container-column-text>

		<c:if test="<%= depotAdminRolesDisplayContext.isDeletable() %>">
			<liferay-ui:search-container-column-text>
				<a class="modify-link" data-entityId="<%= userGroupRole.getGroupId() %>-<%= userGroupRole.getRoleId() %>" href="javascript:void(0);"><%= removeDepotRoleIcon %></a>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<c:if test="<%= depotAdminRolesDisplayContext.isSelectable() %>">
	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"portletNamespace", liferayPortletResponse.getNamespace()
			).put(
				"removeDepotRoleIcon", removeDepotRoleIcon
			).put(
				"searchContainerId", "depotRolesSearchContainer"
			).put(
				"selectDepotRolesURL", depotAdminRolesDisplayContext.getSelectDepotRolesURL()
			).put(
				"selectEventName", depotAdminRolesDisplayContext.getSelectDepotRolesEventName()
			).build()
		%>'
		module="{DepotRoles} from depot-web"
	/>
</c:if>