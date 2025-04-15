/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.subscription.type.web.internal;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.subscription.type.web.internal.display.context.MonthlyCPSubscriptionTypeDisplayContext;
import com.liferay.commerce.product.util.CPSubscriptionTypeJSPContributor;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.product.subscription.type.jsp.contributor.key=" + CPConstants.MONTHLY_SUBSCRIPTION_TYPE,
	service = CPSubscriptionTypeJSPContributor.class
)
public class MonthlyCPSubscriptionTypeJSPContributor
	implements CPSubscriptionTypeJSPContributor {

	@Override
	public void render(
			Object object, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		render(object, httpServletRequest, httpServletResponse, true);
	}

	@Override
	public void render(
			Object object, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean payment)
		throws Exception {

		MonthlyCPSubscriptionTypeDisplayContext
			monthlyCPSubscriptionTypeDisplayContext =
				new MonthlyCPSubscriptionTypeDisplayContext(object, payment);

		httpServletRequest.setAttribute(
			"view.jsp-monthlyCPSubscriptionTypeDisplayContext",
			monthlyCPSubscriptionTypeDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/monthly/view.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.subscription.type.web)"
	)
	private ServletContext _servletContext;

}