<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
User selUser = userDisplayContext.getSelectedUser();

List<Organization> organizations = userDisplayContext.getOrganizations();

String organizationIdsString = ParamUtil.getString(request, "organizationsSearchContainerPrimaryKeys");

currentURLObj.setParameter("historyKey", liferayPortletResponse.getNamespace() + "organizations");
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="organizations"
/>

<liferay-site:membership-policy-error />

<clay:content-row
	containerElement="div"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="organizations" /></span>
	</clay:content-col>

	<c:if test="<%= !portletName.equals(myAccountPortletId) %>">
		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "organizations") %>'
				cssClass="heading-end modify-link"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectOrganizationLink" %>'
				label='<%= LanguageUtil.get(request, "select") %>'
				small="<%= true %>"
			/>
		</clay:content-col>
	</c:if>
</clay:content-row>

<aui:input name="addOrganizationIds" type="hidden" value="<%= organizationIdsString %>" />
<aui:input name="deleteOrganizationIds" type="hidden" />

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-organizations"
	curParam="organizationsCur"
	emptyResultsMessage="this-user-does-not-belong-to-an-organization"
	headerNames="name,type,roles,null"
	iteratorURL="<%= currentURLObj %>"
	total="<%= organizations.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= organizations %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Organization"
		escapedModel="<%= true %>"
		keyProperty="organizationId"
		modelVar="organization"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="name"
			property="name"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, organization.getType()) %>"
		/>

		<%
		List<UserGroupRole> userGroupRoles = new ArrayList<UserGroupRole>();
		int userGroupRolesCount = 0;

		if (selUser != null) {
			userGroupRoles = UserGroupRoleLocalServiceUtil.getUserGroupRoles(selUser.getUserId(), organization.getGroupId(), 0, PropsValues.USERS_ADMIN_ROLE_COLUMN_LIMIT);
			userGroupRolesCount = UserGroupRoleLocalServiceUtil.getUserGroupRolesCount(selUser.getUserId(), organization.getGroupId());
		}
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="roles"
			value="<%= HtmlUtil.escape(UsersAdminUtil.getUserColumnText(locale, userGroupRoles, UsersAdminUtil.USER_GROUP_ROLE_TITLE_ACCESSOR, userGroupRolesCount)) %>"
		/>

		<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="status"
			>
				<clay:label
					displayType="<%= WorkflowConstants.getStatusStyle(organization.getStatus()) %>"
					label="<%= WorkflowConstants.getStatusLabel(organization.getStatus()) %>"
				/>
			</liferay-ui:search-container-column-text>
		</c:if>

		<c:if test="<%= !portletName.equals(myAccountPortletId) && ((selUser == null) || !OrganizationMembershipPolicyUtil.isMembershipProtected(permissionChecker, selUser.getUserId(), organization.getOrganizationId())) %>">
			<liferay-ui:search-container-column-text>
				<clay:button
					aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(organization.getName())) %>'
					cssClass="lfr-portal-tooltip modify-link"
					data-rowId="<%= organization.getOrganizationId() %>"
					displayType="unstyled"
					icon="times-circle"
					small="<%= true %>"
					title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(organization.getName())) %>'
				/>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<c:if test="<%= !portletName.equals(myAccountPortletId) %>">
	<aui:script use="liferay-search-container">
		var searchContainer = Liferay.SearchContainer.get(
			'<portlet:namespace />organizationsSearchContainer'
		);

		searchContainer.get('contentBox').delegate(
			'click',
			(event) => {
				var link = event.currentTarget;

				document.<portlet:namespace />fm.<portlet:namespace />deleteOrganizationIds.value =
					link.attr('data-rowId');

				submitForm(document.<portlet:namespace />fm);
			},
			'.modify-link'
		);

		const selectOrganizationLink = document.getElementById(
			'<portlet:namespace />selectOrganizationLink'
		);

		if (selectOrganizationLink) {
			selectOrganizationLink.addEventListener('click', (event) => {
				Liferay.Util.openSelectionModal({
					multiple: true,
					onSelect(data) {
						if (data.value && data.value.length) {
							document.<portlet:namespace />fm.<portlet:namespace />addOrganizationIds.value =
								Array.from(data.value)
									.map((selectedItem) => {
										const organization = JSON.parse(selectedItem);

										return organization.organizationId;
									})
									.join(',');

							submitForm(document.<portlet:namespace />fm);
						}
					},
					selectEventName: '<portlet:namespace />selectOrganization',
					title: '<liferay-ui:message arguments="organization" key="select-x" />',
					url: '<%= userDisplayContext.getOrganizationItemSelectorURL(true) %>',
				});
			});
		}
	</aui:script>
</c:if>