<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommercePriceListItemSelectorViewDisplayContext commercePriceListItemSelectorViewDisplayContext = (CommercePriceListItemSelectorViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

String itemSelectedEventName = commercePriceListItemSelectorViewDisplayContext.getItemSelectedEventName();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CommercePriceListItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, commercePriceListItemSelectorViewDisplayContext.getSearchContainer()) %>"
/>

<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />commercePriceListSelectorWrapper">
	<liferay-ui:search-container
		id="commercePriceLists"
		searchContainer="<%= commercePriceListItemSelectorViewDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.commerce.price.list.model.CommercePriceList"
			cssClass="commerce-price-list-row"
			keyPropertyName="commercePriceListId"
			modelVar="commercePriceList"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				property="name"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="author"
				property="userName"
			/>

			<liferay-ui:search-container-column-status
				cssClass="table-cell-expand"
				name="status"
				status="<%= commercePriceList.getStatus() %>"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="create-date"
				property="createDate"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="display-date"
				property="displayDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
			searchContainer="<%= commercePriceListItemSelectorViewDisplayContext.getSearchContainer() %>"
		/>
	</liferay-ui:search-container>
</div>

<aui:script use="liferay-search-container">
	var commercePriceListSelectorWrapper = A.one(
		'#<portlet:namespace />commercePriceListSelectorWrapper'
	);

	var searchContainer = Liferay.SearchContainer.get(
		'<portlet:namespace />commercePriceLists'
	);

	searchContainer.on('rowToggled', (event) => {
		Liferay.Util.getOpener().Liferay.fire(
			'<%= HtmlUtil.escapeJS(itemSelectedEventName) %>',
			{
				data: Liferay.Util.getCheckedCheckboxes(
					commercePriceListSelectorWrapper,
					'<portlet:namespace />allRowIds'
				),
			}
		);
	});
</aui:script>