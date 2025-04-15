/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.entry.type;

import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeJSPContributor;
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
	property = "commerce.order.rule.entry.type.jsp.contributor.key=" + COREntryConstants.TYPE_MINIMUM_ORDER_AMOUNT,
	service = COREntryTypeJSPContributor.class
)
public class MinimumAmountCOREntryTypeJSPContributor
	implements COREntryTypeJSPContributor {

	@Override
	public void render(
			long corEntryId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/cor_entry/type/minimum_amount.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.rule.web)"
	)
	private ServletContext _servletContext;

}