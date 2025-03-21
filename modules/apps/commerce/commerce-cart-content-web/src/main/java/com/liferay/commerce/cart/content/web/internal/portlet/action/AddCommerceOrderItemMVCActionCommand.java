/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT,
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT_MINI,
		"mvc.command.name=/commerce_cart_content/add_commerce_order_item"
	},
	service = MVCActionCommand.class
)
public class AddCommerceOrderItemMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");

		String formFieldValues = ParamUtil.getString(
			actionRequest, "formFieldValues");

		if (cpInstanceId == 0) {
			long cpDefinitionId = ParamUtil.getLong(
				actionRequest, "cpDefinitionId");

			CPInstance cpInstance = _cpInstanceHelper.fetchCPInstance(
				cpDefinitionId, formFieldValues);

			if (cpInstance != null) {
				cpInstanceId = cpInstance.getCPInstanceId();
			}
		}

		try {
			CommerceOrder commerceOrder =
				_commerceOrderHttpHelper.getCurrentCommerceOrder(
					httpServletRequest);

			if (commerceOrder == null) {
				commerceOrder = _commerceOrderHttpHelper.addCommerceOrder(
					httpServletRequest);
			}

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemService.addOrUpdateCommerceOrderItem(
					commerceOrder.getCommerceOrderId(), cpInstanceId,
					formFieldValues,
					_commerceOrderItemQuantityFormatter.parse(
						actionRequest, CommerceOrderItem.class.getName(),
						"quantity"),
					0, BigDecimal.ZERO,
					ParamUtil.getString(actionRequest, "unitOfMeasureKey"),
					(CommerceContext)httpServletRequest.getAttribute(
						CommerceWebKeys.COMMERCE_CONTEXT),
					ServiceContextFactory.getInstance(
						CommerceOrderItem.class.getName(), httpServletRequest));

			jsonObject.put(
				"commerceOrderItemId",
				commerceOrderItem.getCommerceOrderItemId()
			).put(
				"commerceOrderItemsQuantity",
				_commerceOrderItemService.getCommerceOrderItemsQuantity(
					commerceOrder.getCommerceOrderId())
			).put(
				"success", true
			).put(
				"successMessage",
				_language.get(
					httpServletRequest,
					"the-product-was-successfully-added-to-the-cart")
			);
		}
		catch (CommerceOrderValidatorException
					commerceOrderValidatorException) {

			List<CommerceOrderValidatorResult> commerceOrderValidatorResults =
				commerceOrderValidatorException.
					getCommerceOrderValidatorResults();

			JSONArray errorJSONArray = _jsonFactory.createJSONArray();

			for (CommerceOrderValidatorResult commerceOrderValidatorResult :
					commerceOrderValidatorResults) {

				JSONObject errorJSONObject = _jsonFactory.createJSONObject();

				errorJSONObject.put(
					"message",
					commerceOrderValidatorResult.getLocalizedMessage());

				errorJSONArray.put(errorJSONObject);
			}

			jsonObject.put(
				"success", false
			).put(
				"validatorErrors", errorJSONArray
			);
		}
		catch (Exception exception) {
			_log.error(exception);

			jsonObject.put(
				"error", exception.getMessage()
			).put(
				"success", false
			);
		}

		hideDefaultSuccessMessage(actionRequest);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		_writeJSON(actionResponse, jsonObject);
	}

	private void _writeJSON(ActionResponse actionResponse, Object object)
		throws Exception {

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, object.toString());

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddCommerceOrderItemMVCActionCommand.class);

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}