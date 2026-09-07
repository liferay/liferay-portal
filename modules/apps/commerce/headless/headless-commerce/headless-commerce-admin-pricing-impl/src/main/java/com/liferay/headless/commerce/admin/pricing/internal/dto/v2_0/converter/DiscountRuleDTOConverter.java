/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter;

import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.discount.model.CommerceDiscountRule"
	},
	service = DTOConverter.class
)
public class DiscountRuleDTOConverter
	implements DTOConverter<CommerceDiscountRule, DiscountRule> {

	@Override
	public String getContentType() {
		return DiscountRule.class.getSimpleName();
	}

	@Override
	public DiscountRule toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceDiscountRule commerceDiscountRule =
			_commerceDiscountRuleService.getCommerceDiscountRule(
				(Long)dtoConverterContext.getId());

		return new DiscountRule() {
			{
				setActions(dtoConverterContext::getActions);
				setDiscountId(commerceDiscountRule::getCommerceDiscountId);
				setId(commerceDiscountRule::getCommerceDiscountRuleId);
				setName(commerceDiscountRule::getName);
				setType(commerceDiscountRule::getType);
				setTypeSettings(commerceDiscountRule::getTypeSettings);
			}
		};
	}

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

}