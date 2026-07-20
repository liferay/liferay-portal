/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.portal.db.migration.schema.exporter.DBMigrationSchemaExporter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;
import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/export_database_schema"
	},
	service = MVCActionCommand.class
)
public class ExportDatabaseSchemaMVCActionCommand extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		if (!StringUtil.equals(actionRequest.getMethod(), HttpMethods.POST)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			SessionErrors.add(
				actionRequest,
				PrincipalException.MustBeOmniadmin.class.getName());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		CaptchaUtil.check(actionRequest);

		String exportFilesPath = ParamUtil.getString(
			actionRequest, "exportFilesPath");

		try {
			File exportFilesDirectory = new File(exportFilesPath);

			exportFilesPath = exportFilesDirectory.getCanonicalPath();

			_dbMigrationSchemaExporter.export(exportFilesPath);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to write to \"" + exportFilesPath + "\"", ioException);

			SessionErrors.add(actionRequest, "databaseSchemaExportFailed");

			return;
		}

		hideDefaultSuccessMessage(actionRequest);

		SessionMessages.add(
			actionRequest, "databaseSchemaExported", exportFilesPath);

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportDatabaseSchemaMVCActionCommand.class);

	@Reference
	private DBMigrationSchemaExporter _dbMigrationSchemaExporter;

}