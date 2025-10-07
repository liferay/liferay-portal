<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
User selUser = userDisplayContext.getSelectedUser();
List<Group> siteGroups = userDisplayContext.getSiteGroups();
List<Group> inheritedSiteGroups = userDisplayContext.getInheritedSiteGroups();

currentURLObj.setParameter("historyKey", liferayPortletResponse.getNamespace() + "sites");
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="sites"
/>

<liferay-site:membership-policy-error />

<clay:content-row
	containerElement="div"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="sites" /></span>
	</clay:content-col>

	<c:if test="<%= !portletName.equals(myAccountPortletId) %>">
		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "sites") %>'
				cssClass="heading-end modify-link"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectSiteLink" %>'
				label='<%= LanguageUtil.get(request, "select") %>'
				small="<%= true %>"
			/>
		</clay:content-col>
	</c:if>
</clay:content-row>

<liferay-util:buffer
	var="removeButtonSites"
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

<aui:input name="addGroupIds" type="hidden" />
<aui:input name="deleteGroupIds" type="hidden" />

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-sites"
	curParam="sitesCur"
	emptyResultsMessage="this-user-does-not-belong-to-a-site"
	headerNames="name,roles,null"
	iteratorURL="<%= currentURLObj %>"
	total="<%= siteGroups.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= siteGroups %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Group"
		escapedModel="<%= true %>"
		keyProperty="groupId"
		modelVar="group"
		rowIdProperty="friendlyURL"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="name"
		>
			<liferay-staging:descriptive-name
				group="<%= group %>"
			/>
		</liferay-ui:search-container-column-text>

		<%
		List<UserGroupRole> userGroupRoles = new ArrayList<UserGroupRole>();
		int userGroupRolesCount = 0;

		if (selUser != null) {
			userGroupRoles = UserGroupRoleLocalServiceUtil.getUserGroupRoles(selUser.getUserId(), group.getGroupId(), 0, PropsValues.USERS_ADMIN_ROLE_COLUMN_LIMIT);
			userGroupRolesCount = UserGroupRoleLocalServiceUtil.getUserGroupRolesCount(selUser.getUserId(), group.getGroupId());
		}
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="roles"
			value="<%= HtmlUtil.escape(UsersAdminUtil.getUserColumnText(locale, userGroupRoles, UsersAdminUtil.USER_GROUP_ROLE_TITLE_ACCESSOR, userGroupRolesCount)) %>"
		/>

		<c:if test="<%= !portletName.equals(myAccountPortletId) && (selUser != null) && !SiteMembershipPolicyUtil.isMembershipRequired(selUser.getUserId(), group.getGroupId()) && !SiteMembershipPolicyUtil.isMembershipProtected(permissionChecker, selUser.getUserId(), group.getGroupId()) %>">
			<liferay-ui:search-container-column-text>
				<c:if test="<%= group.isManualMembership() %>">
					<clay:button
						aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(group.getDescriptiveName(locale))) %>'
						cssClass="lfr-portal-tooltip modify-link"
						data-rowId="<%= group.getGroupId() %>"
						displayType="unstyled"
						icon="times-circle"
						small="<%= true %>"
						title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(group.getDescriptiveName(locale))) %>'
					/>
				</c:if
			>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<c:if test="<%= !portletName.equals(myAccountPortletId) %>">
	<aui:script use="liferay-search-container">
		var Util = Liferay.Util;

		var addGroupIds = [];
		var deleteGroupIds = [];

		var searchContainer = Liferay.SearchContainer.get(
			'<portlet:namespace />groupsSearchContainer'
		);

		var searchContainerContentBox = searchContainer.get('contentBox');

		const selectSiteButton = document.getElementById(
			'<portlet:namespace />selectSiteLink'
		);

		const handleOnSelect = selectSiteButton.addEventListener('click', (event) => {
			var searchContainerData = searchContainer.getData();

			if (!searchContainerData.length) {
				searchContainerData = [];
			}
			else {
				searchContainerData = searchContainerData.split(',');
			}

			Util.openSelectionModal({
				onSelect: (selectedItem) => {
					if (selectedItem) {
						const entityId = selectedItem.groupid;
						const entityName = A.Escape.html(
							selectedItem.groupdescriptivename
						);
						const label = Liferay.Util.sub(
							'<liferay-ui:message key="remove-x" />',
							entityName
						);
						const rowColumns = [];

						let removeButton =
							'<%= UnicodeFormatter.toString(removeButtonSites) %>';

						removeButton = removeButton
							.replace('TOKEN_ARIA_LABEL', label)
							.replace('TOKEN_DATA_ROW_ID', entityId)
							.replace('TOKEN_TITLE', label);

						rowColumns.push(entityName);
						rowColumns.push('');
						rowColumns.push(removeButton);

						searchContainer.addRow(rowColumns, entityId);

						searchContainer.updateDataStore();

						addGroupIds.push(entityId);

						deleteGroupIds = deleteGroupIds.filter((deleteGroupId) => {
							return deleteGroupId !== entityId;
						});

						document.<portlet:namespace />fm.<portlet:namespace />addGroupIds.value =
							addGroupIds.join(',');
						document.<portlet:namespace />fm.<portlet:namespace />deleteGroupIds.value =
							deleteGroupIds.join(',');
					}
				},
				selectEventName:
					'<%= liferayPortletResponse.getNamespace() + "selectGroup" %>',
				selectedData: [searchContainerData],
				title: '<liferay-ui:message arguments="site" key="select-x" />',
				url: '<%= userDisplayContext.getGroupItemSelectorURL() %>',
			});
		});

		var handleOnModifyLink = searchContainerContentBox.delegate(
			'click',
			(event) => {
				var link = event.currentTarget;

				var rowId = link.attr('data-rowId');
				var tr = link.ancestor('tr');

				var selectGroup = Util.getWindow('<portlet:namespace />selectGroup');

				if (selectGroup) {
					var selectButton = selectGroup.iframe.node
						.get('contentWindow.document')
						.one('.selector-button[data-entityid="' + rowId + '"]');

					Util.toggleDisabled(selectButton, false);
				}

				searchContainer.deleteRow(tr, rowId);

				addGroupIds = addGroupIds.filter((addGroupId) => {
					return addGroupId !== event.rowId;
				});

				deleteGroupIds.push(rowId);

				document.<portlet:namespace />fm.<portlet:namespace />addGroupIds.value =
					addGroupIds.join(',');
				document.<portlet:namespace />fm.<portlet:namespace />deleteGroupIds.value =
					deleteGroupIds.join(',');
			},
			'.modify-link'
		);

		var handleEnableRemoveSite = Liferay.on(
			'<portlet:namespace />enableRemovedSites',
			(event) => {
				event.selectors.each((item, index, collection) => {
					var groupId = item.attr('data-entityid');

					if (deleteGroupIds.indexOf(groupId) != -1) {
						Util.toggleDisabled(item, false);
					}
				});
			}
		);

		var onDestroyPortlet = function (event) {
			if (event.portletId === '<%= portletDisplay.getId() %>') {
				removeEventListener('click', handleOnSelect);
				Liferay.detach(handleOnModifyLink);
				Liferay.detach(handleEnableRemoveSite);

				Liferay.detach('destroyPortlet', onDestroyPortlet);
			}
		};

		Liferay.on('destroyPortlet', onDestroyPortlet);
	</aui:script>
</c:if>

<c:if test="<%= !inheritedSiteGroups.isEmpty() %>">
	<div class="sheet-tertiary-title"><liferay-ui:message key="inherited-sites" /></div>

	<liferay-ui:search-container
		cssClass="lfr-search-container-inherited-sites"
		curParam="inheritedSitesCur"
		headerNames="name,roles"
		iteratorURL="<%= currentURLObj %>"
		total="<%= inheritedSiteGroups.size() %>"
	>
		<liferay-ui:search-container-results
			calculateStartAndEnd="<%= true %>"
			results="<%= inheritedSiteGroups %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Group"
			escapedModel="<%= true %>"
			keyProperty="groupId"
			modelVar="inheritedSite"
			rowIdProperty="friendlyURL"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="<%= HtmlUtil.escape(inheritedSite.getDescriptiveName(locale)) %>"
			/>

			<%
			List<UserGroupRole> inheritedRoles = new ArrayList<UserGroupRole>();
			int inheritedRolesCount = 0;

			if (selUser != null) {
				inheritedRoles = UserGroupRoleLocalServiceUtil.getUserGroupRoles(selUser.getUserId(), inheritedSite.getGroupId(), 0, PropsValues.USERS_ADMIN_ROLE_COLUMN_LIMIT);
				inheritedRolesCount = UserGroupRoleLocalServiceUtil.getUserGroupRolesCount(selUser.getUserId(), inheritedSite.getGroupId());
			}
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="roles"
				value="<%= HtmlUtil.escape(UsersAdminUtil.getUserColumnText(locale, inheritedRoles, UsersAdminUtil.USER_GROUP_ROLE_TITLE_ACCESSOR, inheritedRolesCount)) %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</c:if>