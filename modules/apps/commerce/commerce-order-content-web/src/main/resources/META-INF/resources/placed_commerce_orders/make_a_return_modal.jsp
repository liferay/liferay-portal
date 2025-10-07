<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<aui:form method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="" />

	<frontend-data-set:headless-display
		additionalProps="<%= commerceOrderContentDisplayContext.getReturnableOrderItemsContextParams() %>"
		apiURL="<%= commerceOrderContentDisplayContext.getCommerceReturnableItemsAPIURL() %>"
		bulkActionDropdownItems="<%= commerceOrderContentDisplayContext.getCommerceReturnableItemsBulkActionDropdownItems() %>"
		defaultSelectedItems="<%= commerceOrderContentDisplayContext.getReturnableSelectedItems() %>"
		id="<%= CommerceOrderFDSNames.RETURNABLE_ORDER_ITEMS %>"
		propsTransformer="{returnableOrderItemsPropsTransformer} from commerce-order-content-web"
		selectedItemsKey="id"
		selectionType="multiple"
		showBulkActionsManagementBarActions="<%= false %>"
		style="fluid"
	/>
</aui:form>