/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.search.spi.model.permission;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = SearchPermissionFilterContributor.class)
public class CommercePriceListSearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		if (!className.equals(CommercePriceList.class.getName())) {
			return;
		}

		try {
			TermsFilter termsFilter = new TermsFilter("accountEntryId");

			List<AccountEntry> accountEntries =
				_accountEntryLocalService.getUserAccountEntries(
					permissionChecker.getUserId(), 0L, StringPool.BLANK,
					new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_SUPPLIER},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (AccountEntry accountEntry : accountEntries) {
				if (!_userGroupRoleLocalService.hasUserGroupRole(
						permissionChecker.getUserId(),
						accountEntry.getAccountEntryGroupId(),
						AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER)) {

					continue;
				}

				termsFilter.addValue(
					String.valueOf(accountEntry.getAccountEntryId()));
			}

			if (!termsFilter.isEmpty()) {
				booleanFilter.add(termsFilter, BooleanClauseOccur.SHOULD);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListSearchPermissionFilterContributor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}