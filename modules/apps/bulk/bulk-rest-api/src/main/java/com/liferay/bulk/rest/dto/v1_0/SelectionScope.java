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
@GraphQLName("SelectionScope")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SelectionScope")
public class SelectionScope implements Serializable {

	public static SelectionScope toDTO(String json) {
		return ObjectMapperUtil.readValue(SelectionScope.class, json);
	}

	public static SelectionScope unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SelectionScope.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getFolderId() {
		if (_folderIdSupplier != null) {
			folderId = _folderIdSupplier.get();

			_folderIdSupplier = null;
		}

		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;

		_folderIdSupplier = null;
	}

	@JsonIgnore
	public void setFolderId(
		UnsafeSupplier<Long, Exception> folderIdUnsafeSupplier) {

		_folderIdSupplier = () -> {
			try {
				return folderIdUnsafeSupplier.get();
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
	protected Long folderId;

	@JsonIgnore
	private Supplier<Long> _folderIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getRepositoryId() {
		if (_repositoryIdSupplier != null) {
			repositoryId = _repositoryIdSupplier.get();

			_repositoryIdSupplier = null;
		}

		return repositoryId;
	}

	public void setRepositoryId(Long repositoryId) {
		this.repositoryId = repositoryId;

		_repositoryIdSupplier = null;
	}

	@JsonIgnore
	public void setRepositoryId(
		UnsafeSupplier<Long, Exception> repositoryIdUnsafeSupplier) {

		_repositoryIdSupplier = () -> {
			try {
				return repositoryIdUnsafeSupplier.get();
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
	protected Long repositoryId;

	@JsonIgnore
	private Supplier<Long> _repositoryIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSelectAll() {
		if (_selectAllSupplier != null) {
			selectAll = _selectAllSupplier.get();

			_selectAllSupplier = null;
		}

		return selectAll;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;

		_selectAllSupplier = null;
	}

	@JsonIgnore
	public void setSelectAll(
		UnsafeSupplier<Boolean, Exception> selectAllUnsafeSupplier) {

		_selectAllSupplier = () -> {
			try {
				return selectAllUnsafeSupplier.get();
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
	protected Boolean selectAll;

	@JsonIgnore
	private Supplier<Boolean> _selectAllSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SelectionScope)) {
			return false;
		}

		SelectionScope selectionScope = (SelectionScope)object;

		return Objects.equals(toString(), selectionScope.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long folderId = getFolderId();

		if (folderId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"folderId\": ");

			sb.append(folderId);
		}

		Long repositoryId = getRepositoryId();

		if (repositoryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repositoryId\": ");

			sb.append(repositoryId);
		}

		Boolean selectAll = getSelectAll();

		if (selectAll != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(selectAll);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.bulk.rest.dto.v1_0.SelectionScope",
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