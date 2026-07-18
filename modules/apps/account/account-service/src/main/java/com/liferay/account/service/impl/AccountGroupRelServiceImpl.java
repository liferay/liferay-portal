/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.base.AccountGroupRelServiceBaseImpl;
import com.liferay.account.service.persistence.AccountGroupPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=account",
		"json.web.service.context.path=AccountGroupRel"
	},
	service = AopService.class
)
public class AccountGroupRelServiceImpl extends AccountGroupRelServiceBaseImpl {

	@Override
	public AccountGroupRel addAccountGroupRel(
			long accountGroupId, String className, long classPK)
		throws PortalException {

		_checkPermission(
			accountGroupId, className, AccountActionKeys.ASSIGN_ACCOUNTS);

		return accountGroupRelLocalService.addAccountGroupRel(
			accountGroupId, className, classPK);
	}

	@Override
	public void addAccountGroupRels(
			long accountGroupId, String className, long[] classPKs)
		throws PortalException {

		_checkPermission(
			accountGroupId, className, AccountActionKeys.ASSIGN_ACCOUNTS);

		accountGroupRelLocalService.addAccountGroupRels(
			accountGroupId, className, classPKs);
	}

	@Override
	public AccountGroupRel deleteAccountGroupRel(long accountGroupRelId)
		throws PortalException {

		AccountGroupRel accountGroupRel =
			accountGroupRelPersistence.fetchByPrimaryKey(accountGroupRelId);

		if (accountGroupRel == null) {
			return null;
		}

		_checkPermission(
			accountGroupRel.getAccountGroupId(), accountGroupRel.getClassName(),
			AccountActionKeys.ASSIGN_ACCOUNTS);

		return accountGroupRelLocalService.deleteAccountGroupRel(
			accountGroupRel);
	}

	@Override
	public void deleteAccountGroupRels(
			long accountGroupId, String className, long[] classPKs)
		throws PortalException {

		_checkPermission(
			accountGroupId, className, AccountActionKeys.ASSIGN_ACCOUNTS);

		accountGroupRelLocalService.deleteAccountGroupRels(
			accountGroupId, className, classPKs);
	}

	@Override
	public AccountGroupRel fetchAccountGroupRel(
			long accountGroupId, String className, long classPK)
		throws PortalException {

		AccountGroupRel accountGroupRel =
			accountGroupRelPersistence.fetchByA_C_C(
				accountGroupId,
				_classNameLocalService.getClassNameId(className), classPK);

		if (accountGroupRel != null) {
			_checkPermission(
				accountGroupId, className, AccountActionKeys.VIEW_ACCOUNTS);
		}

		return accountGroupRel;
	}

	@Override
	public AccountGroupRel getAccountGroupRel(long accountGroupRelId)
		throws PortalException {

		AccountGroupRel accountGroupRel =
			accountGroupRelPersistence.findByPrimaryKey(accountGroupRelId);

		_checkPermission(
			accountGroupRel.getAccountGroupId(), accountGroupRel.getClassName(),
			AccountActionKeys.VIEW_ACCOUNTS);

		return accountGroupRel;
	}

	private void _checkPermission(
			long accountGroupId, String className, String actionId)
		throws PortalException {

		AccountGroup accountGroup = _accountGroupPersistence.findByPrimaryKey(
			accountGroupId);

		PermissionChecker permissionChecker = getPermissionChecker();

		if (accountGroup.getCompanyId() != permissionChecker.getCompanyId()) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountGroup.class.getName(), accountGroupId,
				ActionKeys.VIEW);
		}

		if (Objects.equals(AccountEntry.class.getName(), className)) {
			_accountGroupModelResourcePermission.check(
				permissionChecker, accountGroupId, actionId);
		}
	}

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountGroup)"
	)
	private ModelResourcePermission<AccountGroup>
		_accountGroupModelResourcePermission;

	@Reference
	private AccountGroupPersistence _accountGroupPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

}