/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.order.dto.v1_0.OrderRuleOrderType;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.order.rule.model.COREntryRel-OrderType"
	},
	service = DTOConverter.class
)
public class OrderRuleOrderTypeDTOConverter
	implements DTOConverter<COREntryRel, OrderRuleOrderType> {

	@Override
	public String getContentType() {
		return OrderRuleOrderType.class.getSimpleName();
	}

	@Override
	public OrderRuleOrderType toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		COREntryRel corEntryRel = _corEntryRelService.getCOREntryRel(
			(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				corEntryRel.getClassPK());
		COREntry corEntry = corEntryRel.getCOREntry();

		return new OrderRuleOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setOrderRuleExternalReferenceCode(
					corEntry::getExternalReferenceCode);
				setOrderRuleId(corEntry::getCOREntryId);
				setOrderRuleOrderTypeId(corEntryRel::getCOREntryRelId);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
			}
		};
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private COREntryRelService _corEntryRelService;

}