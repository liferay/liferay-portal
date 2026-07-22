/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
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
import com.liferay.headless.admin.user.dto.v1_0.UserAccountBrief;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.AccountBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.EmailAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PermissionUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PhoneUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.RoleBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.UserAccountBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.WebUrlUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.OrgLaborService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.User",
		"dto.class.name=com.liferay.portal.kernel.model.Organization",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class OrganizationResourceDTOConverter
	implements DTOConverter
		<com.liferay.portal.kernel.model.Organization, Organization> {

	@Override
	public String getContentType() {
		return Organization.class.getSimpleName();
	}

	@Override
	public com.liferay.portal.kernel.model.Organization getObject(
			String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Organization organization =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				externalReferenceCode, CompanyThreadLocal.getCompanyId());

		if (organization == null) {
			long organizationId = GetterUtil.getLong(externalReferenceCode);

			if (_hasUserOrganization(organizationId)) {
				organization = _organizationLocalService.getOrganization(
					organizationId);
			}
			else {
				organization = _organizationService.getOrganization(
					organizationId);
			}
		}

		return organization;
	}

	@Override
	public Organization toDTO(
			DTOConverterContext dtoConverterContext,
			com.liferay.portal.kernel.model.Organization organization)
		throws Exception {

		if (organization == null) {
			return null;
		}

		Country country = _countryService.fetchCountry(
			organization.getCountryId());
		OrganizationResourceDTOConverter organizationResourceDTOConverter =
			this;
		Region region = _regionService.fetchRegion(organization.getRegionId());

		return new Organization() {
			{
				setAccountBriefs(
					() -> NestedFieldsSupplier.supply(
						"accountBriefs",
						fieldName -> TransformUtil.transformToArray(
							_accountEntryOrganizationRelLocalService.
								getAccountEntryOrganizationRelsByOrganizationId(
									organization.getOrganizationId()),
							accountEntryOrganizationRel ->
								AccountBriefUtil.toAccountBrief(
									_accountEntryLocalService.fetchAccountEntry(
										accountEntryOrganizationRel.
											getAccountEntryId())),
							AccountBrief.class)));
				setActions(dtoConverterContext::getActions);
				setComment(organization::getComments);
				setCreator(
					() -> NestedFieldsSupplier.supply(
						"creator",
						fieldName -> CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(
								organization.getUserId()))));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						com.liferay.portal.kernel.model.Organization.class.
							getName(),
						organization.getOrganizationId(),
						organization.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(organization::getCreateDate);
				setDateModified(organization::getModifiedDate);
				setExternalReferenceCode(
					organization::getExternalReferenceCode);
				setId(() -> String.valueOf(organization.getOrganizationId()));
				setImage(
					() -> {
						if (organization.getLogoId() <= 0) {
							return null;
						}

						return organization.getLogoURL();
					});
				setImageBase64(
					() -> NestedFieldsSupplier.supply(
						"imageBase64",
						nestedFieldNames -> {
							if (organization.getLogoId() == 0) {
								return null;
							}

							Image image = _imageLocalService.fetchImage(
								organization.getLogoId());

							if (image == null) {
								return null;
							}

							return Base64.encode(image.getTextObj());
						}));
				setImageId(organization::getLogoId);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							organization.getModelClassName(),
							organization.getOrganizationId()),
						AssetTag.NAME_ACCESSOR));
				setLocation(
					() -> new Location() {
						{
							setAddressCountry(
								() -> {
									if (country == null) {
										return null;
									}

									return country.getName(
										dtoConverterContext.getLocale());
								});
							setAddressCountry_i18n(
								() -> {
									if (country == null) {
										return null;
									}

									return LocalizedMapUtil.getI18nMap(
										dtoConverterContext.
											isAcceptAllLanguages(),
										_language.getCompanyAvailableLocales(
											country.getCompanyId()),
										country.getLanguageIdToTitleMap());
								});
							setAddressCountryCode(
								() -> {
									if (country == null) {
										return null;
									}

									return country.getA2();
								});
							setAddressCountryExternalReferenceCode(
								() -> {
									if (country == null) {
										return null;
									}

									return country.getExternalReferenceCode();
								});
							setAddressRegion(
								() -> {
									if (region == null) {
										return null;
									}

									return region.getName();
								});
							setAddressRegionCode(
								() -> {
									if (region == null) {
										return null;
									}

									return region.getRegionCode();
								});
							setAddressRegionExternalReferenceCode(
								() -> {
									if (region == null) {
										return null;
									}

									return region.getExternalReferenceCode();
								});
						}
					});
				setName(organization::getName);
				setNumberOfAccounts(
					() ->
						_accountEntryOrganizationRelLocalService.
							getAccountEntryOrganizationRelsCountByOrganizationId(
								organization.getOrganizationId()));
				setNumberOfOrganizations(
					() -> _organizationService.getOrganizationsCount(
						organization.getCompanyId(),
						organization.getOrganizationId()));
				setNumberOfUsers(
					() -> _userService.getOrganizationUsersCount(
						organization.getOrganizationId(),
						WorkflowConstants.STATUS_ANY));
				setOrganizationContactInformation(
					() -> new OrganizationContactInformation() {
						{
							setEmailAddresses(
								() -> TransformUtil.transformToArray(
									_emailAddressService.getEmailAddresses(
										organization.getModelClassName(),
										organization.getOrganizationId()),
									EmailAddressUtil::toEmailAddress,
									EmailAddress.class));
							setPostalAddresses(
								() -> TransformUtil.transformToArray(
									organization.getAddresses(),
									address -> _postalAddressDTOConverter.toDTO(
										new DefaultDTOConverterContext(
											dtoConverterContext.
												isAcceptAllLanguages(),
											null, _dtoConverterRegistry,
											address.getAddressId(),
											dtoConverterContext.getLocale(),
											dtoConverterContext.getUriInfo(),
											dtoConverterContext.getUser())),
									PostalAddress.class));
							setTelephones(
								() -> TransformUtil.transformToArray(
									_phoneService.getPhones(
										organization.getModelClassName(),
										organization.getOrganizationId()),
									PhoneUtil::toPhone, Phone.class));
							setWebUrls(
								() -> TransformUtil.transformToArray(
									_websiteService.getWebsites(
										organization.getModelClassName(),
										organization.getOrganizationId()),
									WebUrlUtil::toWebUrl, WebUrl.class));
						}
					});
				setParentOrganization(
					() -> organizationResourceDTOConverter.toDTO(
						dtoConverterContext,
						organization.getParentOrganization()));
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> PermissionUtil.toPermissions(
							organization.getCompanyId(),
							organization.getGroupId(),
							organization.getOrganizationId(),
							com.liferay.portal.kernel.model.Organization.class.
								getName(),
							_permissionService, _resourceActionLocalService)));
				setRoleBriefs(
					() -> NestedFieldsSupplier.supply(
						"roleBriefs",
						fieldName -> TransformUtil.transformToArray(
							_roleService.getGroupRoles(
								organization.getGroupId()),
							role -> RoleBriefUtil.toRoleBrief(
								dtoConverterContext, role),
							RoleBrief.class)));
				setServices(
					() -> TransformUtil.transformToArray(
						_orgLaborService.getOrgLabors(
							organization.getOrganizationId()),
						OrganizationResourceDTOConverter.this::_toService,
						Service.class));
				setTaxonomyCategoryBriefs(
					() -> NestedFieldsSupplier.supply(
						"taxonomyCategoryBriefs",
						nestedFieldNames -> TransformUtil.transformToArray(
							_assetCategoryService.getCategories(
								com.liferay.portal.kernel.model.Organization.
									class.getName(),
								organization.getOrganizationId()),
							assetCategory ->
								TaxonomyCategoryBriefUtil.
									toTaxonomyCategoryBrief(
										assetCategory, dtoConverterContext),
							TaxonomyCategoryBrief.class)));
				setTreePath(organization::getTreePath);
				setUserAccountBriefs(
					() -> NestedFieldsSupplier.supply(
						"userAccountBriefs",
						fieldName -> TransformUtil.transformToArray(
							_userLocalService.getOrganizationUsers(
								organization.getOrganizationId()),
							UserAccountBriefUtil::toUserAccountBrief,
							UserAccountBrief.class)));
			}
		};
	}

	private HoursAvailable _createHoursAvailable(
		int closeHour, String day, int openHour) {

		return new HoursAvailable() {
			{
				setCloses(() -> _formatHour(closeHour));
				setDayOfWeek(() -> day);
				setOpens(() -> _formatHour(openHour));
			}
		};
	}

	private String _formatHour(int hour) {
		if (hour == -1) {
			return null;
		}

		DecimalFormat decimalFormat = new DecimalFormat("00,00") {
			{
				setDecimalFormatSymbols(
					new DecimalFormatSymbols() {
						{
							setGroupingSeparator(':');
						}
					});
				setGroupingSize(2);
			}
		};

		return decimalFormat.format(hour);
	}

	private boolean _hasUserOrganization(long organizationId) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return false;
		}

		return _organizationLocalService.hasUserOrganization(
			permissionChecker.getUserId(), organizationId);
	}

	private Service _toService(OrgLabor orgLabor) throws Exception {
		ListType listType = orgLabor.getListType();

		return new Service() {
			{
				setHoursAvailable(
					() -> new HoursAvailable[] {
						_createHoursAvailable(
							orgLabor.getSunClose(), "Sunday",
							orgLabor.getSunOpen()),
						_createHoursAvailable(
							orgLabor.getMonClose(), "Monday",
							orgLabor.getMonOpen()),
						_createHoursAvailable(
							orgLabor.getTueClose(), "Tuesday",
							orgLabor.getTueOpen()),
						_createHoursAvailable(
							orgLabor.getWedClose(), "Wednesday",
							orgLabor.getWedOpen()),
						_createHoursAvailable(
							orgLabor.getThuClose(), "Thursday",
							orgLabor.getThuOpen()),
						_createHoursAvailable(
							orgLabor.getFriClose(), "Friday",
							orgLabor.getFriOpen()),
						_createHoursAvailable(
							orgLabor.getSatClose(), "Saturday",
							orgLabor.getSatOpen())
					});
				setServiceType(listType::getName);
			}
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CountryService _countryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private EmailAddressService _emailAddressService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private Language _language;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private OrgLaborService _orgLaborService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private PhoneService _phoneService;

	@Reference
	private Portal _portal;

	@Reference(target = DTOConverterConstants.POSTAL_ADDRESS_DTO_CONVERTER)
	private DTOConverter<Address, PostalAddress> _postalAddressDTOConverter;

	@Reference
	private RegionService _regionService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

	@Reference
	private WebsiteService _websiteService;

}