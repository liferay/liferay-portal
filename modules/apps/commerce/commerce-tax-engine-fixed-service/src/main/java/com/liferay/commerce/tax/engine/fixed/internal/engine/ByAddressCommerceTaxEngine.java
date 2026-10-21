/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.internal.engine;

import com.liferay.commerce.exception.CommerceTaxEngineException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.tax.CommerceTaxCalculateRequest;
import com.liferay.commerce.tax.CommerceTaxEngine;
import com.liferay.commerce.tax.CommerceTaxValue;
import com.liferay.commerce.tax.engine.fixed.configuration.CommerceTaxByAddressTypeConfiguration;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.tax.engine.key=" + ByAddressCommerceTaxEngine.KEY,
	service = CommerceTaxEngine.class
)
public class ByAddressCommerceTaxEngine implements CommerceTaxEngine {

	public static final String KEY = "by-address";

	@Override
	public CommerceTaxValue getCommerceTaxValue(
			CommerceTaxCalculateRequest commerceTaxCalculateRequest)
		throws CommerceTaxEngineException {

		long countryId = 0;
		long regionId = 0;
		String zip = StringPool.BLANK;

		long commerceAddressId =
			commerceTaxCalculateRequest.getCommerceBillingAddressId();

		if (_isTaxAppliedToShippingAddress(
				commerceTaxCalculateRequest.getCommerceChannelGroupId())) {

			commerceAddressId =
				commerceTaxCalculateRequest.getCommerceShippingAddressId();
		}

		CommerceAddress commerceAddress =
			_commerceAddressLocalService.fetchCommerceAddress(
				commerceAddressId);

		if (commerceAddress != null) {
			countryId = commerceAddress.getCountryId();
			regionId = commerceAddress.getRegionId();
			zip = commerceAddress.getZip();
		}

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxCalculateRequest.getCommerceTaxMethodId(),
					commerceTaxCalculateRequest.getTaxCategoryId(), countryId,
					regionId, zip);

		if (commerceTaxFixedRateAddressRel == null) {
			return null;
		}

		BigDecimal rate = BigDecimal.valueOf(
			commerceTaxFixedRateAddressRel.getRate());

		BigDecimal taxValue = rate;

		if (commerceTaxCalculateRequest.isPercentage()) {
			BigDecimal amount = commerceTaxCalculateRequest.getPrice();

			taxValue = amount.multiply(rate);

			BigDecimal denominator = _ONE_HUNDRED;

			if (commerceTaxCalculateRequest.isIncludeTax()) {
				denominator = _ONE_HUNDRED.add(rate);
			}

			taxValue = taxValue.divide(
				denominator, _SCALE, RoundingMode.HALF_EVEN);
		}

		return new CommerceTaxValue(KEY, KEY, taxValue);
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			_getResourceBundle(locale), "by-address-tax-rate-description");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(_getResourceBundle(locale), KEY);
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private boolean _isTaxAppliedToShippingAddress(long groupId) {
		try {
			CommerceTaxByAddressTypeConfiguration
				commerceTaxByAddressTypeConfiguration =
					_configurationProvider.getGroupConfiguration(
						CommerceTaxByAddressTypeConfiguration.class, groupId);

			return commerceTaxByAddressTypeConfiguration.
				taxAppliedToShippingAddress();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final int _SCALE = 10;

	private static final Log _log = LogFactoryUtil.getLog(
		ByAddressCommerceTaxEngine.class);

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

}