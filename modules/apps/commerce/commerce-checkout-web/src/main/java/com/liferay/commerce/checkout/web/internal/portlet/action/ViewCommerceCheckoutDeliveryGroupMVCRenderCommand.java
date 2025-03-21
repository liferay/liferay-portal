/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.portlet.action;

import com.liferay.commerce.checkout.web.internal.display.context.DeliveryGroupDisplayContext;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CHECKOUT,
		"mvc.command.name=/commerce_checkout/view_commerce_checkout_delivery_group"
	},
	service = MVCRenderCommand.class
)
public class ViewCommerceCheckoutDeliveryGroupMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		DeliveryGroupDisplayContext deliveryGroupDisplayContext =
			new DeliveryGroupDisplayContext(_commerceAddressService);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, deliveryGroupDisplayContext);

		return "/view_commerce_checkout_delivery_group.jsp";
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

}