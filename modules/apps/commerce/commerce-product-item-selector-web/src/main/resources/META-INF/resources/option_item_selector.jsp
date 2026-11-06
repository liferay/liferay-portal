<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPOptionItemSelectorViewDisplayContext cpOptionItemSelectorViewDisplayContext = (CPOptionItemSelectorViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<CPOption> cpOptionSearchContainer = cpOptionItemSelectorViewDisplayContext.getSearchContainer();

String displayStyle = cpOptionItemSelectorViewDisplayContext.getDisplayStyle();

String itemSelectedEventName = cpOptionItemSelectorViewDisplayContext.getItemSelectedEventName();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CPSpecificationOptionItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, cpOptionSearchContainer) %>"
/>

<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />cpOptionSelectorWrapper">
	<liferay-ui:search-container
		id="cpOptions"
		searchContainer="<%= cpOptionSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.commerce.product.model.CPOption"
			cssClass="commerce-product-option-row"
			keyPropertyName="CPOptionId"
			modelVar="cpOption"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
			>
				<div class="commerce-product-option-title" data-id="<%= cpOption.getCPOptionId() %>">
					<%= HtmlUtil.escape(cpOption.getName(themeDisplay.getLanguageId())) %>
				</div>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="modified-date"
				property="modifiedDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>

<aui:script use="liferay-search-container">
	var cpOptionSelectorWrapper = A.one(
		'#<portlet:namespace />cpOptionSelectorWrapper'
	);

	var searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />cpOptions'
	);

	searchContainer.on('rowToggled', (event) => {
		Liferay.Util.getOpener().Liferay.fire(
			'<%= HtmlUtil.escapeJS(itemSelectedEventName) %>',
			{
				data: Liferay.Util.getCheckedCheckboxes(
					cpOptionSelectorWrapper,
					'<portlet:namespace />allRowIds'
				),
			}
		);
	});
</aui:script>