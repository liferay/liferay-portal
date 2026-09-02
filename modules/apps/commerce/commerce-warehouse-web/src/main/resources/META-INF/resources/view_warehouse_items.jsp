<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceInventoryWarehouseItemsDisplayContext commerceInventoryWarehouseItemsDisplayContext = (CommerceInventoryWarehouseItemsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

String backURL = commerceInventoryWarehouseItemsDisplayContext.getBackURL();

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}
%>

<c:if test="<%= commerceInventoryWarehouseItemsDisplayContext.hasManageCommerceInventoryWarehousePermission() %>">

	<%
	List<CommerceInventoryWarehouse> commerceInventoryWarehouses = commerceInventoryWarehouseItemsDisplayContext.getCommerceInventoryWarehouses();
	CPInstance cpInstance = commerceInventoryWarehouseItemsDisplayContext.getCPInstance();
	%>

	<c:choose>
		<c:when test="<%= commerceInventoryWarehouses.isEmpty() %>">
			<div class="alert alert-info">
				<liferay-ui:message key="there-are-no-active-warehouses" />
			</div>
		</c:when>
		<c:otherwise>
			<portlet:actionURL name="/cp_definitions/edit_commerce_inventory_warehouse_item" var="updateCommerceInventoryWarehouseItemURL" />

			<aui:form action="<%= updateCommerceInventoryWarehouseItemURL %>" method="post" name="fm">
				<aui:input name="<%= Constants.CMD %>" type="hidden" />
				<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
				<aui:input name="commerceInventoryWarehouseId" type="hidden" />
				<aui:input name="commerceInventoryWarehouseItemId" type="hidden" />
				<aui:input name="quantity" type="hidden" />
				<aui:input name="reservedQuantity" type="hidden" />
				<aui:input name="sku" type="hidden" value="<%= cpInstance.getSku() %>" />
				<aui:input name="unitOfMeasureKey" type="hidden" />
				<aui:input name="mvccVersion" type="hidden" />

				<liferay-ui:error exception="<%= CommerceInventoryWarehouseItemQuantityException.class %>" message="please-enter-a-valid-quantity" />

				<table class="show-quick-actions-on-hover table table-autofit table-list table-responsive-lg">
					<thead>
						<tr>
							<th class="table-cell-expand"><liferay-ui:message key="warehouse" /></th>

							<th><liferay-ui:message key="uom" /></th>

							<th><liferay-ui:message key="quantity" /></th>
							<th></th>
						</tr>
					</thead>

					<tbody>

						<%
						List<String> cpInstanceUnitOfMeasureKeys = commerceInventoryWarehouseItemsDisplayContext.getCPInstanceUnitOfMeasureKeys();
						int curIndex = 0;

						for (CommerceInventoryWarehouse commerceInventoryWarehouse : commerceInventoryWarehouses) {
							long commerceInventoryWarehouseItemId = 0;
							BigDecimal quantity = BigDecimal.ZERO;
							BigDecimal reservedQuantity = BigDecimal.ZERO;
							long mvccVersion = 0;

							CommerceInventoryWarehouseItem commerceInventoryWarehouseItem = null;

							for (String cpInstanceUnitOfMeasureKey : cpInstanceUnitOfMeasureKeys) {
								commerceInventoryWarehouseItem = commerceInventoryWarehouseItemsDisplayContext.getCommerceInventoryWarehouseItem(commerceInventoryWarehouse, cpInstanceUnitOfMeasureKey);

								if (commerceInventoryWarehouseItem != null) {
									commerceInventoryWarehouseItemId = commerceInventoryWarehouseItem.getCommerceInventoryWarehouseItemId();

									BigDecimal commerceInventoryWarehouseItemQuantity = commerceInventoryWarehouseItem.getQuantity();

									if (commerceInventoryWarehouseItemQuantity != null) {
										quantity = commerceInventoryWarehouseItemQuantity;
									}

									BigDecimal commerceInventoryWarehouseItemReservedQuantity = commerceInventoryWarehouseItem.getReservedQuantity();

									if (commerceInventoryWarehouseItemReservedQuantity != null) {
										reservedQuantity = commerceInventoryWarehouseItemReservedQuantity;
									}

									mvccVersion = commerceInventoryWarehouseItem.getMvccVersion();
								}
						%>

								<tr data-commerce-inventory-warehouse-id="<%= commerceInventoryWarehouse.getCommerceInventoryWarehouseId() %>" data-commerce-inventory-warehouse-item-id="<%= commerceInventoryWarehouseItemId %>" data-commerce-inventory-warehouse-item-uom="<%= HtmlUtil.escapeAttribute(cpInstanceUnitOfMeasureKey) %>" data-index="<%= curIndex %>" data-mvcc-version="<%= mvccVersion %>" data-reserved-quantity="<%= reservedQuantity %>">
									<td>
										<%= HtmlUtil.escape(commerceInventoryWarehouse.getName(locale)) %>
									</td>
									<td>
										<%= HtmlUtil.escape(cpInstanceUnitOfMeasureKey) %>
									</td>
									<td>
										<aui:input id='<%= "commerceInventoryWarehouseItemQuantity" + curIndex %>' label="" min="0" name="commerceInventoryWarehouseItemQuantity" type="text" value="<%= commerceInventoryWarehouseItemsDisplayContext.getFormattedQuantity(commerceInventoryWarehouseItem) %>" wrapperCssClass="mb-0" />
									</td>
									<td class="text-center">
										<aui:button cssClass="warehouse-save-btn" name='<%= "saveButton" + curIndex %>' primary="<%= true %>" value="save" />
									</td>
								</tr>

						<%
								curIndex++;
							}
						}
						%>

					</tbody>
				</table>
			</aui:form>
		</c:otherwise>
	</c:choose>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"ADD", Constants.ADD
			).put(
				"CMD", Constants.CMD
			).put(
				"UPDATE", Constants.UPDATE
			).build()
		%>'
		module="{viewWarehouseItems} from commerce-warehouse-web"
	/>
</c:if>