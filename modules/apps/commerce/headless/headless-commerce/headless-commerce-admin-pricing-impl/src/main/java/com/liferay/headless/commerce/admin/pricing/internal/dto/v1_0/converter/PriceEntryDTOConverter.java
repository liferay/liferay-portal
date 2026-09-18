/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceEntry;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.price.list.model.CommercePriceEntry",
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

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		CPDefinition cpDefinition = _fetchCPDefinition(cpInstance);

		return new PriceEntry() {
			{
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commercePriceEntry.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setExternalReferenceCode(
					commercePriceEntry::getExternalReferenceCode);
				setHasTierPrice(commercePriceEntry::isHasTierPrice);
				setId(commercePriceEntry::getCommercePriceEntryId);
				setPrice(commercePriceEntry::getPrice);
				setPriceListExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setPriceListId(commercePriceEntry::getCommercePriceListId);
				setProductExternalReferenceCode(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return cpDefinition.getCProductExternalReferenceCode();
					});
				setProductType(
					() -> {
						if (cpDefinition == null) {
							return null;
						}

						return cpDefinition.getProductTypeName();
					});
				setPromoPrice(commercePriceEntry::getPromoPrice);
				setSku(
					() -> {
						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getSku();
					});
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
			}
		};
	}

	private CPDefinition _fetchCPDefinition(CPInstance cpInstance)
		throws Exception {

		if (cpInstance == null) {
			return null;
		}

		return cpInstance.getCPDefinition();
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

}