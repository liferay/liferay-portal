/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionQualifierService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionTerm;
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
		"dto.class.name=com.liferay.commerce.payment.model.CommerceShippingFixedOptionQualifier-Term"
	},
	service = DTOConverter.class
)
public class ShippingFixedOptionTermDTOConverter
	implements DTOConverter
		<CommerceShippingFixedOptionQualifier, ShippingFixedOptionTerm> {

	@Override
	public String getContentType() {
		return ShippingFixedOptionTerm.class.getSimpleName();
	}

	@Override
	public ShippingFixedOptionTerm toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				_commerceShippingFixedOptionQualifierService.
					getCommerceShippingFixedOptionQualifier(
						(Long)dtoConverterContext.getId());

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryService.getCommerceTermEntry(
				commerceShippingFixedOptionQualifier.getClassPK());

		return new ShippingFixedOptionTerm() {
			{
				setActions(dtoConverterContext::getActions);
				setShippingFixedOptionId(
					commerceShippingFixedOptionQualifier::
						getCommerceShippingFixedOptionId);
				setShippingFixedOptionTermId(
					commerceShippingFixedOptionQualifier::
						getCommerceShippingFixedOptionQualifierId);
				setTermExternalReferenceCode(
					commerceTermEntry::getExternalReferenceCode);
				setTermId(commerceTermEntry::getCommerceTermEntryId);
			}
		};
	}

	@Reference
	private CommerceShippingFixedOptionQualifierService
		_commerceShippingFixedOptionQualifierService;

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

}