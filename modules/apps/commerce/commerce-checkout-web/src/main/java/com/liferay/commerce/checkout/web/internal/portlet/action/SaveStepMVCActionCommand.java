/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_CHECKOUT,
		"mvc.command.name=/commerce_checkout/save_step"
	},
	service = MVCActionCommand.class
)
public class SaveStepMVCActionCommand extends BaseMVCActionCommand {

	public String getRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String checkoutStepName)
		throws Exception {

		String redirect = GetterUtil.getString(
			actionRequest.getAttribute(WebKeys.REDIRECT));

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		if (!SessionErrors.isEmpty(actionRequest)) {
			return _getPortletURL(
				actionRequest, actionResponse, checkoutStepName);
		}

		CommerceCheckoutStep commerceCheckoutStep =
			_commerceCheckoutStepRegistry.getNextCommerceCheckoutStep(
				checkoutStepName, _portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse));

		if (commerceCheckoutStep == null) {
			return ParamUtil.getString(actionRequest, "redirect");
		}

		return _getPortletURL(
			actionRequest, actionResponse, commerceCheckoutStep.getName());
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String checkoutStepName = ParamUtil.getString(
			actionRequest, "checkoutStepName");

		CommerceCheckoutStep commerceCheckoutStep =
			_commerceCheckoutStepRegistry.getCommerceCheckoutStep(
				checkoutStepName);

		commerceCheckoutStep.processAction(actionRequest, actionResponse);

		hideDefaultSuccessMessage(actionRequest);

		String redirect = getRedirect(
			actionRequest, actionResponse, checkoutStepName);

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	private String _getPortletURL(
		ActionRequest actionRequest, ActionResponse actionResponse,
		String checkoutStepName) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setParameter(
			"checkoutStepName", checkoutStepName
		).setParameter(
			"commerceOrderUuid",
			ParamUtil.getString(actionRequest, "commerceOrderUuid")
		).buildString();
	}

	@Reference
	private CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;

	@Reference
	private Portal _portal;

}