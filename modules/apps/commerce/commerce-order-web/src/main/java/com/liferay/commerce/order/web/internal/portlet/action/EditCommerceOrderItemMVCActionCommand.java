/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.exception.CommerceOrderItemPriceException;
import com.liferay.commerce.exception.CommerceOrderItemQuantityException;
import com.liferay.commerce.exception.CommerceOrderItemRequestedDeliveryDateException;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/edit_commerce_order_item"
	},
	service = MVCActionCommand.class
)
public class EditCommerceOrderItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {
				_addCommerceOrderItems(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				Callable<Object> commerceOrderItemCallable =
					new CommerceOrderItemCallable(actionRequest);

				TransactionInvokerUtil.invoke(
					_transactionConfig, commerceOrderItemCallable);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceOrderItems(actionRequest);
			}
			else if (cmd.equals("customFields")) {
				updateCustomFields(actionRequest);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof CommerceOrderItemPriceException ||
				throwable instanceof CommerceOrderItemQuantityException ||
				throwable instanceof
					CommerceOrderItemRequestedDeliveryDateException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (throwable instanceof CommerceOrderValidatorException) {
				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				_log.error(throwable, throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	protected void updateCustomFields(ActionRequest actionRequest)
		throws PortalException {

		long commerceOrderItemId = ParamUtil.getLong(
			actionRequest, "commerceOrderItemId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceOrderItem.class.getName(), actionRequest);

		_commerceOrderItemService.updateCustomFields(
			commerceOrderItemId, serviceContext);
	}

	private void _addCommerceOrderItems(ActionRequest actionRequest)
		throws Exception {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		String unitOfMeasureKey = ParamUtil.getString(
			actionRequest, "unitOfMeasureKey");

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceOrderItem.class.getName(), actionRequest);

		long[] cpInstanceIds = ParamUtil.getLongValues(
			actionRequest, "cpInstanceIds");

		for (long cpInstanceId : cpInstanceIds) {
			_commerceOrderItemService.addCommerceOrderItem(
				commerceOrderId, cpInstanceId, null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, unitOfMeasureKey, commerceContext,
				serviceContext);
		}
	}

	private void _deleteCommerceOrderItems(ActionRequest actionRequest)
		throws Exception {

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		long[] deleteCommerceOrderItemIds = null;

		long commerceOrderItemId = ParamUtil.getLong(
			actionRequest, "commerceOrderItemId");

		if (commerceOrderItemId > 0) {
			deleteCommerceOrderItemIds = new long[] {commerceOrderItemId};
		}
		else {
			deleteCommerceOrderItemIds = ParamUtil.getLongValues(
				actionRequest, "deleteCommerceOrderItemIds");
		}

		for (long deleteCommerceOrderItemId : deleteCommerceOrderItemIds) {
			_commerceOrderItemService.deleteCommerceOrderItem(
				deleteCommerceOrderItemId, commerceContext);
		}
	}

	private void _updateCommerceOrderItem(ActionRequest actionRequest)
		throws Exception {

		long commerceOrderItemId = ParamUtil.getLong(
			actionRequest, "commerceOrderItemId");

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(commerceOrderItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		long cpMeasurementUnitId = ParamUtil.getLong(
			actionRequest, "cpMeasurementUnitId");

		BigDecimal decimalQuantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CommerceOrderItem.class.getName(),
			"decimalQuantity");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceOrderItem.class.getName(), actionRequest);

		serviceContext.setAttribute("validateOrder", Boolean.FALSE);

		commerceOrderItem = _commerceOrderItemService.updateCommerceOrderItem(
			commerceOrderItemId, cpMeasurementUnitId, decimalQuantity,
			serviceContext);

		if (!commerceOrder.isOpen()) {
			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemUnitPrice(
					commerceOrderItemId, decimalQuantity,
					_commercePriceFormatter.parse(
						actionRequest, false, CommerceOrderItem.class.getName(),
						"price"));

			commerceOrderItem =
				_commerceOrderItemService.updateCommerceOrderItemPrices(
					commerceOrderItemId,
					_commercePriceFormatter.parse(
						actionRequest, false, CommerceOrderItem.class.getName(),
						"discountAmount"),
					commerceOrderItem.getDiscountPercentageLevel1(),
					commerceOrderItem.getDiscountPercentageLevel2(),
					commerceOrderItem.getDiscountPercentageLevel3(),
					commerceOrderItem.getDiscountPercentageLevel4(),
					_commercePriceFormatter.parse(
						actionRequest, false, CommerceOrderItem.class.getName(),
						"finalPrice"),
					commerceOrderItem.getPromoPrice(),
					commerceOrderItem.getUnitPrice());
		}

		String deliveryGroupName = ParamUtil.getString(
			actionRequest, "deliveryGroupName");
		int requestedDeliveryDateMonth = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateMonth");
		int requestedDeliveryDateDay = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateDay");
		int requestedDeliveryDateYear = ParamUtil.getInteger(
			actionRequest, "requestedDeliveryDateYear");

		_commerceOrderItemService.updateCommerceOrderItemInfo(
			commerceOrderItem.getCommerceOrderItemId(),
			commerceOrderItem.getShippingAddressId(), deliveryGroupName,
			commerceOrderItem.getPrintedNote(), requestedDeliveryDateMonth,
			requestedDeliveryDateDay, requestedDeliveryDateYear);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceOrderItemMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	private class CommerceOrderItemCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			_updateCommerceOrderItem(_actionRequest);

			return null;
		}

		private CommerceOrderItemCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}