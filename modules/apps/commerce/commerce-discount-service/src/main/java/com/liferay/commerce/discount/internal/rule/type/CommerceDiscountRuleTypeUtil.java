/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.rule.type;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceDiscountRuleTypeUtil {

	public static String[] getCProductExternalReferenceCodes(
			CommerceOrder commerceOrder,
			CPDefinitionLocalService cpDefinitionLocalService,
			CProductLocalService cProductLocalService)
		throws PortalException {

		return TransformUtil.transformToArray(
			commerceOrder.getCommerceOrderItems(),
			commerceOrderItem -> {
				CPDefinition cpDefinition =
					cpDefinitionLocalService.fetchCPDefinition(
						commerceOrderItem.getCPDefinitionId());

				if (cpDefinition == null) {
					return null;
				}

				CProduct cProduct = cProductLocalService.fetchCProduct(
					cpDefinition.getCProductId());

				if (cProduct == null) {
					return null;
				}

				return cProduct.getExternalReferenceCode();
			},
			String.class);
	}

}