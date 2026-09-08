/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifier;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.pricing.model.CommercePriceModifier"
	},
	service = DTOConverter.class
)
public class PriceModifierDTOConverter
	implements DTOConverter<CommercePriceModifier, PriceModifier> {

	@Override
	public String getContentType() {
		return PriceModifier.class.getSimpleName();
	}

	@Override
	public PriceModifier toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierService.getCommercePriceModifier(
				(Long)dtoConverterContext.getId());

		CommercePriceList commercePriceList =
			_commercePriceListService.getCommercePriceList(
				commercePriceModifier.getCommercePriceListId());

		return new PriceModifier() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(() -> !commercePriceModifier.isInactive());
				setDisplayDate(commercePriceModifier::getDisplayDate);
				setExpirationDate(commercePriceModifier::getExpirationDate);
				setExternalReferenceCode(
					commercePriceModifier::getExternalReferenceCode);
				setId(commercePriceModifier::getCommercePriceModifierId);
				setModifierAmount(commercePriceModifier::getModifierAmount);
				setModifierType(commercePriceModifier::getModifierType);
				setPriceListExternalReferenceCode(
					commercePriceList::getExternalReferenceCode);
				setPriceListId(commercePriceList::getCommercePriceListId);
				setPriority(commercePriceModifier::getPriority);
				setTarget(commercePriceModifier::getTarget);
				setTitle(commercePriceModifier::getTitle);
			}
		};
	}

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommercePriceModifierService _commercePriceModifierService;

}