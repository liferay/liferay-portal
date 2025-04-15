/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.headless.commerce.admin.account.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountRole;
import com.liferay.headless.commerce.admin.account.internal.util.v1_0.AccountMemberUtil;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountMemberResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-member.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = AccountMemberResource.class
)
@Deprecated
public class AccountMemberResourceImpl extends BaseAccountMemberResourceImpl {

	@Override
	public Response deleteAccountByExternalReferenceCodeAccountMember(
			String externalReferenceCode, Long userId)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		_accountEntryUserRelService.deleteAccountEntryUserRels(
			accountEntry.getAccountEntryId(), new long[] {userId});

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deleteAccountIdAccountMember(Long id, Long userId)
		throws Exception {

		_accountEntryUserRelService.deleteAccountEntryUserRels(
			id, new long[] {userId});

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public AccountMember getAccountByExternalReferenceCodeAccountMember(
			String externalReferenceCode, Long userId)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelService.getAccountEntryUserRel(
				accountEntry.getAccountEntryId(), userId);

		return _accountMemberDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountEntryUserRel.getPrimaryKey(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public Page<AccountMember>
			getAccountByExternalReferenceCodeAccountMembersPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelService.getAccountEntryUserRelsByAccountEntryId(
				accountEntry.getAccountEntryId(), pagination.getStartPosition(),
				pagination.getEndPosition());

		long totalItems =
			_accountEntryUserRelService.
				getAccountEntryUserRelsCountByAccountEntryId(
					accountEntry.getAccountEntryId());

		return Page.of(
			_toAccountMembers(accountEntryUserRels), pagination, totalItems);
	}

	@Override
	public AccountMember getAccountIdAccountMember(Long id, Long userId)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelService.getAccountEntryUserRel(id, userId);

		return _accountMemberDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountEntryUserRel.getPrimaryKey(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@NestedField(parentClass = Account.class, value = "accountMembers")
	@Override
	public Page<AccountMember> getAccountIdAccountMembersPage(
			Long id, Pagination pagination)
		throws Exception {

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelService.getAccountEntryUserRelsByAccountEntryId(
				id, pagination.getStartPosition(), pagination.getEndPosition());

		long totalItems =
			_accountEntryUserRelService.
				getAccountEntryUserRelsCountByAccountEntryId(id);

		return Page.of(
			_toAccountMembers(accountEntryUserRels), pagination, totalItems);
	}

	@Override
	public Response patchAccountByExternalReferenceCodeAccountMember(
			String externalReferenceCode, Long userId,
			AccountMember accountMember)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		_updateAccountEntryUserRel(
			accountEntry, _userLocalService.getUser(userId), accountMember);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchAccountIdAccountMember(
			Long id, Long userId, AccountMember accountMember)
		throws Exception {

		_updateAccountEntryUserRel(
			_accountEntryLocalService.getAccountEntry(id),
			_userLocalService.getUser(userId), accountMember);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public AccountMember postAccountByExternalReferenceCodeAccountMember(
			String externalReferenceCode, AccountMember accountMember)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		AccountEntryUserRel accountEntryUserRel =
			AccountMemberUtil.addAccountEntryUserRel(
				_accountEntryModelResourcePermission,
				_accountEntryUserRelService, accountMember, accountEntry,
				_commerceAccountHelper,
				AccountMemberUtil.getUser(
					_userLocalService, accountMember,
					contextCompany.getCompanyId()),
				_serviceContextHelper.getServiceContext());

		return _accountMemberDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountEntryUserRel.getPrimaryKey(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Override
	public AccountMember postAccountIdAccountMember(
			Long id, AccountMember accountMember)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			AccountMemberUtil.addAccountEntryUserRel(
				_accountEntryModelResourcePermission,
				_accountEntryUserRelService, accountMember,
				_accountEntryLocalService.getAccountEntry(id),
				_commerceAccountHelper,
				AccountMemberUtil.getUser(
					_userLocalService, accountMember,
					contextCompany.getCompanyId()),
				_serviceContextHelper.getServiceContext());

		return _accountMemberDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				accountEntryUserRel.getPrimaryKey(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<AccountMember> _toAccountMembers(
			List<AccountEntryUserRel> accountEntryUserRels)
		throws Exception {

		return transform(
			accountEntryUserRels,
			accountEntryUserRel -> _accountMemberDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					accountEntryUserRel.getPrimaryKey(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	private void _updateAccountEntryUserRel(
			AccountEntry accountEntry, User user, AccountMember accountMember)
		throws Exception {

		_userGroupRoleLocalService.deleteUserGroupRoles(
			user.getUserId(),
			new long[] {accountEntry.getAccountEntryGroupId()});

		AccountRole[] accountRoles = accountMember.getAccountRoles();

		if (accountRoles != null) {
			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), accountEntry.getAccountEntryGroupId(),
				transformToLongArray(
					Arrays.asList(accountRoles), AccountRole::getRoleId));
		}
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountEntryUserRelService _accountEntryUserRelService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter.AccountMemberDTOConverter)"
	)
	private DTOConverter<AccountEntryUserRel, AccountMember>
		_accountMemberDTOConverter;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}