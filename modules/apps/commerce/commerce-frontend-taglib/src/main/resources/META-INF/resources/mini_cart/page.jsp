<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/mini_cart/init.jsp" %>

<c:choose>
	<c:when test="<%= commerceChannelId == 0 %>">
		<div class="alert alert-info mx-auto">
			<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:otherwise>
		<div class="<%= (cssClasses != null) ? "cart-root " + cssClasses : "cart-root" %>" id="<%= miniCartId %>"></div>

		<liferay-frontend:component
			context='<%=
				HashMapBuilder.<String, Object>put(
					"accountId", accountEntryId
				).put(
					"baseOrderDetailURL", baseOrderDetailURL
				).put(
					"cartViews", cartViews
				).put(
					"checkoutURL", checkoutURL
				).put(
					"currencyCode", commerceCurrencyCode
				).put(
					"displayDiscountLevels", displayDiscountLevels
				).put(
					"displayTotalItemsQuantity", displayTotalItemsQuantity
				).put(
					"groupId", commerceChannelGroupId
				).put(
					"guestOrderEnabled", guestOrderEnabled
				).put(
					"id", commerceChannelId
				).put(
					"itemsQuantity", itemsQuantity
				).put(
					"labels", labels
				).put(
					"miniCartId", miniCartId
				).put(
					"orderDetailURL", orderDetailURL
				).put(
					"orderId", orderId
				).put(
					"productURLSeparator", productURLSeparator
				).put(
					"requestQuoteEnabled", requestCodeEnabled
				).put(
					"signInURL", signInURL
				).put(
					"siteDefaultURL", siteDefaultURL
				).put(
					"slowConnectionOrderFlowEnabled", slowConnectionOrderFlowEnabled
				).put(
					"toggleable", toggleable
				).put(
					"undoCartItemDeletionDisabled", undoCartItemDeletionDisabled
				).build()
			%>'
			module="{cart} from commerce-frontend-taglib"
		/>
	</c:otherwise>
</c:choose>