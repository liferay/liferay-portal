/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.test.util;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.account.service.AccountEntryUserRelLocalServiceUtil;
import com.liferay.account.service.AccountGroupLocalServiceUtil;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceAccountTestUtil {

	public static void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds,
			ServiceContext serviceContext)
		throws PortalException {

		AccountEntryOrganizationRelLocalServiceUtil.
			addAccountEntryOrganizationRels(accountEntryId, organizationIds);
	}

	public static void addAccountEntryUserRels(
			long accountEntryId, long[] userIds, ServiceContext serviceContext)
		throws PortalException {

		if (userIds != null) {
			for (long userId : userIds) {
				User user = UserLocalServiceUtil.getUser(userId);

				AccountEntryUserRelLocalServiceUtil.addAccountEntryUserRel(
					accountEntryId, user.getUserId());

				AccountEntry accountEntry =
					AccountEntryLocalServiceUtil.getAccountEntry(
						accountEntryId);

				if (!ArrayUtil.contains(
						user.getGroupIds(),
						accountEntry.getAccountEntryGroupId())) {

					UserLocalServiceUtil.addGroupUsers(
						accountEntry.getAccountEntryGroupId(),
						new long[] {userId});
				}

				if (ArrayUtil.contains(
						user.getGroupIds(), serviceContext.getScopeGroupId())) {

					continue;
				}

				UserLocalServiceUtil.addGroupUsers(
					serviceContext.getScopeGroupId(), new long[] {userId});
			}
		}
	}

	public static AccountGroup addAccountGroup(
			long companyId, String name, String type,
			String externalReferenceCode, ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isBlank(externalReferenceCode)) {
			externalReferenceCode = null;
		}

		AccountGroup accountGroup =
			AccountGroupLocalServiceUtil.addAccountGroup(
				StringPool.BLANK, serviceContext.getUserId(), null, name,
				serviceContext);

		accountGroup.setExternalReferenceCode(externalReferenceCode);
		accountGroup.setDefaultAccountGroup(false);
		accountGroup.setType(type);
		accountGroup.setExpandoBridgeAttributes(serviceContext);

		return AccountGroupLocalServiceUtil.updateAccountGroup(accountGroup);
	}

	public static AccountGroup addAccountGroupAndAccountRel(
			long companyId, String name, String type, long accountEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		AccountGroup accountGroup = addAccountGroup(
			companyId, name, type, RandomTestUtil.randomString(),
			serviceContext);

		AccountGroupRelLocalServiceUtil.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			accountEntryId);

		return accountGroup;
	}

	public static AccountEntry addBusinessAccountEntry(
			long userId, String name, String email,
			ServiceContext serviceContext)
		throws Exception {

		return addBusinessAccountEntry(
			userId, name, email, StringPool.BLANK, null, null, serviceContext);
	}

	public static AccountEntry addBusinessAccountEntry(
			long userId, String name, String email,
			String externalReferenceCode, long[] userIds,
			long[] organizationIds, ServiceContext serviceContext)
		throws Exception {

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.addAccountEntry(
				StringPool.BLANK, userId,
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, name, null,
				null, email, null, StringPool.BLANK,
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		addAccountEntryUserRels(
			accountEntry.getAccountEntryId(), new long[] {userId},
			serviceContext);

		Role role = RoleLocalServiceUtil.getRole(
			serviceContext.getCompanyId(),
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			userId, accountEntry.getAccountEntryGroupId(),
			new long[] {role.getRoleId()});

		if (externalReferenceCode != null) {
			accountEntry.setExternalReferenceCode(externalReferenceCode);

			accountEntry = AccountEntryLocalServiceUtil.updateAccountEntry(
				accountEntry);
		}

		addAccountEntryUserRels(
			accountEntry.getAccountEntryId(), userIds, serviceContext);

		addAccountEntryOrganizationRels(
			accountEntry.getAccountEntryId(), organizationIds, serviceContext);

		return accountEntry;
	}

	public static AccountEntry addBusinessAccountEntry(
			long userId, String name, String email,
			String externalReferenceCode, ServiceContext serviceContext)
		throws Exception {

		return addBusinessAccountEntry(
			userId, name, email, externalReferenceCode, null, null,
			serviceContext);
	}

	public static AccountEntry addPersonAccountEntry(
			long userId, ServiceContext serviceContext)
		throws Exception {

		User user = UserLocalServiceUtil.getUser(userId);

		serviceContext.setCompanyId(user.getCompanyId());

		serviceContext.setUserId(userId);

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.addAccountEntry(
				StringPool.BLANK, userId,
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				user.getFullName(), null, null, user.getEmailAddress(), null,
				StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		AccountEntryUserRelLocalServiceUtil.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), userId);

		return accountEntry;
	}

	public static AccountEntry getPersonAccountEntry(long userId)
		throws Exception {

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.fetchPersonAccountEntry(userId);

		if (accountEntry != null) {
			return accountEntry;
		}

		return addPersonAccountEntry(userId, new ServiceContext());
	}

}