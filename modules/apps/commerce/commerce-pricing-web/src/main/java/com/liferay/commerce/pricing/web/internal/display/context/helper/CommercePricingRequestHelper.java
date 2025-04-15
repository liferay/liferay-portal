/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.JavaConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Riccardo Alberti
 */
public class CommercePricingRequestHelper extends BaseRequestHelper {

	public CommercePricingRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);

		Object portletRequest = httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		Object portletResponse = httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (!(portletRequest instanceof RenderRequest) ||
			!(portletResponse instanceof RenderResponse)) {

			throw new IllegalArgumentException();
		}

		_renderRequest = (RenderRequest)portletRequest;
		_renderResponse = (RenderResponse)portletResponse;
	}

	public RenderResponse getRenderResponse() {
		return _renderResponse;
	}

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}