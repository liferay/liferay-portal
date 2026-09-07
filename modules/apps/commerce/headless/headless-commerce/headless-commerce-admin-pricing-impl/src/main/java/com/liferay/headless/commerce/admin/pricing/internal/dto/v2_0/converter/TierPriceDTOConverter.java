/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.price.list.model.CommerceTierPriceEntry"
	},
	service = DTOConverter.class
)
public class TierPriceDTOConverter
	implements DTOConverter<CommerceTierPriceEntry, TierPrice> {

	@Override
	public String getContentType() {
		return TierPrice.class.getSimpleName();
	}

	@Override
	public TierPrice toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryService.getCommerceTierPriceEntry(
				(Long)dtoConverterContext.getId());

		CommercePriceEntry commercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		CommercePriceList commercePriceList =
			commercePriceEntry.getCommercePriceList();

		CommerceCurrency commerceCurrency =
			commercePriceList.getCommerceCurrency();

		ExpandoBridge expandoBridge = commerceTierPriceEntry.getExpandoBridge();

		BigDecimal tierPriceEntryPrice = commerceTierPriceEntry.getPrice();

		Locale locale = dtoConverterContext.getLocale();

		return new TierPrice() {
			{
				setActions(dtoConverterContext::getActions);
				setCustomFields(expandoBridge::getAttributes);
				setDiscountDiscovery(
					commerceTierPriceEntry::isDiscountDiscovery);
				setDiscountLevel1(commerceTierPriceEntry::getDiscountLevel1);
				setDiscountLevel2(commerceTierPriceEntry::getDiscountLevel2);
				setDiscountLevel3(commerceTierPriceEntry::getDiscountLevel3);
				setDiscountLevel4(commerceTierPriceEntry::getDiscountLevel4);
				setDisplayDate(commerceTierPriceEntry::getDisplayDate);
				setExpirationDate(commerceTierPriceEntry::getExpirationDate);
				setExternalReferenceCode(
					commerceTierPriceEntry::getExternalReferenceCode);
				setId(commerceTierPriceEntry::getCommerceTierPriceEntryId);
				setMinimumQuantity(
					() -> _commerceQuantityFormatter.format(
						_cpInstanceLocalService.fetchCPInstance(
							commercePriceEntry.getCProductId(),
							commercePriceEntry.getCPInstanceUuid()),
						commerceTierPriceEntry.getMinQuantity(),
						commercePriceEntry.getUnitOfMeasureKey()));
				setPrice(tierPriceEntryPrice::doubleValue);
				setPriceEntryExternalReferenceCode(
					commercePriceEntry::getExternalReferenceCode);
				setPriceEntryId(commercePriceEntry::getCommercePriceEntryId);
				setPriceFormatted(
					() -> {
						BigDecimal price = tierPriceEntryPrice;

						if (price == null) {
							price = BigDecimal.ZERO;
						}

						return _commercePriceFormatter.format(
							commerceCurrency, true, locale, price);
					});
				setUnitOfMeasureKey(commercePriceEntry::getUnitOfMeasureKey);
			}
		};
	}

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

}