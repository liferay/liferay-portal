<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
DepotAdminMembershipsDisplayContext depotAdminMembershipsDisplayContext = (DepotAdminMembershipsDisplayContext)request.getAttribute(DepotAdminMembershipsDisplayContext.class.getName());

currentURLObj.setParameter("historyKey", liferayPortletResponse.getNamespace() + "asset-libraries");
%>

<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="/depot/update_memberships" />

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="asset-libraries"
/>

<liferay-site:membership-policy-error />

<clay:content-row
	containerElement="div"
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><%= depotAdminMembershipsDisplayContext.getLabel() %></span>
	</clay:content-col>

	<c:if test="<%= depotAdminMembershipsDisplayContext.isSelectable() %>">
		<clay:content-col>
			<clay:button
				aria-label='<%= LanguageUtil.format(request, "select-x", "asset-libraries") %>'
				cssClass="heading-end modify-link"
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectDepotGroupLink" %>'
				label='<%= LanguageUtil.get(request, "select") %>'
				small="<%= true %>"
			/>
		</clay:content-col>
	</c:if>
</clay:content-row>

<liferay-util:buffer
	var="removeButtonAssetLibraries"
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

<aui:input name="addDepotGroupIds" type="hidden" />
<aui:input name="deleteDepotGroupIds" type="hidden" />

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-repositories"
	curParam="depotsCur"
	emptyResultsMessage="this-user-does-not-belong-to-an-asset-library"
	headerNames="name,roles,null"
	id="depotGroupsSearchContainer"
	iteratorURL="<%= currentURLObj %>"
	total="<%= depotAdminMembershipsDisplayContext.getDepotGroupsCount() %>"
>
	<liferay-ui:search-container-results
		results="<%= depotAdminMembershipsDisplayContext.getDepotGroups(searchContainer.getStart(), searchContainer.getEnd()) %>"
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
			value="<%= HtmlUtil.escape(group.getName(locale)) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="roles"
			value="<%= HtmlUtil.escape(depotAdminMembershipsDisplayContext.getRoles(group)) %>"
		/>

		<c:if test="<%= depotAdminMembershipsDisplayContext.isDeletable() %>">
			<liferay-ui:search-container-column-text>
				<clay:button
					aria-label='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(group.getDescriptiveName(locale))) %>'
					cssClass="lfr-portal-tooltip modify-link"
					data-rowId="<%= group.getGroupId() %>"
					displayType="unstyled"
					icon="times-circle"
					small="<%= true %>"
					title='<%= LanguageUtil.format(request, "remove-x", HtmlUtil.escape(group.getDescriptiveName(locale))) %>'
				/>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>

<c:if test="<%= depotAdminMembershipsDisplayContext.isSelectable() %>">
	<aui:script use="liferay-search-container">
		var AArray = A.Array;

		var addDepotGroupIds = [];
		var deleteDepotGroupIds = [];

		var searchContainer = Liferay.SearchContainer.get(
			'<portlet:namespace />depotGroupsSearchContainer'
		);

		var searchContainerContentBox = searchContainer.get('contentBox');

		var selectDepotGroupLink = document.getElementById(
			'<portlet:namespace />selectDepotGroupLink'
		);

		selectDepotGroupLink.addEventListener('click', (event) => {
			Liferay.Util.openSelectionModal({
				onSelect: function (selectedItem) {
					if (selectedItem) {
						const itemValue = JSON.parse(selectedItem.value);
						const label = Liferay.Util.sub(
							'<liferay-ui:message key="remove-x" />',
							Liferay.Util.escapeHTML(itemValue.title)
						);
						const rowColumns = [];

						let removeButton =
							'<%= UnicodeFormatter.toString(removeButtonAssetLibraries) %>';

						removeButton = removeButton
							.replace('TOKEN_ARIA_LABEL', label)
							.replace('TOKEN_DATA_ROW_ID', itemValue.classPK)
							.replace('TOKEN_TITLE', label);

						rowColumns.push(Liferay.Util.escapeHTML(itemValue.title));
						rowColumns.push('');
						rowColumns.push(removeButton);

						const searchContainerData = searchContainer.getData();

						const itemsValues = searchContainerData.split(',');

						if (!itemsValues.includes(itemValue.classPK)) {
							searchContainer.addRow(rowColumns, itemValue.classPK);

							searchContainer.updateDataStore();
						}

						addDepotGroupIds.push(itemValue.classPK);

						AArray.removeItem(deleteDepotGroupIds, itemValue.classPK);

						document.<portlet:namespace />fm.<portlet:namespace />addDepotGroupIds.value =
							addDepotGroupIds.join(',');
						document.<portlet:namespace />fm.<portlet:namespace />deleteDepotGroupIds.value =
							deleteDepotGroupIds.join(',');
					}
				},
				selectEventName:
					'<%= depotAdminMembershipsDisplayContext.getItemSelectorEventName() %>',
				title: '<liferay-ui:message arguments="asset-library" key="select-x" />',
				url: '<%= depotAdminMembershipsDisplayContext.getItemSelectorURL() %>',
			});
		});

		var handleOnModifyLink = searchContainerContentBox.delegate(
			'click',
			(event) => {
				var link = event.currentTarget;

				var rowId = link.attr('data-rowId');
				var tr = link.ancestor('tr');

				var selectGroup = Liferay.Util.getWindow(
					'<portlet:namespace />selectGroup'
				);

				if (selectGroup) {
					var selectButton = selectGroup.iframe.node
						.get('contentWindow.document')
						.one('.selector-button[data-entityid="' + rowId + '"]');

					Liferay.Util.toggleDisabled(selectButton, false);
				}

				searchContainer.deleteRow(tr, rowId);

				AArray.removeItem(addDepotGroupIds, event.rowId);

				deleteDepotGroupIds.push(rowId);

				document.<portlet:namespace />fm.<portlet:namespace />addDepotGroupIds.value =
					addDepotGroupIds.join(',');
				document.<portlet:namespace />fm.<portlet:namespace />deleteDepotGroupIds.value =
					deleteDepotGroupIds.join(',');
			},
			'.modify-link'
		);

		var onDestroyPortlet = function (event) {
			if (event.portletId === '<%= portletDisplay.getId() %>') {
				Liferay.detach(handleOnModifyLink);

				Liferay.detach('destroyPortlet', onDestroyPortlet);
			}
		};

		Liferay.on('destroyPortlet', onDestroyPortlet);
	</aui:script>
</c:if>

<c:if test="<%= !depotAdminMembershipsDisplayContext.isInheritedDepotGroupsEmpty() %>">
	<div class="sheet-tertiary-title"><liferay-ui:message key="inherited-asset-libraries" /></div>

	<liferay-ui:search-container
		cssClass="lfr-search-container-inherited-depot"
		curParam="inheritedDepotCur"
		headerNames="name"
		iteratorURL="<%= currentURLObj %>"
		total="<%= depotAdminMembershipsDisplayContext.getInheritedDepotGroupsCount() %>"
	>
		<liferay-ui:search-container-results
			calculateStartAndEnd="<%= true %>"
			results="<%= depotAdminMembershipsDisplayContext.getInheritedDepotGroups() %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Group"
			escapedModel="<%= true %>"
			keyProperty="groupId"
			modelVar="inheritedDepot"
			rowIdProperty="friendlyURL"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="<%= HtmlUtil.escape(inheritedDepot.getName(locale)) %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</c:if>