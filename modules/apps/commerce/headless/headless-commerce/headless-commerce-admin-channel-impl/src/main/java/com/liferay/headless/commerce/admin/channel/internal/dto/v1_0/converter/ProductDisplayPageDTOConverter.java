/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.headless.commerce.admin.channel.dto.v1_0.ProductDisplayPage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.admin.channel.dto.v1_0.ProductDisplayPage"
	},
	service = DTOConverter.class
)
public class ProductDisplayPageDTOConverter
	implements DTOConverter<CPDisplayLayout, ProductDisplayPage> {

	@Override
	public String getContentType() {
		return ProductDisplayPage.class.getSimpleName();
	}

	@Override
	public ProductDisplayPage toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDisplayLayout cpDisplayLayout =
			_cpDisplayLayoutLocalService.getCPDisplayLayout(
				(Long)dtoConverterContext.getId());

		return new ProductDisplayPage() {
			{
				setActions(dtoConverterContext::getActions);
				setId(cpDisplayLayout::getCPDisplayLayoutId);
				setPageTemplateUuid(
					cpDisplayLayout::getLayoutPageTemplateEntryUuid);
				setPageUuid(cpDisplayLayout::getLayoutUuid);
				setProductExternalReferenceCode(
					() -> {
						CPDefinition cpDefinition =
							_cpDefinitionLocalService.fetchCPDefinition(
								cpDisplayLayout.getClassPK());

						if (cpDefinition == null) {
							return null;
						}

						CProduct cProduct = _cProductLocalService.fetchCProduct(
							cpDefinition.getCProductId());

						if (cProduct == null) {
							return null;
						}

						return cProduct.getExternalReferenceCode();
					});
				setProductId(cpDisplayLayout::getClassPK);
			}
		};
	}

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

}