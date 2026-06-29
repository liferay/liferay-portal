/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.server.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.constraints.NotEmpty;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Luis Ortiz
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Database schema export.", value = "DatabaseSchemaExport"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Database schema export.",
	requiredProperties = {"exportFilesPath"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DatabaseSchemaExport")
public class DatabaseSchemaExport implements Serializable {

	public static DatabaseSchemaExport toDTO(String json) {
		return ObjectMapperUtil.readValue(DatabaseSchemaExport.class, json);
	}

	public static DatabaseSchemaExport unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DatabaseSchemaExport.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Target directory for the generated files."
	)
	public String getExportFilesPath() {
		if (_exportFilesPathSupplier != null) {
			exportFilesPath = _exportFilesPathSupplier.get();

			_exportFilesPathSupplier = null;
		}

		return exportFilesPath;
	}

	public void setExportFilesPath(String exportFilesPath) {
		this.exportFilesPath = exportFilesPath;

		_exportFilesPathSupplier = null;
	}

	@JsonIgnore
	public void setExportFilesPath(
		UnsafeSupplier<String, Exception> exportFilesPathUnsafeSupplier) {

		_exportFilesPathSupplier = () -> {
			try {
				return exportFilesPathUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Target directory for the generated files.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String exportFilesPath;

	@JsonIgnore
	private Supplier<String> _exportFilesPathSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Generated schema file names."
	)
	public String[] getFileNames() {
		if (_fileNamesSupplier != null) {
			fileNames = _fileNamesSupplier.get();

			_fileNamesSupplier = null;
		}

		return fileNames;
	}

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;

		_fileNamesSupplier = null;
	}

	@JsonIgnore
	public void setFileNames(
		UnsafeSupplier<String[], Exception> fileNamesUnsafeSupplier) {

		_fileNamesSupplier = () -> {
			try {
				return fileNamesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Generated schema file names.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] fileNames;

	@JsonIgnore
	private Supplier<String[]> _fileNamesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Generated report file name."
	)
	public String getReportFileName() {
		if (_reportFileNameSupplier != null) {
			reportFileName = _reportFileNameSupplier.get();

			_reportFileNameSupplier = null;
		}

		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;

		_reportFileNameSupplier = null;
	}

	@JsonIgnore
	public void setReportFileName(
		UnsafeSupplier<String, Exception> reportFileNameUnsafeSupplier) {

		_reportFileNameSupplier = () -> {
			try {
				return reportFileNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Generated report file name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String reportFileName;

	@JsonIgnore
	private Supplier<String> _reportFileNameSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		String exportFilesPath = getExportFilesPath();

		if (exportFilesPath != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exportFilesPath\": ");

			sb.append("\"");

			sb.append(_escape(exportFilesPath));

			sb.append("\"");
		}

		String[] fileNames = getFileNames();

		if (fileNames != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileNames\": ");

			sb.append("[");

			for (int i = 0; i < fileNames.length; i++) {
				sb.append("\"");

				sb.append(_escape(fileNames[i]));

				sb.append("\"");

				if ((i + 1) < fileNames.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String reportFileName = getReportFileName();

		if (reportFileName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reportFileName\": ");

			sb.append("\"");

			sb.append(_escape(reportFileName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.server.dto.v1_0.DatabaseSchemaExport",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-1709515782