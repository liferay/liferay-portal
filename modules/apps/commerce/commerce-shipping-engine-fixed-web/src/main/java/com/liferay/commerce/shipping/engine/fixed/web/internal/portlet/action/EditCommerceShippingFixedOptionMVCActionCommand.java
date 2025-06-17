/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionAmountException;
import com.liferay.commerce.shipping.engine.fixed.exception.CommerceShippingFixedOptionKeyException;
import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPPING_METHODS,
		"mvc.command.name=/commerce_shipping_methods/edit_commerce_shipping_fixed_option"
	},
	service = MVCActionCommand.class
)
public class EditCommerceShippingFixedOptionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceShippingFixedOption(actionRequest);

				String redirect = _getSaveAndContinueRedirect(actionRequest);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceShippingFixedOptions(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceShippingFixedOptionAmountException ||
				exception instanceof CommerceShippingFixedOptionKeyException) {

				SessionErrors.add(actionRequest, exception.getClass());

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				String redirect = _getSaveAndContinueRedirect(actionRequest);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (exception instanceof NoSuchShippingFixedOptionException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceShippingFixedOptions(
			ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceShippingFixedOptionIds = null;

		long commerceShippingFixedOptionId = ParamUtil.getLong(
			actionRequest, "commerceShippingFixedOptionId");

		if (commerceShippingFixedOptionId > 0) {
			deleteCommerceShippingFixedOptionIds = new long[] {
				commerceShippingFixedOptionId
			};
		}
		else {
			deleteCommerceShippingFixedOptionIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceShippingFixedOptionIds"),
				0L);
		}

		for (long deleteCommerceShippingFixedOptionId :
				deleteCommerceShippingFixedOptionIds) {

			_commerceShippingFixedOptionService.
				deleteCommerceShippingFixedOption(
					deleteCommerceShippingFixedOptionId);
		}
	}

	private String _getSaveAndContinueRedirect(ActionRequest actionRequest)
		throws Exception {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG);

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, portletConfig.getPortletName(),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName",
			"/commerce_shipping_methods/edit_commerce_shipping_fixed_option");
		portletURL.setParameter(
			"commerceShippingFixedOptionId",
			String.valueOf(
				ParamUtil.getLong(
					actionRequest, "commerceShippingFixedOptionId")));
		portletURL.setParameter(
			"commerceShippingMethodId",
			String.valueOf(
				ParamUtil.getLong(actionRequest, "commerceShippingMethodId")));
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	private CommerceShippingFixedOption _updateCommerceShippingFixedOption(
			ActionRequest actionRequest)
		throws Exception {

		long commerceShippingFixedOptionId = ParamUtil.getLong(
			actionRequest, "commerceShippingFixedOptionId");

		BigDecimal amount = _commercePriceFormatter.parse(
			actionRequest, false, CommerceShippingFixedOption.class.getName(),
			"amount");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		String key = ParamUtil.getString(actionRequest, "key");
		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		double priority = ParamUtil.getDouble(actionRequest, "priority");

		CommerceShippingFixedOption commerceShippingFixedOption = null;

		if (commerceShippingFixedOptionId > 0) {
			commerceShippingFixedOption =
				_commerceShippingFixedOptionService.
					updateCommerceShippingFixedOption(
						commerceShippingFixedOptionId, amount, descriptionMap,
						key, nameMap, priority);
		}
		else {
			long commerceShippingMethodId = ParamUtil.getLong(
				actionRequest, "commerceShippingMethodId");

			CommerceShippingMethod commerceShippingMethod =
				_commerceShippingMethodService.getCommerceShippingMethod(
					commerceShippingMethodId);

			commerceShippingFixedOption =
				_commerceShippingFixedOptionService.
					addCommerceShippingFixedOption(
						commerceShippingMethod.getGroupId(),
						commerceShippingMethod.getCommerceShippingMethodId(),
						amount, descriptionMap, key, nameMap, priority);
		}

		return commerceShippingFixedOption;
	}

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private Localization _localization;

}