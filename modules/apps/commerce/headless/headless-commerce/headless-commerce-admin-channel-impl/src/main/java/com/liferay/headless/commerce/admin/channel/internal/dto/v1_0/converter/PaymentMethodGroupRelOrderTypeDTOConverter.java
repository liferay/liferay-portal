/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelOrderType;
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
		"dto.class.name=com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier-OrderType"
	},
	service = DTOConverter.class
)
public class PaymentMethodGroupRelOrderTypeDTOConverter
	implements DTOConverter
		<CommercePaymentMethodGroupRelQualifier,
		 PaymentMethodGroupRelOrderType> {

	@Override
	public String getContentType() {
		return PaymentMethodGroupRelOrderType.class.getSimpleName();
	}

	@Override
	public PaymentMethodGroupRelOrderType toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				_commercePaymentMethodGroupRelQualifierService.
					getCommercePaymentMethodGroupRelQualifier(
						(Long)dtoConverterContext.getId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.getCommerceOrderType(
				commercePaymentMethodGroupRelQualifier.getClassPK());

		return new PaymentMethodGroupRelOrderType() {
			{
				setActions(dtoConverterContext::getActions);
				setOrderTypeExternalReferenceCode(
					commerceOrderType::getExternalReferenceCode);
				setOrderTypeId(commerceOrderType::getCommerceOrderTypeId);
				setPaymentMethodGroupRelId(
					commercePaymentMethodGroupRelQualifier::
						getCommercePaymentMethodGroupRelId);
				setPaymentMethodGroupRelOrderTypeId(
					commercePaymentMethodGroupRelQualifier::
						getCommercePaymentMethodGroupRelQualifierId);
			}
		};
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePaymentMethodGroupRelQualifierService
		_commercePaymentMethodGroupRelQualifierService;

}