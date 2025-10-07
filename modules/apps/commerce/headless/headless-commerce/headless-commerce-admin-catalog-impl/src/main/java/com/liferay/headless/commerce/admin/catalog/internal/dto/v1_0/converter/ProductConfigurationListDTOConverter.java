/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.model.CPConfigurationList",
	service = DTOConverter.class
)
public class ProductConfigurationListDTOConverter
	implements DTOConverter<CPConfigurationList, ProductConfigurationList> {

	@Override
	public String getContentType() {
		return ProductConfiguration.class.getSimpleName();
	}

	@Override
	public ProductConfigurationList toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.getCPConfigurationList(
				(Long)dtoConverterContext.getId());

		return new ProductConfigurationList() {
			{
				setActions(dtoConverterContext::getActions);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPConfigurationList.class.getName(),
						cpConfigurationList.getCPConfigurationListId(),
						cpConfigurationList.getCompanyId(),
						dtoConverterContext.getLocale()));
				setExternalReferenceCode(
					cpConfigurationList::getExternalReferenceCode);
				setId(cpConfigurationList::getCPConfigurationListId);
				setMaster(cpConfigurationList::getMaster);
				setName(cpConfigurationList::getName);
				setParentProductConfigurationListId(
					cpConfigurationList::getParentCPConfigurationListId);
				setPriority(cpConfigurationList::getPriority);
			}
		};
	}

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

}