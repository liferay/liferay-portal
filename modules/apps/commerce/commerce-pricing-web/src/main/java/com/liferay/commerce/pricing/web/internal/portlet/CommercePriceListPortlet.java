/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.commerce.pricing.type.CommercePriceModifierTypeRegistry;
import com.liferay.commerce.pricing.web.internal.display.context.CommercePriceListDisplayContext;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-price-list",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Price Lists",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/commerce_price_lists/view.jsp",
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommercePriceListPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CommercePriceListDisplayContext commercePriceListDisplayContext =
			new CommercePriceListDisplayContext(
				_commerceCatalogService, _commerceCurrencyLocalService,
				_commercePriceListModelResourcePermission,
				_commercePriceListService, _commercePriceModifierService,
				_commercePriceModifierTypeRegistry,
				_portal.getHttpServletRequest(renderRequest));

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commercePriceListDisplayContext);

		super.render(renderRequest, renderResponse);
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