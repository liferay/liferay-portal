/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.product.definitions.web.internal.display.context.CPConfigurationListDisplayContext;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.commerce.stock.activity.CommerceLowStockActivityRegistry;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CPConfigurationListDetailsScreenNavigationEntry
	extends CPConfigurationListDetailsScreenNavigationCategory
	implements ScreenNavigationEntry<CPConfigurationList> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CPConfigurationListDisplayContext cpConfigurationListDisplayContext =
			new CPConfigurationListDisplayContext(
				_commerceAvailabilityEstimateService, _commerceCatalogService,
				_commerceLowStockActivityRegistry, _cpConfigurationEntryService,
				_cpConfigurationListService,
				_cpDefinitionInventoryEngineRegistry, _cpDefinitionService,
				_cpMeasurementUnitLocalService, _cpTaxCategoryLocalService,
				httpServletRequest);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, cpConfigurationListDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/configuration_list/details.jsp");
	}

	@Reference
	private CommerceAvailabilityEstimateService
		_commerceAvailabilityEstimateService;

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceLowStockActivityRegistry _commerceLowStockActivityRegistry;

	@Reference
	private CPConfigurationEntryService _cpConfigurationEntryService;

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@Reference
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.definitions.web)"
	)
	private ServletContext _servletContext;

}