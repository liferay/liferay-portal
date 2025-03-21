/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.web.internal.checkout;

import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.punchout.constants.PunchOutConstants;
import com.liferay.commerce.punchout.service.PunchOutReturnService;
import com.liferay.commerce.punchout.web.internal.helper.PunchOutSessionHelper;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	property = {
		"commerce.checkout.step.name=" + PunchOutCommerceCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=" + Integer.MIN_VALUE
	},
	service = CommerceCheckoutStep.class
)
public class PunchOutCommerceCheckoutStep extends BaseCommerceCheckoutStep {

	public static final String NAME = "punch-out";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isActive(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (_punchOutSessionHelper.punchOutEnabled(httpServletRequest) &&
			_punchOutSessionHelper.punchOutAllowed(httpServletRequest) &&
			_punchOutSessionHelper.punchOutSession(httpServletRequest)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isOrder() {
		return true;
	}

	@Override
	public boolean isVisible(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		return false;
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String punchOutReturnURL = _punchOutSessionHelper.getPunchOutReturnURL(
			httpServletRequest);

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (_log.isDebugEnabled()) {
			_log.debug("Transferring cart to " + punchOutReturnURL);
		}

		String punchOutRedirectURL =
			_punchOutReturnService.returnToPunchOutVendor(
				commerceOrder, punchOutReturnURL);

		if (Validator.isBlank(punchOutRedirectURL)) {
			_jspRenderer.renderJSP(
				_servletContext, httpServletRequest, httpServletResponse,
				"/checkout_step/punch_out_error.jsp");

			return;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		httpSession.setAttribute(
			PunchOutConstants.PUNCH_OUT_REDIRECT_URL_ATTRIBUTE_NAME,
			punchOutRedirectURL);

		httpServletRequest.setAttribute(
			PunchOutConstants.PUNCH_OUT_REDIRECT_URL_ATTRIBUTE_NAME,
			punchOutRedirectURL);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/checkout_step/punch_out.jsp");
	}

	@Override
	public boolean showControls(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PunchOutCommerceCheckoutStep.class);

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

	@Reference
	private PunchOutReturnService _punchOutReturnService;

	@Reference
	private PunchOutSessionHelper _punchOutSessionHelper;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.punchout.web)"
	)
	private ServletContext _servletContext;

}