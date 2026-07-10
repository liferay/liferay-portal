/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.portlet.action;

import com.liferay.commerce.address.CommerceAddressFormatter;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipment.web.internal.display.context.CommerceShipmentDisplayContext;
import com.liferay.commerce.shipment.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
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
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPMENT,
		"mvc.command.name=/commerce_shipment/edit_commerce_shipment"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceShipmentMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		CommerceShipmentDisplayContext commerceShipmentDisplayContext =
			new CommerceShipmentDisplayContext(
				_actionHelper, _commerceAddressFormatter,
				_commerceAddressLocalService, _commerceChannelService,
				_commerceOrderItemService, _commerceOrderLocalService,
				_commerceShipmentItemService,
				_commerceShipmentModelResourcePermission,
				_commerceShippingMethodService, _countryService,
				_portal.getHttpServletRequest(renderRequest), _regionService);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commerceShipmentDisplayContext);

		_populatePortletDisplay(renderRequest);

		return "/edit_commerce_shipment.jsp";
	}

	private void _populatePortletDisplay(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setShowBackIcon(true);

		long commerceOrderId = ParamUtil.getLong(
			renderRequest, "commerceOrderId");

		if (commerceOrderId > 0) {
			portletDisplay.setURLBack(
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						renderRequest, CommercePortletKeys.COMMERCE_ORDER,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/commerce_order/edit_commerce_order"
				).setParameter(
					"commerceOrderId", commerceOrderId
				).setParameter(
					"screenNavigationCategoryKey", "shipments"
				).buildString());
		}
		else {
			portletDisplay.setURLBack(
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						renderRequest, CommercePortletKeys.COMMERCE_SHIPMENT,
						PortletRequest.RENDER_PHASE)
				).buildString());
		}
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceAddressFormatter _commerceAddressFormatter;

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceShipment)"
	)
	private ModelResourcePermission<CommerceShipment>
		_commerceShipmentModelResourcePermission;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private CountryService _countryService;

	@Reference
	private Portal _portal;

	@Reference
	private RegionService _regionService;

}