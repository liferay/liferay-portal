/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
@GraphQLName("DSDocument")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DSDocument")
public class DSDocument implements Serializable {

	public static DSDocument toDTO(String json) {
		return ObjectMapperUtil.readValue(DSDocument.class, json);
	}

	public static DSDocument unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(DSDocument.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssignTabsToDSRecipientId() {
		if (_assignTabsToDSRecipientIdSupplier != null) {
			assignTabsToDSRecipientId =
				_assignTabsToDSRecipientIdSupplier.get();

			_assignTabsToDSRecipientIdSupplier = null;
		}

		return assignTabsToDSRecipientId;
	}

	public void setAssignTabsToDSRecipientId(String assignTabsToDSRecipientId) {
		this.assignTabsToDSRecipientId = assignTabsToDSRecipientId;

		_assignTabsToDSRecipientIdSupplier = null;
	}

	@JsonIgnore
	public void setAssignTabsToDSRecipientId(
		UnsafeSupplier<String, Exception>
			assignTabsToDSRecipientIdUnsafeSupplier) {

		_assignTabsToDSRecipientIdSupplier = () -> {
			try {
				return assignTabsToDSRecipientIdUnsafeSupplier.get();
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
	protected String assignTabsToDSRecipientId;

	@JsonIgnore
	private Supplier<String> _assignTabsToDSRecipientIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getData() {
		if (_dataSupplier != null) {
			data = _dataSupplier.get();

			_dataSupplier = null;
		}

		return data;
	}

	public void setData(String data) {
		this.data = data;

		_dataSupplier = null;
	}

	@JsonIgnore
	public void setData(UnsafeSupplier<String, Exception> dataUnsafeSupplier) {
		_dataSupplier = () -> {
			try {
				return dataUnsafeSupplier.get();
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
	protected String data;

	@JsonIgnore
	private Supplier<String> _dataSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFileEntryExternalReferenceCode() {
		if (_fileEntryExternalReferenceCodeSupplier != null) {
			fileEntryExternalReferenceCode =
				_fileEntryExternalReferenceCodeSupplier.get();

			_fileEntryExternalReferenceCodeSupplier = null;
		}

		return fileEntryExternalReferenceCode;
	}

	public void setFileEntryExternalReferenceCode(
		String fileEntryExternalReferenceCode) {

		this.fileEntryExternalReferenceCode = fileEntryExternalReferenceCode;

		_fileEntryExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setFileEntryExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			fileEntryExternalReferenceCodeUnsafeSupplier) {

		_fileEntryExternalReferenceCodeSupplier = () -> {
			try {
				return fileEntryExternalReferenceCodeUnsafeSupplier.get();
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
	protected String fileEntryExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _fileEntryExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFileExtension() {
		if (_fileExtensionSupplier != null) {
			fileExtension = _fileExtensionSupplier.get();

			_fileExtensionSupplier = null;
		}

		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;

		_fileExtensionSupplier = null;
	}

	@JsonIgnore
	public void setFileExtension(
		UnsafeSupplier<String, Exception> fileExtensionUnsafeSupplier) {

		_fileExtensionSupplier = () -> {
			try {
				return fileExtensionUnsafeSupplier.get();
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
	protected String fileExtension;

	@JsonIgnore
	private Supplier<String> _fileExtensionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getTransformPDFFields() {
		if (_transformPDFFieldsSupplier != null) {
			transformPDFFields = _transformPDFFieldsSupplier.get();

			_transformPDFFieldsSupplier = null;
		}

		return transformPDFFields;
	}

	public void setTransformPDFFields(Boolean transformPDFFields) {
		this.transformPDFFields = transformPDFFields;

		_transformPDFFieldsSupplier = null;
	}

	@JsonIgnore
	public void setTransformPDFFields(
		UnsafeSupplier<Boolean, Exception> transformPDFFieldsUnsafeSupplier) {

		_transformPDFFieldsSupplier = () -> {
			try {
				return transformPDFFieldsUnsafeSupplier.get();
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
	protected Boolean transformPDFFields;

	@JsonIgnore
	private Supplier<Boolean> _transformPDFFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUri() {
		if (_uriSupplier != null) {
			uri = _uriSupplier.get();

			_uriSupplier = null;
		}

		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;

		_uriSupplier = null;
	}

	@JsonIgnore
	public void setUri(UnsafeSupplier<String, Exception> uriUnsafeSupplier) {
		_uriSupplier = () -> {
			try {
				return uriUnsafeSupplier.get();
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
	protected String uri;

	@JsonIgnore
	private Supplier<String> _uriSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DSDocument)) {
			return false;
		}

		DSDocument dsDocument = (DSDocument)object;

		return Objects.equals(toString(), dsDocument.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String assignTabsToDSRecipientId = getAssignTabsToDSRecipientId();

		if (assignTabsToDSRecipientId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assignTabsToDSRecipientId\": ");

			sb.append("\"");

			sb.append(_escape(assignTabsToDSRecipientId));

			sb.append("\"");
		}

		String data = getData();

		if (data != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"data\": ");

			sb.append("\"");

			sb.append(_escape(data));

			sb.append("\"");
		}

		String fileEntryExternalReferenceCode =
			getFileEntryExternalReferenceCode();

		if (fileEntryExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fileEntryExternalReferenceCode));

			sb.append("\"");
		}

		String fileExtension = getFileExtension();

		if (fileExtension != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileExtension\": ");

			sb.append("\"");

			sb.append(_escape(fileExtension));

			sb.append("\"");
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Boolean transformPDFFields = getTransformPDFFields();

		if (transformPDFFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transformPDFFields\": ");

			sb.append(transformPDFFields);
		}

		String uri = getUri();

		if (uri != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uri\": ");

			sb.append("\"");

			sb.append(_escape(uri));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.digital.signature.rest.dto.v1_0.DSDocument",
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