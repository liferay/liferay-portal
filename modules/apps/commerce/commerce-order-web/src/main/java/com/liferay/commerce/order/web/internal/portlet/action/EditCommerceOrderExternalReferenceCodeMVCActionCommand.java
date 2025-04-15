/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.CommerceOrderNoteContentException;
import com.liferay.commerce.exception.DuplicateCommerceOrderExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
		"mvc.command.name=/commerce_order/edit_commerce_order_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceOrderExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommerceOrderExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceOrderExternalReferenceCodeException ||
				exception instanceof NoSuchOrderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceOrderNoteContentException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _updateCommerceOrderExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		long commerceOrderId = ParamUtil.getLong(
			actionRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_commerceOrderService.updateCommerceOrderExternalReferenceCode(
			externalReferenceCode, commerceOrder.getCommerceOrderId());
	}

	@Reference
	private CommerceOrderService _commerceOrderService;

}