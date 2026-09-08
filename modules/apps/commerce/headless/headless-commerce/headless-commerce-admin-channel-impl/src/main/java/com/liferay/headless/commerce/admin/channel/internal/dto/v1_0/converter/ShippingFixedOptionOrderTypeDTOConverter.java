/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionQualifierService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionOrderType;
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
		"dto.class.name=com.liferay.commerce.payment.model.CommerceShippingFixedOptionQualifier-OrderType"
	},
	service = DTOConverter.class
)
public class ShippingFixedOptionOrderTypeDTOConverter
	implements DTOConverter
		<CommerceShippingFixedOptionQualifier, ShippingFixedOptionOrderType> {

	@Override
	public String getContentType() {
		return ShippingFixedOptionOrderType.class.getSimpleName();
	}

	@Override
	public ShippingFixedOptionOrderType toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				_commerceShippingFixedOptionQualifierService.
					getCommerceShippingFixedOptionQualifier(
						(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				commerceShippingFixedOptionQualifier.getClassPK());

		return new ShippingFixedOptionOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
				setShippingFixedOptionId(
					commerceShippingFixedOptionQualifier::
						getCommerceShippingFixedOptionId);
				setShippingFixedOptionOrderTypeId(
					commerceShippingFixedOptionQualifier::
						getCommerceShippingFixedOptionQualifierId);
			}
		};
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommerceShippingFixedOptionQualifierService
		_commerceShippingFixedOptionQualifierService;

}