/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceEntry;
import com.liferay.petra.string.StringPool;
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
		"dto.class.name=com.liferay.commerce.price.list.model.CommercePriceEntry"
	},
	service = DTOConverter.class
)
public class PriceEntryDTOConverter
	implements DTOConverter<CommercePriceEntry, PriceEntry> {

	@Override
	public String getContentType() {
		return PriceEntry.class.getSimpleName();
	}

	@Override
	public PriceEntry toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getCommercePriceEntry(
				(Long)dtoConverterContext.getId());

		CommercePriceList commercePriceList =
			commercePriceEntry.getCommercePriceList();

		CommerceCurrency commerceCurrency =
			commercePriceList.getCommerceCurrency();

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		BigDecimal priceEntryPrice = commercePriceEntry.getPrice();

		return new PriceEntry() {
			{
				setActions(dtoConverterContext::getActions);
				setBulkPricing(commercePriceEntry::isBulkPricing);
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commercePriceEntry.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDiscountDiscovery(commercePriceEntry::isDiscountDiscovery);
				setDiscountLevel1(commercePriceEntry::getDiscountLevel1);
				setDiscountLevel2(commercePriceEntry::getDiscountLevel2);
				setDiscountLevel3(commercePriceEntry::getDiscountLevel3);
				setDiscountLevel4(commercePriceEntry::getDiscountLevel4);
				setDiscountLevelsFormatted(
					() -> _getDiscountLevelsFormatted(commercePriceEntry));
				setDisplayDate(commercePriceEntry::getDisplayDate);
				setExpirationDate(commercePriceEntry::getExpirationDate);
				setExternalReferenceCode(
					commercePriceEntry::getExternalReferenceCode);
				setHasTierPrice(commercePriceEntry::isHasTierPrice);
				setPrice(priceEntryPrice::doubleValue);
				setPriceEntryId(commercePriceEntry::getCommercePriceEntryId);
				setPriceFormatted(
					() -> _formatPrice(
						priceEntryPrice, commerceCurrency,
						dtoConverterContext.getLocale()));
				setPriceListId(commercePriceEntry::getCommercePriceListId);
				setPriceOnApplication(commercePriceEntry::isPriceOnApplication);
				setQuantity(commercePriceEntry::getQuantity);
				setSkuExternalReferenceCode(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getExternalReferenceCode();
					});
				setSkuId(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getCPInstanceId();
					});
				setUnitOfMeasureKey(commercePriceEntry::getUnitOfMeasureKey);
			}
		};
	}

	private String _formatPrice(
			BigDecimal price, CommerceCurrency commerceCurrency, Locale locale)
		throws Exception {

		if (price == null) {
			price = BigDecimal.ZERO;
		}

		return _commercePriceFormatter.format(
			commerceCurrency, true, locale, price);
	}

	private String _getDiscountLevelFormatted(BigDecimal discountLevel) {
		if (discountLevel == null) {
			return StringPool.BLANK;
		}

		return String.valueOf(discountLevel.doubleValue());
	}

	private String _getDiscountLevelsFormatted(
		CommercePriceEntry commercePriceEntry) {

		if (commercePriceEntry.isDiscountDiscovery() ||
			((commercePriceEntry.getDiscountLevel1() == null) &&
			 (commercePriceEntry.getDiscountLevel2() == null) &&
			 (commercePriceEntry.getDiscountLevel3() == null) &&
			 (commercePriceEntry.getDiscountLevel4() == null))) {

			return StringPool.BLANK;
		}

		StringBuffer sb = new StringBuffer();

		sb.append(
			_getDiscountLevelFormatted(commercePriceEntry.getDiscountLevel1()));
		sb.append(" - ");
		sb.append(
			_getDiscountLevelFormatted(commercePriceEntry.getDiscountLevel2()));
		sb.append(" - ");
		sb.append(
			_getDiscountLevelFormatted(commercePriceEntry.getDiscountLevel3()));
		sb.append(" - ");
		sb.append(
			_getDiscountLevelFormatted(commercePriceEntry.getDiscountLevel4()));

		return sb.toString();
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

}