/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.base.AccountGroupServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=account",
		"json.web.service.context.path=AccountGroup"
	},
	service = AopService.class
)
public class AccountGroupServiceImpl extends AccountGroupServiceBaseImpl {

	@Override
	public AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name, ServiceContext serviceContext)
		throws PortalException {

		PortalPermissionUtil.check(
			getPermissionChecker(), AccountActionKeys.ADD_ACCOUNT_GROUP);

		return accountGroupLocalService.addAccountGroup(
			externalReferenceCode, userId, description, name, serviceContext);
	}

	@Override
	public AccountGroup deleteAccountGroup(long accountGroupId)
		throws PortalException {

		_accountGroupModelResourcePermission.check(
			getPermissionChecker(), accountGroupId, ActionKeys.DELETE);

		return accountGroupLocalService.deleteAccountGroup(accountGroupId);
	}

	@Override
	public void deleteAccountGroups(long[] accountGroupIds)
		throws PortalException {

		for (long accountGroupId : accountGroupIds) {
			deleteAccountGroup(accountGroupId);
		}
	}

	@Override
	public AccountGroup fetchAccountGroup(long accountGroupId)
		throws PortalException {

		AccountGroup accountGroup = accountGroupLocalService.fetchAccountGroup(
			accountGroupId);

		if (accountGroup != null) {
			_accountGroupModelResourcePermission.check(
				getPermissionChecker(), accountGroup, ActionKeys.VIEW);
		}

		return accountGroup;
	}

	@Override
	public AccountGroup fetchAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		AccountGroup accountGroup =
			accountGroupLocalService.fetchAccountGroupByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (accountGroup != null) {
			_accountGroupModelResourcePermission.check(
				getPermissionChecker(), accountGroup, ActionKeys.VIEW);
		}

		return accountGroup;
	}

	@Override
	public AccountGroup getAccountGroup(long accountGroupId)
		throws PortalException {

		AccountGroup accountGroup = accountGroupLocalService.getAccountGroup(
			accountGroupId);

		_accountGroupModelResourcePermission.check(
			getPermissionChecker(), accountGroup, ActionKeys.VIEW);

		return accountGroup;
	}

	@Override
	public AccountGroup getAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		AccountGroup accountGroup =
			accountGroupLocalService.getAccountGroupByExternalReferenceCode(
				externalReferenceCode, companyId);

		_accountGroupModelResourcePermission.check(
			getPermissionChecker(), accountGroup, ActionKeys.VIEW);

		return accountGroup;
	}

	@Override
	public List<AccountGroup> getAccountGroupsByAccountEntryId(
			long accountEntryId, int start, int end)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_ACCOUNT_GROUPS);

		return accountGroupLocalService.getAccountGroupsByAccountEntryId(
			accountEntryId, start, end);
	}

	@Override
	public int getAccountGroupsCountByAccountEntryId(long accountEntryId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.VIEW_ACCOUNT_GROUPS);

		return accountGroupLocalService.getAccountGroupsCountByAccountEntryId(
			accountEntryId);
	}

	public AccountGroup getOrAddEmptyAccountGroup(
			String externalReferenceCode, String name)
		throws Exception {

		PermissionChecker permissionChecker = getPermissionChecker();

		AccountGroup accountGroup = fetchAccountGroupByExternalReferenceCode(
			externalReferenceCode, permissionChecker.getCompanyId());

		if (accountGroup != null) {
			return accountGroup;
		}

		PortalPermissionUtil.check(
			permissionChecker, AccountActionKeys.ADD_ACCOUNT_GROUP);

		return accountGroupLocalService.getOrAddEmptyAccountGroup(
			externalReferenceCode, permissionChecker.getCompanyId(),
			permissionChecker.getUserId(), name);
	}

	@Override
	public BaseModelSearchResult<AccountGroup> searchAccountGroups(
			long companyId, String keywords, int start, int end,
			OrderByComparator<AccountGroup> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		return accountGroupLocalService.searchAccountGroups(
			companyId, keywords,
			LinkedHashMapBuilder.<String, Object>put(
				"permissionUserId", permissionChecker.getUserId()
			).build(),
			start, end, orderByComparator);
	}

	@Override
	public AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name, ServiceContext serviceContext)
		throws PortalException {

		_accountGroupModelResourcePermission.check(
			getPermissionChecker(), accountGroupId, ActionKeys.UPDATE);

		return accountGroupLocalService.updateAccountGroup(
			externalReferenceCode, accountGroupId, description, name,
			serviceContext);
	}

	@Override
	public AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws PortalException {

		_accountGroupModelResourcePermission.check(
			getPermissionChecker(), accountGroupId, ActionKeys.UPDATE);

		return accountGroupLocalService.updateExternalReferenceCode(
			accountGroupId, externalReferenceCode);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountGroup)"
	)
	private ModelResourcePermission<AccountGroup>
		_accountGroupModelResourcePermission;

}