/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.tax.exception.CommerceTaxMethodNameException;
import com.liferay.commerce.tax.exception.NoSuchTaxMethodException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_TAX_METHODS,
		"mvc.command.name=/commerce_tax_methods/edit_commerce_tax_method"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTaxMethodMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceTaxMethod(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchTaxMethodException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceTaxMethodNameException) {
				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				throw exception;
			}
		}
	}

	private CommerceTaxMethod _updateCommerceTaxMethod(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceTaxMethodId = ParamUtil.getLong(
			actionRequest, "commerceTaxMethodId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "nameMapAsXML");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "descriptionMapAsXML");
		boolean percentage = ParamUtil.getBoolean(actionRequest, "percentage");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		CommerceTaxMethod commerceTaxMethod = null;

		if (commerceTaxMethodId <= 0) {
			long commerceChannelId = ParamUtil.getLong(
				actionRequest, "commerceChannelId");

			CommerceChannel commerceChannel =
				_commerceChannelService.getCommerceChannel(commerceChannelId);

			String commerceTaxMethodEngineKey = ParamUtil.getString(
				actionRequest, "commerceTaxMethodEngineKey");

			commerceTaxMethod = _commerceTaxMethodService.addCommerceTaxMethod(
				commerceChannel.getGroupId(), nameMap, descriptionMap,
				commerceTaxMethodEngineKey, percentage, active);
		}
		else {
			commerceTaxMethod =
				_commerceTaxMethodService.updateCommerceTaxMethod(
					commerceTaxMethodId, nameMap, descriptionMap, percentage,
					active);
		}

		return commerceTaxMethod;
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private Localization _localization;

}