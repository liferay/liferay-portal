/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util;

import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class SkuUtil {

	public static CPInstance fetchCPInstance(
			CPDefinitionService cpDefinitionService,
			CPInstanceService cpInstanceService, long groupId,
			String productExternalReferenceCode, String productType,
			ServiceContext serviceContext, String skuExternalReferenceCode,
			long skuId)
		throws PortalException {

		if (Validator.isNotNull(skuExternalReferenceCode)) {
			CPInstance cpInstance =
				cpInstanceService.fetchCPInstanceByExternalReferenceCode(
					skuExternalReferenceCode, serviceContext.getCompanyId());

			if (cpInstance != null) {
				return cpInstance;
			}
		}

		if ((skuId > 0) && !LazyReferencingThreadLocal.isEnabled()) {
			CPInstance cpInstance = cpInstanceService.fetchCPInstance(skuId);

			if ((cpInstance != null) &&
				(cpInstance.getCompanyId() == serviceContext.getCompanyId())) {

				return cpInstance;
			}
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			return null;
		}

		if (Validator.isNull(skuExternalReferenceCode)) {
			throw new NoSuchCPInstanceException(
				"Unable to find SKU with external reference code " +
					skuExternalReferenceCode);
		}

		if (Validator.isNull(productExternalReferenceCode)) {
			throw new NoSuchCProductException(
				"Unable to find product with external reference code " +
					productExternalReferenceCode);
		}

		CPDefinition cpDefinition =
			cpDefinitionService.getOrAddEmptyCPDefinition(
				productExternalReferenceCode, groupId,
				GetterUtil.getString(productType, SimpleCPTypeConstants.NAME));

		return cpInstanceService.getOrAddEmptyCPInstance(
			skuExternalReferenceCode, cpDefinition.getCPDefinitionId(),
			groupId);
	}

}