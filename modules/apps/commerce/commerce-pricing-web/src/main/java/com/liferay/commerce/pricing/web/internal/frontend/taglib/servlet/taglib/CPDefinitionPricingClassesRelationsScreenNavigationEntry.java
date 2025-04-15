/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.pricing.web.internal.display.context.CPDefinitionPricingClassDisplayContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CPDefinitionPricingClassesRelationsScreenNavigationEntry
	extends CPDefinitionPricingClassesRelationsScreenNavigationCategory
	implements ScreenNavigationEntry<CPDefinition> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CPDefinition cpDefinition) {
		if (cpDefinition == null) {
			return false;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		CPType cpType = _cpTypeRegistry.getCPType(
			cpDefinition.getProductTypeName());

		try {
			if (_commerceCatalogModelResourcePermission.contains(
					permissionChecker, cpDefinition.getCommerceCatalog(),
					ActionKeys.VIEW) &&
				cpType.isProductGroupsEnabled()) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CPDefinitionPricingClassDisplayContext
			cpDefinitionPricingClassDisplayContext =
				new CPDefinitionPricingClassDisplayContext(
					_actionHelper, httpServletRequest,
					_commerceCatalogModelResourcePermission,
					_commercePricingClassService);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			cpDefinitionPricingClassDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/cp_definitions/cp_definition_pricing_classes.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionPricingClassesRelationsScreenNavigationEntry.class);

	@Reference
	private ActionHelper _actionHelper;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.pricing.web)"
	)
	private ServletContext _servletContext;

}