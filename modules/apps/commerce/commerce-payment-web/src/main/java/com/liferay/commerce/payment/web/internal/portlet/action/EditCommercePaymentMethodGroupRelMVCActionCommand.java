/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.portlet.action;

import com.liferay.commerce.exception.NoSuchPaymentMethodException;
import com.liferay.commerce.payment.exception.CommercePaymentMethodGroupRelNameException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_PAYMENT_METHODS,
		"mvc.command.name=/commerce_payment_methods/edit_commerce_payment_method_group_rel"
	},
	service = MVCActionCommand.class
)
public class EditCommercePaymentMethodGroupRelMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommercePaymentMethodGroupRel(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_updateCommercePaymentMethodGroupRel(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchPaymentMethodException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						CommercePaymentMethodGroupRelNameException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/commerce_payment_methods" +
						"/edit_commerce_payment_method_group_rel");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommercePaymentMethodGroupRel(
			ActionRequest actionRequest)
		throws PortalException {

		long commercePaymentMethodGroupRelId = ParamUtil.getLong(
			actionRequest, "commercePaymentMethodGroupRelId");

		_commercePaymentMethodGroupRelService.
			deleteCommercePaymentMethodGroupRel(
				commercePaymentMethodGroupRelId);
	}

	private CommercePaymentMethodGroupRel _updateCommercePaymentMethodGroupRel(
			ActionRequest actionRequest)
		throws PortalException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel = null;

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "nameMapAsXML");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "descriptionMapAsXML");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		File imageFile = uploadPortletRequest.getFile("imageFile");
		double priority = ParamUtil.getDouble(actionRequest, "priority");

		long commercePaymentMethodGroupRelId = ParamUtil.getLong(
			actionRequest, "commercePaymentMethodGroupRelId");

		if (commercePaymentMethodGroupRelId <= 0) {
			long commerceChannelId = ParamUtil.getLong(
				actionRequest, "commerceChannelId");

			CommerceChannel commerceChannel =
				_commerceChannelService.getCommerceChannel(commerceChannelId);

			String commercePaymentMethodEngineKey = ParamUtil.getString(
				actionRequest, "commercePaymentMethodEngineKey");

			if (Objects.equals(
					commercePaymentMethodEngineKey,
					"function.commerce.payment.integration.configuration")) {

				commercePaymentMethodGroupRel =
					_commercePaymentMethodGroupRelService.
						addCommercePaymentMethodGroupRel(
							commerceChannel.getGroupId(), nameMap,
							descriptionMap, active, imageFile,
							ParamUtil.getString(
								actionRequest, "commercePaymentIntegrationKey"),
							priority, null);
			}
			else {
				commercePaymentMethodGroupRel =
					_commercePaymentMethodGroupRelService.
						addCommercePaymentMethodGroupRel(
							commerceChannel.getGroupId(), nameMap,
							descriptionMap, active, imageFile,
							commercePaymentMethodEngineKey, priority, null);
			}
		}
		else {
			commercePaymentMethodGroupRel =
				_commercePaymentMethodGroupRelService.
					updateCommercePaymentMethodGroupRel(
						commercePaymentMethodGroupRelId, nameMap,
						descriptionMap, imageFile, priority, active);
		}

		return commercePaymentMethodGroupRel;
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}