/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.util;

import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Drew Brokke
 */
public class ServiceBuilderAddressUtil {

	public static Address toServiceBuilderAddress(
		long companyId, PostalAddress postalAddress, String type) {

		String street1 = postalAddress.getStreetAddressLine1();
		String street2 = postalAddress.getStreetAddressLine2();
		String street3 = postalAddress.getStreetAddressLine3();
		String city = postalAddress.getAddressLocality();
		String zip = postalAddress.getPostalCode();
		long countryId = ServiceBuilderCountryUtil.toServiceBuilderCountryId(
			companyId, postalAddress.getAddressCountry());

		if (Validator.isNull(street1) && Validator.isNull(street2) &&
			Validator.isNull(street3) && Validator.isNull(city) &&
			Validator.isNull(zip) && (countryId == 0)) {

			return null;
		}

		Address address = AddressLocalServiceUtil.createAddress(
			GetterUtil.getLong(postalAddress.getId()));

		address.setExternalReferenceCode(
			postalAddress.getExternalReferenceCode());
		address.setCountryId(countryId);
		address.setListTypeId(
			ServiceBuilderListTypeUtil.toServiceBuilderListTypeId(
				companyId, "other", postalAddress.getAddressType(), type));
		address.setRegionId(
			ServiceBuilderRegionUtil.getServiceBuilderRegionId(
				postalAddress.getAddressRegion(), countryId));
		address.setCity(city);
		address.setMailing(true);
		address.setName(postalAddress.getName());
		address.setPrimary(GetterUtil.getBoolean(postalAddress.getPrimary()));
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setStreet3(street3);
		address.setSubtype(postalAddress.getAddressSubtype());
		address.setZip(zip);

		return address;
	}

}