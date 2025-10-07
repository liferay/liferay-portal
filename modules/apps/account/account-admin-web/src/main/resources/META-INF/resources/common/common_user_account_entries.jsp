<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
boolean singleSelect = ParamUtil.getBoolean(request, "singleSelect", true);
%>

<liferay-util:buffer
	var="removeButton"
>
	<clay:button
		aria-label="TOKEN_ARIA_LABEL"
		cssClass="lfr-portal-tooltip remove-link"
		data-entityId="TOKEN_ENTITY_ID"
		displayType="unstyled"
		icon="times-circle"
		small="<%= true %>"
		title="TOKEN_TITLE"
	/>
</liferay-util:buffer>

<clay:content-row
	containerElement="div"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		containerElement="span"
		expand="<%= true %>"
	>
		<span class="heading-text">
			<liferay-ui:message key="accounts" />
		</span>
	</clay:content-col>

	<c:if test="<%= !portletName.equals(UsersAdminPortletKeys.MY_ACCOUNT) %>">
		<clay:content-col
			containerElement="span"
		>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "accounts") %>'
				cssClass="heading-end"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectAccountLink" %>'
				label='<%= LanguageUtil.get(request, "select") %>'
				small="<%= true %>"
			/>
		</clay:content-col>
	</c:if>
</clay:content-row>

<clay:sheet-section>
	<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="/account_admin/edit_account_user_account_entries" />
	<aui:input name="addAccountEntryIds" type="hidden" />
	<aui:input name="deleteAccountEntryIds" type="hidden" />

	<liferay-util:buffer
		var="removeAccountEntryIcon"
	>
		<liferay-ui:icon
			icon="times-circle"
			markupView="lexicon"
			message="remove"
		/>
	</liferay-util:buffer>

	<%
	User selUser = PortalUtil.getSelectedUser(request, false);

	SearchContainer<AccountEntryDisplay> accountEntryDisplaySearchContainer = AccountEntryDisplaySearchContainerFactory.createWithUserId(selUser.getUserId(), liferayPortletRequest, liferayPortletResponse);

	accountEntryDisplaySearchContainer.setRowChecker(null);
	%>

	<liferay-ui:search-container
		compactEmptyResultsMessage="<%= true %>"
		emptyResultsMessage="this-user-does-not-belong-to-any-accounts"
		headerNames="name,roles,null"
		searchContainer="<%= accountEntryDisplaySearchContainer %>"
	>

		<%
		AccountUserDisplay accountUserDisplay = AccountUserDisplay.of(selUser);
		%>

		<liferay-ui:search-container-row
			className="com.liferay.account.admin.web.internal.display.AccountEntryDisplay"
			keyProperty="accountEntryId"
			modelVar="accountEntryDisplay"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="<%= HtmlUtil.escape(accountEntryDisplay.getName()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="roles"
				value="<%= accountUserDisplay.getAccountRoleNamesString(accountEntryDisplay.getAccountEntryId(), locale) %>"
			/>

			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest"
					name="status"
				>
					<clay:label
						displayType="<%= accountEntryDisplay.getStatusLabelStyle() %>"
						label="<%= accountEntryDisplay.getStatusLabel() %>"
					/>
				</liferay-ui:search-container-column-text>
			</c:if>

			<c:if test="<%= !portletName.equals(UsersAdminPortletKeys.MY_ACCOUNT) && AccountEntryPermission.contains(permissionChecker, accountEntryDisplay.getAccountEntryId(), ActionKeys.MANAGE_USERS) %>">
				<liferay-ui:search-container-column-text>
					<clay:button
						aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(accountEntryDisplay.getName())) %>'
						cssClass="lfr-portal-tooltip remove-link"
						data-entityId="<%= accountEntryDisplay.getAccountEntryId() %>"
						displayType="unstyled"
						icon="times-circle"
						small="<%= true %>"
						title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(accountEntryDisplay.getName())) %>'
					/>
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>

	<liferay-portlet:renderURL portletName="<%= AccountPortletKeys.ACCOUNT_USERS_ADMIN %>" var="selectAccountEntryURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
		<portlet:param name="mvcPath" value="/account_users_admin/select_account_entry.jsp" />
		<portlet:param name="singleSelect" value="<%= String.valueOf(singleSelect) %>" />
		<portlet:param name="userId" value="<%= String.valueOf(selUser.getUserId()) %>" />
	</liferay-portlet:renderURL>

	<c:if test="<%= !portletName.equals(UsersAdminPortletKeys.MY_ACCOUNT) %>">
		<aui:script use="liferay-search-container">
			const deleteAccountEntryIdsSet = new Set();
			const searchContainer = Liferay.SearchContainer.get(
				'<portlet:namespace />accountEntries'
			);

			function updateData() {
				document.<portlet:namespace />fm.<portlet:namespace />addAccountEntryIds.value =
					searchContainer.getData();
				document.<portlet:namespace />fm.<portlet:namespace />deleteAccountEntryIds.value =
					Array.from(deleteAccountEntryIdsSet).join(',');
			}

			const searchContainerContentBox = searchContainer.get('contentBox');

			searchContainerContentBox.delegate(
				'click',
				(event) => {
					const link = event.currentTarget.getDOMNode();

					const entityId = link.dataset.entityid;

					const tr = link.closest('tr');

					searchContainer.deleteRow(tr, entityId);

					deleteAccountEntryIdsSet.add(entityId);

					updateData();
				},
				'.remove-link'
			);

			const selectAccountLink = document.getElementById(
				'<portlet:namespace />selectAccountLink'
			);

			if (selectAccountLink) {
				selectAccountLink.addEventListener('click', (event) => {
					Liferay.Util.openSelectionModal({
						id: '<portlet:namespace />selectAccountEntry',
						multiple: !<%= singleSelect %>,
						onSelect: function (selectedItems) {
							if (!Array.isArray(selectedItems)) {
								selectedItems = [selectedItems];
							}

							for (const selectedItem of selectedItems) {
								const entityId = selectedItem.entityid;
								const entityName = A.Escape.html(selectedItem.entityname);
								const label = Liferay.Util.sub(
									'<liferay-ui:message key="remove-x" />',
									entityName
								);

								let removeButton =
									'<%= UnicodeFormatter.toString(removeButton) %>';

								removeButton = removeButton
									.replace('TOKEN_ARIA_LABEL', label)
									.replace('TOKEN_ENTITY_ID', entityId)
									.replace('TOKEN_TITLE', label);

								searchContainer.addRow(
									[selectedItem.entityname, '', removeButton],
									entityId
								);

								deleteAccountEntryIdsSet.delete(entityId);
							}

							updateData();
						},
						selectEventName: '<portlet:namespace />selectAccountEntry',
						selectedData: searchContainer.getData(true),
						selectedDataCheckboxesDisabled: true,
						title: '<liferay-ui:message arguments="account" key="select-x" />',
						url: '<%= selectAccountEntryURL %>',
					});
				});
			}
		</aui:script>
	</c:if>
</clay:sheet-section>