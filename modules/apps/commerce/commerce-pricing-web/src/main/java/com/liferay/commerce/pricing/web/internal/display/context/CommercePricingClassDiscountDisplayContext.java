/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Riccardo Alberti
 */
public class CommercePricingClassDiscountDisplayContext
	extends BasePricingDisplayContext {

	public CommercePricingClassDiscountDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<CommercePricingClass>
			commercePricingClassModelResourcePermission,
		CommercePricingClassService commercePricingClassService) {

		super(httpServletRequest);

		_commercePricingClassModelResourcePermission =
			commercePricingClassModelResourcePermission;
		_commercePricingClassService = commercePricingClassService;
	}

	public CommercePricingClass getCommercePricingClass()
		throws PortalException {

		long commercePricingClassId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(),
			"commercePricingClassId");

		if (commercePricingClassId == 0) {
			return null;
		}

		return _commercePricingClassService.fetchCommercePricingClass(
			commercePricingClassId);
	}

	public boolean hasPermission() throws PortalException {
		CommercePricingClass commercePricingClass = getCommercePricingClass();

		return _commercePricingClassModelResourcePermission.contains(
			commercePricingRequestHelper.getPermissionChecker(),
			commercePricingClass.getCommercePricingClassId(), ActionKeys.VIEW);
	}

	private final ModelResourcePermission<CommercePricingClass>
		_commercePricingClassModelResourcePermission;
	private final CommercePricingClassService _commercePricingClassService;

}