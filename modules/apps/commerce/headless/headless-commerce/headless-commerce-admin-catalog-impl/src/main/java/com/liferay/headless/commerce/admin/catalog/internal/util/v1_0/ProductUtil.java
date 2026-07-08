/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.util.v1_0;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductUtil {

	public static CPDefinition fetchCPDefinitionByCProductExternalReferenceCode(
			String externalReferenceCode, long companyId,
			CPDefinitionService cpDefinitionService)
		throws PortalException {

		CPDefinition cpDefinition =
			cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, companyId, false);

		if (cpDefinition == null) {
			return cpDefinition;
		}

		return fetchCPDefinitionByCProductId(
			cpDefinition.getCProductId(), cpDefinitionService);
	}

	public static CPDefinition fetchCPDefinitionByCProductId(
			long cProductId, CPDefinitionService cpDefinitionService)
		throws PortalException {

		CPDefinition cpDefinition =
			cpDefinitionService.fetchCPDefinitionByCProductId(
				cProductId, false);

		if ((cpDefinition == null) ||
			!cpDefinitionService.isVersionable(cpDefinition) ||
			cpDefinition.isDraft()) {

			return cpDefinition;
		}

		CPDefinition draftCPDefinition =
			cpDefinitionService.fetchCPDefinitionByCProductId(
				cProductId, WorkflowConstants.STATUS_DRAFT);

		if (draftCPDefinition != null) {
			return draftCPDefinition;
		}

		return cpDefinition;
	}

	public static boolean isTaxExempt(
		CPDefinition cpDefinition,
		ProductTaxConfiguration productTaxConfiguration) {

		if (productTaxConfiguration.getTaxable() != null) {
			return !productTaxConfiguration.getTaxable();
		}

		if (cpDefinition != null) {
			return cpDefinition.isTaxExempt();
		}

		return false;
	}

}