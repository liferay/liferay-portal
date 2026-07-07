/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.order.web.internal.display.context.CommerceOrderAccountValidationsDisplayContext;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/view_commerce_order_account_validations"
	},
	service = MVCRenderCommand.class
)
public class ViewCommerceOrderAccountValidationsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceOrderAccountValidationsDisplayContext
				commerceOrderAccountValidationsDisplayContext =
					new CommerceOrderAccountValidationsDisplayContext(
						_accountEntryValidatorRegistry,
						_commerceOrderService.getCommerceOrder(
							ParamUtil.getLong(
								renderRequest, "commerceOrderId")));

			if (Validator.isNull(
					commerceOrderAccountValidationsDisplayContext.
						getFilterString())) {

				return "/error.jsp";
			}

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceOrderAccountValidationsDisplayContext);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/commerce_order/account_validations.jsp";
	}

	@Reference
	private AccountEntryValidatorRegistry _accountEntryValidatorRegistry;

	@Reference
	private CommerceOrderService _commerceOrderService;

}