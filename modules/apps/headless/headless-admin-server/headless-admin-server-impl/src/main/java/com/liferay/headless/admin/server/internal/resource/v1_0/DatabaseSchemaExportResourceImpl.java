/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.server.internal.resource.v1_0;

import com.liferay.headless.admin.server.dto.v1_0.DatabaseSchemaExport;
import com.liferay.headless.admin.server.resource.v1_0.DatabaseSchemaExportResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.migration.schema.exporter.DBMigrationSchemaExportResult;
import com.liferay.portal.db.migration.schema.exporter.DBMigrationSchemaExporter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Luis Ortiz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/database-schema-export.properties",
	scope = ServiceScope.PROTOTYPE, service = DatabaseSchemaExportResource.class
)
public class DatabaseSchemaExportResourceImpl
	extends BaseDatabaseSchemaExportResourceImpl {

	@Override
	public DatabaseSchemaExport postDatabaseSchemaExport(
			DatabaseSchemaExport databaseSchemaExport)
		throws Exception {

		_checkPermission();

		String exportFilesPath = databaseSchemaExport.getExportFilesPath();

		if (Validator.isBlank(exportFilesPath)) {
			throw new BadRequestException("Export files path is null");
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"User ", contextUser.getUserId(),
					" is exporting the database schema definition to ",
					exportFilesPath));
		}

		DatabaseSchemaExport resultDatabaseSchemaExport =
			new DatabaseSchemaExport();

		resultDatabaseSchemaExport.setExportFilesPath(() -> exportFilesPath);

		try {
			DBMigrationSchemaExportResult dbMigrationSchemaExportResult =
				_dbMigrationSchemaExporter.export(exportFilesPath);

			resultDatabaseSchemaExport.setFileNames(
				() -> dbMigrationSchemaExportResult.getFileNames(
				).toArray(
					new String[0]
				));
			resultDatabaseSchemaExport.setReportFileName(
				dbMigrationSchemaExportResult::getReportFileName);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to write to \"" + exportFilesPath + "\"", ioException);

			throw new BadRequestException(
				"Unable to write to \"" + exportFilesPath + "\"");
		}

		return resultDatabaseSchemaExport;
	}

	private void _checkPermission() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException.MustBeOmniadmin(permissionChecker);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DatabaseSchemaExportResourceImpl.class);

	@Reference
	private DBMigrationSchemaExporter _dbMigrationSchemaExporter;

}