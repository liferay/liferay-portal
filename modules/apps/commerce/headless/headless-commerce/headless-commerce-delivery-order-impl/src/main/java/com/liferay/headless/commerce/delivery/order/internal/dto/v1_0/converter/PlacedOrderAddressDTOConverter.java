/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderAddress;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderAddress"
	},
	service = DTOConverter.class
)
public class PlacedOrderAddressDTOConverter
	implements DTOConverter<CommerceAddress, PlacedOrderAddress> {

	@Override
	public String getContentType() {
		return PlacedOrderAddress.class.getSimpleName();
	}

	@Override
	public PlacedOrderAddress toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.fetchCommerceAddress(
				(Long)dtoConverterContext.getId());

		if (commerceAddress == null) {
			return new PlacedOrderAddress();
		}

		Country addressCountry = commerceAddress.getCountry();

		Region addressRegion = commerceAddress.getRegion();

		return new PlacedOrderAddress() {
			{
				setCity(commerceAddress::getCity);
				setCountry(
					() -> addressCountry.getTitle(
						dtoConverterContext.getLocale()));
				setCountryISOCode(addressCountry::getA2);
				setDescription(commerceAddress::getDescription);
				setExternalReferenceCode(
					commerceAddress::getExternalReferenceCode);
				setId(commerceAddress::getCommerceAddressId);
				setLatitude(commerceAddress::getLatitude);
				setLongitude(commerceAddress::getLongitude);
				setName(commerceAddress::getName);
				setPhoneNumber(commerceAddress::getPhoneNumber);
				setRegion(
					() -> {
						if (addressRegion == null) {
							return null;
						}

						return addressRegion.getName();
					});
				setRegionISOCode(
					() -> {
						if (addressRegion == null) {
							return StringPool.BLANK;
						}

						return addressRegion.getRegionCode();
					});
				setStreet1(commerceAddress::getStreet1);
				setStreet2(commerceAddress::getStreet2);
				setStreet3(commerceAddress::getStreet3);
				setSubtype(commerceAddress::getSubtype);
				setTypeId(commerceAddress::getType);
				setZip(commerceAddress::getZip);
			}
		};
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

}