/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order.term.contributor;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.commerce.constants.CommerceDefinitionTermConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceSubscriptionNotificationConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.order.CommerceDefinitionTermContributor;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"commerce.definition.term.contributor.key=" + CommerceRecipientCommerceDefinitionTermContributor.KEY,
		"commerce.notification.type.key=" + CommerceOrderConstants.ORDER_NOTIFICATION_AWAITING_SHIPMENT,
		"commerce.notification.type.key=" + CommerceOrderConstants.ORDER_NOTIFICATION_COMPLETED,
		"commerce.notification.type.key=" + CommerceOrderConstants.ORDER_NOTIFICATION_PARTIALLY_SHIPPED,
		"commerce.notification.type.key=" + CommerceOrderConstants.ORDER_NOTIFICATION_PLACED,
		"commerce.notification.type.key=" + CommerceOrderConstants.ORDER_NOTIFICATION_PROCESSING,
		"commerce.notification.type.key=" + CommerceOrderConstants.ORDER_NOTIFICATION_SHIPPED,
		"commerce.notification.type.key=" + CommerceSubscriptionNotificationConstants.SUBSCRIPTION_ACTIVATED,
		"commerce.notification.type.key=" + CommerceSubscriptionNotificationConstants.SUBSCRIPTION_CANCELLED,
		"commerce.notification.type.key=" + CommerceSubscriptionNotificationConstants.SUBSCRIPTION_RENEWED,
		"commerce.notification.type.key=" + CommerceSubscriptionNotificationConstants.SUBSCRIPTION_SUSPENDED
	},
	service = CommerceDefinitionTermContributor.class
)
public class CommerceRecipientCommerceDefinitionTermContributor
	implements CommerceDefinitionTermContributor {

	public static final String KEY =
		CommerceDefinitionTermConstants.RECIPIENT_DEFINITION_TERMS_CONTRIBUTOR;

	@Override
	public String getFilledTerm(String term, Object object, Locale locale)
		throws PortalException {

		CommerceOrder commerceOrder = null;

		if (object instanceof CommerceOrder) {
			commerceOrder = (CommerceOrder)object;
		}
		else if (object instanceof CommerceSubscriptionEntry) {
			CommerceSubscriptionEntry commerceSubscriptionEntry =
				(CommerceSubscriptionEntry)object;

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemLocalService.getCommerceOrderItem(
					commerceSubscriptionEntry.getCommerceOrderItemId());

			commerceOrder = commerceOrderItem.getCommerceOrder();
		}

		if (commerceOrder == null) {
			return term;
		}

		if (term.equals(_ACCOUNT_ROLE_ADMINISTRATOR)) {
			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			Role accountAdminRole = _roleLocalService.getRole(
				commerceOrder.getCompanyId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

			return _getUserIds(accountEntry, accountAdminRole);
		}

		if (term.equals(_ACCOUNT_ROLE_ORDER_MANAGER)) {
			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			Role orderManagerRole = _roleLocalService.getRole(
				commerceOrder.getCompanyId(),
				AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER);

			return _getUserIds(accountEntry, orderManagerRole);
		}

		if (term.equals(_ORDER_CREATOR)) {
			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			if (accountEntry.isPersonalAccount()) {
				User user = _userLocalService.getUser(accountEntry.getUserId());

				return String.valueOf(user.getUserId());
			}

			if (accountEntry.isGuestAccount()) {
				return accountEntry.getEmailAddress();
			}

			return String.valueOf(commerceOrder.getUserId());
		}

		if (term.startsWith("[%USER_GROUP_")) {
			String[] s = term.split("_");

			String userGroupName = StringUtil.removeChars(s[2], '%', ']');

			return _getUserIds(
				_userGroupLocalService.getUserGroup(
					commerceOrder.getCompanyId(), userGroupName));
		}

		return term;
	}

	@Override
	public String getLabel(String term, Locale locale) {
		return _language.get(locale, _languageKeys.get(term));
	}

	@Override
	public List<String> getTerms() {
		return new ArrayList<>(_languageKeys.keySet());
	}

	private String _getUserIds(AccountEntry accountEntry, Role role)
		throws PortalException {

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					accountEntry.getAccountEntryId());

		StringBundler resultsSB = new StringBundler();

		for (AccountEntryUserRel accountEntryUserRel : accountEntryUserRels) {
			List<Role> userRoles = _roleLocalService.getUserGroupRoles(
				accountEntryUserRel.getAccountUserId(),
				accountEntry.getAccountEntryGroupId());

			if (!userRoles.contains(role)) {
				continue;
			}

			resultsSB.append(accountEntryUserRel.getAccountUserId());
			resultsSB.append(",");
		}

		return resultsSB.toString();
	}

	private String _getUserIds(UserGroup userGroup) throws PortalException {
		long[] userIds = _userGroupLocalService.getUserPrimaryKeys(
			userGroup.getUserGroupId());

		StringBundler resultsSB = new StringBundler();

		for (long userId : userIds) {
			resultsSB.append(userId);
			resultsSB.append(",");
		}

		return resultsSB.toString();
	}

	private static final String _ACCOUNT_ROLE_ADMINISTRATOR =
		"[%ACCOUNT_ROLE_ADMINISTRATOR%]";

	private static final String _ACCOUNT_ROLE_ORDER_MANAGER =
		"[%ACCOUNT_ROLE_ORDER_MANAGER%]";

	private static final String _ORDER_CREATOR = "[%ORDER_CREATOR%]";

	private static final String _USER_GROUP_NAME = "[%USER_GROUP_NAME%]";

	private static final Map<String, String> _languageKeys = HashMapBuilder.put(
		_ACCOUNT_ROLE_ADMINISTRATOR, "account-role-administrator"
	).put(
		_ACCOUNT_ROLE_ORDER_MANAGER, "account-role-order-manager"
	).put(
		_ORDER_CREATOR, "order-creator-definition-term"
	).put(
		_USER_GROUP_NAME, "user-group-name"
	).build();

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}