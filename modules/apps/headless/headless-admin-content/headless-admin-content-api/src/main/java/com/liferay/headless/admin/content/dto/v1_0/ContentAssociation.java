/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.dto.v1_0;

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
	description = "Describes the content that is tied to a Display Page Template",
	value = "ContentAssociation"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentAssociation")
public class ContentAssociation implements Serializable {

	public static ContentAssociation toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentAssociation.class, json);
	}

	public static ContentAssociation unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ContentAssociation.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The subtype of the content. i.e. the Structure name if it's a Structured Content."
	)
	public String getContentSubtype() {
		if (_contentSubtypeSupplier != null) {
			contentSubtype = _contentSubtypeSupplier.get();

			_contentSubtypeSupplier = null;
		}

		return contentSubtype;
	}

	public void setContentSubtype(String contentSubtype) {
		this.contentSubtype = contentSubtype;

		_contentSubtypeSupplier = null;
	}

	@JsonIgnore
	public void setContentSubtype(
		UnsafeSupplier<String, Exception> contentSubtypeUnsafeSupplier) {

		_contentSubtypeSupplier = () -> {
			try {
				return contentSubtypeUnsafeSupplier.get();
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
		description = "The subtype of the content. i.e. the Structure name if it's a Structured Content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentSubtype;

	@JsonIgnore
	private Supplier<String> _contentSubtypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The type of the content, i.e. Structure Content, BlogsPosting, etc."
	)
	public String getContentType() {
		if (_contentTypeSupplier != null) {
			contentType = _contentTypeSupplier.get();

			_contentTypeSupplier = null;
		}

		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;

		_contentTypeSupplier = null;
	}

	@JsonIgnore
	public void setContentType(
		UnsafeSupplier<String, Exception> contentTypeUnsafeSupplier) {

		_contentTypeSupplier = () -> {
			try {
				return contentTypeUnsafeSupplier.get();
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
		description = "The type of the content, i.e. Structure Content, BlogsPosting, etc."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentType;

	@JsonIgnore
	private Supplier<String> _contentTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentAssociation)) {
			return false;
		}

		ContentAssociation contentAssociation = (ContentAssociation)object;

		return Objects.equals(toString(), contentAssociation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String contentSubtype = getContentSubtype();

		if (contentSubtype != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentSubtype\": ");

			sb.append("\"");

			sb.append(_escape(contentSubtype));

			sb.append("\"");
		}

		String contentType = getContentType();

		if (contentType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(contentType));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.content.dto.v1_0.ContentAssociation",
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