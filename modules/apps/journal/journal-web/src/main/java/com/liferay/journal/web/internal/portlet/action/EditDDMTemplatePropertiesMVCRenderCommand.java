/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/edit_ddm_template_properties"
	},
	service = MVCRenderCommand.class
)
public class EditDDMTemplatePropertiesMVCRenderCommand
	extends BaseDDMTemplateMVCRenderCommand {

	@Override
	protected String getPath() {
		return "/ddm_template/edit_properties.jsp";
	}

}