/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.display.context.util;

import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.JavaConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author     Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.commerce.product.display.context.helper.CPRequestHelper}
 */
@Deprecated
public class CPRequestHelper extends BaseRequestHelper {

	public CPRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);

		Object portletRequest = httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest instanceof RenderRequest) {
			_renderRequest = (RenderRequest)portletRequest;
		}

		Object portletResponse = httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse instanceof RenderResponse) {
			_renderResponse = (RenderResponse)portletResponse;
		}
	}

	public long getCommerceChannelGroupId() throws PortalException {
		return CommerceChannelLocalServiceUtil.
			getCommerceChannelGroupIdBySiteGroupId(getScopeGroupId());
	}

	public RenderRequest getRenderRequest() {
		return _renderRequest;
	}

	public RenderResponse getRenderResponse() {
		return _renderResponse;
	}

	public void setRenderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public void setRenderResponse(RenderResponse renderResponse) {
		_renderResponse = renderResponse;
	}

	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;

}