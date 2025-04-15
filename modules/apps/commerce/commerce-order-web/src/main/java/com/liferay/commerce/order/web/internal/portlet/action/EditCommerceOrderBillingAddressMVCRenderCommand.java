/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.configuration.CommerceOrderItemDecimalQuantityConfiguration;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.frontend.helper.CommerceOrderStepTrackerHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryLocalService;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.order.web.internal.display.context.CommerceOrderEditDisplayContext;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.service.CPMeasurementUnitService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderNoteService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.configuration.CommerceOrderItemDecimalQuantityConfiguration",
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/edit_commerce_order_billing_address"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceOrderBillingAddressMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceOrderEditDisplayContext commerceOrderEditDisplayContext =
				new CommerceOrderEditDisplayContext(
					_commerceChannelLocalService,
					_commerceNotificationQueueEntryLocalService,
					_commerceOrderEngine,
					_commerceOrderItemDecimalQuantityConfiguration,
					_commerceOrderItemQuantityFormatter,
					_commerceOrderItemService, _commerceOrderNoteService, null,
					_commerceOrderService, _commerceOrderStatusRegistry,
					_commerceOrderStepTrackerHelper, _commerceOrderTypeService,
					_commercePaymentMethodGroupRelLocalService,
					_commercePriceFormatter, _commerceShipmentService,
					_commerceTermEntryLocalService, _cpMeasurementUnitService,
					_modelResourcePermission, renderRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceOrderEditDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/commerce_order/edit_billing_address.jsp";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_commerceOrderItemDecimalQuantityConfiguration =
			ConfigurableUtil.createConfigurable(
				CommerceOrderItemDecimalQuantityConfiguration.class,
				properties);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	private volatile CommerceOrderItemDecimalQuantityConfiguration
		_commerceOrderItemDecimalQuantityConfiguration;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderNoteService _commerceOrderNoteService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommerceOrderStepTrackerHelper _commerceOrderStepTrackerHelper;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceShipmentService _commerceShipmentService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private CPMeasurementUnitService _cpMeasurementUnitService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder> _modelResourcePermission;

}