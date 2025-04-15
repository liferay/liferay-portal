/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.DuplicateCommerceShipmentException;
import com.liferay.commerce.exception.DuplicateCommerceShipmentExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchShipmentException;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPMENT,
		"mvc.command.name=/commerce_shipment/edit_commerce_shipment_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceShipmentExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long commerceShipmentId = ParamUtil.getLong(
				actionRequest, "commerceShipmentId");

			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			_commerceShipmentService.updateExternalReferenceCode(
				commerceShipmentId, externalReferenceCode);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceShipmentExternalReferenceCodeException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof DuplicateCommerceShipmentException ||
					 exception instanceof NoSuchShipmentException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
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
		EditCommerceShipmentExternalReferenceCodeMVCActionCommand.class);

	@Reference
	private CommerceShipmentService _commerceShipmentService;

}