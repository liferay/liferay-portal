/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(service = SearchPermissionFilterContributor.class)
public class CommerceOrderSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(CommerceOrder.class.getName())) {
			return;
		}

		try {
			TermsFilter groupsTermsFilter = new TermsFilter(Field.GROUP_ID);

			for (long groupId : groupIds) {
				if (!_hasRoleAccountSupplier(permissionChecker, groupId)) {
					continue;
				}

				groupsTermsFilter.addValue(String.valueOf(groupId));
			}

			if (!groupsTermsFilter.isEmpty()) {
				booleanFilter.add(groupsTermsFilter, BooleanClauseOccur.SHOULD);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private boolean _hasRoleAccountSupplier(
			PermissionChecker permissionChecker, long groupId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				groupId);

		if ((commerceChannel != null) &&
			(commerceChannel.getAccountEntryId() == 0)) {

			return false;
		}

		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				permissionChecker.getUserId(), 0L, StringPool.BLANK,
				new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (AccountEntry accountEntry : accountEntries) {
			if ((accountEntry.getAccountEntryId() ==
					commerceChannel.getAccountEntryId()) &&
				_userGroupRoleLocalService.hasUserGroupRole(
					permissionChecker.getUserId(),
					accountEntry.getAccountEntryGroupId(),
					AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER)) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderSearchPermissionFilterContributor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}