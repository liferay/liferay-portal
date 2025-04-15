/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal;

import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleTypeJSPContributor;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.commerce.pricing.web.internal.display.context.AddedAnyCommerceDiscountRuleDisplayContext;
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
	property = "commerce.discount.rule.type.jsp.contributor.key=" + CommerceDiscountRuleConstants.TYPE_ADDED_ANY,
	service = CommerceDiscountRuleTypeJSPContributor.class
)
public class AddedAnyCommerceDiscountRuleTypeJSPContributor
	implements CommerceDiscountRuleTypeJSPContributor {

	@Override
	public void render(
			long commerceDiscountId, long commerceDiscountRuleId,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		AddedAnyCommerceDiscountRuleDisplayContext
			addedAnyCommerceDiscountRuleDisplayContext =
				new AddedAnyCommerceDiscountRuleDisplayContext(
					_commerceDiscountRuleService, httpServletRequest);

		httpServletRequest.setAttribute(
			"view.jsp-addedAnyCommerceDiscountRuleDisplayContext",
			addedAnyCommerceDiscountRuleDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/commerce_discounts/rule/type/added_any.jsp");
	}

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.pricing.web)"
	)
	private ServletContext _servletContext;

}