/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.dto.v1_0;

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
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
@GraphQLName("DocumentBulkSelection")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DocumentBulkSelection")
public class DocumentBulkSelection implements Serializable {

	public static DocumentBulkSelection toDTO(String json) {
		return ObjectMapperUtil.readValue(DocumentBulkSelection.class, json);
	}

	public static DocumentBulkSelection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DocumentBulkSelection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getDocumentIds() {
		if (_documentIdsSupplier != null) {
			documentIds = _documentIdsSupplier.get();

			_documentIdsSupplier = null;
		}

		return documentIds;
	}

	public void setDocumentIds(String[] documentIds) {
		this.documentIds = documentIds;

		_documentIdsSupplier = null;
	}

	@JsonIgnore
	public void setDocumentIds(
		UnsafeSupplier<String[], Exception> documentIdsUnsafeSupplier) {

		_documentIdsSupplier = () -> {
			try {
				return documentIdsUnsafeSupplier.get();
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
	protected String[] documentIds;

	@JsonIgnore
	private Supplier<String[]> _documentIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public SelectionScope getSelectionScope() {
		if (_selectionScopeSupplier != null) {
			selectionScope = _selectionScopeSupplier.get();

			_selectionScopeSupplier = null;
		}

		return selectionScope;
	}

	public void setSelectionScope(SelectionScope selectionScope) {
		this.selectionScope = selectionScope;

		_selectionScopeSupplier = null;
	}

	@JsonIgnore
	public void setSelectionScope(
		UnsafeSupplier<SelectionScope, Exception>
			selectionScopeUnsafeSupplier) {

		_selectionScopeSupplier = () -> {
			try {
				return selectionScopeUnsafeSupplier.get();
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
	protected SelectionScope selectionScope;

	@JsonIgnore
	private Supplier<SelectionScope> _selectionScopeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DocumentBulkSelection)) {
			return false;
		}

		DocumentBulkSelection documentBulkSelection =
			(DocumentBulkSelection)object;

		return Objects.equals(toString(), documentBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] documentIds = getDocumentIds();

		if (documentIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentIds\": ");

			sb.append("[");

			for (int i = 0; i < documentIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(documentIds[i]));

				sb.append("\"");

				if ((i + 1) < documentIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		SelectionScope selectionScope = getSelectionScope();

		if (selectionScope != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(String.valueOf(selectionScope));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.bulk.rest.dto.v1_0.DocumentBulkSelection",
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