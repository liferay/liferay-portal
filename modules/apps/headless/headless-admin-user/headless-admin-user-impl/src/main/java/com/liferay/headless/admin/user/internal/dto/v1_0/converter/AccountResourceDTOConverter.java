/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountContactInformation;
import com.liferay.headless.admin.user.dto.v1_0.AccountGroupBrief;
import com.liferay.headless.admin.user.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.EmailAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PermissionUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PhoneUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PostalAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.WebUrlUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.webserver.WebServerServletToken;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.User",
		"dto.class.name=com.liferay.account.model.AccountEntry", "version=v1.0"
	},
	service = DTOConverter.class
)
public class AccountResourceDTOConverter
	implements DTOConverter<AccountEntry, Account> {

	@Override
	public String getContentType() {
		return Account.class.getSimpleName();
	}

	@Override
	public AccountEntry getObject(String externalReferenceCode)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, CompanyThreadLocal.getCompanyId());

		if (accountEntry == null) {
			accountEntry = _accountEntryLocalService.getAccountEntry(
				GetterUtil.getLong(externalReferenceCode));
		}

		return accountEntry;
	}

	@Override
	public Account toDTO(
			DTOConverterContext dtoConverterContext, AccountEntry accountEntry)
		throws Exception {

		if (accountEntry == null) {
			return null;
		}

		return new Account() {
			{
				setAccountContactInformation(
					() -> _toAccountContactInformation(
						accountEntry, dtoConverterContext));
				setAccountGroupBriefs(
					() -> NestedFieldsSupplier.supply(
						"accountGroupBriefs",
						nestedFieldNames -> TransformUtil.transformToArray(
							_accountGroupService.
								getAccountGroupsByAccountEntryId(
									accountEntry.getAccountEntryId(),
									QueryUtil.ALL_POS, QueryUtil.ALL_POS),
							accountGroup -> _toAccountGroupBrief(accountGroup),
							AccountGroupBrief.class)));
				setAccountRoles(
					() -> NestedFieldsSupplier.supply(
						"accountRoles",
						fieldName -> TransformUtil.transformToArray(
							_accountRoleLocalService.
								getAccountRolesByAccountEntryIds(
									accountEntry.getCompanyId(),
									new long[] {
										accountEntry.getAccountEntryId()
									}),
							accountRole -> _toAccountRole(accountRole),
							AccountRole.class)));
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> NestedFieldsSupplier.supply(
						"creator",
						fieldName -> CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(
								accountEntry.getUserId()))));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						AccountEntry.class.getName(),
						accountEntry.getAccountEntryId(),
						accountEntry.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(accountEntry::getCreateDate);
				setDateModified(accountEntry::getModifiedDate);
				setDefaultBillingAddressExternalReferenceCode(
					() -> {
						Address address = _addressLocalService.fetchAddress(
							accountEntry.getDefaultBillingAddressId());

						if (address == null) {
							return null;
						}

						return address.getExternalReferenceCode();
					});
				setDefaultBillingAddressId(
					accountEntry::getDefaultBillingAddressId);
				setDefaultShippingAddressExternalReferenceCode(
					() -> {
						Address address = _addressLocalService.fetchAddress(
							accountEntry.getDefaultShippingAddressId());

						if (address == null) {
							return null;
						}

						return address.getExternalReferenceCode();
					});
				setDefaultShippingAddressId(
					accountEntry::getDefaultShippingAddressId);
				setDescription(accountEntry::getDescription);
				setDomains(() -> StringUtil.split(accountEntry.getDomains()));
				setExternalReferenceCode(
					accountEntry::getExternalReferenceCode);
				setId(accountEntry::getAccountEntryId);
				setKeywords(
					() -> NestedFieldsSupplier.supply(
						"keywords",
						nestedFieldNames -> ListUtil.toArray(
							_assetTagLocalService.getTags(
								AccountEntry.class.getName(),
								accountEntry.getAccountEntryId()),
							AssetTag.NAME_ACCESSOR)));
				setLogoBase64(
					() -> NestedFieldsSupplier.supply(
						"logoBase64",
						nestedFieldNames -> {
							if (accountEntry.getLogoId() == 0) {
								return null;
							}

							Image image = _imageLocalService.fetchImage(
								accountEntry.getLogoId());

							if (image == null) {
								return null;
							}

							return Base64.encode(image.getTextObj());
						}));
				setLogoId(accountEntry::getLogoId);
				setLogoURL(
					() -> StringBundler.concat(
						"/image/organization_logo?img_id=",
						accountEntry.getLogoId(), "&t=",
						_webServerServletToken.getToken(
							accountEntry.getLogoId())));
				setName(accountEntry::getName);
				setNumberOfUsers(
					() ->
						(int)
							_accountEntryUserRelLocalService.
								getAccountEntryUserRelsCountByAccountEntryId(
									accountEntry.getAccountEntryId()));
				setOrganizationExternalReferenceCodes(
					() -> TransformUtil.transformToArray(
						_accountEntryOrganizationRelLocalService.
							getAccountEntryOrganizationRels(
								accountEntry.getAccountEntryId()),
						accountEntryOrganizationRel -> {
							Organization organization =
								_organizationLocalService.fetchOrganization(
									accountEntryOrganizationRel.
										getOrganizationId());

							if (organization == null) {
								return null;
							}

							return organization.getExternalReferenceCode();
						},
						String.class));
				setOrganizationIds(
					() -> TransformUtil.transformToArray(
						_accountEntryOrganizationRelLocalService.
							getAccountEntryOrganizationRels(
								accountEntry.getAccountEntryId()),
						AccountEntryOrganizationRel::getOrganizationId,
						Long.class));
				setParentAccountExternalReferenceCode(
					() -> {
						AccountEntry parentAccountEntry =
							_accountEntryLocalService.fetchAccountEntry(
								accountEntry.getParentAccountEntryId());

						if (parentAccountEntry == null) {
							return null;
						}

						return parentAccountEntry.getExternalReferenceCode();
					});
				setParentAccountId(accountEntry::getParentAccountEntryId);
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> PermissionUtil.toPermissions(
							accountEntry.getCompanyId(),
							accountEntry.getAccountEntryGroupId(),
							accountEntry.getAccountEntryId(),
							AccountEntry.class.getName(), _permissionService,
							_resourceActionLocalService)));
				setPostalAddresses(
					() -> NestedFieldsSupplier.supply(
						"postalAddresses",
						nestedFieldNames -> TransformUtil.transformToArray(
							accountEntry.getListTypeAddresses(
								PostalAddressUtil.
									getAccountEntryAddressListTypeIds(
										accountEntry.getCompanyId(),
										_listTypeLocalService)),
							address -> _postalAddressDTOConverter.toDTO(
								new DefaultDTOConverterContext(
									dtoConverterContext.isAcceptAllLanguages(),
									null, _dtoConverterRegistry,
									address.getAddressId(),
									dtoConverterContext.getLocale(),
									dtoConverterContext.getUriInfo(),
									dtoConverterContext.getUser())),
							PostalAddress.class)));
				setStatus(accountEntry::getStatus);
				setTaxId(accountEntry::getTaxIdNumber);
				setTaxonomyCategoryBriefs(
					() -> NestedFieldsSupplier.supply(
						"taxonomyCategoryBriefs",
						nestedFieldNames -> TransformUtil.transformToArray(
							_assetCategoryLocalService.getCategories(
								AccountEntry.class.getName(),
								accountEntry.getAccountEntryId()),
							assetCategory ->
								TaxonomyCategoryBriefUtil.
									toTaxonomyCategoryBrief(
										assetCategory, dtoConverterContext),
							TaxonomyCategoryBrief.class)));
				setType(() -> Account.Type.create(accountEntry.getType()));
			}
		};
	}

	private AccountContactInformation _toAccountContactInformation(
			AccountEntry accountEntry, DTOConverterContext dtoConverterContext)
		throws Exception {

		int count = _resourcePermissionLocalService.getResourcePermissionsCount(
			accountEntry.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(accountEntry.getAccountEntryId()));

		if ((count == 0) ||
			(!_accountEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				accountEntry.getAccountEntryId(),
				AccountActionKeys.MANAGE_ADDRESSES) &&
			 !_accountEntryModelResourcePermission.contains(
				 PermissionThreadLocal.getPermissionChecker(),
				 accountEntry.getAccountEntryId(),
				 AccountActionKeys.VIEW_ADDRESSES))) {

			return null;
		}

		Contact contact = accountEntry.fetchContact();

		return new AccountContactInformation() {
			{
				setEmailAddresses(
					() -> TransformUtil.transformToArray(
						accountEntry.getEmailAddresses(),
						EmailAddressUtil::toEmailAddress, EmailAddress.class));
				setFacebook(
					() -> {
						if (contact == null) {
							return null;
						}

						return contact.getFacebookSn();
					});
				setJabber(
					() -> {
						if (contact == null) {
							return null;
						}

						return contact.getJabberSn();
					});
				setPostalAddresses(
					() -> TransformUtil.transformToArray(
						accountEntry.getListTypeAddresses(
							PostalAddressUtil.
								getAccountEntryContactAddressListTypeIds(
									accountEntry.getCompanyId(),
									_listTypeLocalService)),
						address -> _postalAddressDTOConverter.toDTO(
							new DefaultDTOConverterContext(
								dtoConverterContext.isAcceptAllLanguages(),
								null, _dtoConverterRegistry,
								address.getAddressId(),
								dtoConverterContext.getLocale(),
								dtoConverterContext.getUriInfo(),
								dtoConverterContext.getUser())),
						PostalAddress.class));
				setSkype(
					() -> {
						if (contact == null) {
							return null;
						}

						return contact.getSkypeSn();
					});
				setSms(
					() -> {
						if (contact == null) {
							return null;
						}

						return contact.getSmsSn();
					});
				setTelephones(
					() -> TransformUtil.transformToArray(
						accountEntry.getPhones(), PhoneUtil::toPhone,
						Phone.class));
				setTwitter(
					() -> {
						if (contact == null) {
							return null;
						}

						return contact.getTwitterSn();
					});
				setWebUrls(
					() -> TransformUtil.transformToArray(
						accountEntry.getWebsites(), WebUrlUtil::toWebUrl,
						WebUrl.class));
			}
		};
	}

	private AccountGroupBrief _toAccountGroupBrief(AccountGroup accountGroup) {
		return new AccountGroupBrief() {
			{
				setExternalReferenceCode(
					accountGroup::getExternalReferenceCode);
				setId(accountGroup::getAccountGroupId);
				setName(accountGroup::getName);
			}
		};
	}

	private AccountRole _toAccountRole(
		com.liferay.account.model.AccountRole accountRole) {

		return new AccountRole() {
			{
				setAccountId(accountRole::getAccountEntryId);
				setExternalReferenceCode(accountRole::getExternalReferenceCode);
				setId(accountRole::getRoleId);
				setName(accountRole::getRoleName);
				setRoleId(accountRole::getRoleId);
				setRoleType(
					() -> {
						Role role = _roleService.getRole(
							accountRole.getRoleId());

						return role.getType();
					});
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference(target = DTOConverterConstants.POSTAL_ADDRESS_DTO_CONVERTER)
	private DTOConverter<Address, PostalAddress> _postalAddressDTOConverter;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WebServerServletToken _webServerServletToken;

}