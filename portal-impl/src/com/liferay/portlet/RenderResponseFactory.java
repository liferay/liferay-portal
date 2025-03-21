/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayRenderResponse;
import com.liferay.portlet.internal.RenderRequestImpl;
import com.liferay.portlet.internal.RenderResponseImpl;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.filter.RenderRequestWrapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class RenderResponseFactory {

	public static LiferayRenderResponse create() {
		return new RenderResponseImpl();
	}

	public static LiferayRenderResponse create(
		HttpServletResponse httpServletResponse, RenderRequest renderRequest) {

		while (renderRequest instanceof RenderRequestWrapper) {
			RenderRequestWrapper renderRequestWrapper =
				(RenderRequestWrapper)renderRequest;

			renderRequest = renderRequestWrapper.getRequest();
		}

		RenderResponseImpl renderResponseImpl = new RenderResponseImpl();

		renderResponseImpl.init(
			(RenderRequestImpl)renderRequest, httpServletResponse);

		return renderResponseImpl;
	}

}