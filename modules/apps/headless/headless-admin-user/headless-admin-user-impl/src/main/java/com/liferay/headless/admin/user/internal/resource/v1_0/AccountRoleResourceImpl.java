/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.NoSuchRoleException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.role.AccountRolePermissionThreadLocal;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.AccountRoleEntityModel;
import com.liferay.headless.admin.user.resource.v1_0.AccountRoleResource;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-role.properties",
	scope = ServiceScope.PROTOTYPE, service = AccountRoleResource.class
)
public class AccountRoleResourceImpl extends BaseAccountRoleResourceImpl {

	@Override
	public void deleteAccountAccountRoleUserAccountAssociation(
			Long accountId, Long accountRoleId, Long userAccountId)
		throws Exception {

		_accountRoleLocalService.unassociateUser(
			accountId, accountRoleId, userAccountId);
	}

	@Override
	public void
			deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress(
				String externalReferenceCode,
				String accountRoleExternalReferenceCode, String emailAddress)
		throws Exception {

		com.liferay.account.model.AccountRole accountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				accountRoleExternalReferenceCode,
				contextCompany.getCompanyId());

		if (accountRole == null) {
			throw new NoSuchRoleException(
				"Unable to find account role with external reference code " +
					accountRoleExternalReferenceCode);
		}

		deleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
			externalReferenceCode, accountRole.getAccountRoleId(),
			emailAddress);
	}

	@Override
	public void
			deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String accountRoleExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		com.liferay.account.model.AccountRole accountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				accountRoleExternalReferenceCode,
				contextCompany.getCompanyId());

		if (accountRole == null) {
			throw new NoSuchRoleException(
				"Unable to find account role with external reference code " +
					accountRoleExternalReferenceCode);
		}

		deleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
			accountExternalReferenceCode, accountRole.getAccountRoleId(),
			externalReferenceCode);
	}

	@Override
	public void
			deleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
				String externalReferenceCode, Long accountRoleId,
				String emailAddress)
		throws Exception {

		User user = _userLocalService.getUserByEmailAddress(
			contextCompany.getCompanyId(), emailAddress);

		deleteAccountAccountRoleUserAccountAssociation(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			accountRoleId, user.getUserId());
	}

	@Override
	public void
			deleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode, Long accountRoleId,
				String externalReferenceCode)
		throws Exception {

		deleteAccountAccountRoleUserAccountAssociation(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, accountExternalReferenceCode),
			accountRoleId,
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<AccountRole> getAccountAccountRolesByExternalReferenceCodePage(
			String externalReferenceCode, String keywords, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return getAccountAccountRolesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			keywords, filter, pagination, sorts);
	}

	@Override
	public Page<AccountRole> getAccountAccountRolesPage(
			Long accountId, String keywords, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (accountId > 0) {
			_accountEntryLocalService.getAccountEntry(accountId);
		}

		try (SafeCloseable safeCloseable =
				AccountRolePermissionThreadLocal.
					setAccountEntryIdWithSafeCloseable(accountId)) {

			return SearchUtil.search(
				null,
				booleanQuery -> {
					BooleanFilter preBooleanFilter =
						booleanQuery.getPreBooleanFilter();

					TermsFilter termsFilter = new TermsFilter("accountEntryId");

					termsFilter.addValues(
						ArrayUtil.toStringArray(
							new long[] {
								AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
								accountId
							}));

					preBooleanFilter.add(termsFilter, BooleanClauseOccur.MUST);
				},
				filter, com.liferay.account.model.AccountRole.class.getName(),
				keywords, pagination,
				queryConfig -> {
				},
				searchContext -> {
					searchContext.setCompanyId(contextCompany.getCompanyId());

					if (Validator.isNotNull(keywords)) {
						searchContext.setKeywords(keywords);
					}
				},
				sorts,
				document -> _toAccountRole(
					_accountRoleLocalService.getAccountRole(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
		}
	}

	@Override
	public Page<AccountRole>
			getAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage(
				String externalReferenceCode, String emailAddress)
		throws Exception {

		User user = _userLocalService.getUserByEmailAddress(
			contextCompany.getCompanyId(), emailAddress);

		return Page.of(
			transform(
				_accountRoleLocalService.getAccountRoles(
					DTOConverterUtil.getModelPrimaryKey(
						_accountResourceDTOConverter, externalReferenceCode),
					user.getUserId()),
				accountRole -> _toAccountRole(accountRole)));
	}

	@Override
	public Page<AccountRole>
			getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		return Page.of(
			transform(
				_accountRoleLocalService.getAccountRoles(
					DTOConverterUtil.getModelPrimaryKey(
						_accountResourceDTOConverter,
						accountExternalReferenceCode),
					DTOConverterUtil.getModelPrimaryKey(
						_userResourceDTOConverter, externalReferenceCode)),
				accountRole -> _toAccountRole(accountRole)));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public AccountRole postAccountAccountRole(
			Long accountId, AccountRole accountRole)
		throws Exception {

		return _toAccountRole(
			_accountRoleLocalService.addAccountRole(
				accountRole.getExternalReferenceCode(), contextUser.getUserId(),
				accountId, accountRole.getName(),
				Collections.singletonMap(
					contextAcceptLanguage.getPreferredLocale(),
					accountRole.getDisplayName()),
				Collections.singletonMap(
					contextAcceptLanguage.getPreferredLocale(),
					accountRole.getDescription())));
	}

	@Override
	public AccountRole postAccountAccountRoleByExternalReferenceCode(
			String externalReferenceCode, AccountRole accountRole)
		throws Exception {

		return postAccountAccountRole(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			accountRole);
	}

	@Override
	public void postAccountAccountRoleUserAccountAssociation(
			Long accountId, Long accountRoleId, Long userAccountId)
		throws Exception {

		_accountRoleLocalService.associateUser(
			accountId, accountRoleId, userAccountId);
	}

	@Override
	public void
			postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress(
				String externalReferenceCode,
				String accountRoleExternalReferenceCode, String emailAddress)
		throws Exception {

		com.liferay.account.model.AccountRole accountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				accountRoleExternalReferenceCode,
				contextCompany.getCompanyId());

		if (accountRole == null) {
			throw new NoSuchRoleException(
				"Unable to find account role with external reference code " +
					accountRoleExternalReferenceCode);
		}

		postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
			externalReferenceCode, accountRole.getAccountRoleId(),
			emailAddress);
	}

	@Override
	public void
			postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String accountRoleExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		com.liferay.account.model.AccountRole accountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				accountRoleExternalReferenceCode,
				contextCompany.getCompanyId());

		if (accountRole == null) {
			throw new NoSuchRoleException(
				"Unable to find account role with external reference code " +
					accountRoleExternalReferenceCode);
		}

		postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
			accountExternalReferenceCode, accountRole.getAccountRoleId(),
			externalReferenceCode);
	}

	@Override
	public void
			postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress(
				String externalReferenceCode, Long accountRoleId,
				String emailAddress)
		throws Exception {

		User user = _userLocalService.getUserByEmailAddress(
			contextCompany.getCompanyId(), emailAddress);

		postAccountAccountRoleUserAccountAssociation(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			accountRoleId, user.getUserId());
	}

	@Override
	public void
			postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode, Long accountRoleId,
				String externalReferenceCode)
		throws Exception {

		postAccountAccountRoleUserAccountAssociation(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, accountExternalReferenceCode),
			accountRoleId,
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	private AccountRole _toAccountRole(
			com.liferay.account.model.AccountRole serviceBuilderAccountRole)
		throws Exception {

		Role role = serviceBuilderAccountRole.getRole();

		return new AccountRole() {
			{
				setAccountId(serviceBuilderAccountRole::getAccountEntryId);
				setDescription(
					() -> role.getDescription(
						contextAcceptLanguage.getPreferredLocale()));
				setDisplayName(
					() -> role.getTitle(
						contextAcceptLanguage.getPreferredLocale()));
				setExternalReferenceCode(
					serviceBuilderAccountRole::getExternalReferenceCode);
				setId(serviceBuilderAccountRole::getAccountRoleId);
				setName(serviceBuilderAccountRole::getRoleName);
				setRoleId(serviceBuilderAccountRole::getRoleId);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	private final EntityModel _entityModel = new AccountRoleEntityModel();

	@Reference
	private UserLocalService _userLocalService;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

}