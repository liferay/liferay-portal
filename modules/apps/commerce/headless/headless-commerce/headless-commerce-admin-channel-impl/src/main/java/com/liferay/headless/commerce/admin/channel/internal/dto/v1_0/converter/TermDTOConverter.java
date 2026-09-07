/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.Term;
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
		"dto.class.name=com.liferay.commerce.term.model.CommerceTermEntry"
	},
	service = DTOConverter.class
)
public class TermDTOConverter implements DTOConverter<CommerceTermEntry, Term> {

	@Override
	public String getContentType() {
		return Term.class.getSimpleName();
	}

	@Override
	public Term toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryService.getCommerceTermEntry(
				(Long)dtoConverterContext.getId());

		return new Term() {
			{
				setId(commerceTermEntry::getCommerceTermEntryId);
				setName(commerceTermEntry::getName);
			}
		};
	}

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

}