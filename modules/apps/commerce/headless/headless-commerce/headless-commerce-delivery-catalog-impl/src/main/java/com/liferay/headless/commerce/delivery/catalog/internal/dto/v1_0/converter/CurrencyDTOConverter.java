/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Currency;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Currency"
	},
	service = DTOConverter.class
)
public class CurrencyDTOConverter
	implements DTOConverter<CommerceCurrency, Currency> {

	@Override
	public String getContentType() {
		return Currency.class.getSimpleName();
	}

	@Override
	public Currency toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				(Long)dtoConverterContext.getId());

		return new Currency() {
			{
				setActive(commerceCurrency::isActive);
				setCode(commerceCurrency::getCode);
				setExternalReferenceCode(
					commerceCurrency::getExternalReferenceCode);
				setFormatPattern(
					() -> LanguageUtils.getLanguageIdMap(
						commerceCurrency.getFormatPatternMap()));
				setId(commerceCurrency::getCommerceCurrencyId);
				setMaxFractionDigits(commerceCurrency::getMaxFractionDigits);
				setMinFractionDigits(commerceCurrency::getMinFractionDigits);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						commerceCurrency.getNameMap()));
				setPrimary(commerceCurrency::getPrimary);
				setPriority(commerceCurrency::getPriority);
				setRate(commerceCurrency::getRate);
				setRoundingMode(
					() -> RoundingMode.valueOf(
						commerceCurrency.getRoundingMode()));
				setSymbol(commerceCurrency::getSymbol);
			}
		};
	}

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

}