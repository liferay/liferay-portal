/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.CommerceShippingMethodNameException;
import com.liferay.commerce.exception.NoSuchShippingMethodException;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceShippingMethodService;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPPING_METHODS,
		"mvc.command.name=/commerce_shipping_methods/edit_commerce_shipping_method"
	},
	service = MVCActionCommand.class
)
public class EditCommerceShippingMethodMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceShippingMethod(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchShippingMethodException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceShippingMethodNameException) {
				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/commerce_shipping_methods/edit_commerce_shipping_method");
			}
			else {
				throw exception;
			}
		}
	}

	private CommerceShippingMethod _updateCommerceShippingMethod(
			ActionRequest actionRequest)
		throws PortalException {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		long commerceShippingMethodId = ParamUtil.getLong(
			actionRequest, "commerceShippingMethodId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "nameMapAsXML");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "descriptionMapAsXML");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		File imageFile = uploadPortletRequest.getFile("imageFile");
		double priority = ParamUtil.getDouble(actionRequest, "priority");
		String trackingURL = ParamUtil.getString(actionRequest, "trackingURL");

		CommerceShippingMethod commerceShippingMethod = null;

		if (commerceShippingMethodId <= 0) {
			long commerceChannelId = ParamUtil.getLong(
				actionRequest, "commerceChannelId");

			CommerceChannel commerceChannel =
				_commerceChannelService.getCommerceChannel(commerceChannelId);

			String commerceShippingMethodEngineKey = ParamUtil.getString(
				actionRequest, "commerceShippingMethodEngineKey");

			commerceShippingMethod =
				_commerceShippingMethodService.addCommerceShippingMethod(
					commerceChannel.getGroupId(), nameMap, descriptionMap,
					active, commerceShippingMethodEngineKey, imageFile,
					priority, trackingURL);
		}
		else {
			commerceShippingMethod =
				_commerceShippingMethodService.updateCommerceShippingMethod(
					commerceShippingMethodId, nameMap, descriptionMap, active,
					imageFile, priority, trackingURL);
		}

		return commerceShippingMethod;
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}