/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.exception.DuplicateAccountEntryOrganizationRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.HoursAvailable;
import com.liferay.headless.admin.user.dto.v1_0.Location;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.headless.admin.user.dto.v1_0.OrganizationContactInformation;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.dto.v1_0.Service;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderCountryUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderEmailAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderListTypeUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderPhoneUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderRegionUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderWebsiteUtil;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.OrganizationEntityModel;
import com.liferay.headless.admin.user.internal.util.v1_0.ResourcePermissionUtil;
import com.liferay.headless.admin.user.resource.v1_0.OrganizationResource;
import com.liferay.headless.admin.user.resource.v1_0.RoleResource;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrgLaborLocalService;
import com.liferay.portal.kernel.service.OrgLaborService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.custom.field.CustomField;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/organization.properties",
	property = {
		"export.import.vulcan.batch.engine.task.item.delegate=true",
		"nested.field.support=true"
	},
	scope = ServiceScope.PROTOTYPE, service = OrganizationResource.class
)
public class OrganizationResourceImpl
	extends BaseOrganizationResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<Organization> {

	@Override
	public void deleteAccountByExternalReferenceCodeOrganization(
			String externalReferenceCode, String organizationId)
		throws Exception {

		deleteAccountOrganization(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			organizationId);
	}

	@Override
	public void deleteAccountOrganization(Long accountId, String organizationId)
		throws Exception {

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountId, GetterUtil.getLong(organizationId));
	}

	@Override
	public void deleteOrganization(String organizationId) throws Exception {
		_organizationService.deleteOrganization(
			_getServiceBuilderOrganizationId(organizationId));
	}

	@Override
	public void deleteOrganizationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		_organizationService.deleteOrganization(
			DTOConverterUtil.getModelPrimaryKey(
				_organizationResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public void
			deleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
				String externalReferenceCode, String emailAddress)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteUserAccountByEmailAddress(
			String.valueOf(organization.getOrganizationId()), emailAddress);
	}

	@Override
	public void
			deleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
				String externalReferenceCode, String[] emailAddresses)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deleteUserAccountsByEmailAddress(
			String.valueOf(organization.getOrganizationId()), emailAddresses);
	}

	@Override
	public void deleteUserAccountByEmailAddress(
			String organizationId, String emailAddress)
		throws Exception {

		_organizationService.deleteUserOrganizationByEmailAddress(
			emailAddress, _getServiceBuilderOrganizationId(organizationId));
	}

	@Override
	public void deleteUserAccountsByEmailAddress(
			String organizationId, String[] emailAddresses)
		throws Exception {

		for (String emailAddress : emailAddresses) {
			deleteUserAccountByEmailAddress(organizationId, emailAddress);
		}
	}

	@Override
	public Organization getAccountByExternalReferenceCodeOrganization(
			String externalReferenceCode, String organizationId)
		throws Exception {

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_accountEntryOrganizationRelService.getAccountEntryOrganizationRel(
				accountEntry.getAccountEntryId(), Long.valueOf(organizationId));

		return _toOrganization(
			String.valueOf(accountEntryOrganizationRel.getOrganizationId()));
	}

	@Override
	public Page<Organization>
			getAccountByExternalReferenceCodeOrganizationsPage(
				String externalReferenceCode, String search, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		return getAccountOrganizationsPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			search, filter, pagination, sorts);
	}

	@Override
	public Organization getAccountOrganization(
			Long accountId, String organizationId)
		throws Exception {

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountId);

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_accountEntryOrganizationRelService.getAccountEntryOrganizationRel(
				accountEntry.getAccountEntryId(), Long.valueOf(organizationId));

		return _toOrganization(
			String.valueOf(accountEntryOrganizationRel.getOrganizationId()));
	}

	@Override
	public Page<Organization> getAccountOrganizationsPage(
			Long accountId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"accountEntryIds", String.valueOf(accountId)),
					BooleanClauseOccur.MUST);
			},
			filter,
			com.liferay.portal.kernel.model.Organization.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toOrganization(
				GetterUtil.getString(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getKey() {
				return OrganizationResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "organizations";
			}

			@Override
			public String getModelClassName() {
				return com.liferay.portal.kernel.model.Organization.class.
					getName();
			}

			@Override
			public List<String> getNestedFields() {
				return List.of(
					"accountBriefs", "creator", "imageBase64", "roleBriefs",
					"taxonomyCategoryBriefs", "userAccountBriefs");
			}

			@Override
			public Map<String, Serializable> getParameters(
				PortletDataContext portletDataContext) {

				return HashMapBuilder.<String, Serializable>put(
					"flatten", "true"
				).build();
			}

			@Override
			public String getPortletId() {
				return UsersAdminPortletKeys.ORGANIZATIONS_ADMIN;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

		};
	}

	@Override
	public Organization getOrganization(String organizationId)
		throws Exception {

		return _toOrganization(organizationId);
	}

	@Override
	public Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationService.getOrganizationByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return _organizationResourceDTOConverter.toDTO(
			_getDTOConverterContext(
				String.valueOf(serviceBuilderOrganization.getOrganizationId())),
			serviceBuilderOrganization);
	}

	@Override
	public Page<Organization>
			getOrganizationByExternalReferenceCodeChildOrganizationsPage(
				String externalReferenceCode, Boolean flatten, String search,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return getOrganizationChildOrganizationsPage(
			String.valueOf(organization.getOrganizationId()), flatten, search,
			filter, pagination, sorts);
	}

	@NestedField(parentClass = Organization.class, value = "childOrganizations")
	@Override
	public Page<Organization> getOrganizationChildOrganizationsPage(
			@NestedFieldId(value = "id") String organizationId, Boolean flatten,
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getOrganizationsPage(
			HashMapBuilder.put(
				"get",
				addAction(
					"VIEW", "getOrganizationChildOrganizationsPage",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					_getServiceBuilderOrganizationId(organizationId))
			).build(),
			organizationId, flatten, filter, search, pagination, sorts);
	}

	@Override
	public Page<Organization> getOrganizationOrganizationsPage(
			String parentOrganizationId, Boolean flatten, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getOrganizationsPage(
			HashMapBuilder.put(
				"get",
				addAction(
					"VIEW", "getOrganizationOrganizationsPage",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					_getServiceBuilderOrganizationId(parentOrganizationId))
			).build(),
			parentOrganizationId, flatten, filter, search, pagination, sorts);
	}

	@Override
	public Page<Organization> getOrganizationsPage(
			Boolean flatten, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getOrganizationsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					"ADD_ORGANIZATION", "postOrganization",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					0L)
			).put(
				"get",
				addAction(
					"VIEW", "getOrganizationsPage",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					0L)
			).build(),
			null, flatten, filter, search, pagination, sorts);
	}

	@Override
	public Organization patchOrganization(
			String organizationId, Organization organization)
		throws Exception {

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationResourceDTOConverter.getObject(organizationId);

		long countryId = _getCountryId(
			serviceBuilderOrganization.getCountryId(), organization);
		Group group = serviceBuilderOrganization.getGroup();

		serviceBuilderOrganization = _organizationService.updateOrganization(
			GetterUtil.get(
				organization.getExternalReferenceCode(),
				serviceBuilderOrganization.getExternalReferenceCode()),
			serviceBuilderOrganization.getOrganizationId(),
			_getParentOrganizationId(
				serviceBuilderOrganization.getParentOrganizationId(),
				organization),
			GetterUtil.get(
				organization.getName(), serviceBuilderOrganization.getName()),
			serviceBuilderOrganization.getType(),
			_getRegionId(
				countryId, serviceBuilderOrganization.getRegionId(),
				organization),
			countryId, serviceBuilderOrganization.getStatusListTypeId(),
			GetterUtil.get(
				organization.getComment(),
				serviceBuilderOrganization.getComments()),
			_hasLogo(organization, serviceBuilderOrganization),
			_getLogoBytes(organization, serviceBuilderOrganization, true),
			group.isSite(),
			_getAddresses(organization, serviceBuilderOrganization),
			_getEmailAddresses(organization, serviceBuilderOrganization),
			_getOrgLabors(organization, serviceBuilderOrganization),
			_getPhones(organization, serviceBuilderOrganization),
			_getWebsites(organization, serviceBuilderOrganization),
			_createServiceContext(organization));

		return _organizationResourceDTOConverter.toDTO(
			_getDTOConverterContext(organizationId),
			_updateNestedResources(organization, serviceBuilderOrganization));
	}

	@Override
	public Organization patchOrganizationByExternalReferenceCode(
			String externalReferenceCode, Organization organization)
		throws Exception {

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationService.getOrganizationByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return patchOrganization(
			String.valueOf(serviceBuilderOrganization.getOrganizationId()),
			organization);
	}

	@Override
	public void postAccountByExternalReferenceCodeOrganization(
			String externalReferenceCode, String organizationId)
		throws Exception {

		postAccountOrganization(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			organizationId);
	}

	@Override
	public void postAccountOrganization(Long accountId, String organizationId)
		throws Exception {

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountId, GetterUtil.getLong(organizationId));
	}

	@Override
	public Organization postOrganization(Organization organization)
		throws Exception {

		long countryId = _getCountryId(0, organization);

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization = _organizationService.addOrganization(
				organization.getExternalReferenceCode(),
				_getParentOrganizationId(
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
					organization),
				organization.getName(), OrganizationConstants.TYPE_ORGANIZATION,
				_getRegionId(countryId, 0, organization), countryId,
				_listTypeLocalService.getListTypeId(
					contextCompany.getCompanyId(),
					ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
					ListTypeConstants.ORGANIZATION_STATUS),
				organization.getComment(), false,
				_getAddresses(organization, null),
				_getEmailAddresses(organization, null),
				_getOrgLabors(organization, null),
				_getPhones(organization, null),
				_getWebsites(organization, null),
				_createServiceContext(organization));

		byte[] logoBytes = _getLogoBytes(organization, null, false);

		if (ArrayUtil.isNotEmpty(logoBytes)) {
			serviceBuilderOrganization = _organizationService.updateLogo(
				serviceBuilderOrganization.getOrganizationId(), logoBytes);
		}

		return _organizationResourceDTOConverter.toDTO(
			_getDTOConverterContext(
				String.valueOf(serviceBuilderOrganization.getOrganizationId())),
			_updateNestedResources(organization, serviceBuilderOrganization));
	}

	@Override
	public UserAccount
			postOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
				String externalReferenceCode, String emailAddress)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return postUserAccountByEmailAddress(
			String.valueOf(organization.getOrganizationId()), emailAddress);
	}

	@Override
	public Page<UserAccount>
			postOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
				String externalReferenceCode, String organizationRoleIds,
				String[] emailAddresses)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return postUserAccountsByEmailAddress(
			String.valueOf(organization.getOrganizationId()),
			organizationRoleIds, emailAddresses);
	}

	@Override
	public UserAccount postUserAccountByEmailAddress(
			String organizationId, String emailAddress)
		throws Exception {

		User user = _organizationService.addOrganizationUserByEmailAddress(
			emailAddress, _getServiceBuilderOrganizationId(organizationId),
			new ServiceContext() {
				{
					setCompanyId(contextCompany.getCompanyId());
					setLanguageId(
						contextAcceptLanguage.getPreferredLanguageId());
					setUserId(contextUser.getUserId());
				}
			});

		return _userResourceDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, user.getUserId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			user);
	}

	@Override
	public Page<UserAccount> postUserAccountsByEmailAddress(
			String organizationId, String organizationRoleIds,
			String[] emailAddresses)
		throws Exception {

		List<UserAccount> userAccounts = transformToList(
			emailAddresses,
			emailAddress -> postUserAccountByEmailAddress(
				organizationId, emailAddress));

		if (Validator.isNull(organizationRoleIds)) {
			return Page.of(userAccounts);
		}

		String[] organizationRoleIdsArray = StringUtil.split(
			organizationRoleIds, CharPool.COMMA);

		for (UserAccount userAccount : userAccounts) {
			for (String organizationRoleId : organizationRoleIdsArray) {
				_roleResource.postOrganizationRoleUserAccountAssociation(
					GetterUtil.getLong(organizationRoleId), userAccount.getId(),
					GetterUtil.getLong(organizationId));
			}
		}

		return Page.of(
			transform(
				userAccounts,
				userAccount -> _userResourceDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(), null,
						_dtoConverterRegistry, userAccount.getId(),
						contextAcceptLanguage.getPreferredLocale(),
						contextUriInfo, contextUser),
					_userService.getUserByEmailAddress(
						contextCompany.getCompanyId(),
						userAccount.getEmailAddress()))));
	}

	@Override
	public Organization putOrganization(
			String organizationId, Organization organization)
		throws Exception {

		if (GetterUtil.getLong(organizationId) <= 0) {
			return postOrganization(organization);
		}

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationResourceDTOConverter.getObject(organizationId);
		long countryId = _getCountryId(0, organization);
		Group group = serviceBuilderOrganization.getGroup();

		serviceBuilderOrganization = _organizationService.updateOrganization(
			organization.getExternalReferenceCode(),
			serviceBuilderOrganization.getOrganizationId(),
			_getParentOrganizationId(
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				organization),
			organization.getName(), serviceBuilderOrganization.getType(),
			_getRegionId(countryId, 0, organization), countryId,
			serviceBuilderOrganization.getStatusListTypeId(),
			organization.getComment(), _hasLogo(organization, null),
			_getLogoBytes(organization, serviceBuilderOrganization, false),
			group.isSite(), _getAddresses(organization, null),
			_getEmailAddresses(organization, null),
			_getOrgLabors(organization, null), _getPhones(organization, null),
			_getWebsites(organization, null),
			_createServiceContext(organization));

		return _organizationResourceDTOConverter.toDTO(
			_getDTOConverterContext(organizationId),
			_updateNestedResources(organization, serviceBuilderOrganization));
	}

	@Override
	public Organization putOrganizationByExternalReferenceCode(
			String externalReferenceCode, Organization organization)
		throws Exception {

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationLocalService.
					fetchOrganizationByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderOrganization == null) {
			if (Validator.isNull(organization.getExternalReferenceCode())) {
				organization.setExternalReferenceCode(
					() -> externalReferenceCode);
			}

			return postOrganization(organization);
		}

		return putOrganization(
			String.valueOf(serviceBuilderOrganization.getOrganizationId()),
			organization);
	}

	@Override
	protected void preparePatch(
		Organization organization, Organization existingOrganization) {

		OrganizationContactInformation organizationContactInformation =
			organization.getOrganizationContactInformation();

		if (organizationContactInformation != null) {
			OrganizationContactInformation
				existingOrganizationContactInformation =
					existingOrganization.getOrganizationContactInformation();

			if (organizationContactInformation.getEmailAddresses() != null) {
				existingOrganizationContactInformation.setEmailAddresses(
					organizationContactInformation::getEmailAddresses);
			}

			if (organizationContactInformation.getPostalAddresses() != null) {
				existingOrganizationContactInformation.setPostalAddresses(
					organizationContactInformation::getPostalAddresses);
			}

			if (organizationContactInformation.getTelephones() != null) {
				existingOrganizationContactInformation.setTelephones(
					organizationContactInformation::getTelephones);
			}

			if (organizationContactInformation.getWebUrls() != null) {
				existingOrganizationContactInformation.setWebUrls(
					organizationContactInformation::getWebUrls);
			}
		}

		_patchCustomFields(
			organization.getCustomFields(), existingOrganization);

		Organization parentOrganization = organization.getParentOrganization();

		if (parentOrganization != null) {
			try {
				existingOrganization.setParentOrganization(
					() -> _toOrganization(parentOrganization.getId()));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		if (organization.getServices() != null) {
			existingOrganization.setServices(organization::getServices);
		}
	}

	private com.liferay.portal.kernel.model.Organization
			_addAccountEntryOrganizationRel(
				AccountBrief accountBrief,
				com.liferay.portal.kernel.model.Organization organization)
		throws Exception {

		String externalReferenceCode = accountBrief.getExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return organization;
		}

		try {
			AccountEntry accountEntry =
				_accountEntryService.getOrAddEmptyAccountEntry(
					externalReferenceCode, accountBrief.getName(),
					accountBrief.getType());

			_accountEntryOrganizationRelService.addAccountEntryOrganizationRel(
				accountEntry.getAccountEntryId(),
				organization.getOrganizationId());
		}
		catch (DuplicateAccountEntryOrganizationRelException
					duplicateAccountEntryOrganizationRelException) {

			if (_log.isDebugEnabled()) {
				_log.debug(duplicateAccountEntryOrganizationRelException);
			}
		}

		return organization;
	}

	private com.liferay.portal.kernel.model.Organization _addGroupRole(
			com.liferay.portal.kernel.model.Organization organization,
			RoleBrief roleBrief)
		throws Exception {

		String externalReferenceCode = roleBrief.getExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return organization;
		}

		Role role = _roleService.getOrAddEmptyRole(
			externalReferenceCode, Role.class.getName(), 0, roleBrief.getName(),
			roleBrief.getRoleType());

		_roleLocalService.addGroupRole(
			organization.getGroupId(), role.getRoleId());

		return organization;
	}

	private ServiceContext _createServiceContext(Organization organization)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			contextHttpServletRequest);

		serviceContext.setAssetCategoryIds(_getAssetCategoryIds(organization));
		serviceContext.setAssetTagNames(organization.getKeywords());
		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setExpandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				com.liferay.portal.kernel.model.Organization.class.getName(),
				contextCompany.getCompanyId(), organization.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale()));
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private List<Address> _getAddresses(
		Organization organization,
		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization) {

		OrganizationContactInformation organizationContactInformation =
			organization.getOrganizationContactInformation();

		if (organizationContactInformation == null) {
			if (serviceBuilderOrganization != null) {
				return serviceBuilderOrganization.getAddresses();
			}

			return Collections.emptyList();
		}

		PostalAddress[] postalAddresses =
			organizationContactInformation.getPostalAddresses();

		if (postalAddresses == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				postalAddresses,
				_postalAddress ->
					ServiceBuilderAddressUtil.toServiceBuilderAddress(
						contextCompany.getCompanyId(), _postalAddress,
						ListTypeConstants.ORGANIZATION_ADDRESS)),
			Objects::nonNull);
	}

	private long[] _getAssetCategoryIds(Organization organization) {
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			organization.getTaxonomyCategoryBriefs();

		if (ArrayUtil.isEmpty(taxonomyCategoryBriefs)) {
			return null;
		}

		return ArrayUtil.toArray(
			transform(
				taxonomyCategoryBriefs,
				taxonomyCategoryBrief -> {
					TaxonomyCategoryReference taxonomyCategoryReference =
						taxonomyCategoryBrief.getTaxonomyCategoryReference();

					String externalReferenceCode =
						taxonomyCategoryReference.getExternalReferenceCode();

					if (Validator.isNull(externalReferenceCode) ||
						Validator.isNull(
							taxonomyCategoryReference.getSiteKey())) {

						return null;
					}

					Group group = _groupLocalService.fetchGroup(
						contextCompany.getCompanyId(),
						taxonomyCategoryReference.getSiteKey());

					if (group == null) {
						return null;
					}

					AssetCategory assetCategory =
						_assetCategoryService.
							getOrAddEmptyCategoryWithAncestors(
								externalReferenceCode, contextUser.getUserId(),
								group.getGroupId(),
								taxonomyCategoryBrief.
									getParentTaxonomyCategoryExternalReferenceCode(),
								taxonomyCategoryBrief.
									getParentVocabularyExternalReferenceCode());

					return assetCategory.getCategoryId();
				},
				Long.class));
	}

	private long _getCountryId(long defaultValue, Organization organization) {
		Location location = organization.getLocation();

		if (location == null) {
			return defaultValue;
		}

		return ServiceBuilderCountryUtil.toServiceBuilderCountryId(
			contextCompany.getCompanyId(), location.getAddressCountry());
	}

	private DefaultDTOConverterContext _getDTOConverterContext(
			String organizationId)
		throws Exception {

		Long serviceBuilderOrganizationId = _getServiceBuilderOrganizationId(
			organizationId);

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"delete",
				addAction(
					"DELETE", "deleteOrganization",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					serviceBuilderOrganizationId)
			).put(
				"get",
				addAction(
					"VIEW", "getOrganization",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					serviceBuilderOrganizationId)
			).put(
				"replace",
				addAction(
					"UPDATE", "putOrganization",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					serviceBuilderOrganizationId)
			).put(
				"update",
				addAction(
					"UPDATE", "patchOrganization",
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					serviceBuilderOrganizationId)
			).build(),
			null, organizationId, contextAcceptLanguage.getPreferredLocale(),
			contextUriInfo, contextUser);
	}

	private List<com.liferay.portal.kernel.model.EmailAddress>
			_getEmailAddresses(
				Organization organization,
				com.liferay.portal.kernel.model.Organization
					serviceBuilderOrganization)
		throws Exception {

		OrganizationContactInformation organizationContactInformation =
			organization.getOrganizationContactInformation();

		if (organizationContactInformation == null) {
			if (serviceBuilderOrganization != null) {
				return _emailAddressService.getEmailAddresses(
					serviceBuilderOrganization.getModelClassName(),
					serviceBuilderOrganization.getOrganizationId());
			}

			return Collections.emptyList();
		}

		EmailAddress[] emailAddresses =
			organizationContactInformation.getEmailAddresses();

		if (emailAddresses == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				emailAddresses,
				emailAddress ->
					ServiceBuilderEmailAddressUtil.toServiceBuilderEmailAddress(
						contextCompany.getCompanyId(), emailAddress,
						ListTypeConstants.ORGANIZATION_EMAIL_ADDRESS)),
			Objects::nonNull);
	}

	private byte[] _getLogoBytes(
			Organization organization,
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization,
			boolean useOrganizationDefault)
		throws Exception {

		String imageBase64 = organization.getImageBase64();

		if (Validator.isNotNull(imageBase64)) {
			return Base64.decode(imageBase64);
		}

		long imageId = GetterUtil.getLong(organization.getImageId());

		if (imageId == 0) {
			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					contextCompany.getGroupId(),
					organization.getImageExternalReferenceCode());

			if (fileEntry != null) {
				imageId = fileEntry.getFileEntryId();
			}
			else if ((serviceBuilderOrganization != null) &&
					 useOrganizationDefault) {

				imageId = serviceBuilderOrganization.getLogoId();
			}
		}

		if ((imageId <= 0) ||
			((serviceBuilderOrganization != null) &&
			 (serviceBuilderOrganization.getLogoId() == imageId))) {

			return null;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(imageId);

		return _file.getBytes(fileEntry.getContentStream());
	}

	private Page<Organization> _getOrganizationsPage(
			Map<String, Map<String, String>> actions,
			String parentOrganizationId, Boolean flatten, Filter filter,
			String keywords, Pagination pagination, Sort[] sorts)
		throws Exception {

		long serviceBuilderOrganizationId = _getServiceBuilderOrganizationId(
			parentOrganizationId);

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				if (GetterUtil.getBoolean(flatten)) {
					if (serviceBuilderOrganizationId != 0L) {
						booleanFilter.add(
							new QueryFilter(
								new WildcardQueryImpl(
									"treePath",
									"*" + parentOrganizationId + "*")));
						booleanFilter.add(
							new TermFilter(
								"organizationId",
								String.valueOf(parentOrganizationId)),
							BooleanClauseOccur.MUST_NOT);
					}
				}
				else {
					booleanFilter.add(
						new TermFilter(
							"parentOrganizationId",
							String.valueOf(serviceBuilderOrganizationId)),
						BooleanClauseOccur.MUST);
				}
			},
			filter,
			com.liferay.portal.kernel.model.Organization.class.getName(),
			keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toOrganization(
				GetterUtil.getString(document.get(Field.ENTRY_CLASS_PK))));
	}

	private List<OrgLabor> _getOrgLabors(
			Organization organization,
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization)
		throws Exception {

		Service[] services = organization.getServices();

		if (services == null) {
			if (serviceBuilderOrganization != null) {
				return _orgLaborService.getOrgLabors(
					serviceBuilderOrganization.getOrganizationId());
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(services, this::_toOrgLabor), Objects::nonNull);
	}

	private long _getParentOrganizationId(
			long defaultValue, Organization organization)
		throws Exception {

		Organization parentOrganization = organization.getParentOrganization();

		if (parentOrganization == null) {
			return defaultValue;
		}

		String externalReferenceCode =
			parentOrganization.getExternalReferenceCode();

		if (Validator.isBlank(externalReferenceCode)) {
			return Long.valueOf(parentOrganization.getId());
		}

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationLocalService.getOrAddEmptyOrganization(
					externalReferenceCode, contextUser.getCompanyId(),
					contextUser.getUserId(), parentOrganization.getName());

		return serviceBuilderOrganization.getOrganizationId();
	}

	private List<com.liferay.portal.kernel.model.Phone> _getPhones(
			Organization organization,
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization)
		throws Exception {

		OrganizationContactInformation organizationContactInformation =
			organization.getOrganizationContactInformation();

		if (organizationContactInformation == null) {
			if (serviceBuilderOrganization != null) {
				return _phoneService.getPhones(
					serviceBuilderOrganization.getModelClassName(),
					serviceBuilderOrganization.getOrganizationId());
			}

			return Collections.emptyList();
		}

		Phone[] telephones = organizationContactInformation.getTelephones();

		if (telephones == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				telephones,
				telephone -> ServiceBuilderPhoneUtil.toServiceBuilderPhone(
					contextCompany.getCompanyId(), telephone,
					ListTypeConstants.ORGANIZATION_PHONE)),
			Objects::nonNull);
	}

	private long _getRegionId(
		long countryId, long defaultValue, Organization organization) {

		Location location = organization.getLocation();

		if (location == null) {
			return defaultValue;
		}

		return ServiceBuilderRegionUtil.getServiceBuilderRegionId(
			location.getAddressRegion(), countryId);
	}

	private long _getServiceBuilderOrganizationId(String organizationId)
		throws Exception {

		if (organizationId == null) {
			return 0;
		}

		return DTOConverterUtil.getModelPrimaryKey(
			_organizationResourceDTOConverter, organizationId);
	}

	private List<Website> _getWebsites(
			Organization organization,
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization)
		throws Exception {

		OrganizationContactInformation organizationContactInformation =
			organization.getOrganizationContactInformation();

		if (organizationContactInformation == null) {
			if (serviceBuilderOrganization != null) {
				return _websiteService.getWebsites(
					serviceBuilderOrganization.getModelClassName(),
					serviceBuilderOrganization.getOrganizationId());
			}

			return Collections.emptyList();
		}

		WebUrl[] webUrls = organizationContactInformation.getWebUrls();

		if (webUrls == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				webUrls,
				webUrl -> ServiceBuilderWebsiteUtil.toServiceBuilderWebsite(
					contextCompany.getCompanyId(),
					ListTypeConstants.ORGANIZATION_WEBSITE, webUrl)),
			Objects::nonNull);
	}

	private boolean _hasLogo(
			Organization organization,
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization)
		throws Exception {

		long imageId = GetterUtil.getLong(organization.getImageId());

		if (imageId == 0) {
			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					contextCompany.getGroupId(),
					organization.getImageExternalReferenceCode());

			if (fileEntry != null) {
				imageId = fileEntry.getFileEntryId();
			}
			else if (serviceBuilderOrganization != null) {
				imageId = serviceBuilderOrganization.getLogoId();
			}
		}

		if (imageId == 0) {
			return false;
		}

		return true;
	}

	private void _patchCustomFields(
		CustomField[] customFields, Organization organization) {

		if (ArrayUtil.isEmpty(customFields)) {
			return;
		}

		CustomField[] existingCustomFields = organization.getCustomFields();

		for (CustomField customField : customFields) {
			for (int i = 0; i < existingCustomFields.length; i++) {
				CustomField existingCustomField = existingCustomFields[i];

				if (Objects.equals(
						customField.getName(), existingCustomField.getName())) {

					existingCustomFields[i] = customField;

					break;
				}
			}
		}
	}

	private Organization _toOrganization(String organizationId)
		throws Exception {

		if (Validator.isBlank(organizationId)) {
			return null;
		}

		return _organizationResourceDTOConverter.toDTO(
			_getDTOConverterContext(organizationId));
	}

	private OrgLabor _toOrgLabor(Service service) {
		long listTypeId = ServiceBuilderListTypeUtil.toServiceBuilderListTypeId(
			contextCompany.getCompanyId(), "administrative",
			service.getServiceType(), ListTypeConstants.ORGANIZATION_SERVICE);

		if (listTypeId == -1) {
			return null;
		}

		OrgLabor orgLabor = _orgLaborLocalService.createOrgLabor(0);

		orgLabor.setListTypeId(listTypeId);

		HoursAvailable[] hoursAvailableArray = service.getHoursAvailable();

		if (ArrayUtil.isEmpty(hoursAvailableArray)) {
			return null;
		}

		orgLabor.setSunOpen(-1);
		orgLabor.setSunClose(-1);
		orgLabor.setMonOpen(-1);
		orgLabor.setMonClose(-1);
		orgLabor.setTueOpen(-1);
		orgLabor.setTueClose(-1);
		orgLabor.setWedOpen(-1);
		orgLabor.setWedClose(-1);
		orgLabor.setThuOpen(-1);
		orgLabor.setThuClose(-1);
		orgLabor.setFriOpen(-1);
		orgLabor.setFriClose(-1);
		orgLabor.setSatOpen(-1);
		orgLabor.setSatClose(-1);

		for (HoursAvailable hoursAvailable : hoursAvailableArray) {
			String dayOfWeek = hoursAvailable.getDayOfWeek();

			if (Validator.isNull(dayOfWeek)) {
				continue;
			}

			dayOfWeek = StringUtil.toLowerCase(dayOfWeek);

			int opens = _toTime(hoursAvailable.getOpens());
			int closes = _toTime(hoursAvailable.getCloses());

			if (dayOfWeek.startsWith("sun")) {
				orgLabor.setSunOpen(opens);
				orgLabor.setSunClose(closes);
			}
			else if (dayOfWeek.startsWith("mon")) {
				orgLabor.setMonOpen(opens);
				orgLabor.setMonClose(closes);
			}
			else if (dayOfWeek.startsWith("tue")) {
				orgLabor.setTueOpen(opens);
				orgLabor.setTueClose(closes);
			}
			else if (dayOfWeek.startsWith("wed")) {
				orgLabor.setWedOpen(opens);
				orgLabor.setWedClose(closes);
			}
			else if (dayOfWeek.startsWith("thu")) {
				orgLabor.setThuOpen(opens);
				orgLabor.setThuClose(closes);
			}
			else if (dayOfWeek.startsWith("fri")) {
				orgLabor.setFriOpen(opens);
				orgLabor.setFriClose(closes);
			}
			else if (dayOfWeek.startsWith("sat")) {
				orgLabor.setSatOpen(opens);
				orgLabor.setSatClose(closes);
			}
		}

		return orgLabor;
	}

	private int _toTime(String timeString) {
		if (Validator.isNull(timeString)) {
			return -1;
		}

		Date date = null;

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"HH:mm");

		try {
			date = dateFormat.parse(timeString);
		}
		catch (ParseException parseException) {
			if (_log.isWarnEnabled()) {
				_log.warn(parseException);
			}

			return -1;
		}

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat("HHmm");

		return GetterUtil.getInteger(format.format(date));
	}

	private com.liferay.portal.kernel.model.Organization _updateNestedResources(
			Organization organization,
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization)
		throws Exception {

		AccountBrief[] accountBriefs = organization.getAccountBriefs();

		if (ArrayUtil.isNotEmpty(accountBriefs)) {
			for (AccountBrief accountBrief : accountBriefs) {
				serviceBuilderOrganization = _addAccountEntryOrganizationRel(
					accountBrief, serviceBuilderOrganization);
			}
		}

		RoleBrief[] roleBriefs = organization.getRoleBriefs();

		if (ArrayUtil.isNotEmpty(roleBriefs)) {
			for (RoleBrief roleBrief : roleBriefs) {
				serviceBuilderOrganization = _addGroupRole(
					serviceBuilderOrganization, roleBrief);
			}
		}

		return ResourcePermissionUtil.setResourcePermissions(
			serviceBuilderOrganization,
			serviceBuilderOrganization.getCompanyId(),
			organization.getPermissions(), _resourcePermissionLocalService,
			_roleService, _roleTypeContributorProvider);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationResourceImpl.class);

	private static final EntityModel _entityModel =
		new OrganizationEntityModel();

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountEntryOrganizationRelService
		_accountEntryOrganizationRelService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private EmailAddressService _emailAddressService;

	@Reference
	private File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<com.liferay.portal.kernel.model.Organization, Organization>
			_organizationResourceDTOConverter;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private OrgLaborLocalService _orgLaborLocalService;

	@Reference
	private OrgLaborService _orgLaborService;

	@Reference
	private PhoneService _phoneService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleResource _roleResource;

	@Reference
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

	@Reference
	private UserService _userService;

	@Reference
	private WebsiteService _websiteService;

}