/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.commerce.pricing.type.CommercePriceModifierTypeRegistry;
import com.liferay.commerce.pricing.web.internal.display.context.CommercePriceListDisplayContext;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PROMOTION,
		"mvc.command.name=/commerce_price_list/edit_commerce_price_list"
	},
	service = MVCRenderCommand.class
)
public class EditCommercePriceListMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		CommercePriceListDisplayContext commercePriceListDisplayContext =
			new CommercePriceListDisplayContext(
				_commerceCatalogService, _commerceCurrencyLocalService,
				_commercePriceListModelResourcePermission,
				_commercePriceListService, _commercePriceModifierService,
				_commercePriceModifierTypeRegistry,
				_portal.getHttpServletRequest(renderRequest));

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commercePriceListDisplayContext);

		_populatePortletDisplay(renderRequest);

		return "/commerce_price_lists/edit_commerce_price_list.jsp";
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					renderRequest, portletDisplay.getPortletName(),
					PortletRequest.RENDER_PHASE)
			).buildString());
	}

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommercePriceModifierService _commercePriceModifierService;

	@Reference
	private CommercePriceModifierTypeRegistry
		_commercePriceModifierTypeRegistry;

	@Reference
	private Portal _portal;

}