/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.service.CommerceTermEntryRelService;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.TermOrderType;
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
		"dto.class.name=com.liferay.commerce.term.model.CommerceTermEntryRel-OrderType"
	},
	service = DTOConverter.class
)
public class TermOrderTypeDTOConverter
	implements DTOConverter<CommerceTermEntryRel, TermOrderType> {

	@Override
	public String getContentType() {
		return TermOrderType.class.getSimpleName();
	}

	@Override
	public TermOrderType toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceTermEntryRel commerceTermEntryRel =
			_commerceTermEntryRelService.getCommerceTermEntryRel(
				(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				commerceTermEntryRel.getClassPK());

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryService.getCommerceTermEntry(
				commerceTermEntryRel.getCommerceTermEntryId());

		return new TermOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
				setTermExternalReferenceCode(
					commerceTermEntry::getExternalReferenceCode);
				setTermId(commerceTermEntry::getCommerceTermEntryId);
				setTermOrderTypeId(
					commerceTermEntryRel::getCommerceTermEntryRelId);
			}
		};
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommerceTermEntryRelService _commerceTermEntryRelService;

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

}