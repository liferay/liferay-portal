/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelTerm;
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
		"dto.class.name=com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier-Term"
	},
	service = DTOConverter.class
)
public class PaymentMethodGroupRelTermDTOConverter
	implements DTOConverter
		<CommercePaymentMethodGroupRelQualifier, PaymentMethodGroupRelTerm> {

	@Override
	public String getContentType() {
		return PaymentMethodGroupRelTerm.class.getSimpleName();
	}

	@Override
	public PaymentMethodGroupRelTerm toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				_commercePaymentMethodGroupRelQualifierService.
					getCommercePaymentMethodGroupRelQualifier(
						(Long)dtoConverterContext.getId());

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryService.getCommerceTermEntry(
				commercePaymentMethodGroupRelQualifier.getClassPK());

		return new PaymentMethodGroupRelTerm() {
			{
				setActions(dtoConverterContext::getActions);
				setPaymentMethodGroupRelId(
					commercePaymentMethodGroupRelQualifier::
						getCommercePaymentMethodGroupRelId);
				setPaymentMethodGroupRelTermId(
					commercePaymentMethodGroupRelQualifier::
						getCommercePaymentMethodGroupRelQualifierId);
				setTermExternalReferenceCode(
					commerceTermEntry::getExternalReferenceCode);
				setTermId(commerceTermEntry::getCommerceTermEntryId);
			}
		};
	}

	@Reference
	private CommercePaymentMethodGroupRelQualifierService
		_commercePaymentMethodGroupRelQualifierService;

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

}