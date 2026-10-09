/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.CommerceChannelConfigurationUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = SearchPermissionFilterContributor.class)
public class CommerceShipmentSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(CommerceShipment.class.getName()) ||
			!permissionChecker.isSignedIn()) {

			return;
		}

		try {
			booleanFilter.add(
				new TermFilter("commerceOrderUserIds", String.valueOf(userId)),
				BooleanClauseOccur.SHOULD);

			TermsFilter manageCommerceAccountIdsTermsFilter = new TermsFilter(
				"commerceAccountId");
			TermsFilter viewCommerceAccountIdsTermsFilter = new TermsFilter(
				"commerceAccountId");

			Set<Long> supplierAccountEntryIds = new HashSet<>();

			for (AccountEntry accountEntry :
					_accountEntryLocalService.getUserAccountEntries(
						userId, null, StringPool.BLANK, null, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS)) {

				long accountEntryGroupId =
					accountEntry.getAccountEntryGroupId();

				Group group = _groupLocalService.getGroup(accountEntryGroupId);

				List<Group> groups = ListUtil.copy(group.getAncestors());

				groups.add(group);

				if (_hasAncestorPermission(
						permissionChecker, groups,
						CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS,
						CommerceOrderActionKeys.
							MANAGE_ACCOUNTS_SCOPED_COMMERCE_ORDERS,
						CommerceOrderActionKeys.
							VIEW_ORGANIZATION_COMMERCE_ORDERS)) {

					manageCommerceAccountIdsTermsFilter.addValue(
						String.valueOf(accountEntry.getAccountEntryId()));
				}
				else if (_hasAncestorPermission(
							permissionChecker, groups,
							CommerceOrderActionKeys.VIEW_COMMERCE_ORDERS)) {

					viewCommerceAccountIdsTermsFilter.addValue(
						String.valueOf(accountEntry.getAccountEntryId()));
				}

				if (!_userGroupRoleLocalService.hasUserGroupRole(
						userId, accountEntryGroupId,
						AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER)) {

					continue;
				}

				supplierAccountEntryIds.add(accountEntry.getAccountEntryId());
			}

			if (!manageCommerceAccountIdsTermsFilter.isEmpty()) {
				booleanFilter.add(
					manageCommerceAccountIdsTermsFilter,
					BooleanClauseOccur.SHOULD);
			}

			TermsFilter accountScopeCommerceChannelIdsTermsFilter =
				new TermsFilter("commerceChannelId");
			TermsFilter supplierCommerceChannelIdsTermsFilter = new TermsFilter(
				"commerceChannelId");

			if (!viewCommerceAccountIdsTermsFilter.isEmpty() ||
				!supplierAccountEntryIds.isEmpty()) {

				for (CommerceChannel commerceChannel :
						_commerceChannelLocalService.getCommerceChannels(
							companyId)) {

					if (!viewCommerceAccountIdsTermsFilter.isEmpty() &&
						!CommerceOrderConstants.ORDER_VISIBILITY_SCOPE_USER.
							equals(
								CommerceChannelConfigurationUtil.
									getPlacedCommerceOrderVisibilityScope(
										commerceChannel.getGroupId()))) {

						accountScopeCommerceChannelIdsTermsFilter.addValue(
							String.valueOf(
								commerceChannel.getCommerceChannelId()));
					}

					if (!supplierAccountEntryIds.contains(
							commerceChannel.getAccountEntryId())) {

						continue;
					}

					supplierCommerceChannelIdsTermsFilter.addValue(
						String.valueOf(commerceChannel.getCommerceChannelId()));
				}
			}

			if (!accountScopeCommerceChannelIdsTermsFilter.isEmpty()) {
				BooleanFilter accountScopeBooleanFilter = new BooleanFilter();

				accountScopeBooleanFilter.add(
					viewCommerceAccountIdsTermsFilter, BooleanClauseOccur.MUST);
				accountScopeBooleanFilter.add(
					accountScopeCommerceChannelIdsTermsFilter,
					BooleanClauseOccur.MUST);

				booleanFilter.add(
					accountScopeBooleanFilter, BooleanClauseOccur.SHOULD);
			}

			if (!supplierCommerceChannelIdsTermsFilter.isEmpty()) {
				booleanFilter.add(
					supplierCommerceChannelIdsTermsFilter,
					BooleanClauseOccur.SHOULD);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private boolean _hasAncestorPermission(
		PermissionChecker permissionChecker, List<Group> groups,
		String... actionIds) {

		for (Group curGroup : groups) {
			for (String actionId : actionIds) {
				if (_portletResourcePermission.contains(
						permissionChecker, curGroup.getGroupId(), actionId)) {

					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentSearchPermissionFilterContributor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}