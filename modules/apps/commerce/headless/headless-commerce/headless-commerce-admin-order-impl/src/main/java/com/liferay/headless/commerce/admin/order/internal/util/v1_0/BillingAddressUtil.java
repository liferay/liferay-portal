/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.util.v1_0;

import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.BillingAddress;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class BillingAddressUtil {

	public static CommerceOrder addOrUpdateBillingAddress(
			BillingAddress billingAddress,
			CommerceAddressService commerceAddressService,
			CommerceOrder commerceOrder,
			CommerceOrderService commerceOrderService,
			CountryService countryService, ServiceContext serviceContext)
		throws Exception {

		if (commerceOrder.getBillingAddressId() > 0) {
			return _updateCommerceOrderBillingAddress(
				billingAddress, commerceAddressService, commerceOrder,
				commerceOrderService, countryService, serviceContext);
		}

		CommerceAddress commerceAddress = _addCommerceAddress(
			commerceAddressService, commerceOrder, billingAddress,
			serviceContext);

		return commerceOrderService.updateBillingAddress(
			commerceOrder.getCommerceOrderId(),
			commerceAddress.getCommerceAddressId());
	}

	private static CommerceAddress _addCommerceAddress(
			CommerceAddressService commerceAddressService,
			CommerceOrder commerceOrder, BillingAddress billingAddress,
			ServiceContext serviceContext)
		throws Exception {

		Country country = CountryLocalServiceUtil.getCountryByA2(
			commerceOrder.getCompanyId(), billingAddress.getCountryISOCode());

		return commerceAddressService.addCommerceAddress(
			billingAddress.getExternalReferenceCode(),
			commerceOrder.getModelClassName(),
			commerceOrder.getCommerceOrderId(), country.getCountryId(),
			_getRegionId(billingAddress, null, country),
			billingAddress.getCity(), billingAddress.getDescription(),
			billingAddress.getName(), billingAddress.getPhoneNumber(),
			billingAddress.getStreet1(), billingAddress.getStreet2(),
			billingAddress.getStreet3(), billingAddress.getSubtype(),
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			billingAddress.getZip(), serviceContext);
	}

	private static long _getCountryId(
		BillingAddress billingAddress, CommerceAddress commerceAddress,
		Country country) {

		if (Validator.isNull(billingAddress.getCountryISOCode()) &&
			(commerceAddress != null)) {

			return commerceAddress.getCountryId();
		}

		if (country == null) {
			return 0;
		}

		return country.getCountryId();
	}

	private static String _getDescription(CommerceAddress commerceAddress) {
		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getDescription();
	}

	private static String _getPhoneNumber(CommerceAddress commerceAddress) {
		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getPhoneNumber();
	}

	private static long _getRegionId(
			BillingAddress billingAddress, CommerceAddress commerceAddress,
			Country country)
		throws Exception {

		if (Validator.isNull(billingAddress.getRegionISOCode()) &&
			(commerceAddress != null)) {

			return commerceAddress.getRegionId();
		}

		if (Validator.isNull(billingAddress.getRegionISOCode()) ||
			(country == null)) {

			return 0;
		}

		Region region = RegionLocalServiceUtil.getRegion(
			country.getCountryId(), billingAddress.getRegionISOCode());

		return region.getRegionId();
	}

	private static String _getStreet2(CommerceAddress commerceAddress) {
		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getStreet2();
	}

	private static String _getStreet3(CommerceAddress commerceAddress) {
		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getStreet3();
	}

	private static String _getSubtype(CommerceAddress commerceAddress) {
		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getSubtype();
	}

	private static String _getZip(CommerceAddress commerceAddress) {
		if (commerceAddress == null) {
			return null;
		}

		return commerceAddress.getZip();
	}

	private static CommerceOrder _updateCommerceOrderBillingAddress(
			BillingAddress billingAddress,
			CommerceAddressService commerceAddressService,
			CommerceOrder commerceOrder,
			CommerceOrderService commerceOrderService,
			CountryService countryService, ServiceContext serviceContext)
		throws Exception {

		CommerceAddress commerceAddress =
			commerceAddressService.fetchCommerceAddress(
				commerceOrder.getBillingAddressId());
		Country country = countryService.fetchCountryByA2(
			commerceOrder.getCompanyId(), billingAddress.getCountryISOCode());

		return commerceOrderService.updateBillingAddress(
			commerceOrder.getCommerceOrderId(),
			_getCountryId(billingAddress, commerceAddress, country),
			_getRegionId(billingAddress, commerceAddress, country),
			billingAddress.getCity(),
			GetterUtil.get(
				billingAddress.getDescription(),
				_getDescription(commerceAddress)),
			billingAddress.getName(), billingAddress.getStreet1(),
			GetterUtil.get(
				billingAddress.getStreet2(), _getStreet2(commerceAddress)),
			GetterUtil.get(
				billingAddress.getStreet3(), _getStreet3(commerceAddress)),
			GetterUtil.get(
				billingAddress.getSubtype(), _getSubtype(commerceAddress)),
			GetterUtil.get(
				billingAddress.getPhoneNumber(),
				_getPhoneNumber(commerceAddress)),
			GetterUtil.get(billingAddress.getZip(), _getZip(commerceAddress)),
			serviceContext);
	}

}