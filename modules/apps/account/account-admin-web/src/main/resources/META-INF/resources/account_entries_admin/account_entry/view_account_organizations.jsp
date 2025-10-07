<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

SearchContainer<Organization> accountOrganizationSearchContainer = AccountOrganizationSearchContainerFactory.create(accountEntryDisplay.getAccountEntryId(), liferayPortletRequest, liferayPortletResponse);

ViewAccountOrganizationsManagementToolbarDisplayContext viewAccountOrganizationsManagementToolbarDisplayContext = new ViewAccountOrganizationsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountOrganizationSearchContainer);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));

renderResponse.setTitle(accountEntryDisplay.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewAccountOrganizationsManagementToolbarDisplayContext %>"
	propsTransformer="{AccountOrganizationsManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="accountEntryId" type="hidden" value="<%= accountEntryDisplay.getAccountEntryId() %>" />
		<aui:input name="accountOrganizationIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= accountOrganizationSearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.Organization"
				keyProperty="organizationId"
				modelVar="accountOrganization"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="name"
					value="<%= HtmlUtil.escape(accountOrganization.getName()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="parent-organization"
					value="<%= HtmlUtil.escape(accountOrganization.getParentOrganizationName()) %>"
				/>

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small table-cell-minw-150"
						name="status"
					>
						<clay:label
							displayType="<%= WorkflowConstants.getStatusStyle(accountOrganization.getStatus()) %>"
							label="<%= WorkflowConstants.getStatusLabel(accountOrganization.getStatus()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:if>

				<c:if test="<%= AccountEntryPermission.hasEditOrManageOrganizationsPermission(permissionChecker, accountEntryDisplay.getAccountEntryId()) %>">
					<liferay-ui:search-container-column-text>
						<portlet:actionURL name="/account_admin/remove_account_organizations" var="removeAccountOrganizationsURL">
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="accountEntryId" value="<%= String.valueOf(accountEntryDisplay.getAccountEntryId()) %>" />
							<portlet:param name="accountOrganizationIds" value="<%= String.valueOf(accountOrganization.getOrganizationId()) %>" />
						</portlet:actionURL>

						<liferay-ui:icon-delete
							confirmation="are-you-sure-you-want-to-remove-this-organization"
							icon="times-circle"
							message="remove"
							showIcon="<%= true %>"
							url="<%= removeAccountOrganizationsURL %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:if>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>