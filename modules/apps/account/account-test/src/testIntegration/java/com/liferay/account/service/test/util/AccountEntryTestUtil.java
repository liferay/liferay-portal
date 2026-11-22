/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.account.service.AccountEntryUserRelLocalServiceUtil;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.account.service.persistence.AccountEntryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class AccountEntryTestUtil {

	public static List<AccountEntry> addAccountEntries(
			int number, AccountEntryArgs.Consumer... consumers)
		throws Exception {

		List<AccountEntry> accountEntries = new ArrayList<>();

		for (int i = 0; i < number; i++) {
			accountEntries.add(addAccountEntry(consumers));
		}

		return accountEntries;
	}

	public static AccountEntry addAccountEntry(
			AccountEntryArgs.Consumer... consumers)
		throws Exception {

		AccountEntryArgs accountEntryArgs = new AccountEntryArgs();

		for (AccountEntryArgs.Consumer consumer : consumers) {
			consumer.accept(accountEntryArgs);
		}

		return _addAccountEntry(accountEntryArgs);
	}

	public static List<AccountEntry> getAccountEntries(
			long companyId, int status, int start, int end,
			OrderByComparator<AccountEntry> orderByComparator)
		throws Exception {

		return TransactionTestUtil.invoke(
			AccountEntryUtil.getPersistence(),
			accountEntryPersistence -> accountEntryPersistence.findByC_S(
				companyId, status, start, end, orderByComparator));
	}

	private static AccountEntry _addAccountEntry(
			AccountEntryArgs accountEntryArgs)
		throws Exception {

		ServiceContext serviceContext = accountEntryArgs.serviceContext;

		if (serviceContext == null) {
			User user = UserLocalServiceUtil.getUser(accountEntryArgs.userId);

			Group group = GroupLocalServiceUtil.getGroup(
				user.getCompanyId(), GroupConstants.GUEST);

			serviceContext = ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());
		}

		if (ArrayUtil.isNotEmpty(accountEntryArgs.assetTagNames)) {
			serviceContext.setAssetTagNames(accountEntryArgs.assetTagNames);
		}

		long parentAccountEntryId = 0;

		AccountEntry parentAccountEntry = accountEntryArgs.parentAccountEntry;

		if (parentAccountEntry != null) {
			parentAccountEntryId = parentAccountEntry.getAccountEntryId();
		}

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.addAccountEntry(
				accountEntryArgs.externalReferenceCode, accountEntryArgs.userId,
				parentAccountEntryId, accountEntryArgs.name,
				accountEntryArgs.description, accountEntryArgs.domains,
				accountEntryArgs.emailAddress, accountEntryArgs.logoBytes,
				accountEntryArgs.taxIdNumber, accountEntryArgs.type,
				accountEntryArgs.status, serviceContext);

		if (ArrayUtil.isNotEmpty(accountEntryArgs.accountGroups)) {
			for (AccountGroup accountGroup : accountEntryArgs.accountGroups) {
				AccountGroupRelLocalServiceUtil.addAccountGroupRel(
					accountGroup.getAccountGroupId(),
					AccountEntry.class.getName(),
					accountEntry.getAccountEntryId());
			}
		}

		if (ArrayUtil.isNotEmpty(accountEntryArgs.organizations)) {
			AccountEntryOrganizationRelLocalServiceUtil.
				addAccountEntryOrganizationRels(
					accountEntry.getAccountEntryId(),
					ListUtil.toLongArray(
						Arrays.asList(accountEntryArgs.organizations),
						Organization.ORGANIZATION_ID_ACCESSOR));
		}

		if (ArrayUtil.isNotEmpty(accountEntryArgs.users)) {
			AccountEntryUserRelLocalServiceUtil.addAccountEntryUserRels(
				accountEntry.getAccountEntryId(),
				ListUtil.toLongArray(
					Arrays.asList(accountEntryArgs.users),
					User.USER_ID_ACCESSOR));
		}

		if (accountEntryArgs.restrictMembership !=
				accountEntry.isRestrictMembership()) {

			accountEntry =
				AccountEntryLocalServiceUtil.updateRestrictMembership(
					accountEntry.getAccountEntryId(),
					accountEntryArgs.restrictMembership);
		}

		return accountEntry;
	}

}