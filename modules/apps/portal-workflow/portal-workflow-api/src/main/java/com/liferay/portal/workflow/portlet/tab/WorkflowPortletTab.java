/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.portlet.tab;

import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;

/**
 * @author Adam Brandizzi
 */
public interface WorkflowPortletTab extends DynamicInclude {

	public String getName();

	public PortletURL getSearchURL(
		RenderRequest renderRequest, RenderResponse renderResponse);

	public ServletContext getServletContext();

	public void prepareDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException;

	public void prepareProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException;

	public void prepareRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException;

}