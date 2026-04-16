/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.util.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author Brian I. Kim
 */
public class AccountUtil {

	public static long getAccountId(
			long companyId, long groupId, long userId,
			AccountEntryLocalService accountEntryLocalService,
			AccountEntryService accountEntryService, Long accountId,
			CommerceAccountHelper commerceAccountHelper,
			MultivaluedMap<String, String> queryParameters)
		throws PortalException {

		if ((accountId != null) && (accountId > 0)) {
			AccountEntry accountEntry = accountEntryService.fetchAccountEntry(
				accountId);

			if (accountEntry != null) {
				return accountEntry.getAccountEntryId();
			}
		}

		int countUserCommerceAccounts =
			commerceAccountHelper.countUserCommerceAccounts(userId, groupId);

		if (countUserCommerceAccounts > 1) {
			if (accountId == null) {
				if (queryParameters != null) {
					String accountIdString = queryParameters.getFirst(
						"accountId");

					if (accountIdString != null) {
						return GetterUtil.getLong(accountIdString);
					}
				}

				throw new NoSuchEntryException();
			}
		}

		long[] accountIds = commerceAccountHelper.getUserCommerceAccountIds(
			userId, groupId);

		if (accountIds.length != 0) {
			return accountIds[0];
		}

		AccountEntry accountEntry =
			accountEntryLocalService.getGuestAccountEntry(companyId);

		return accountEntry.getAccountEntryId();
	}

}