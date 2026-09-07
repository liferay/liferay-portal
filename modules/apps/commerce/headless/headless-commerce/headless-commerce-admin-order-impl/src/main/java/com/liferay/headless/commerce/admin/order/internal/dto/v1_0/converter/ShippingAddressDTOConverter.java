/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.ShippingAddress;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Commerce.Admin.Order",
		"default=true", "dto.class.name=ShippingAddress", "version=v1.0"
	},
	service = DTOConverter.class
)
public class ShippingAddressDTOConverter
	implements DTOConverter<CommerceAddress, ShippingAddress> {

	@Override
	public String getContentType() {
		return ShippingAddress.class.getSimpleName();
	}

	@Override
	public ShippingAddress toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.getCommerceAddress(
				(Long)dtoConverterContext.getId());

		return new ShippingAddress() {
			{
				setCity(commerceAddress::getCity);
				setCountryISOCode(
					() -> {
						Country country = commerceAddress.getCountry();

						return country.getA2();
					});
				setDescription(commerceAddress::getDescription);
				setExternalReferenceCode(
					commerceAddress::getExternalReferenceCode);
				setId(commerceAddress::getCommerceAddressId);
				setLatitude(commerceAddress::getLatitude);
				setLongitude(commerceAddress::getLongitude);
				setName(commerceAddress::getName);
				setPhoneNumber(commerceAddress::getPhoneNumber);
				setRegionISOCode(() -> _getRegionISOCode(commerceAddress));
				setStreet1(commerceAddress::getStreet1);
				setStreet2(commerceAddress::getStreet2);
				setStreet3(commerceAddress::getStreet3);
				setSubtype(commerceAddress::getSubtype);
				setZip(commerceAddress::getZip);
			}
		};
	}

	private String _getRegionISOCode(CommerceAddress commerceAddress)
		throws Exception {

		Region region = commerceAddress.getRegion();

		if (region == null) {
			return StringPool.BLANK;
		}

		return region.getRegionCode();
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

}