<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OrganizationsDisplayContext organizationsDisplayContext = new OrganizationsDisplayContext(request, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems="<%= siteMembershipsDisplayContext.getViewNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new OrganizationsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, organizationsDisplayContext) %>"
	propsTransformer="{OrganizationsManagementToolbarPropsTransformer} from site-memberships-web"
/>

<portlet:actionURL name="deleteGroupOrganizations" var="deleteGroupOrganizationsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteGroupOrganizationsURL %>" cssClass="container-fluid" method="post" name="fm">
	<aui:input name="tabs1" type="hidden" value="organizations" />
	<aui:input name="groupId" type="hidden" value="<%= String.valueOf(siteMembershipsDisplayContext.getGroupId()) %>" />

	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<liferay-ui:search-container
		id="organizations"
		searchContainer="<%= organizationsDisplayContext.getOrganizationSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Organization"
			escapedModel="<%= true %>"
			keyPropertyName="organizationId"
			modelVar="organization"
		>

			<%
			String displayStyle = organizationsDisplayContext.getDisplayStyle();
			%>

			<c:choose>
				<c:when test='<%= displayStyle.equals("icon") %>'>
					<liferay-ui:search-container-column-text>
						<clay:user-card
							propsTransformer="{OrganizationCardPropsTransformer} from site-memberships-web"
							userCard="<%= new OrganizationsUserCard(organization, renderRequest, renderResponse, searchContainer.getRowChecker()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= displayStyle.equals("descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="organizations"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<div class="h5"><%= organization.getName() %></div>

						<div class="h6 text-default">
							<span><%= HtmlUtil.escape(organization.getParentOrganizationName()) %></span>
						</div>

						<div class="h6 text-default">
							<span><liferay-ui:message key="<%= organization.getType() %>" /></span>
						</div>

						<div class="h6 text-default">
							<span><%= HtmlUtil.escape(organization.getAddress().getCity()) %></span>
							<span><%= UsersAdminUtil.ORGANIZATION_REGION_NAME_ACCESSOR.get(organization) %></span>
							<span><%= UsersAdminUtil.ORGANIZATION_COUNTRY_NAME_ACCESSOR.get(organization) %></span>
						</div>
					</liferay-ui:search-container-column-text>

					<%
					OrganizationActionDropdownItemsProvider organizationActionDropdownItemsProvider = new OrganizationActionDropdownItemsProvider(organization, renderRequest, renderResponse);
					%>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= organizationActionDropdownItemsProvider.getActionDropdownItems() %>"
							propsTransformer="{OrganizationDropdownDefaultPropsTransformer} from site-memberships-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="name"
						orderable="<%= true %>"
						value="<%= organization.getName() %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200"
						name="parent-organization"
						value="<%= HtmlUtil.escape(organization.getParentOrganizationName()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-100"
						name="type"
						orderable="<%= true %>"
						value="<%= LanguageUtil.get(request, organization.getType()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-150"
						name="city"
						value="<%= HtmlUtil.escape(organization.getAddress().getCity()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-150"
						name="region"
						value="<%= UsersAdminUtil.ORGANIZATION_REGION_NAME_ACCESSOR.get(organization) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-150"
						name="country"
						value="<%= UsersAdminUtil.ORGANIZATION_COUNTRY_NAME_ACCESSOR.get(organization) %>"
					/>

					<%
					OrganizationActionDropdownItemsProvider organizationActionDropdownItemsProvider = new OrganizationActionDropdownItemsProvider(organization, renderRequest, renderResponse);
					%>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= organizationActionDropdownItemsProvider.getActionDropdownItems() %>"
							propsTransformer="{OrganizationDropdownDefaultPropsTransformer} from site-memberships-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:otherwise>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= organizationsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="addGroupOrganizations" var="addGroupOrganizationsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= addGroupOrganizationsURL %>" cssClass="hide" name="addGroupOrganizationsFm">
	<aui:input name="tabs1" type="hidden" value="organizations" />
</aui:form>