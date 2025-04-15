/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.CommerceTaxEngine;
import com.liferay.commerce.tax.engine.internal.FunctionCommerceTaxEngine;
import com.liferay.commerce.tax.engine.internal.display.context.CommerceTaxCategoryMappingsDisplayContext;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxCategoryMappingService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.commerce.util.CommerceTaxEngineRegistry;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author JIvica Cardic
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class FunctionCommerceTaxMethodTaxCategoryMappingsScreenNavigationEntry
	extends FunctionCommerceTaxMethodTaxCategoryMappingsScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceTaxMethod> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceTaxMethod commerceTaxMethod) {
		if (commerceTaxMethod == null) {
			return false;
		}

		CommerceTaxEngine commerceTaxEngine =
			_commerceTaxEngineRegistry.getCommerceTaxEngine(
				commerceTaxMethod.getEngineKey());

		return commerceTaxEngine instanceof FunctionCommerceTaxEngine;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		CommerceTaxCategoryMappingsDisplayContext
			commerceTaxFixedRatesDisplayContext =
				new CommerceTaxCategoryMappingsDisplayContext(
					_commerceChannelLocalService,
					_commerceChannelModelResourcePermission,
					_commerceTaxCategoryMappingService,
					_commerceTaxMethodService, _cpTaxCategoryService,
					renderRequest);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceTaxFixedRatesDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/tax_category_mappings.jsp");
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceTaxCategoryMappingService
		_commerceTaxCategoryMappingService;

	@Reference
	private CommerceTaxEngineRegistry _commerceTaxEngineRegistry;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private CPTaxCategoryService _cpTaxCategoryService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.tax.engine)"
	)
	private ServletContext _servletContext;

}