<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/order_actions/init.jsp" %>

<div>
	<react:component
		module="{OrderActions} from commerce-order-content-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"checkoutURL", checkoutURL
			).put(
				"isOpen", open
			).put(
				"orderId", commerceOrderId
			).put(
				"orderSummaryURL", orderSummaryURL
			).put(
				"quickCheckoutEnabled", quickCheckoutEnabled
			).put(
				"reorderURL", reorderURL
			).put(
				"viewReturnableOrderItemsURL", viewReturnableOrderItemsURL
			).build()
		%>'
	/>
</div>

<div>
	<react:component
		module="{DropdownMenuComponent} from commerce-frontend-js"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"items", dropdownItems
			).put(
				"spritemap", themeDisplay.getPathThemeSpritemap()
			).build()
		%>'
	/>
</div>

<c:if test='<%= FeatureFlagManagerUtil.isEnabled(company.getCompanyId(), "LPD-10562") && Validator.isNotNull(viewReturnableOrderItemsURL) %>'>
	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"namespace", namespace
			).put(
				"returnableOrderItemsContextParams", returnableOrderItemsContextParams
			).put(
				"viewReturnableOrderItemsURL", viewReturnableOrderItemsURL
			).build()
		%>'
		module="{viewCommerceOrderDetailsCTAs} from commerce-order-content-web"
	/>
</c:if>