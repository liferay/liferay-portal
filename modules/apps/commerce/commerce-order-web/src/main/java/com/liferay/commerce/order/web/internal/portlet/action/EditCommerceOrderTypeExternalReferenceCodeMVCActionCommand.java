/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.DuplicateCommerceOrderTypeExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
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
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER_TYPE,
		"mvc.command.name=/commerce_order_type/edit_commerce_order_type_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceOrderTypeExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long commerceOrderTypeId = ParamUtil.getLong(
				actionRequest, "commerceOrderTypeId");

			CommerceOrderType commerceOrderType =
				_commerceOrderTypeService.getCommerceOrderType(
					commerceOrderTypeId);

			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			_commerceOrderTypeService.
				updateCommerceOrderTypeExternalReferenceCode(
					externalReferenceCode,
					commerceOrderType.getCommerceOrderTypeId());
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceOrderTypeExternalReferenceCodeException ||
				exception instanceof NoSuchOrderTypeException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(exception);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceOrderTypeExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

}