/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StagingProcessesPortletKeys.STAGING_PROCESSES,
		"mvc.command.name=/staging_processes/edit_publish_configuration"
	},
	service = MVCRenderCommand.class
)
public class EditPublishConfigurationMVCRenderCommand
	extends BaseGroupMVCRenderCommand {

	@Override
	protected String getPath() {
		return "/publish_templates/edit_publish_configuration.jsp";
	}

}