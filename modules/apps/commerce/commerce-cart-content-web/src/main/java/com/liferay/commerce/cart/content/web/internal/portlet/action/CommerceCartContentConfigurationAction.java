/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.portlet.action;

import com.liferay.commerce.cart.content.web.internal.display.context.CommerceCartContentDisplayContext;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT,
	service = ConfigurationAction.class
)
public class CommerceCartContentConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/cart/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			CommerceCartContentDisplayContext
				commerceCartContentDisplayContext =
					new CommerceCartContentDisplayContext(
						_commerceChannelLocalService, _commerceOrderItemService,
						_commerceOrderModelResourcePermission,
						_commerceOrderPriceCalculation,
						_commerceOrderValidatorRegistry,
						_commerceProductPortletResourcePermission,
						_configurationProvider, _cpDefinitionHelper,
						_cpInstanceHelper, _groupLocalService,
						httpServletRequest, _portal);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceCartContentDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCartContentConfigurationAction.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

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