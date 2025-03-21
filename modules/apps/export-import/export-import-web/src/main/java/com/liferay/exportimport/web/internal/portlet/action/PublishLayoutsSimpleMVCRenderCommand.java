/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationFactory;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
		"mvc.command.name=/export_import/publish_layouts_simple"
	},
	service = MVCRenderCommand.class
)
public class PublishLayoutsSimpleMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			long exportImportConfigurationId = ParamUtil.getLong(
				renderRequest, "exportImportConfigurationId");

			if (exportImportConfigurationId <= 0) {
				_createExportImportConfiguration(renderRequest);
			}

			ActionUtil.getGroup(renderRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchGroupException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/publish/simple/publish_layouts_simple.jsp";
	}

	private void _createExportImportConfiguration(RenderRequest renderRequest)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration = null;

		boolean localPublishing = ParamUtil.getBoolean(
			renderRequest, "localPublishing");

		if (localPublishing) {
			exportImportConfiguration =
				ExportImportConfigurationFactory.
					buildDefaultLocalPublishingExportImportConfiguration(
						renderRequest);
		}
		else {
			exportImportConfiguration =
				ExportImportConfigurationFactory.
					buildDefaultRemotePublishingExportImportConfiguration(
						renderRequest);
		}

		renderRequest.setAttribute(
			"exportImportConfigurationId",
			exportImportConfiguration.getExportImportConfigurationId());
	}

}