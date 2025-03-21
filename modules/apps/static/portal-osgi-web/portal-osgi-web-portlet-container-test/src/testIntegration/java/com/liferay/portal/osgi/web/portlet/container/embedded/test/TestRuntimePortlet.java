/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.portal.osgi.web.portlet.container.test.TestPortlet;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

/**
 * @author Manuel de la Peña
 */
public class TestRuntimePortlet extends TestPortlet {

	public boolean isCalledRuntime() {
		return _calledRuntime;
	}

	/**
	 * @throws IOException
	 */
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_calledRuntime = true;

		super.render(renderRequest, renderResponse);
	}

	private boolean _calledRuntime;

}