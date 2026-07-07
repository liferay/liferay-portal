/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The result of exporting a portal instance.",
	value = "PortalInstanceExport"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The result of exporting a portal instance."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PortalInstanceExport")
public class PortalInstanceExport implements Serializable {

	public static PortalInstanceExport toDTO(String json) {
		return ObjectMapperUtil.readValue(PortalInstanceExport.class, json);
	}

	public static PortalInstanceExport unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PortalInstanceExport.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the schema created by the export (e.g., lexported_12345)."
	)
	public String getExportedPartitionName() {
		if (_exportedPartitionNameSupplier != null) {
			exportedPartitionName = _exportedPartitionNameSupplier.get();

			_exportedPartitionNameSupplier = null;
		}

		return exportedPartitionName;
	}

	public void setExportedPartitionName(String exportedPartitionName) {
		this.exportedPartitionName = exportedPartitionName;

		_exportedPartitionNameSupplier = null;
	}

	@JsonIgnore
	public void setExportedPartitionName(
		UnsafeSupplier<String, Exception> exportedPartitionNameUnsafeSupplier) {

		_exportedPartitionNameSupplier = () -> {
			try {
				return exportedPartitionNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The name of the schema created by the export (e.g., lexported_12345)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String exportedPartitionName;

	@JsonIgnore
	private Supplier<String> _exportedPartitionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the source partition that was exported (e.g., lpartition_12345)."
	)
	public String getSourcePartitionName() {
		if (_sourcePartitionNameSupplier != null) {
			sourcePartitionName = _sourcePartitionNameSupplier.get();

			_sourcePartitionNameSupplier = null;
		}

		return sourcePartitionName;
	}

	public void setSourcePartitionName(String sourcePartitionName) {
		this.sourcePartitionName = sourcePartitionName;

		_sourcePartitionNameSupplier = null;
	}

	@JsonIgnore
	public void setSourcePartitionName(
		UnsafeSupplier<String, Exception> sourcePartitionNameUnsafeSupplier) {

		_sourcePartitionNameSupplier = () -> {
			try {
				return sourcePartitionNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The name of the source partition that was exported (e.g., lpartition_12345)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sourcePartitionName;

	@JsonIgnore
	private Supplier<String> _sourcePartitionNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortalInstanceExport)) {
			return false;
		}

		PortalInstanceExport portalInstanceExport =
			(PortalInstanceExport)object;

		return Objects.equals(toString(), portalInstanceExport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String exportedPartitionName = getExportedPartitionName();

		if (exportedPartitionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exportedPartitionName\": ");

			sb.append("\"");

			sb.append(_escape(exportedPartitionName));

			sb.append("\"");
		}

		String sourcePartitionName = getSourcePartitionName();

		if (sourcePartitionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourcePartitionName\": ");

			sb.append("\"");

			sb.append(_escape(sourcePartitionName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceExport",
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
// LIFERAY-REST-BUILDER-HASH:1269064002