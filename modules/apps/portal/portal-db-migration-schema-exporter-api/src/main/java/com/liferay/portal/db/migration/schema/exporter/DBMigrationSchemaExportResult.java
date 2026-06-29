/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter;

import java.util.List;

/**
 * @author Luis Ortiz
 */
public class DBMigrationSchemaExportResult {

	public DBMigrationSchemaExportResult(
		List<String> fileNames, String reportFileName) {

		_fileNames = fileNames;
		_reportFileName = reportFileName;
	}

	public List<String> getFileNames() {
		return _fileNames;
	}

	public String getReportFileName() {
		return _reportFileName;
	}

	private final List<String> _fileNames;
	private final String _reportFileName;

}