/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.portlet.action;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.frontend.helper.CommerceOrderStepTrackerHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.content.web.internal.display.context.CommerceOrderContentDisplayContext;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterTypeRegistry;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
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
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER_CONTENT,
	service = ConfigurationAction.class
)
public class CommerceOrderContentConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/placed_commerce_orders/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			CommerceOrderContentDisplayContext
				commerceOrderContentDisplayContext =
					new CommerceOrderContentDisplayContext(
						_commerceAddressService, _commerceChannelLocalService,
						_commerceOrderEngine, _commerceOrderHttpHelper,
						_commerceOrderImporterTypeRegistry,
						_commerceOrderNoteService,
						_commerceOrderPriceCalculation, _commerceOrderService,
						_commerceOrderStatusRegistry,
						_commerceOrderStepTrackerHelper,
						_commerceOrderTypeService,
						_commercePaymentIntegrationRegistry,
						_commercePaymentMethodGroupRelServiceService,
						_commercePaymentMethodRegistry,
						_commerceTermEntryService, _configurationProvider,
						_dlAppLocalService, _groupLocalService,
						httpServletRequest, _itemSelector,
						_modelResourcePermission, _percentageFormatter,
						_portletResourcePermission);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceOrderContentDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderContentConfigurationAction.class);

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderImporterTypeRegistry
		_commerceOrderImporterTypeRegistry;

	@Reference
	private CommerceOrderNoteService _commerceOrderNoteService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommerceOrderStepTrackerHelper _commerceOrderStepTrackerHelper;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelServiceService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder> _modelResourcePermission;

	@Reference
	private PercentageFormatter _percentageFormatter;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}