/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.changeset.web.internal.portlet.action;

import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Akos Thurzo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ChangesetPortletKeys.CHANGESET,
		"mvc.command.name=/export_import_changeset/export_import_changeset"
	},
	service = MVCRenderCommand.class
)
public class ExportImportChangesetMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		return "/error.jsp";
	}

}