/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.base.AccountEntryOrganizationRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

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
		"json.web.service.context.path=AccountEntryOrganizationRel"
	},
	service = AopService.class
)
public class AccountEntryOrganizationRelServiceImpl
	extends AccountEntryOrganizationRelServiceBaseImpl {

	@Override
	public AccountEntryOrganizationRel addAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		return accountEntryOrganizationRelLocalService.
			addAccountEntryOrganizationRel(accountEntryId, organizationId);
	}

	@Override
	public void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	@Override
	public void deleteAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(accountEntryId, organizationId);
	}

	@Override
	public void deleteAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRels(accountEntryId, organizationIds);
	}

	@Override
	public AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException {

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			accountEntryOrganizationRelLocalService.
				fetchAccountEntryOrganizationRel(accountEntryOrganizationRelId);

		if (accountEntryOrganizationRel != null) {
			_accountEntryModelResourcePermission.check(
				getPermissionChecker(),
				accountEntryOrganizationRel.getAccountEntryId(),
				AccountActionKeys.VIEW_ORGANIZATIONS);
		}

		return accountEntryOrganizationRel;
	}

	@Override
	public AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			accountEntryOrganizationRelLocalService.
				fetchAccountEntryOrganizationRel(
					accountEntryId, organizationId);

		if (accountEntryOrganizationRel != null) {
			_accountEntryModelResourcePermission.check(
				getPermissionChecker(),
				accountEntryOrganizationRel.getAccountEntryId(),
				AccountActionKeys.UPDATE_ORGANIZATIONS);
		}

		return accountEntryOrganizationRel;
	}

	@Override
	public AccountEntryOrganizationRel getAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		return accountEntryOrganizationRelLocalService.
			getAccountEntryOrganizationRel(accountEntryId, organizationId);
	}

	@Override
	public List<AccountEntryOrganizationRel> getAccountEntryOrganizationRels(
			long accountEntryId, int start, int end)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		return accountEntryOrganizationRelLocalService.
			getAccountEntryOrganizationRels(accountEntryId, start, end);
	}

	@Override
	public int getAccountEntryOrganizationRelsCount(long accountEntryId)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		return accountEntryOrganizationRelLocalService.
			getAccountEntryOrganizationRelsCount(accountEntryId);
	}

	@Override
	public void setAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		_accountEntryModelResourcePermission.check(
			getPermissionChecker(), accountEntryId,
			AccountActionKeys.UPDATE_ORGANIZATIONS);

		accountEntryOrganizationRelLocalService.setAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

}