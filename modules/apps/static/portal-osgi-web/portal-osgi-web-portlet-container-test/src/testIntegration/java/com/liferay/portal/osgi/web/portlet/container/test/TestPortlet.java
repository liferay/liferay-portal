/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.GenericPortlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

/**
 * @author Raymond Augé
 */
public class TestPortlet extends GenericPortlet {

	public boolean isCalledAction() {
		return _calledProcessAction;
	}

	public boolean isCalledRender() {
		return _calledRender;
	}

	public boolean isCalledServeResource() {
		return _calledServeResource;
	}

	@Override
	public void processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		_calledProcessAction = true;
	}

	/**
	 * @throws IOException
	 */
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_calledRender = true;
	}

	public void reset() {
		_calledProcessAction = false;
		_calledRender = false;
		_calledServeResource = false;
	}

	/**
	 * @throws IOException
	 */
	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		_calledServeResource = true;
	}

	private boolean _calledProcessAction;
	private boolean _calledRender;
	private boolean _calledServeResource;

}