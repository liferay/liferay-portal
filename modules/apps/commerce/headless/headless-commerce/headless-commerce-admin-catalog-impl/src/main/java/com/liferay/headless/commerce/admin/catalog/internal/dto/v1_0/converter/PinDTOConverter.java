/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramPinService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Pin;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramPin"
	},
	service = DTOConverter.class
)
public class PinDTOConverter implements DTOConverter<CSDiagramEntry, Pin> {

	@Override
	public String getContentType() {
		return Pin.class.getSimpleName();
	}

	@Override
	public Pin toDTO(DTOConverterContext dtoConverterContext) throws Exception {
		CSDiagramPin csDiagramPin = _csDiagramPinService.getCSDiagramPin(
			(Long)dtoConverterContext.getId());

		return new Pin() {
			{
				setId(csDiagramPin::getCSDiagramPinId);
				setMappedProduct(
					() -> {
						CSDiagramEntry csDiagramEntry =
							_csDiagramEntryService.fetchCSDiagramEntry(
								csDiagramPin.getCPDefinitionId(),
								csDiagramPin.getSequence());

						if (csDiagramEntry == null) {
							return null;
						}

						return _mappedProductDTOConverter.toDTO(
							new DefaultDTOConverterContext(
								csDiagramEntry.getCSDiagramEntryId(),
								dtoConverterContext.getLocale()));
					});
				setPositionX(csDiagramPin::getPositionX);
				setPositionY(csDiagramPin::getPositionY);
				setSequence(csDiagramPin::getSequence);
			}
		};
	}

	@Reference
	private CSDiagramEntryService _csDiagramEntryService;

	@Reference
	private CSDiagramPinService _csDiagramPinService;

	@Reference(target = DTOConverterConstants.MAPPED_PRODUCT_DTO_CONVERTER)
	private DTOConverter<CSDiagramEntry, MappedProduct>
		_mappedProductDTOConverter;

}