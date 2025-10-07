<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/account_selector/init.jsp" %>

<c:choose>
	<c:when test="<%= commerceChannelId == 0 %>">
		<div class="alert alert-info mx-auto">
			<liferay-ui:message key="this-site-does-not-have-a-channel" />
		</div>
	</c:when>
	<c:when test="<%= !user.isGuestUser() %>">
		<div class="<%= (cssClasses != null) ? "account-selector-root " + cssClasses : "account-selector-root" %>" id="<%= accountSelectorId %>"></div>

		<liferay-frontend:component
			context='<%=
				HashMapBuilder.<String, Object>put(
					"accountEntryAllowedTypes", accountEntryAllowedTypes
				).put(
					"accountSelectorId", accountSelectorId
				).put(
					"checkoutURL", checkoutURL
				).put(
					"commerceChannelId", commerceChannelId
				).put(
					"createNewOrderURL", createNewOrderURL
				).put(
					"currencyCode", currencyCode
				).put(
					"currentCommerceAccount", currentCommerceAccount
				).put(
					"currentCommerceOrder", currentCommerceOrder
				).put(
					"hasAddCommerceOrderPermission", hasAddCommerceOrderPermission
				).put(
					"hasManageAccountsPermission", hasManageAccountsPermission
				).put(
					"orderSelectionDisabled", orderSelectionDisabled
				).put(
					"refreshPageOnAccountSelected", true
				).put(
					"selectOrderURL", selectOrderURL
				).put(
					"setCurrentAccountURL", setCurrentAccountURL
				).build()
			%>'
			module="{accountSelectorTag} from commerce-frontend-taglib"
		/>
	</c:when>
</c:choose>