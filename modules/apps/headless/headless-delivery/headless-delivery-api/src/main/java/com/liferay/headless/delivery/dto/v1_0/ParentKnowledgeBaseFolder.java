/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The folder's parent Knowledge Base folder, if it exists.",
	value = "ParentKnowledgeBaseFolder"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ParentKnowledgeBaseFolder")
public class ParentKnowledgeBaseFolder implements Serializable {

	public static ParentKnowledgeBaseFolder toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ParentKnowledgeBaseFolder.class, json);
	}

	public static ParentKnowledgeBaseFolder unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ParentKnowledgeBaseFolder.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent folder's ID."
	)
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

	@GraphQLField(description = "The parent folder's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long folderId;

	@JsonIgnore
	private Supplier<Long> _folderIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The parent folder's name."
	)
	public String getFolderName() {
		if (_folderNameSupplier != null) {
			folderName = _folderNameSupplier.get();

			_folderNameSupplier = null;
		}

		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;

		_folderNameSupplier = null;
	}

	@JsonIgnore
	public void setFolderName(
		UnsafeSupplier<String, Exception> folderNameUnsafeSupplier) {

		_folderNameSupplier = () -> {
			try {
				return folderNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The parent folder's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String folderName;

	@JsonIgnore
	private Supplier<String> _folderNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ParentKnowledgeBaseFolder)) {
			return false;
		}

		ParentKnowledgeBaseFolder parentKnowledgeBaseFolder =
			(ParentKnowledgeBaseFolder)object;

		return Objects.equals(toString(), parentKnowledgeBaseFolder.toString());
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

		String folderName = getFolderName();

		if (folderName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"folderName\": ");

			sb.append("\"");

			sb.append(_escape(folderName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.ParentKnowledgeBaseFolder",
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