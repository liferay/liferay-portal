/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountRule;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Alessio Antonio Rendina
 */
public class DiscountRuleUtil {

	public static CommerceDiscountRule addOrUpdateCommerceDiscountRule(
			CommerceDiscountRuleService commerceDiscountRuleService,
			DiscountRule discountRule, CommerceDiscount commerceDiscount,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		long discountRuleId = 0;

		if (Validator.isNull(discountRule.getExternalReferenceCode())) {
			discountRuleId = GetterUtil.getLong(discountRule.getId());
		}

		return commerceDiscountRuleService.addOrUpdateCommerceDiscountRule(
			discountRule.getExternalReferenceCode(), discountRuleId,
			commerceDiscount.getCommerceDiscountId(), discountRule.getName(),
			discountRule.getType(), discountRule.getTypeSettings(),
			serviceContextHelper.getServiceContext());
	}

}