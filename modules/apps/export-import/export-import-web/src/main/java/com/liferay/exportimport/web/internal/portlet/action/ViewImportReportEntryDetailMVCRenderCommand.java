/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.PortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jaime León Rosado
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
		"jakarta.portlet.name=" + ExportImportPortletKeys.IMPORT,
		"jakarta.portlet.name=" + PortletKeys.COMPANY_IMPORT,
		"mvc.command.name=/export_import/view_import_report_entry_detail"
	},
	service = MVCRenderCommand.class
)
public class ViewImportReportEntryDetailMVCRenderCommand
	extends BaseGroupMVCRenderCommand {

	@Override
	protected String getPath() {
		return "/import/view_import_report_entry_detail.jsp";
	}

}