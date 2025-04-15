/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.cart.content.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT,
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CART_CONTENT_MINI,
		"mvc.command.name=/commerce_cart_content/edit_commerce_order_item"
	},
	service = MVCActionCommand.class
)
public class EditCommerceOrderItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long commerceOrderItemId = ParamUtil.getLong(
			actionRequest, "commerceOrderItemId");

		try {
			if (cmd.equals(Constants.DELETE)) {
				_commerceOrderItemService.deleteCommerceOrderItem(
					commerceOrderItemId, commerceContext);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				CommerceOrderItem commerceOrderItem =
					_commerceOrderItemService.getCommerceOrderItem(
						commerceOrderItemId);

				_commerceOrderItemService.updateCommerceOrderItem(
					commerceOrderItem.getExternalReferenceCode(),
					commerceOrderItem.getCommerceOrderItemId(),
					commerceOrderItem.getJson(),
					_commerceOrderItemQuantityFormatter.parse(
						actionRequest, CommerceOrderItem.class.getName(),
						"quantity"),
					commerceContext,
					ServiceContextFactory.getInstance(
						CommerceOrderItem.class.getName(), actionRequest));
			}
		}
		catch (CommerceOrderValidatorException
					commerceOrderValidatorException) {

			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(
				actionRequest, commerceOrderValidatorException.getClass(),
				commerceOrderValidatorException);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderItemException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

}