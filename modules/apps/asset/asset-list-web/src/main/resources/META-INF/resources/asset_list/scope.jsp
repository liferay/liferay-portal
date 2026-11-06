<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<Group> selectedGroups = editAssetListDisplayContext.getSelectedGroups();
%>

<aui:input name="TypeSettingsProperties--groupIds--" type="hidden" value="<%= StringUtil.merge(editAssetListDisplayContext.getSelectedGroupIds()) %>" />

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="none"
	headerNames="name,type,options"
	iteratorURL="<%= editAssetListDisplayContext.getPortletURL() %>"
	total="<%= selectedGroups.size() %>"
>
	<liferay-ui:search-container-results
		results="<%= selectedGroups %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Group"
		keyPropertyName="groupId"
		modelVar="group"
	>
		<liferay-ui:search-container-column-text
			name="name"
			truncate="<%= true %>"
			value="<%= group.getScopeDescriptiveName(themeDisplay) %>"
		/>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
		/>

		<c:if test="<%= !editAssetListDisplayContext.isLiveGroup() %>">
			<liferay-ui:search-container-column-text
				name="options"
			>
				<clay:button
					aria-label='<%= LanguageUtil.get(request, "remove") %>'
					cssClass="modify-link"
					data-rowId="<%= group.getGroupId() %>"
					displayType="unstyled"
					icon="times-circle"
					monospaced="<%= true %>"
					small="<%= true %>"
				/>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= false %>"
	/>
</liferay-ui:search-container>

<c:if test="<%= !editAssetListDisplayContext.isLiveGroup() %>">

	<%
	ScopeActionDropdownItemsProvider scopeActionDropdownItemsProvider = new ScopeActionDropdownItemsProvider(editAssetListDisplayContext, liferayPortletRequest);
	%>

	<clay:dropdown-menu
		aria-label='<%= LanguageUtil.get(request, "select-site") %>'
		cssClass="btn btn-secondary"
		dropdownItems="<%= scopeActionDropdownItemsProvider.getActionDropdownItems() %>"
		label='<%= LanguageUtil.get(request, "select") %>'
		propsTransformer="{ScopeDefaultPropsTransformer} from asset-list-web"
	/>
</c:if>

<aui:script use="liferay-search-container">
	const searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />groupsSearchContainer'
	);

	searchContainer.get('contentBox').delegate(
		'click',
		(event) => {
			const link = event.currentTarget;

			const tr = link.ancestor('tr');

			searchContainer.deleteRow(tr, link.getAttribute('data-rowId'));

			searchContainer.updateDataStore();

			const groupIds = document.getElementById(
				'<portlet:namespace />groupIds'
			);

			if (groupIds) {
				const searchContainerData = searchContainer.getData();

				groupIds.setAttribute('value', searchContainerData.split(','));

				submitForm(document.<portlet:namespace />fm);
			}
		},
		'.modify-link'
	);
</aui:script>