/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortlet;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

/**
 * @author     Brian Wing Shun Chan
 * @author     Raymond Augé*
 * @deprecated As of Judson (7.1.x), with no direct replacement
 */
@Deprecated
public class StrutsPortlet extends LiferayPortlet {

	@Override
	public void doAbout(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doConfig(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doEdit(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doEditDefaults(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doEditGuest(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doHelp(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doPreview(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doPrint(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {
	}

	@Override
	public void processEvent(EventRequest request, EventResponse response)
		throws IOException, PortletException {
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {
	}

	protected void include(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

	protected String aboutAction;
	protected String configAction;
	protected boolean copyRequestParameters;
	protected String editAction;
	protected String editDefaultsAction;
	protected String editGuestAction;
	protected String helpAction;
	protected String previewAction;
	protected String printAction;
	protected String templatePath;
	protected String viewAction;

}