/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.LiferayActionResponse;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderURL;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class ActionResponseImpl
	extends StateAwareResponseImpl implements LiferayActionResponse {

	@Override
	public RenderURL createRedirectURL(MimeResponse.Copy copy) {
		return createRenderURL(copy);
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.ACTION_PHASE;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		if ((location == null) ||
			(!location.startsWith("/") && !location.contains("://"))) {

			throw new IllegalArgumentException(
				location + " is not a valid redirect");
		}

		if (isCalledSetRenderParameter()) {
			throw new IllegalStateException(
				"Set render parameter has already been called");
		}

		setRedirectLocation(location);
	}

	@Override
	public void sendRedirect(String location, String renderUrlParamName)
		throws IOException {
	}

}