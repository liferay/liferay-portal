<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceShipmentDisplayContext commerceShipmentDisplayContext = (CommerceShipmentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceShipment commerceShipment = commerceShipmentDisplayContext.getCommerceShipment();
%>

<portlet:actionURL name="/commerce_shipment/edit_commerce_shipment" var="editCommerceShipmentURL" />

<commerce-ui:modal-content
	contentCssClasses="px-0"
	redirect="<%= redirect %>"
	showSubmitButton="<%= true %>"
	title='<%= LanguageUtil.get(request, "add-shipment-items") %>'
	useNativeSubmit="<%= false %>"
>
	<aui:form action="<%= editCommerceShipmentURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="addShipmentItems" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commerceShipmentId" type="hidden" value="<%= commerceShipment.getCommerceShipmentId() %>" />

		<frontend-data-set:classic-display
			bulkActionDropdownItems="<%= commerceShipmentDisplayContext.getShipmentItemBulkActions() %>"
			contextParams='<%=
				HashMapBuilder.<String, String>put(
					"commerceShipmentId", String.valueOf(commerceShipment.getCommerceShipmentId())
				).build()
			%>'
			dataProviderKey="<%= CommerceShipmentFDSNames.SHIPPABLE_ORDER_ITEMS %>"
			formName="fm"
			id="<%= CommerceShipmentFDSNames.SHIPPABLE_ORDER_ITEMS %>"
			itemsPerPage="<%= 10 %>"
			selectedItemsKey="orderItemId"
			selectionType="multiple"
			showManagementBar="<%= false %>"
		/>
	</aui:form>
</commerce-ui:modal-content>