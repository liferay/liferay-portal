/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductTaxConfigurationUtil {

	public static CPDefinition updateCPDefinitionTaxCategoryInfo(
			long companyId, CPDefinition cpDefinition,
			CPDefinitionService cpDefinitionService,
			CPTaxCategoryLocalService cpTaxCategoryLocalService,
			ProductTaxConfiguration productTaxConfiguration, long userId)
		throws PortalException {

		return cpDefinitionService.updateTaxCategoryInfo(
			cpDefinition.getCPDefinitionId(),
			_getCPTaxCategoryId(
				companyId, cpTaxCategoryLocalService, productTaxConfiguration,
				userId),
			ProductUtil.isTaxExempt(cpDefinition, productTaxConfiguration),
			false);
	}

	private static long _getCPTaxCategoryId(
			long companyId, CPTaxCategoryLocalService cpTaxCategoryLocalService,
			ProductTaxConfiguration productTaxConfiguration, long userId)
		throws PortalException {

		String externalReferenceCode =
			productTaxConfiguration.getTaxCategoryExternalReferenceCode();

		if (Validator.isNull(externalReferenceCode)) {
			return GetterUtil.getLong(productTaxConfiguration.getId());
		}

		CPTaxCategory cpTaxCategory =
			cpTaxCategoryLocalService.getOrAddEmptyCPTaxCategory(
				externalReferenceCode, companyId, userId);

		return cpTaxCategory.getCPTaxCategoryId();
	}

}