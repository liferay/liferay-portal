/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductTaxConfigurationUtil {

	public static CPDefinition updateCPDefinitionTaxCategoryInfo(
			CPDefinition cpDefinition, CPDefinitionService cpDefinitionService,
			CPTaxCategoryService cpTaxCategoryService,
			ProductTaxConfiguration productTaxConfiguration)
		throws PortalException {

		return cpDefinitionService.updateTaxCategoryInfo(
			cpDefinition.getCPDefinitionId(),
			_getCPTaxCategoryId(cpTaxCategoryService, productTaxConfiguration),
			ProductUtil.isTaxExempt(cpDefinition, productTaxConfiguration),
			false);
	}

	private static long _getCPTaxCategoryId(
			CPTaxCategoryService cpTaxCategoryService,
			ProductTaxConfiguration productTaxConfiguration)
		throws PortalException {

		String externalReferenceCode =
			productTaxConfiguration.getTaxCategoryExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return GetterUtil.getLong(productTaxConfiguration.getId());
		}

		CPTaxCategory cpTaxCategory =
			cpTaxCategoryService.getOrAddEmptyCPTaxCategory(
				externalReferenceCode);

		return cpTaxCategory.getCPTaxCategoryId();
	}

}