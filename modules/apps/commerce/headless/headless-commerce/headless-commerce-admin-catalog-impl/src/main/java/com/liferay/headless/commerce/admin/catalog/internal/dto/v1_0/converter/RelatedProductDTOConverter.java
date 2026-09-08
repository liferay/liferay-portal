/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.RelatedProduct;
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
		"dto.class.name=com.liferay.commerce.product.model.CPDefinitionLink"
	},
	service = DTOConverter.class
)
public class RelatedProductDTOConverter
	implements DTOConverter<CPDefinitionLink, RelatedProduct> {

	@Override
	public String getContentType() {
		return RelatedProduct.class.getSimpleName();
	}

	@Override
	public RelatedProduct toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinitionLink cpDefinitionLink =
			_cpDefinitionLinkService.getCPDefinitionLink(
				(Long)dtoConverterContext.getId());

		CProduct cProduct = cpDefinitionLink.getCProduct();

		return new RelatedProduct() {
			{
				setId(cpDefinitionLink::getCPDefinitionLinkId);
				setPriority(cpDefinitionLink::getPriority);
				setProductExternalReferenceCode(
					cProduct::getExternalReferenceCode);
				setProductId(cProduct::getCProductId);
				setType(cpDefinitionLink::getType);
			}
		};
	}

	@Reference
	private CPDefinitionLinkService _cpDefinitionLinkService;

}