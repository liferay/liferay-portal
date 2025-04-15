/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.portlet;

import com.liferay.commerce.cart.content.web.internal.display.context.CommerceCartContentTotalDisplayContext;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
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
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-cart-content-total",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=false",
		"jakarta.portlet.display-name=Cart Summary",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/cart_total/view.jsp",
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT_TOTAL,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommerceCartContentTotalPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			CommerceCartContentTotalDisplayContext
				commerceCartContentTotalDisplayContext =
					new CommerceCartContentTotalDisplayContext(
						_commerceChannelLocalService, _commerceOrderHttpHelper,
						_commerceOrderItemService,
						_commerceOrderPriceCalculation,
						_commerceOrderValidatorRegistry, _configurationProvider,
						_cpDefinitionHelper, _cpInstanceHelper,
						_commerceOrderModelResourcePermission,
						_commerceProductPortletResourcePermission,
						_groupLocalService,
						_portal.getHttpServletRequest(renderRequest), _portal);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceCartContentTotalDisplayContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		super.render(renderRequest, renderResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCartContentTotalPortlet.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderValidatorRegistry _commerceOrderValidatorRegistry;

	@Reference(
		target = "(resource.name=" + CPConstants.RESOURCE_NAME_PRODUCT + ")"
	)
	private PortletResourcePermission _commerceProductPortletResourcePermission;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}