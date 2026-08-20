/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Raposo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StagingProcessesPortletKeys.STAGING_PROCESSES,
		"mvc.command.name=/staging_processes/view_new_publish"
	},
	service = MVCRenderCommand.class
)
public class ViewNewPublishMVCRenderCommand extends BaseGroupMVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			"exportImportWebServletContext", _servletContext);

		return super.render(renderRequest, renderResponse);
	}

	@Override
	protected String getPath() {
		return "/new_publish.jsp";
	}

	@Reference(target = "(osgi.web.symbolicname=com.liferay.exportimport.web)")
	private ServletContext _servletContext;

}