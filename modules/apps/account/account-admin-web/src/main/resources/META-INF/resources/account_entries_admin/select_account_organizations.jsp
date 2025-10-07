<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long accountEntryId = ParamUtil.getLong(request, "accountEntryId");

SearchContainer<Organization> organizationSearchContainer = AssignableAccountOrganizationSearchContainerFactory.create(accountEntryId, liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SelectAccountOrganizationsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, organizationSearchContainer) %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		searchContainer="<%= organizationSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Organization"
			keyProperty="organizationId"
			modelVar="organization"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="name"
				value="<%= HtmlUtil.escape(organization.getName()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="parent-organization"
				value="<%= HtmlUtil.escape(organization.getParentOrganizationName()) %>"
			/>

			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-ws-nowrap table-column-text-center"
					name="status"
				>
					<clay:label
						displayType="<%= WorkflowConstants.getStatusStyle(organization.getStatus()) %>"
						label="<%= WorkflowConstants.getStatusLabel(organization.getStatus()) %>"
					/>
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>