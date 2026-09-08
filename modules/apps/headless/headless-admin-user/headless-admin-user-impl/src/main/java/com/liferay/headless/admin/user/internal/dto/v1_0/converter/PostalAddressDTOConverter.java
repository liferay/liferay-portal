/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.User", "default=true",
		"dto.class.name=com.liferay.portal.kernel.model.Address", "version=v1.0"
	},
	service = DTOConverter.class
)
public class PostalAddressDTOConverter
	implements DTOConverter<Address, PostalAddress> {

	@Override
	public String getContentType() {
		return PostalAddress.class.getSimpleName();
	}

	@Override
	public Address getObject(String externalReferenceCode) throws Exception {
		Address address =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, CompanyThreadLocal.getCompanyId());

		if (address == null) {
			address = _addressLocalService.getAddress(
				GetterUtil.getLong(externalReferenceCode));
		}

		return address;
	}

	@Override
	public PostalAddress toDTO(
			DTOConverterContext dtoConverterContext, Address address)
		throws Exception {

		if (address == null) {
			return null;
		}

		return new PostalAddress() {
			{
				setAddressCountry(
					() -> {
						if (address.getCountryId() <= 0) {
							return null;
						}

						Country country = address.getCountry();

						return country.getName(dtoConverterContext.getLocale());
					});
				setAddressCountry_i18n(
					() -> {
						Country country = address.getCountry();

						if (country == null) {
							return null;
						}

						return LocalizedMapUtil.getI18nMap(
							dtoConverterContext.isAcceptAllLanguages(),
							LanguageUtil.getCompanyAvailableLocales(
								address.getCompanyId()),
							country.getLanguageIdToTitleMap());
					});
				setAddressCountryExternalReferenceCode(
					() -> {
						if (address.getCountryId() <= 0) {
							return null;
						}

						Country country = address.getCountry();

						return country.getExternalReferenceCode();
					});
				setAddressLocality(address::getCity);
				setAddressRegion(
					() -> {
						if (address.getRegionId() <= 0) {
							return null;
						}

						Region region = address.getRegion();

						return region.getName();
					});
				setAddressRegionExternalReferenceCode(
					() -> {
						if (address.getRegionId() <= 0) {
							return null;
						}

						Region region = address.getRegion();

						return region.getExternalReferenceCode();
					});
				setAddressSubtype(address::getSubtype);
				setAddressType(
					() -> {
						ListType listType = address.getListType();

						return listType.getName();
					});
				setExternalReferenceCode(address::getExternalReferenceCode);
				setId(address::getAddressId);
				setName(address::getName);
				setPhoneNumber(address::getPhoneNumber);
				setPostalCode(address::getZip);
				setPrimary(address::isPrimary);
				setStreetAddressLine1(address::getStreet1);
				setStreetAddressLine2(address::getStreet2);
				setStreetAddressLine3(address::getStreet3);
			}
		};
	}

	@Reference
	private AddressLocalService _addressLocalService;

}