/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.util.v1_0;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.account.service.AccountRoleServiceUtil;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountRole;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class AccountMemberUtil {

	public static AccountEntryUserRel addAccountEntryUserRel(
			ModelResourcePermission<AccountEntry>
				accountEntryModelResourcePermission,
			AccountEntryUserRelService accountEntryUserRelService,
			AccountMember accountMember, AccountEntry accountEntry,
			CommerceAccountHelper commerceAccountHelper, User user,
			ServiceContext serviceContext)
		throws PortalException {

		accountEntryModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			accountEntry.getAccountEntryId(), AccountActionKeys.ASSIGN_USERS);

		long[] roleIds = TransformUtil.transformToLongArray(
			ListUtil.fromArray(accountMember.getAccountRoles()),
			AccountRole::getRoleId);

		validateAccountRoleIds(accountEntry.getAccountEntryId(), roleIds);

		commerceAccountHelper.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId(), roleIds,
			serviceContext);

		return accountEntryUserRelService.getAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());
	}

	public static User getUser(
			UserLocalService userLocalService, AccountMember accountMember,
			long companyId)
		throws PortalException {

		User user = null;

		if (Validator.isNotNull(accountMember.getEmail())) {
			user = userLocalService.getUserByEmailAddress(
				companyId, accountMember.getEmail());
		}
		else if (Validator.isNotNull(
					accountMember.getUserExternalReferenceCode())) {

			user = userLocalService.fetchUserByExternalReferenceCode(
				accountMember.getUserExternalReferenceCode(), companyId);

			if (user == null) {
				throw new NoSuchUserException(
					"Unable to get user with external reference code " +
						accountMember.getUserExternalReferenceCode());
			}
		}
		else {
			user = userLocalService.getUser(accountMember.getUserId());
		}

		return user;
	}

	public static List<UserGroupRole> setUserGroupRoles(
			AccountEntry accountEntry,
			ModelResourcePermission<AccountEntry>
				accountEntryModelResourcePermission,
			AccountMember accountMember, User user)
		throws PortalException {

		accountEntryModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			accountEntry.getAccountEntryId(), AccountActionKeys.ASSIGN_USERS);

		long[] roleIds = TransformUtil.transformToLongArray(
			ListUtil.fromArray(accountMember.getAccountRoles()),
			AccountRole::getRoleId);

		validateAccountRoleIds(accountEntry.getAccountEntryId(), roleIds);

		UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(
			user.getUserId(),
			new long[] {accountEntry.getAccountEntryGroupId()});

		if (ArrayUtil.isEmpty(roleIds)) {
			return Collections.emptyList();
		}

		return UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), accountEntry.getAccountEntryGroupId(), roleIds);
	}

	public static void validateAccountRoleIds(
			long accountEntryId, long[] roleIds)
		throws PortalException {

		for (long roleId : roleIds) {
			com.liferay.account.model.AccountRole serviceBuilderAccountRole =
				AccountRoleServiceUtil.getAccountRoleByRoleId(roleId);

			if ((serviceBuilderAccountRole.getAccountEntryId() !=
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) &&
				(serviceBuilderAccountRole.getAccountEntryId() !=
					accountEntryId)) {

				throw new RoleAssignmentException(
					StringBundler.concat(
						"Account role ", roleId,
						" does not belong to account entry ", accountEntryId));
			}
		}
	}

}