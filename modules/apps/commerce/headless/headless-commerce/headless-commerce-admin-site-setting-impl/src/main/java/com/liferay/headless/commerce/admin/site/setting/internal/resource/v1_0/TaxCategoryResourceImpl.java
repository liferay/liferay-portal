/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.internal.resource.v1_0;

import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.headless.commerce.admin.site.setting.dto.v1_0.TaxCategory;
import com.liferay.headless.commerce.admin.site.setting.internal.mapper.v1_0.util.DTOMapperUtil;
import com.liferay.headless.commerce.admin.site.setting.resource.v1_0.TaxCategoryResource;

import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/tax-category.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxCategoryResource.class
)
public class TaxCategoryResourceImpl extends BaseTaxCategoryResourceImpl {

	@Override
	public Response deleteTaxCategory(Long id) throws Exception {
		_cpTaxCategoryService.deleteCPTaxCategory(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public TaxCategory getTaxCategory(Long id) throws Exception {
		return DTOMapperUtil.modelToDTO(
			_cpTaxCategoryService.getCPTaxCategory(id));
	}

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

}