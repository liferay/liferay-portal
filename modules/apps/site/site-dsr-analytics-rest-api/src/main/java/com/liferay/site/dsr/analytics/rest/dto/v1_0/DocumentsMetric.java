/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.dto.v1_0;

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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
@GraphQLName("DocumentsMetric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DocumentsMetric")
public class DocumentsMetric implements Serializable {

	public static DocumentsMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(DocumentsMetric.class, json);
	}

	public static DocumentsMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(DocumentsMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DocumentMetric[] getDocumentMetrics() {
		if (_documentMetricsSupplier != null) {
			documentMetrics = _documentMetricsSupplier.get();

			_documentMetricsSupplier = null;
		}

		return documentMetrics;
	}

	public void setDocumentMetrics(DocumentMetric[] documentMetrics) {
		this.documentMetrics = documentMetrics;

		_documentMetricsSupplier = null;
	}

	@JsonIgnore
	public void setDocumentMetrics(
		UnsafeSupplier<DocumentMetric[], Exception>
			documentMetricsUnsafeSupplier) {

		_documentMetricsSupplier = () -> {
			try {
				return documentMetricsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DocumentMetric[] documentMetrics;

	@JsonIgnore
	private Supplier<DocumentMetric[]> _documentMetricsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTotal() {
		if (_totalSupplier != null) {
			total = _totalSupplier.get();

			_totalSupplier = null;
		}

		return total;
	}

	public void setTotal(Long total) {
		this.total = total;

		_totalSupplier = null;
	}

	@JsonIgnore
	public void setTotal(UnsafeSupplier<Long, Exception> totalUnsafeSupplier) {
		_totalSupplier = () -> {
			try {
				return totalUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long total;

	@JsonIgnore
	private Supplier<Long> _totalSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DocumentsMetric)) {
			return false;
		}

		DocumentsMetric documentsMetric = (DocumentsMetric)object;

		return Objects.equals(toString(), documentsMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DocumentMetric[] documentMetrics = getDocumentMetrics();

		if (documentMetrics != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentMetrics\": ");

			sb.append("[");

			for (int i = 0; i < documentMetrics.length; i++) {
				sb.append(String.valueOf(documentMetrics[i]));

				if ((i + 1) < documentMetrics.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long total = getTotal();

		if (total != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(total);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.site.dsr.analytics.rest.dto.v1_0.DocumentsMetric",
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
// LIFERAY-REST-BUILDER-HASH:2020597693