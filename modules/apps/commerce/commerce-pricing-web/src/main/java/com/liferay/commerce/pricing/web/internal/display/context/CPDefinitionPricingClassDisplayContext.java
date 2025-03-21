/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Riccardo Alberti
 */
public class CPDefinitionPricingClassDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionPricingClassDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		ModelResourcePermission<CommerceCatalog>
			commerceCatalogModelResourcePermission,
		CommercePricingClassService commercePricingClassService) {

		super(actionHelper, httpServletRequest);

		_commerceCatalogModelResourcePermission =
			commerceCatalogModelResourcePermission;
		_commercePricingClassService = commercePricingClassService;
	}

	public CommercePricingClass getCommercePricingClass()
		throws PortalException {

		long commercePricingClassId = ParamUtil.getLong(
			cpRequestHelper.getRequest(), "commercePricingClassId");

		if (commercePricingClassId == 0) {
			return null;
		}

		return _commercePricingClassService.fetchCommercePricingClass(
			commercePricingClassId);
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition_pricing_class"
		).setParameter(
			"cpDefinitionId", getCPDefinitionId()
		).setParameter(
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CPDefinitionScreenNavigationConstants.CATEGORY_KEY_OPTIONS;
	}

	public boolean hasPermission(
			PermissionChecker permissionChecker, CPDefinition cpDefinition,
			String actionId)
		throws PortalException {

		return _commerceCatalogModelResourcePermission.contains(
			permissionChecker, cpDefinition.getCommerceCatalog(), actionId);
	}

	private final ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;
	private final CommercePricingClassService _commercePricingClassService;

}