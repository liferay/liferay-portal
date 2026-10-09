/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.exception.CommerceShippingEngineException;
import com.liferay.commerce.helper.CommerceShippingHelper;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressRestrictionLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionQualifierLocalService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionRelLocalService;
import com.liferay.commerce.shipping.engine.fixed.util.comparator.CommerceShippingFixedOptionPriorityComparator;
import com.liferay.commerce.shipping.engine.fixed.web.internal.util.CommerceShippingFixedOptionEngineUtil;
import com.liferay.commerce.util.comparator.CommerceShippingOptionPriorityComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.shipping.engine.key=" + ByWeightCommerceShippingEngine.KEY,
	service = CommerceShippingEngine.class
)
public class ByWeightCommerceShippingEngine implements CommerceShippingEngine {

	public static final String KEY = "by-weight";

	@Override
	public String getCommerceShippingOptionLabel(String name, Locale locale) {
		CommerceShippingFixedOption commerceShippingFixedOption =
			_commerceShippingFixedOptionLocalService.
				fetchCommerceShippingFixedOption(
					CompanyThreadLocal.getCompanyId(), name);

		if (commerceShippingFixedOption == null) {
			return StringPool.BLANK;
		}

		return commerceShippingFixedOption.getName(locale);
	}

	@Override
	public List<CommerceShippingOption> getCommerceShippingOptions(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			Locale locale)
		throws CommerceShippingEngineException {

		List<CommerceShippingOption> commerceShippingOptions =
			new ArrayList<>();

		try {
			commerceShippingOptions = _getCommerceShippingOptions(
				false, commerceOrder, locale);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return commerceShippingOptions;
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(
			_getResourceBundle(locale), "by-weight-description");
	}

	@Override
	public List<CommerceShippingOption> getEnabledCommerceShippingOptions(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			Locale locale)
		throws CommerceShippingEngineException {

		List<CommerceShippingOption> commerceShippingOptions =
			new ArrayList<>();

		try {
			commerceShippingOptions = _getCommerceShippingOptions(
				true, commerceOrder, locale);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return commerceShippingOptions;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(_getResourceBundle(locale), "variable-rate");
	}

	private List<CommerceShippingFixedOption> _getCommerceShippingFixedOptions(
		long groupId) {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				groupId, KEY);

		if (commerceShippingMethod == null) {
			return Collections.emptyList();
		}

		return _commerceShippingFixedOptionLocalService.
			getCommerceShippingFixedOptions(
				commerceShippingMethod.getCommerceShippingMethodId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				CommerceShippingFixedOptionPriorityComparator.getInstance(
					false));
	}

	private CommerceShippingOption _getCommerceShippingOption(
			CommerceOrder commerceOrder, Locale locale,
			CommerceAddress commerceAddress,
			CommerceShippingFixedOption commerceShippingFixedOption)
		throws PortalException {

		double orderWeight = _commerceShippingHelper.getWeight(
			commerceOrder.getCommerceOrderItems());

		long commerceCountryId = 0;
		long commerceRegionId = 0;
		String zip = StringPool.BLANK;

		if (commerceAddress != null) {
			commerceCountryId = commerceAddress.getCountryId();
			commerceRegionId = commerceAddress.getRegionId();
			zip = commerceAddress.getZip();
		}

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			_commerceShippingFixedOptionRelLocalService.
				fetchCommerceShippingFixedOptionRel(
					commerceShippingFixedOption.
						getCommerceShippingFixedOptionId(),
					commerceCountryId, commerceRegionId, zip, orderWeight);

		if (commerceShippingFixedOptionRel == null) {
			return null;
		}

		String key = commerceShippingFixedOption.getKey();
		String name = commerceShippingFixedOption.getName(locale);
		double priority = commerceShippingFixedOption.getPriority();

		if (_commerceShippingHelper.isFreeShipping(commerceOrder)) {
			return new CommerceShippingOption(
				BigDecimal.ZERO, KEY, key, name, priority);
		}

		BigDecimal amount = commerceShippingFixedOptionRel.getFixedPrice();

		BigDecimal rateUnitWeightPrice =
			commerceShippingFixedOptionRel.getRateUnitWeightPrice();

		if (rateUnitWeightPrice.compareTo(BigDecimal.ZERO) > 0) {
			amount = amount.add(
				rateUnitWeightPrice.multiply(new BigDecimal(orderWeight)));
		}

		BigDecimal ratePercentage = new BigDecimal(
			commerceShippingFixedOptionRel.getRatePercentage());

		BigDecimal orderPrice = _getOrderShippableSubtotal(commerceOrder);

		amount = amount.add(
			ratePercentage.multiply(orderPrice.divide(new BigDecimal(100))));

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		String commerceCurrencyCode = commerceCurrency.getCode();

		if (!commerceCurrencyCode.equals(
				commerceChannel.getCommerceCurrencyCode())) {

			CommerceCurrency commerceChannelCommerceCurrency =
				_commerceCurrencyLocalService.getCommerceCurrency(
					commerceOrder.getCompanyId(),
					commerceChannel.getCommerceCurrencyCode());

			amount = amount.divide(
				commerceChannelCommerceCurrency.getRate(),
				RoundingMode.valueOf(
					commerceChannelCommerceCurrency.getRoundingMode()));

			amount = amount.multiply(commerceCurrency.getRate());
		}

		return new CommerceShippingOption(amount, KEY, key, name, priority);
	}

	private List<CommerceShippingOption> _getCommerceShippingOptions(
			boolean checkEligibility, CommerceOrder commerceOrder,
			Locale locale)
		throws PortalException {

		List<CommerceShippingOption> commerceShippingOptions =
			new ArrayList<>();

		long commerceCountryId = 0;

		CommerceAddress commerceAddress = commerceOrder.getShippingAddress();

		if (commerceAddress != null) {
			commerceCountryId = commerceAddress.getCountryId();
		}

		List<CommerceShippingFixedOption> commerceShippingFixedOptions = null;

		if (checkEligibility) {
			commerceShippingFixedOptions =
				CommerceShippingFixedOptionEngineUtil.
					getEligibleCommerceShippingFixedOptions(
						commerceOrder.getCommerceOrderTypeId(),
						_commerceShippingFixedOptionQualifierLocalService,
						_getCommerceShippingFixedOptions(
							commerceOrder.getGroupId()));
		}
		else {
			commerceShippingFixedOptions = _getCommerceShippingFixedOptions(
				commerceOrder.getGroupId());
		}

		for (CommerceShippingFixedOption commerceShippingFixedOption :
				commerceShippingFixedOptions) {

			boolean restricted =
				_commerceAddressRestrictionLocalService.
					isCommerceShippingMethodRestricted(
						commerceShippingFixedOption.
							getCommerceShippingMethodId(),
						commerceCountryId);

			if (restricted) {
				continue;
			}

			CommerceShippingOption commerceShippingOption =
				_getCommerceShippingOption(
					commerceOrder, locale, commerceAddress,
					commerceShippingFixedOption);

			if (commerceShippingOption == null) {
				continue;
			}

			commerceShippingOptions.add(commerceShippingOption);
		}

		return ListUtil.sort(
			commerceShippingOptions,
			new CommerceShippingOptionPriorityComparator());
	}

	private BigDecimal _getOrderShippableSubtotal(CommerceOrder commerceOrder) {
		BigDecimal subtotal = BigDecimal.ZERO;
		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			if (!commerceOrderItem.isShippable()) {
				continue;
			}

			subtotal = subtotal.add(commerceOrderItem.getFinalPrice());
		}

		return subtotal;
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ByWeightCommerceShippingEngine.class);

	@Reference
	private CommerceAddressRestrictionLocalService
		_commerceAddressRestrictionLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingFixedOptionQualifierLocalService
		_commerceShippingFixedOptionQualifierLocalService;

	@Reference
	private CommerceShippingFixedOptionRelLocalService
		_commerceShippingFixedOptionRelLocalService;

	@Reference
	private CommerceShippingHelper _commerceShippingHelper;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private Language _language;

}