<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
User selUser = userDisplayContext.getSelectedUser();
List<UserGroup> userGroups = userDisplayContext.getUserGroups();

currentURLObj.setParameter("historyKey", liferayPortletResponse.getNamespace() + "userGroups");
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="user-groups"
/>

<liferay-site:membership-policy-error />

<clay:content-row
	containerElement="div"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="user-groups" /></span>
	</clay:content-col>

	<c:if test="<%= !portletName.equals(myAccountPortletId) %>">
		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "user-groups") %>'
				cssClass="heading-end modify-link"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "openUserGroupsLink" %>'
				label='<%= LanguageUtil.get(request, "select") %>'
				small="<%= true %>"
			/>
		</clay:content-col>
	</c:if>
</clay:content-row>

<liferay-util:buffer
	var="removeButtonUserGroups"
>
	<clay:button
		aria-label="TOKEN_ARIA_LABEL"
		cssClass="lfr-portal-tooltip modify-link"
		data-rowId="TOKEN_DATA_ROW_ID"
		displayType="unstyled"
		icon="times-circle"
		small="<%= true %>"
		title="TOKEN_TITLE"
	/>
</liferay-util:buffer>

<aui:input name="addUserGroupIds" type="hidden" />
<aui:input name="deleteUserGroupIds" type="hidden" />

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-user-groups"
	curParam="userGroupsCur"
	emptyResultsMessage="this-user-does-not-belong-to-a-user-group"
	headerNames="name,null"
	iteratorURL="<%= currentURLObj %>"
	total="<%= userGroups.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= userGroups %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.UserGroup"
		escapedModel="<%= true %>"
		keyPropertyName="userGroupId"
		modelVar="userGroup"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="name"
			property="name"
		/>

		<c:if test="<%= !portletName.equals(myAccountPortletId) && !UserGroupMembershipPolicyUtil.isMembershipRequired((selUser != null) ? selUser.getUserId() : 0, userGroup.getUserGroupId()) %>">
			<liferay-ui:search-container-column-text>
				<clay:button
					aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(userGroup.getName())) %>'
					cssClass="lfr-portal-tooltip modify-link"
					data-rowId="<%= userGroup.getUserGroupId() %>"
					displayType="unstyled"
					icon="times-circle"
					small="<%= true %>"
					title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(userGroup.getName())) %>'
				/>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<c:if test="<%= !portletName.equals(myAccountPortletId) %>">
	<aui:script use="escape,liferay-search-container">
		var Util = Liferay.Util;

		var searchContainer = Liferay.SearchContainer.get(
			'<portlet:namespace />userGroupsSearchContainer'
		);

		var searchContainerContentBox = searchContainer.get('contentBox');

		var addUserGroupIds = [];
		var deleteUserGroupIds = [];

		searchContainerContentBox.delegate(
			'click',
			(event) => {
				var link = event.currentTarget;

				var rowId = link.attr('data-rowId');

				var tr = link.ancestor('tr');

				var selectUserGroup = Util.getWindow(
					'<portlet:namespace />selectUserGroup'
				);

				if (selectUserGroup) {
					var selectButton = selectUserGroup.iframe.node
						.get('contentWindow.document')
						.one('.selector-button[data-usergroupid="' + rowId + '"]');

					Util.toggleDisabled(selectButton, false);
				}

				searchContainer.deleteRow(tr, rowId);

				addUserGroupIds = addUserGroupIds.filter((userGroupId) => {
					return userGroupId !== rowId;
				});

				deleteUserGroupIds.push(rowId);

				document.<portlet:namespace />fm.<portlet:namespace />addUserGroupIds.value =
					addUserGroupIds.join(',');
				document.<portlet:namespace />fm.<portlet:namespace />deleteUserGroupIds.value =
					deleteUserGroupIds.join(',');
			},
			'.modify-link'
		);

		const selectUserGroupButton = document.getElementById(
			'<portlet:namespace />openUserGroupsLink'
		);

		selectUserGroupButton.addEventListener('click', (event) => {
			Liferay.Util.openSelectionModal({
				onSelect: function (selectedItem) {
					const itemValue = JSON.parse(selectedItem.value);

					const label = Liferay.Util.sub(
						'<liferay-ui:message key="remove-x" />',
						itemValue.name
					);
					const rowColumns = [];

					let removeButton =
						'<%= UnicodeFormatter.toString(removeButtonUserGroups) %>';

					removeButton = removeButton
						.replace('TOKEN_ARIA_LABEL', label)
						.replace('TOKEN_DATA_ROW_ID', itemValue.userGroupId)
						.replace('TOKEN_TITLE', label);

					rowColumns.push(itemValue.name);
					rowColumns.push(removeButton);

					searchContainer.addRow(rowColumns, itemValue.userGroupId);

					searchContainer.updateDataStore();

					deleteUserGroupIds = deleteUserGroupIds.filter((userGroupId) => {
						return userGroupId !== itemValue.userGroupId;
					});

					addUserGroupIds.push(itemValue.userGroupId);

					document.<portlet:namespace />fm.<portlet:namespace />addUserGroupIds.value =
						addUserGroupIds.join(',');
					document.<portlet:namespace />fm.<portlet:namespace />deleteUserGroupIds.value =
						deleteUserGroupIds.join(',');
				},
				selectedData: searchContainer.getData(true),
				selectEventName: '<portlet:namespace />selectUserGroup',
				title: '<liferay-ui:message arguments="user-group" key="select-x" />',
				url: '<%= userDisplayContext.getUserGroupItemSelectorURL() %>',
			});
		});
	</aui:script>
</c:if>