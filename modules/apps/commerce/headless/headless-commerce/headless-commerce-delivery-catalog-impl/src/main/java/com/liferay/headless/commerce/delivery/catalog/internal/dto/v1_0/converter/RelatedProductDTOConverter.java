/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.RelatedProduct;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {"default=true", "dto.class.name=CPDefinitionLink"},
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
			_cpDefinitionLinkLocalService.getCPDefinitionLink(
				(Long)dtoConverterContext.getId());

		return new RelatedProduct() {
			{
				setId(cpDefinitionLink::getCPDefinitionLinkId);
				setPriority(cpDefinitionLink::getPriority);
				setProductId(
					() -> {
						CProduct cProduct = cpDefinitionLink.getCProduct();

						return cProduct.getCProductId();
					});
				setType(cpDefinitionLink::getType);
			}
		};
	}

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

}