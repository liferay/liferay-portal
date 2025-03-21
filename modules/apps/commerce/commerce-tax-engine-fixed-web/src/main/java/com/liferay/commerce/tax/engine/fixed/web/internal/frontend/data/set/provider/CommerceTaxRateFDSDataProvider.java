/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.web.internal.frontend.data.set.provider;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateService;
import com.liferay.commerce.tax.engine.fixed.web.internal.constants.CommerceTaxRateSettingFDSNames;
import com.liferay.commerce.tax.engine.fixed.web.internal.model.TaxRate;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceTaxRateSettingFDSNames.TAX_RATES,
	service = FDSDataProvider.class
)
public class CommerceTaxRateFDSDataProvider
	implements FDSDataProvider<TaxRate> {

	@Override
	public List<TaxRate> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");
		long commerceTaxMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceTaxMethodId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				commerceChannel.getCompanyId(),
				commerceChannel.getCommerceCurrencyCode());

		CommerceTaxMethod commerceTaxMethod =
			_commerceTaxMethodLocalService.getCommerceTaxMethod(
				commerceTaxMethodId);

		return TransformUtil.transform(
			_commerceTaxFixedRateService.getCommerceTaxFixedRates(
				commerceChannel.getGroupId(), commerceTaxMethodId,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), null),
			commerceTaxFixedRate -> {
				CPTaxCategory cpTaxCategory =
					commerceTaxFixedRate.getCPTaxCategory();

				return new TaxRate(
					cpTaxCategory.getName(themeDisplay.getLanguageId()),
					_getLocalizedRate(
						commerceCurrency, commerceTaxMethod.isPercentage(),
						commerceTaxFixedRate.getRate(),
						_portal.getLocale(httpServletRequest)),
					commerceTaxFixedRate.getCommerceTaxFixedRateId());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");
		long commerceTaxMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceTaxMethodId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		return _commerceTaxFixedRateService.getCommerceTaxFixedRatesCount(
			commerceChannel.getGroupId(), commerceTaxMethodId);
	}

	private String _getLocalizedRate(
			CommerceCurrency commerceCurrency, boolean percentage, double rate,
			Locale locale)
		throws PortalException {

		BigDecimal bigDecimalPercentage = new BigDecimal(rate);

		if (percentage) {
			return _percentageFormatter.getLocalizedPercentage(
				locale, commerceCurrency.getMaxFractionDigits(),
				commerceCurrency.getMinFractionDigits(), bigDecimalPercentage);
		}

		CommerceMoney commerceMoney = _commerceMoneyFactory.create(
			commerceCurrency, bigDecimalPercentage);

		return commerceMoney.format(locale);
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceMoneyFactory _commerceMoneyFactory;

	@Reference
	private CommerceTaxFixedRateService _commerceTaxFixedRateService;

	@Reference
	private CommerceTaxMethodLocalService _commerceTaxMethodLocalService;

	@Reference
	private PercentageFormatter _percentageFormatter;

	@Reference
	private Portal _portal;

}