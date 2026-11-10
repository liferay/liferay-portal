/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.model;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.currency.model.CommerceMoneyFactoryUtil;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.currency.util.PriceFormat;
import com.liferay.portal.kernel.exception.PortalException;

import java.math.BigDecimal;

import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = CommerceMoneyFactory.class)
public class CommerceMoneyFactoryImpl implements CommerceMoneyFactory {

	@Override
	public CommerceMoney create(
		CommerceCurrency commerceCurrency, BigDecimal price) {

		return new CommerceMoneyImpl(
			commerceCurrency, _commercePriceFormatter, price);
	}

	@Override
	public CommerceMoney create(
		CommerceCurrency commerceCurrency, BigDecimal price,
		PriceFormat priceFormat) {

		if (priceFormat == null) {
			throw new IllegalArgumentException("Price format must not be null");
		}

		CommerceMoney commerceMoney = new CommerceMoneyImpl(
			commerceCurrency, _commercePriceFormatter, price);

		if (priceFormat == PriceFormat.RELATIVE) {
			commerceMoney = new RelativeCommerceMoneyImpl(
				commerceCurrency, _commercePriceFormatter, price);
		}

		return commerceMoney;
	}

	@Override
	public CommerceMoney create(long commerceCurrencyId, BigDecimal price)
		throws PortalException {

		return create(
			_commerceCurrencyLocalService.getCommerceCurrency(
				commerceCurrencyId),
			price);
	}

	@Override
	public CommerceMoney emptyCommerceMoney() {
		if (_emptyCommerceMoney != null) {
			return _emptyCommerceMoney;
		}

		_emptyCommerceMoney = new CommerceMoney() {

			@Override
			public String format(Locale locale) throws PortalException {
				return _commercePriceFormatter.format(BigDecimal.ZERO, locale);
			}

			@Override
			public CommerceCurrency getCommerceCurrency() {
				throw new UnsupportedOperationException();
			}

			@Override
			public BigDecimal getPrice() {
				return BigDecimal.ZERO;
			}

			@Override
			public boolean isEmpty() {
				return true;
			}

			@Override
			public boolean isPriceOnApplication() {
				return false;
			}

		};

		return _emptyCommerceMoney;
	}

	@Override
	public CommerceMoney priceOnApplicationCommerceMoney() {
		if (_priceOnApplicationCommerceMoney != null) {
			return _priceOnApplicationCommerceMoney;
		}

		_priceOnApplicationCommerceMoney = new CommerceMoney() {

			@Override
			public String format(Locale locale) throws PortalException {
				return _commercePriceFormatter.format(BigDecimal.ZERO, locale);
			}

			@Override
			public CommerceCurrency getCommerceCurrency() {
				return null;
			}

			@Override
			public BigDecimal getPrice() {
				return BigDecimal.ZERO;
			}

			@Override
			public boolean isEmpty() {
				return true;
			}

			@Override
			public boolean isPriceOnApplication() {
				return true;
			}

		};

		return _priceOnApplicationCommerceMoney;
	}

	@Activate
	protected void activate() {
		CommerceMoneyFactoryUtil.setCommerceMoneyFactory(this);
	}

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	private CommerceMoney _emptyCommerceMoney;
	private CommerceMoney _priceOnApplicationCommerceMoney;

}