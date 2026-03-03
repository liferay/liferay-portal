/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.DuplicateAccountGroupRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountGroupRelService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountContactInformation;
import com.liferay.headless.admin.user.dto.v1_0.AccountGroupBrief;
import com.liferay.headless.admin.user.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PostalAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderEmailAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderPhoneUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderWebsiteUtil;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.AccountEntityModel;
import com.liferay.headless.admin.user.internal.util.v1_0.ResourcePermissionUtil;
import com.liferay.headless.admin.user.resource.v1_0.AccountResource;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ContactService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Drew Brokke
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account.properties",
	property = {
		"export.import.vulcan.batch.engine.task.item.delegate=true",
		"nested.field.support=true"
	},
	scope = ServiceScope.PROTOTYPE, service = AccountResource.class
)
public class AccountResourceImpl
	extends BaseAccountResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<Account> {

	@Override
	public void deleteAccount(Long accountId) throws Exception {
		_accountEntryService.deleteAccountEntry(accountId);
	}

	@Override
	public void deleteAccountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		deleteAccount(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public void deleteOrganizationAccounts(
			Long organizationId, Long[] accountIds)
		throws Exception {

		for (Long accountId : accountIds) {
			_accountEntryOrganizationRelLocalService.
				deleteAccountEntryOrganizationRel(accountId, organizationId);
		}
	}

	@Override
	public void deleteOrganizationAccountsByExternalReferenceCode(
			Long organizationId, String[] externalReferenceCodes)
		throws Exception {

		for (String externalReferenceCode : externalReferenceCodes) {
			_accountEntryOrganizationRelLocalService.
				deleteAccountEntryOrganizationRel(
					DTOConverterUtil.getModelPrimaryKey(
						_accountResourceDTOConverter, externalReferenceCode),
					organizationId);
		}
	}

	@Override
	public void deleteOrganizationByExternalReferenceCodeAccounts(
			String externalReferenceCode, Long[] accountIds)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteOrganizationAccounts(
			organization.getOrganizationId(), accountIds);
	}

	@Override
	public void
			deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				String organizationExternalReferenceCode,
				String[] externalReferenceCodes)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				organizationExternalReferenceCode,
				contextCompany.getCompanyId());

		deleteOrganizationAccountsByExternalReferenceCode(
			organization.getOrganizationId(), externalReferenceCodes);
	}

	@Override
	public Account getAccount(Long accountId) throws Exception {
		return _toAccount(_accountEntryService.getAccountEntry(accountId));
	}

	@Override
	public Account getAccountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return getAccount(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<Account> getAccountGroupAccountsPage(
			Long accountGroupId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		AccountGroup accountGroup = _accountGroupService.getAccountGroup(
			accountGroupId);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"accountGroupIds",
						String.valueOf(accountGroup.getAccountGroupId())),
					BooleanClauseOccur.MUST);
			},
			filter, AccountEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toAccount(
				Collections.emptyMap(),
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Page<Account> getAccountGroupByExternalReferenceCodeAccountsPage(
			String accountGroupExternalReferenceCode, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		AccountGroup accountGroup =
			_accountGroupService.getAccountGroupByExternalReferenceCode(
				accountGroupExternalReferenceCode,
				contextCompany.getCompanyId());

		return getAccountGroupAccountsPage(
			accountGroup.getAccountGroupId(), search, filter, pagination,
			sorts);
	}

	@Override
	public Page<Account> getAccountsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				addAction(
					AccountActionKeys.ADD_ACCOUNT_ENTRY, "postAccount",
					PortletKeys.PORTAL, 0L)
			).put(
				"create-by-external-reference-code",
				addAction(
					AccountActionKeys.ADD_ACCOUNT_ENTRY,
					"putAccountByExternalReferenceCode", PortletKeys.PORTAL, 0L)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, 0L, "getAccountsPage",
					_accountEntryModelResourcePermission)
			).build(),
			booleanQuery -> {
			},
			filter, AccountEntry.class.getName(), search, pagination,
			queryConfig -> {
			},
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> {
				long accountEntryId = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				return _toAccount(
					_accountEntryLocalService.getAccountEntry(accountEntryId));
			});
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return new AccountEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(AccountEntry.class.getName()),
				contextCompany.getCompanyId(), _expandoColumnLocalService,
				_expandoTableLocalService));
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getKey() {
				return AccountResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "accounts";
			}

			@Override
			public String getModelClassName() {
				return AccountEntry.class.getName();
			}

			@Override
			public List<String> getNestedFields() {
				return List.of(
					"accountGroupBriefs", "accountRoles", "creator", "keywords",
					"logoBase64", "postalAddresses", "taxonomyCategoryBriefs");
			}

			@Override
			public String getPortletId() {
				return AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@NestedField(
		parentClass = Organization.class, value = "organizationAccounts"
	)
	@Override
	public Page<Account> getOrganizationAccountsPage(
			@NestedFieldId(value = "id") String organizationId, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getOrganizationAccountsPage(
			Collections.emptyMap(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"organizationIds",
						String.valueOf(
							DTOConverterUtil.getModelPrimaryKey(
								_organizationResourceDTOConverter,
								organizationId))),
					BooleanClauseOccur.MUST);
			},
			search, filter, pagination, sorts);
	}

	@Override
	public Page<Account> getOrganizationByExternalReferenceCodeAccountsPage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return getOrganizationAccountsPage(
			String.valueOf(organization.getOrganizationId()), search, filter,
			pagination, sorts);
	}

	@Override
	public Page<Account>
			getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
				String organizationExternalReferenceCode, String search,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				organizationExternalReferenceCode,
				contextCompany.getCompanyId());

		return getOrganizationAccountsPage(
			String.valueOf(organization.getOrganizationId()), search, filter,
			pagination, sorts);
	}

	@Override
	public Account patchAccount(Long accountId, Account account)
		throws Exception {

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountId);

		accountEntry = _accountEntryService.updateAccountEntry(
			GetterUtil.getString(
				account.getExternalReferenceCode(),
				accountEntry.getExternalReferenceCode()),
			accountId,
			_getParentAccountId(
				account,
				GetterUtil.getLong(
					accountEntry.getParentAccountEntryId(),
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT)),
			GetterUtil.getString(account.getName(), accountEntry.getName()),
			GetterUtil.getString(
				account.getDescription(), accountEntry.getDescription()),
			_isDeleteLogo(account, accountEntry),
			GetterUtil.getStringValues(
				account.getDomains(), accountEntry.getDomainsArray()),
			accountEntry.getEmailAddress(),
			_getLogoBytes(account, accountEntry, true),
			GetterUtil.getString(
				account.getTaxId(), accountEntry.getTaxIdNumber()),
			GetterUtil.getInteger(
				account.getStatus(),
				GetterUtil.getInteger(
					accountEntry.getStatus(),
					WorkflowConstants.STATUS_APPROVED)),
			_createServiceContext(account));

		accountEntry = _updateNestedResources(account, accountEntry, accountId);

		return _toAccount(accountEntry);
	}

	@Override
	public Account patchAccountByExternalReferenceCode(
			String externalReferenceCode, Account account)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return patchAccount(accountEntry.getAccountEntryId(), account);
	}

	@Override
	public void patchOrganizationMoveAccounts(
			Long sourceOrganizationId, Long targetOrganizationId,
			Long[] accountIds)
		throws Exception {

		deleteOrganizationAccounts(sourceOrganizationId, accountIds);
		postOrganizationAccounts(targetOrganizationId, accountIds);
	}

	@Override
	public void patchOrganizationMoveAccountsByExternalReferenceCode(
			Long sourceOrganizationId, Long targetOrganizationId,
			String[] externalReferenceCodes)
		throws Exception {

		deleteOrganizationAccountsByExternalReferenceCode(
			sourceOrganizationId, externalReferenceCodes);
		postOrganizationAccountsByExternalReferenceCode(
			targetOrganizationId, externalReferenceCodes);
	}

	@Override
	public Account postAccount(Account account) throws Exception {
		AccountEntry accountEntry = _accountEntryService.addAccountEntry(
			account.getExternalReferenceCode(), contextUser.getUserId(),
			_getParentAccountId(
				account, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT),
			account.getName(), account.getDescription(), _getDomains(account),
			null, _getLogoBytes(account, null, false), account.getTaxId(),
			_getType(account), _getStatus(account),
			_createServiceContext(account));

		return _toAccount(
			_updateNestedResources(
				account, accountEntry, accountEntry.getAccountEntryId()));
	}

	@Override
	public void postOrganizationAccounts(Long organizationId, Long[] accountIds)
		throws Exception {

		for (Long accountId : accountIds) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(accountId, organizationId);
		}
	}

	@Override
	public void postOrganizationAccountsByExternalReferenceCode(
			Long organizationId, String[] externalReferenceCodes)
		throws Exception {

		for (String externalReferenceCode : externalReferenceCodes) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					DTOConverterUtil.getModelPrimaryKey(
						_accountResourceDTOConverter, externalReferenceCode),
					organizationId);
		}
	}

	@Override
	public void postOrganizationByExternalReferenceCodeAccounts(
			String externalReferenceCode, Long[] accountIds)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		postOrganizationAccounts(organization.getOrganizationId(), accountIds);
	}

	@Override
	public void
			postOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				String organizationExternalReferenceCode,
				String[] externalReferenceCodes)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				organizationExternalReferenceCode,
				contextCompany.getCompanyId());

		postOrganizationAccountsByExternalReferenceCode(
			organization.getOrganizationId(), externalReferenceCodes);
	}

	@Override
	public Account putAccount(Long accountId, Account account)
		throws Exception {

		AccountEntry accountEntry = _accountEntryService.fetchAccountEntry(
			accountId);

		if (accountEntry == null) {
			return postAccount(account);
		}

		accountEntry = _accountEntryService.updateAccountEntry(
			account.getExternalReferenceCode(), accountId,
			_getParentAccountId(
				account,
				GetterUtil.getLong(
					accountEntry.getParentAccountEntryId(),
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT)),
			account.getName(), account.getDescription(),
			_isDeleteLogo(account, null), _getDomains(account),
			accountEntry.getEmailAddress(),
			_getLogoBytes(account, accountEntry, false), account.getTaxId(),
			_getStatus(account), _createServiceContext(account));

		accountEntry = _updateNestedResources(account, accountEntry, accountId);

		return _toAccount(accountEntry);
	}

	@Override
	public Account putAccountByExternalReferenceCode(
			String externalReferenceCode, Account account)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			return putAccount(0L, account);
		}

		return putAccount(accountEntry.getAccountEntryId(), account);
	}

	private AccountEntry _addAccountGroupRel(
			AccountEntry accountEntry, AccountGroupBrief accountGroupBrief)
		throws Exception {

		String externalReferenceCode =
			accountGroupBrief.getExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return accountEntry;
		}

		try {
			AccountGroup accountGroup =
				_accountGroupService.getOrAddEmptyAccountGroup(
					externalReferenceCode, accountGroupBrief.getName());

			_accountGroupRelService.addAccountGroupRel(
				accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId());
		}
		catch (DuplicateAccountGroupRelException
					duplicateAccountGroupRelException) {

			if (_log.isDebugEnabled()) {
				_log.debug(duplicateAccountGroupRelException);
			}
		}

		return accountEntry;
	}

	private AccountEntry _addAccountRole(
			AccountEntry accountEntry, AccountRole accountRole)
		throws Exception {

		String externalReferenceCode = accountRole.getExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return accountEntry;
		}

		_accountRoleLocalService.getOrAddEmptyAccountRole(
			externalReferenceCode, contextCompany.getCompanyId(),
			contextUser.getUserId(), accountEntry.getAccountEntryId(),
			accountRole.getName());

		return accountEntry;
	}

	private void _addAddresses(
			Account account, Long accountId, AccountEntry accountEntry)
		throws Exception {

		PostalAddress[] postalAddresses = account.getPostalAddresses();

		if (ArrayUtil.isEmpty(postalAddresses)) {
			return;
		}

		Set<Long> addressIds = new HashSet<>(
			transform(
				accountEntry.getListTypeAddresses(
					PostalAddressUtil.getAccountEntryAddressListTypeIds(
						accountEntry.getCompanyId(), _listTypeLocalService)),
				Address::getAddressId));

		for (PostalAddress postalAddress :
				ListUtil.filter(
					Arrays.asList(postalAddresses), Objects::nonNull)) {

			Address address = ServiceBuilderAddressUtil.toServiceBuilderAddress(
				contextCompany.getCompanyId(), postalAddress,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);

			if (address == null) {
				continue;
			}

			Address existingAddress = null;

			if (postalAddress.getId() != null) {
				existingAddress = _addressLocalService.fetchAddress(
					postalAddress.getId());
			}
			else if (postalAddress.getExternalReferenceCode() != null) {
				existingAddress =
					_addressLocalService.fetchAddressByExternalReferenceCode(
						postalAddress.getExternalReferenceCode(),
						contextCompany.getCompanyId());
			}

			if (existingAddress == null) {
				_addressLocalService.addAddress(
					address.getExternalReferenceCode(), contextUser.getUserId(),
					AccountEntry.class.getName(), accountId,
					address.getCountryId(), address.getListTypeId(),
					address.getRegionId(), address.getCity(),
					address.getDescription(), address.isMailing(),
					address.getName(), address.isPrimary(),
					address.getStreet1(), address.getStreet2(),
					address.getStreet3(), address.getSubtype(),
					address.getZip(), postalAddress.getPhoneNumber(),
					_createServiceContext(account));
			}
			else if (addressIds.contains(existingAddress.getAddressId())) {
				_addressLocalService.updateAddress(
					GetterUtil.getString(
						address.getExternalReferenceCode(),
						existingAddress.getExternalReferenceCode()),
					existingAddress.getAddressId(),
					(address.getCountryId() == 0) ?
						existingAddress.getCountryId() : address.getCountryId(),
					(address.getListTypeId() == 0) ?
						existingAddress.getListTypeId() :
							address.getListTypeId(),
					(address.getRegionId() == 0) ?
						existingAddress.getRegionId() : address.getRegionId(),
					GetterUtil.getString(
						address.getCity(), existingAddress.getCity()),
					existingAddress.getDescription(),
					existingAddress.isMailing(),
					GetterUtil.getString(
						address.getName(), existingAddress.getName()),
					GetterUtil.getBoolean(
						postalAddress.getPrimary(),
						existingAddress.isPrimary()),
					GetterUtil.getString(
						address.getStreet1(), existingAddress.getStreet1()),
					GetterUtil.getString(
						address.getStreet2(), existingAddress.getStreet2()),
					GetterUtil.getString(
						address.getStreet3(), existingAddress.getStreet3()),
					GetterUtil.getString(
						address.getSubtype(), existingAddress.getSubtype()),
					GetterUtil.getString(
						address.getZip(), existingAddress.getZip()),
					GetterUtil.getString(
						postalAddress.getPhoneNumber(),
						existingAddress.getPhoneNumber()));
			}
		}
	}

	private void _addOrUpdateContact(
			long contactId, long userId, String className, long classPK,
			String emailAddress, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String smsSn, String facebookSn, String jabberSn, String skypeSn,
			String twitterSn, String jobTitle)
		throws Exception {

		if (contactId == 0) {
			_contactService.addContact(
				userId, className, classPK, emailAddress, firstName, middleName,
				lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, smsSn, facebookSn,
				jabberSn, skypeSn, twitterSn, jobTitle);
		}
		else {
			_contactService.updateContact(
				contactId, emailAddress, firstName, middleName, lastName,
				prefixListTypeId, suffixListTypeId, male, birthdayMonth,
				birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
				twitterSn, jobTitle);
		}
	}

	private ServiceContext _createServiceContext(Account account)
		throws Exception {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			contextCompany.getGroupId(), contextHttpServletRequest, null
		).assetCategoryIds(
			_getAssetCategoryIds(account)
		).assetTagNames(
			account.getKeywords()
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				AccountEntry.class.getName(), contextCompany.getCompanyId(),
				account.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private Long[] _getAssetCategoryIds(Account account) {
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			account.getTaxonomyCategoryBriefs();

		if (ArrayUtil.isEmpty(taxonomyCategoryBriefs)) {
			return null;
		}

		return transform(
			taxonomyCategoryBriefs,
			taxonomyCategoryBrief -> {
				TaxonomyCategoryReference taxonomyCategoryReference =
					taxonomyCategoryBrief.getTaxonomyCategoryReference();

				String externalReferenceCode =
					taxonomyCategoryReference.getExternalReferenceCode();

				if (Validator.isNull(externalReferenceCode) ||
					Validator.isNull(taxonomyCategoryReference.getSiteKey())) {

					return null;
				}

				Group group = _groupLocalService.fetchGroup(
					contextCompany.getCompanyId(),
					taxonomyCategoryReference.getSiteKey());

				if (group == null) {
					return null;
				}

				AssetCategory assetCategory =
					_assetCategoryService.getOrAddEmptyCategoryWithAncestors(
						externalReferenceCode, contextUser.getUserId(),
						group.getGroupId(),
						taxonomyCategoryBrief.
							getParentTaxonomyCategoryExternalReferenceCode(),
						taxonomyCategoryBrief.
							getParentVocabularyExternalReferenceCode());

				return assetCategory.getCategoryId();
			},
			Long.class);
	}

	private List<Address> _getContactAddresses(
			Account account, AccountEntry accountEntry)
		throws Exception {

		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if ((accountContactInformation == null) ||
			(accountContactInformation.getPostalAddresses() == null)) {

			if (accountEntry != null) {
				return accountEntry.getListTypeAddresses(
					PostalAddressUtil.getAccountEntryContactAddressListTypeIds(
						accountEntry.getCompanyId(), _listTypeLocalService));
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				accountContactInformation.getPostalAddresses(),
				_postalAddress ->
					ServiceBuilderAddressUtil.toServiceBuilderAddress(
						contextCompany.getCompanyId(), _postalAddress,
						AccountListTypeConstants.
							ACCOUNT_ENTRY_CONTACT_ADDRESS)),
			Objects::nonNull);
	}

	private long _getDefaultBillingAddressId(
			Account account, long accountEntryId, long defaultBillingAddressId)
		throws Exception {

		if (Validator.isNotNull(
				account.getDefaultBillingAddressExternalReferenceCode())) {

			Address address = _addressLocalService.getOrAddEmptyAddress(
				account.getDefaultBillingAddressExternalReferenceCode(),
				contextCompany.getCompanyId(), contextUser.getUserId(),
				AccountEntry.class.getName(), accountEntryId);

			return address.getAddressId();
		}

		long billingAddressId = GetterUtil.getLong(
			account.getDefaultBillingAddressId());

		if (billingAddressId != 0) {
			return billingAddressId;
		}

		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				account.getDefaultBillingAddressExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (address != null) {
			return address.getAddressId();
		}

		return defaultBillingAddressId;
	}

	private long _getDefaultShippingAddressId(
			Account account, long accountEntryId, long defaultShippingAddressId)
		throws Exception {

		if (Validator.isNotNull(
				account.getDefaultShippingAddressExternalReferenceCode())) {

			Address address = _addressLocalService.getOrAddEmptyAddress(
				account.getDefaultShippingAddressExternalReferenceCode(),
				contextCompany.getCompanyId(), contextUser.getUserId(),
				AccountEntry.class.getName(), accountEntryId);

			return address.getAddressId();
		}

		long shippingAddressId = GetterUtil.getLong(
			account.getDefaultShippingAddressId());

		if (shippingAddressId != 0) {
			return shippingAddressId;
		}

		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				account.getDefaultShippingAddressExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (address != null) {
			return address.getAddressId();
		}

		return defaultShippingAddressId;
	}

	private String[] _getDomains(Account account) {
		String[] domains = account.getDomains();

		if (domains == null) {
			return new String[0];
		}

		return domains;
	}

	private DTOConverterContext _getDTOConverterContext(long accountEntryId) {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.<String, Map<String, String>>put(
				"create-organization-accounts",
				addAction(
					AccountActionKeys.MANAGE_ORGANIZATIONS, accountEntryId,
					"postOrganizationAccounts",
					_accountEntryModelResourcePermission)
			).put(
				"create-organization-accounts-by-external-reference-code",
				addAction(
					AccountActionKeys.MANAGE_ORGANIZATIONS, accountEntryId,
					"postOrganizationAccountsByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).put(
				"delete",
				addAction(
					ActionKeys.DELETE, accountEntryId, "deleteAccount",
					_accountEntryModelResourcePermission)
			).put(
				"delete-by-external-reference-code",
				addAction(
					ActionKeys.DELETE, accountEntryId,
					"deleteAccountByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).put(
				"delete-organization-accounts",
				addAction(
					AccountActionKeys.MANAGE_ORGANIZATIONS, accountEntryId,
					"deleteOrganizationAccounts",
					_accountEntryModelResourcePermission)
			).put(
				"delete-organization-accounts-by-external-reference-code",
				addAction(
					AccountActionKeys.MANAGE_ORGANIZATIONS, accountEntryId,
					"deleteOrganizationAccountsByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, accountEntryId, "getAccount",
					_accountEntryModelResourcePermission)
			).put(
				"get-by-external-reference-code",
				addAction(
					ActionKeys.VIEW, accountEntryId,
					"getAccountByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).put(
				"move-organization-accounts",
				addAction(
					AccountActionKeys.MANAGE_ORGANIZATIONS, accountEntryId,
					"patchOrganizationMoveAccounts",
					_accountEntryModelResourcePermission)
			).put(
				"move-organization-accounts-by-external-reference-code",
				addAction(
					AccountActionKeys.MANAGE_ORGANIZATIONS, accountEntryId,
					"patchOrganizationMoveAccountsByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).put(
				"replace",
				addAction(
					ActionKeys.UPDATE, accountEntryId, "putAccount",
					_accountEntryModelResourcePermission)
			).put(
				"replace-by-external-reference-code",
				addAction(
					ActionKeys.UPDATE, accountEntryId,
					"putAccountByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).put(
				"update",
				addAction(
					ActionKeys.UPDATE, accountEntryId, "patchAccount",
					_accountEntryModelResourcePermission)
			).put(
				"update-by-external-reference-code",
				addAction(
					ActionKeys.UPDATE, accountEntryId,
					"patchAccountByExternalReferenceCode",
					_accountEntryModelResourcePermission)
			).build(),
			null, contextHttpServletRequest, accountEntryId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private List<EmailAddress> _getEmailAddresses(
			Account account, AccountEntry accountEntry)
		throws Exception {

		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if ((accountContactInformation == null) ||
			(accountContactInformation.getEmailAddresses() == null)) {

			if (accountEntry != null) {
				return accountEntry.getEmailAddresses();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				accountContactInformation.getEmailAddresses(),
				emailAddress ->
					ServiceBuilderEmailAddressUtil.toServiceBuilderEmailAddress(
						contextCompany.getCompanyId(), emailAddress,
						AccountListTypeConstants.ACCOUNT_ENTRY_EMAIL_ADDRESS)),
			Objects::nonNull);
	}

	private byte[] _getLogoBytes(
			Account account, AccountEntry accountEntry,
			boolean useAccountEntryDefault)
		throws Exception {

		String logoBase64 = account.getLogoBase64();

		if (Validator.isNotNull(logoBase64)) {
			return Base64.decode(logoBase64);
		}

		Long logoId = GetterUtil.getLong(account.getLogoId());

		if (logoId == 0) {
			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					contextCompany.getGroupId(),
					account.getLogoExternalReferenceCode());

			if (fileEntry != null) {
				logoId = fileEntry.getFileEntryId();
			}
			else if ((fileEntry == null) && (accountEntry != null) &&
					 useAccountEntryDefault) {

				logoId = accountEntry.getLogoId();
			}
		}

		if ((logoId <= 0) ||
			((accountEntry != null) && (accountEntry.getLogoId() == logoId))) {

			return null;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(logoId);

		return _file.getBytes(fileEntry.getContentStream());
	}

	private Page<Account> _getOrganizationAccountsPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer, filter,
			AccountEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toAccount(
				_accountEntryService.getAccountEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private long[] _getOrganizationIds(Account account) {
		String[] organizationExternalReferenceCodes =
			account.getOrganizationExternalReferenceCodes();
		Long[] organizationIds = account.getOrganizationIds();

		if ((organizationExternalReferenceCodes == null) &&
			(organizationIds == null)) {

			return null;
		}

		if (ArrayUtil.isNotEmpty(organizationExternalReferenceCodes)) {
			organizationIds = transformToArray(
				Arrays.asList(organizationExternalReferenceCodes),
				externalReferenceCode -> {
					com.liferay.portal.kernel.model.Organization organization =
						_organizationService.getOrAddEmptyOrganization(
							externalReferenceCode, StringPool.BLANK);

					return organization.getOrganizationId();
				},
				Long.class);

			return ArrayUtil.toArray(organizationIds);
		}

		if (ArrayUtil.isNotEmpty(organizationIds)) {
			return ArrayUtil.toArray(organizationIds);
		}

		return new long[0];
	}

	private long _getParentAccountId(
			Account account, long defaultParentAccountId)
		throws Exception {

		if (Validator.isNotNull(
				account.getParentAccountExternalReferenceCode())) {

			AccountEntry accountEntry =
				_accountEntryService.getOrAddEmptyAccountEntry(
					account.getParentAccountExternalReferenceCode(),
					account.getParentAccountExternalReferenceCode(),
					AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

			return accountEntry.getAccountEntryId();
		}

		Long parentAccountId = GetterUtil.getLong(account.getParentAccountId());

		if (parentAccountId != 0) {
			return parentAccountId;
		}

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				account.getParentAccountExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (accountEntry != null) {
			return accountEntry.getAccountEntryId();
		}

		return defaultParentAccountId;
	}

	private List<Phone> _getPhones(Account account, AccountEntry accountEntry)
		throws Exception {

		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if ((accountContactInformation == null) ||
			(accountContactInformation.getTelephones() == null)) {

			if (accountEntry != null) {
				return accountEntry.getPhones();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				accountContactInformation.getTelephones(),
				telephone -> ServiceBuilderPhoneUtil.toServiceBuilderPhone(
					contextCompany.getCompanyId(), telephone,
					AccountListTypeConstants.ACCOUNT_ENTRY_PHONE)),
			Objects::nonNull);
	}

	private int _getStatus(Account account) {
		Integer status = account.getStatus();

		if (status == null) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return status;
	}

	private String _getType(Account account) {
		String type = account.getTypeAsString();

		if (type == null) {
			return AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
		}

		return type;
	}

	private List<Website> _getWebsites(
			Account account, AccountEntry accountEntry)
		throws Exception {

		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if ((accountContactInformation == null) ||
			(accountContactInformation.getWebUrls() == null)) {

			if (accountEntry != null) {
				return accountEntry.getWebsites();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				accountContactInformation.getWebUrls(),
				webUrl -> ServiceBuilderWebsiteUtil.toServiceBuilderWebsite(
					contextCompany.getCompanyId(),
					AccountListTypeConstants.ACCOUNT_ENTRY_WEBSITE, webUrl)),
			Objects::nonNull);
	}

	private boolean _isDeleteLogo(Account account, AccountEntry accountEntry)
		throws Exception {

		Long logoId = GetterUtil.getLong(account.getLogoId());

		if (logoId == 0) {
			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					contextCompany.getGroupId(),
					account.getLogoExternalReferenceCode());

			if (fileEntry != null) {
				logoId = fileEntry.getFileEntryId();
			}
			else if ((fileEntry == null) && (accountEntry != null)) {
				logoId = accountEntry.getLogoId();
			}
		}

		if ((logoId == null) || (logoId == 0)) {
			return true;
		}

		return false;
	}

	private Account _toAccount(AccountEntry accountEntry) throws Exception {
		return _accountResourceDTOConverter.toDTO(
			_getDTOConverterContext(accountEntry.getAccountEntryId()));
	}

	private Account _toAccount(
			Map<String, Map<String, String>> actions, long accountId)
		throws Exception {

		DTOConverterContext dtoConverterContext = _getDTOConverterContext(
			accountId);

		Map<String, Map<String, String>> actionsMap = new HashMap<>();

		if (!actions.isEmpty()) {
			actionsMap.putAll(actions);
		}

		actionsMap.putAll(dtoConverterContext.getActions());

		return _accountResourceDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), actionsMap,
				_dtoConverterRegistry, accountId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private AccountEntry _updateNestedResources(
			Account account, AccountEntry accountEntry, Long accountId)
		throws Exception {

		_addAddresses(account, accountId, accountEntry);

		accountEntry = _accountEntryLocalService.updateDefaultBillingAddressId(
			accountId,
			_getDefaultBillingAddressId(
				account, accountEntry.getAccountEntryId(),
				accountEntry.getDefaultBillingAddressId()));

		accountEntry = _accountEntryLocalService.updateDefaultShippingAddressId(
			accountId,
			_getDefaultShippingAddressId(
				account, accountEntry.getAccountEntryId(),
				accountEntry.getDefaultShippingAddressId()));

		long[] organizationIds = _getOrganizationIds(account);

		if (organizationIds != null) {
			_accountEntryOrganizationRelLocalService.
				setAccountEntryOrganizationRels(accountId, organizationIds);
		}

		UserAccount[] userAccounts = account.getAccountUserAccounts();

		if (userAccounts != null) {
			_accountEntryUserRelLocalService.setAccountEntryUserRels(
				accountId,
				transformToLongArray(
					Arrays.asList(userAccounts), UserAccount::getId));
		}

		AccountContactInformation accountContactInformation =
			account.getAccountContactInformation();

		if (accountContactInformation != null) {
			UsersAdminUtil.updateAddresses(
				AccountEntry.class.getName(), accountId,
				_getContactAddresses(account, accountEntry),
				AccountListTypeConstants.ACCOUNT_ENTRY_CONTACT_ADDRESS);
			UsersAdminUtil.updateEmailAddresses(
				AccountEntry.class.getName(), accountId,
				_getEmailAddresses(account, accountEntry));
			UsersAdminUtil.updatePhones(
				AccountEntry.class.getName(), accountId,
				_getPhones(account, accountEntry));
			UsersAdminUtil.updateWebsites(
				AccountEntry.class.getName(), accountId,
				_getWebsites(account, accountEntry));

			Contact contact = accountEntry.fetchContact();

			if (contact == null) {
				_addOrUpdateContact(
					0, contextUser.getUserId(), AccountEntry.class.getName(),
					accountEntry.getAccountEntryId(), null, null, null, null, 0,
					0, true, 0, 1, 1970,
					GetterUtil.getString(accountContactInformation.getSms()),
					GetterUtil.getString(
						accountContactInformation.getFacebook()),
					GetterUtil.getString(accountContactInformation.getJabber()),
					GetterUtil.getString(accountContactInformation.getSkype()),
					GetterUtil.getString(
						accountContactInformation.getTwitter()),
					null);
			}
			else {
				_addOrUpdateContact(
					contact.getContactId(), contact.getUserId(),
					contact.getClassName(), contact.getClassPK(),
					contact.getEmailAddress(), contact.getFirstName(),
					contact.getMiddleName(), contact.getLastName(),
					contact.getPrefixListTypeId(),
					contact.getSuffixListTypeId(), contact.isMale(), 0, 1, 1970,
					GetterUtil.getString(
						accountContactInformation.getSms(), contact.getSmsSn()),
					GetterUtil.getString(
						accountContactInformation.getFacebook(),
						contact.getFacebookSn()),
					GetterUtil.getString(
						accountContactInformation.getJabber(),
						contact.getJabberSn()),
					GetterUtil.getString(
						accountContactInformation.getSkype(),
						contact.getSkypeSn()),
					GetterUtil.getString(
						accountContactInformation.getTwitter(),
						contact.getTwitterSn()),
					contact.getJobTitle());
			}
		}

		AccountGroupBrief[] accountGroupBriefs =
			account.getAccountGroupBriefs();

		if (ArrayUtil.isNotEmpty(accountGroupBriefs)) {
			for (AccountGroupBrief accountGroupBrief : accountGroupBriefs) {
				accountEntry = _addAccountGroupRel(
					accountEntry, accountGroupBrief);
			}
		}

		AccountRole[] accountRoles = account.getAccountRoles();

		if (ArrayUtil.isNotEmpty(accountRoles)) {
			for (AccountRole accountRole : accountRoles) {
				accountEntry = _addAccountRole(accountEntry, accountRole);
			}
		}

		return ResourcePermissionUtil.setResourcePermissions(
			accountEntry, accountEntry.getCompanyId(), account.getPermissions(),
			_resourcePermissionLocalService, _roleService,
			_roleTypeContributorProvider);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountResourceImpl.class);

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
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountGroupRelService _accountGroupRelService;

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private ContactService _contactService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<com.liferay.portal.kernel.model.Organization, Organization>
			_organizationResourceDTOConverter;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

}