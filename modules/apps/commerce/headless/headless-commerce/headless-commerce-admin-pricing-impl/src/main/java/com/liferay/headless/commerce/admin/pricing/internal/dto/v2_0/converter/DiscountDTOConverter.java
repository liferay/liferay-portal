/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscount"
	},
	service = DTOConverter.class
)
public class DiscountDTOConverter
	implements DTOConverter<CommerceDiscount, Discount> {

	@Override
	public String getContentType() {
		return Discount.class.getSimpleName();
	}

	@Override
	public Discount toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.getCommerceDiscount(
				(Long)dtoConverterContext.getId());

		return new Discount() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(commerceDiscount::isActive);
				setAmountFormatted(
					() -> _getAmountFormatted(
						commerceDiscount, dtoConverterContext.getLocale()));
				setCouponCode(commerceDiscount::getCouponCode);
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commerceDiscount.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDisplayDate(commerceDiscount::getDisplayDate);
				setExpirationDate(commerceDiscount::getExpirationDate);
				setExternalReferenceCode(
					commerceDiscount::getExternalReferenceCode);
				setId(commerceDiscount::getCommerceDiscountId);
				setLevel(commerceDiscount::getLevel);
				setLimitationTimes(commerceDiscount::getLimitationTimes);
				setLimitationTimesPerAccount(
					commerceDiscount::getLimitationTimesPerAccount);
				setLimitationType(commerceDiscount::getLimitationType);
				setMaximumDiscountAmount(
					commerceDiscount::getMaximumDiscountAmount);
				setModifiedDate(commerceDiscount::getModifiedDate);
				setNumberOfUse(commerceDiscount::getNumberOfUse);
				setPercentageLevel1(commerceDiscount::getLevel1);
				setPercentageLevel2(commerceDiscount::getLevel2);
				setPercentageLevel3(commerceDiscount::getLevel3);
				setPercentageLevel4(commerceDiscount::getLevel4);
				setRulesConjunction(commerceDiscount::isRulesConjunction);
				setTarget(
					() -> _language.get(
						LanguageResources.getResourceBundle(
							dtoConverterContext.getLocale()),
						commerceDiscount.getTarget()));
				setTitle(commerceDiscount::getTitle);
				setUseCouponCode(commerceDiscount::isUseCouponCode);
				setUsePercentage(commerceDiscount::isUsePercentage);
			}
		};
	}

	private BigDecimal _getAmount(CommerceDiscount commerceDiscount) {
		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L1)) {

			return commerceDiscount.getLevel1();
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L2)) {

			return commerceDiscount.getLevel2();
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L3)) {

			return commerceDiscount.getLevel3();
		}

		if (Objects.equals(
				commerceDiscount.getLevel(),
				CommerceDiscountConstants.LEVEL_L4)) {

			return commerceDiscount.getLevel4();
		}

		return BigDecimal.ZERO;
	}

	private String _getAmountFormatted(
			CommerceDiscount commerceDiscount, Locale locale)
		throws Exception {

		BigDecimal amount = _getAmount(commerceDiscount);

		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		CommerceCurrency commerceCurrency =
			_commerceCurrencyService.fetchPrimaryCommerceCurrency(
				commerceDiscount.getCompanyId());

		if (commerceDiscount.isUsePercentage()) {
			return _percentageFormatter.getLocalizedPercentage(
				locale, commerceCurrency.getMaxFractionDigits(),
				commerceCurrency.getMinFractionDigits(), amount);
		}

		return _commercePriceFormatter.format(
			commerceCurrency, true, locale, amount);
	}

	@Reference
	private CommerceCurrencyService _commerceCurrencyService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private Language _language;

	@Reference
	private PercentageFormatter _percentageFormatter;

}