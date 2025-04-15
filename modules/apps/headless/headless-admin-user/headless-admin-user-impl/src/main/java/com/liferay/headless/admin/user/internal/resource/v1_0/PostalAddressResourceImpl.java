/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PostalAddressUtil;
import com.liferay.headless.admin.user.resource.v1_0.PostalAddressResource;
import com.liferay.portal.kernel.exception.NoSuchAddressException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.AddressService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.permission.CommonPermissionUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.BadRequestException;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/postal-address.properties",
	scope = ServiceScope.PROTOTYPE, service = PostalAddressResource.class
)
public class PostalAddressResourceImpl extends BasePostalAddressResourceImpl {

	@Override
	public void deletePostalAddress(Long postalAddressId) throws Exception {
		Address address = _addressService.getAddress(postalAddressId);

		_addressService.deleteAddress(postalAddressId);

		if (address.isPrimary()) {
			_updatePrimaryAddress(address.getClassName(), address.getClassPK());
		}
	}

	@Override
	public void deletePostalAddressByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (address == null) {
			throw new NoSuchAddressException(
				"No address found with external reference code " +
					externalReferenceCode);
		}

		_addressService.deleteAddress(address.getAddressId());
	}

	@Override
	public Page<PostalAddress>
			getAccountByExternalReferenceCodePostalAddressesPage(
				String externalReferenceCode)
		throws Exception {

		return getAccountPostalAddressesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<PostalAddress> getAccountPostalAddressesPage(Long accountId)
		throws Exception {

		_accountEntryService.getAccountEntry(accountId);

		return Page.of(
			transform(
				_addressService.getAddresses(
					AccountEntry.class.getName(), accountId),
				address -> PostalAddressUtil.toPostalAddress(
					contextAcceptLanguage.isAcceptAllLanguages(), address,
					contextCompany.getCompanyId(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	@Override
	public Page<PostalAddress>
			getOrganizationByExternalReferenceCodePostalAddressesPage(
				String externalReferenceCode)
		throws Exception {

		return getOrganizationPostalAddressesPage(
			String.valueOf(
				DTOConverterUtil.getModelPrimaryKey(
					_organizationResourceDTOConverter, externalReferenceCode)));
	}

	@Override
	public Page<PostalAddress> getOrganizationPostalAddressesPage(
			String organizationId)
		throws Exception {

		Organization organization = _organizationResourceDTOConverter.getObject(
			organizationId);

		return Page.of(
			transform(
				_addressService.getAddresses(
					organization.getModelClassName(),
					organization.getOrganizationId()),
				address -> PostalAddressUtil.toPostalAddress(
					contextAcceptLanguage.isAcceptAllLanguages(), address,
					contextCompany.getCompanyId(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	@Override
	public PostalAddress getPostalAddress(Long postalAddressId)
		throws Exception {

		return PostalAddressUtil.toPostalAddress(
			contextAcceptLanguage.isAcceptAllLanguages(),
			_addressService.getAddress(postalAddressId),
			contextCompany.getCompanyId(),
			contextAcceptLanguage.getPreferredLocale());
	}

	@Override
	public PostalAddress getPostalAddressByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (address == null) {
			throw new NoSuchAddressException(
				"No address found with external reference code " +
					externalReferenceCode);
		}

		return getPostalAddress(address.getAddressId());
	}

	@Override
	public Page<PostalAddress>
			getUserAccountByExternalReferenceCodePostalAddressesPage(
				String externalReferenceCode)
		throws Exception {

		return getUserAccountPostalAddressesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<PostalAddress> getUserAccountPostalAddressesPage(
			Long userAccountId)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		CommonPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			user.getModelClassName(), user.getUserId(), ActionKeys.VIEW);

		return Page.of(
			transform(
				_addressService.getAddresses(
					Contact.class.getName(), user.getContactId()),
				address -> PostalAddressUtil.toPostalAddress(
					contextAcceptLanguage.isAcceptAllLanguages(), address,
					contextCompany.getCompanyId(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	@Override
	public PostalAddress patchPostalAddress(
			Long postalAddressId, PostalAddress postalAddress)
		throws Exception {

		Address address = _addressService.getAddress(postalAddressId);

		Country country = null;

		if (postalAddress.getAddressCountry() != null) {
			country = _getCountryByTitle(postalAddress);

			address.setCountryId(country.getCountryId());
			address.setRegionId(_getRegionId(postalAddress, country));
		}

		if ((postalAddress.getAddressRegion() != null) && (country == null)) {
			country = _countryService.getCountry(address.getCountryId());

			address.setRegionId(_getRegionId(postalAddress, country));
		}

		if (Validator.isNotNull(postalAddress.getAddressType())) {
			address.setListTypeId(_getListTypeId(address, postalAddress));
		}

		boolean oldPrimary = address.isPrimary();

		boolean newPrimary = GetterUtil.getBoolean(
			postalAddress.getPrimary(), oldPrimary);

		address = _addressService.updateAddress(
			GetterUtil.getString(
				postalAddress.getExternalReferenceCode(),
				address.getExternalReferenceCode()),
			address.getAddressId(), address.getCountryId(),
			address.getListTypeId(), address.getRegionId(),
			GetterUtil.getString(
				postalAddress.getAddressLocality(), address.getCity()),
			address.getDescription(), address.isMailing(),
			GetterUtil.getString(postalAddress.getName(), address.getName()),
			newPrimary,
			GetterUtil.getString(
				postalAddress.getStreetAddressLine1(), address.getStreet1()),
			GetterUtil.getString(
				postalAddress.getStreetAddressLine2(), address.getStreet2()),
			GetterUtil.getString(
				postalAddress.getStreetAddressLine3(), address.getStreet3()),
			GetterUtil.getString(
				postalAddress.getAddressSubtype(), address.getSubtype()),
			GetterUtil.getString(
				postalAddress.getPostalCode(), address.getZip()),
			GetterUtil.getString(
				postalAddress.getPhoneNumber(), address.getPhoneNumber()));

		if (!newPrimary && oldPrimary) {
			List<Address> addresses = _addressService.getAddresses(
				address.getClassName(), address.getClassPK());

			for (Address currentAddress : addresses) {
				if ((addresses.size() == 1) ||
					(currentAddress.getAddressId() != address.getAddressId())) {

					_addressService.updateAddress(
						currentAddress.getExternalReferenceCode(),
						currentAddress.getAddressId(),
						currentAddress.getCountryId(),
						currentAddress.getListTypeId(),
						currentAddress.getRegionId(), currentAddress.getCity(),
						currentAddress.getDescription(),
						currentAddress.isMailing(), currentAddress.getName(),
						true, currentAddress.getStreet1(),
						currentAddress.getStreet2(),
						currentAddress.getStreet3(),
						currentAddress.getSubtype(), currentAddress.getZip(),
						currentAddress.getPhoneNumber());

					break;
				}
			}
		}

		return PostalAddressUtil.toPostalAddress(
			contextAcceptLanguage.isAcceptAllLanguages(), address,
			contextCompany.getCompanyId(),
			contextAcceptLanguage.getPreferredLocale());
	}

	@Override
	public PostalAddress patchPostalAddressByExternalReferenceCode(
			String externalReferenceCode, PostalAddress postalAddress)
		throws Exception {

		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (address == null) {
			throw new NoSuchAddressException(
				"No address found with external reference code " +
					externalReferenceCode);
		}

		return patchPostalAddress(address.getAddressId(), postalAddress);
	}

	@Override
	public PostalAddress postAccountPostalAddress(
			Long accountId, PostalAddress postalAddress)
		throws Exception {

		Country country = _getCountryByTitle(postalAddress);

		long regionId = _getRegionId(postalAddress, country);

		Address address = _addressService.addAddress(
			postalAddress.getExternalReferenceCode(),
			AccountEntry.class.getName(), accountId, country.getCountryId(),
			_getListTypeId(null, postalAddress), regionId,
			postalAddress.getAddressLocality(), null, false,
			postalAddress.getName(), postalAddress.getPrimary(),
			postalAddress.getStreetAddressLine1(),
			postalAddress.getStreetAddressLine2(),
			postalAddress.getStreetAddressLine3(),
			postalAddress.getAddressSubtype(), postalAddress.getPostalCode(),
			postalAddress.getPhoneNumber(),
			ServiceContextFactory.getInstance(contextHttpServletRequest));

		return PostalAddressUtil.toPostalAddress(
			contextAcceptLanguage.isAcceptAllLanguages(), address,
			contextCompany.getCompanyId(),
			contextAcceptLanguage.getPreferredLocale());
	}

	@Override
	public PostalAddress putPostalAddress(
			Long postalAddressId, PostalAddress postalAddress)
		throws Exception {

		Address address = _addressService.getAddress(postalAddressId);

		Country country = _getCountryByTitle(postalAddress);

		long regionId = _getRegionId(postalAddress, country);

		address = _addressService.updateAddress(
			GetterUtil.getString(
				postalAddress.getExternalReferenceCode(),
				address.getExternalReferenceCode()),
			address.getAddressId(), country.getCountryId(),
			_getListTypeId(address, postalAddress), regionId,
			postalAddress.getAddressLocality(), address.getDescription(),
			address.isMailing(), postalAddress.getName(),
			postalAddress.getPrimary(), postalAddress.getStreetAddressLine1(),
			postalAddress.getStreetAddressLine2(),
			postalAddress.getStreetAddressLine3(),
			postalAddress.getAddressSubtype(), postalAddress.getPostalCode(),
			postalAddress.getPhoneNumber());

		return PostalAddressUtil.toPostalAddress(
			contextAcceptLanguage.isAcceptAllLanguages(), address,
			contextCompany.getCompanyId(),
			contextAcceptLanguage.getPreferredLocale());
	}

	@Override
	public PostalAddress putPostalAddressByExternalReferenceCode(
			String externalReferenceCode, PostalAddress postalAddress)
		throws Exception {

		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (address == null) {
			throw new NoSuchAddressException(
				"No address found with external reference code " +
					externalReferenceCode);
		}

		return putPostalAddress(address.getAddressId(), postalAddress);
	}

	private Country _getCountryByTitle(PostalAddress postalAddress) {
		List<Country> countries = _countryService.getCompanyCountries(
			contextCompany.getCompanyId());

		Iterator<Country> countryIterator = countries.iterator();

		Boolean found = false;

		String title = null;

		Country country = null;

		while (countryIterator.hasNext() && !found) {
			country = countryIterator.next();

			title = country.getTitle(
				contextAcceptLanguage.getPreferredLocale());

			if (title.equals(postalAddress.getAddressCountry())) {
				found = true;
			}
		}

		if (!found) {
			throw new BadRequestException("Country not found");
		}

		return country;
	}

	private long _getListTypeId(Address address, PostalAddress postalAddress)
		throws Exception {

		String type = AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS;

		if (address != null) {
			ClassName className = _classNameLocalService.getClassName(
				address.getClassNameId());

			type = className.getClassName() + ListTypeConstants.ADDRESS;
		}

		ListType listType = _listTypeLocalService.getListType(
			contextCompany.getCompanyId(), postalAddress.getAddressType(),
			type);

		if (listType == null) {
			throw new BadRequestException("Type not found");
		}

		return listType.getListTypeId();
	}

	private long _getRegionId(PostalAddress postalAddress, Country country) {
		List<Region> regions = _regionService.getRegions(
			country.getCountryId());

		if ((postalAddress.getAddressRegion() == null) ||
			Objects.equals(postalAddress.getAddressRegion(), "")) {

			if (regions.isEmpty()) {
				return 0;
			}

			throw new BadRequestException("Region not found");
		}

		Iterator<Region> regionIterator = regions.iterator();

		Region region = null;
		String title = null;

		Boolean found = false;

		while (regionIterator.hasNext() && !found) {
			region = regionIterator.next();

			title = region.getTitle(
				contextAcceptLanguage.getPreferredLanguageId());

			if (title.equals(postalAddress.getAddressRegion())) {
				found = true;
			}
		}

		if (!found) {
			throw new BadRequestException("Region not found");
		}

		return region.getRegionId();
	}

	private void _updatePrimaryAddress(String className, long contactId)
		throws Exception {

		List<Address> addresses = _addressService.getAddresses(
			className, contactId);

		if (addresses.isEmpty()) {
			return;
		}

		Address address = addresses.get(0);

		_addressService.updateAddress(
			address.getExternalReferenceCode(), address.getAddressId(),
			address.getCountryId(), address.getListTypeId(),
			address.getRegionId(), address.getCity(), address.getDescription(),
			address.isMailing(), address.getName(), true, address.getStreet1(),
			address.getStreet2(), address.getStreet3(), address.getSubtype(),
			address.getZip(), address.getPhoneNumber());
	}

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private AddressService _addressService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CountryService _countryService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<Organization, com.liferay.headless.admin.user.dto.v1_0.Organization>
			_organizationResourceDTOConverter;

	@Reference
	private RegionService _regionService;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

	@Reference
	private UserService _userService;

}