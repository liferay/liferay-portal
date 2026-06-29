/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.server.client.dto.v1_0;

import com.liferay.headless.admin.server.client.function.UnsafeSupplier;
import com.liferay.headless.admin.server.client.serdes.v1_0.DatabaseSchemaExportSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Luis Ortiz
 * @generated
 */
@Generated("")
public class DatabaseSchemaExport implements Cloneable, Serializable {

	public static DatabaseSchemaExport toDTO(String json) {
		return DatabaseSchemaExportSerDes.toDTO(json);
	}

	public String getExportFilesPath() {
		return exportFilesPath;
	}

	public void setExportFilesPath(String exportFilesPath) {
		this.exportFilesPath = exportFilesPath;
	}

	public void setExportFilesPath(
		UnsafeSupplier<String, Exception> exportFilesPathUnsafeSupplier) {

		try {
			exportFilesPath = exportFilesPathUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String exportFilesPath;

	public String[] getFileNames() {
		return fileNames;
	}

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}

	public void setFileNames(
		UnsafeSupplier<String[], Exception> fileNamesUnsafeSupplier) {

		try {
			fileNames = fileNamesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] fileNames;

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public void setReportFileName(
		UnsafeSupplier<String, Exception> reportFileNameUnsafeSupplier) {

		try {
			reportFileName = reportFileNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String reportFileName;

	@Override
	public DatabaseSchemaExport clone() throws CloneNotSupportedException {
		return (DatabaseSchemaExport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DatabaseSchemaExport)) {
			return false;
		}

		DatabaseSchemaExport databaseSchemaExport =
			(DatabaseSchemaExport)object;

		return Objects.equals(toString(), databaseSchemaExport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DatabaseSchemaExportSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1254767022