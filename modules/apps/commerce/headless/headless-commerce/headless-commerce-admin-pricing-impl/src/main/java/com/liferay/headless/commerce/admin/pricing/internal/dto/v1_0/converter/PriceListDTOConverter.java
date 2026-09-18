/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceList;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.price.list.model.CommercePriceList",
	service = DTOConverter.class
)
public class PriceListDTOConverter
	implements DTOConverter<CommercePriceList, PriceList> {

	@Override
	public String getContentType() {
		return PriceList.class.getSimpleName();
	}

	@Override
	public PriceList toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListService.getCommercePriceList(
				(Long)dtoConverterContext.getId());

		CommerceCatalog commerceCatalog =
			_commerceCatalogService.fetchCommerceCatalogByGroupId(
				commercePriceList.getGroupId());

		return new PriceList() {
			{
				setActive(() -> !commercePriceList.isInactive());
				setCatalogCurrencyCode(
					() -> {
						if (commerceCatalog == null) {
							return null;
						}

						return commerceCatalog.getCommerceCurrencyCode();
					});
				setCatalogCurrencyExternalReferenceCode(
					() -> {
						CommerceCurrency catalogCommerceCurrency =
							_fetchCommerceCurrency(commerceCatalog);

						if (catalogCommerceCurrency == null) {
							return null;
						}

						return catalogCommerceCurrency.
							getExternalReferenceCode();
					});
				setCatalogExternalReferenceCode(
					() -> {
						if (commerceCatalog == null) {
							return null;
						}

						return commerceCatalog.getExternalReferenceCode();
					});
				setCatalogId(
					() -> {
						if (commerceCatalog == null) {
							return 0L;
						}

						return commerceCatalog.getCommerceCatalogId();
					});
				setCurrencyCode(
					() -> {
						CommerceCurrency commerceCurrency =
							commercePriceList.getCommerceCurrency();

						return commerceCurrency.getCode();
					});
				setCurrencyExternalReferenceCode(
					() -> {
						CommerceCurrency commerceCurrency =
							commercePriceList.getCommerceCurrency();

						return commerceCurrency.getExternalReferenceCode();
					});
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commercePriceList.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDisplayDate(commercePriceList::getDisplayDate);
				setExpirationDate(commercePriceList::getExpirationDate);
				setExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setId(commercePriceList::getCommercePriceListId);
				setName(commercePriceList::getName);
				setPriority(commercePriceList::getPriority);
			}
		};
	}

	private CommerceCurrency _fetchCommerceCurrency(
		CommerceCatalog commerceCatalog) {

		if (commerceCatalog == null) {
			return null;
		}

		return _commerceCurrencyLocalService.fetchCommerceCurrency(
			commerceCatalog.getCompanyId(),
			commerceCatalog.getCommerceCurrencyCode());
	}

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePriceListService _commercePriceListService;

}