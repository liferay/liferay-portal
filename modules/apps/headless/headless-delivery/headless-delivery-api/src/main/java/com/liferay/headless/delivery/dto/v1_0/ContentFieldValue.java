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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the value of a content field. Can contain different information types (e.g., geolocation, documents, etc.).",
	value = "ContentFieldValue"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentFieldValue")
public class ContentFieldValue implements Serializable {

	public static ContentFieldValue toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentFieldValue.class, json);
	}

	public static ContentFieldValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ContentFieldValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's content for simple types."
	)
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

	@GraphQLField(description = "The field's content for simple types.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String data;

	@JsonIgnore
	private Supplier<String> _dataSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A content document element."
	)
	@Valid
	public ContentDocument getDocument() {
		if (_documentSupplier != null) {
			document = _documentSupplier.get();

			_documentSupplier = null;
		}

		return document;
	}

	public void setDocument(ContentDocument document) {
		this.document = document;

		_documentSupplier = null;
	}

	@JsonIgnore
	public void setDocument(
		UnsafeSupplier<ContentDocument, Exception> documentUnsafeSupplier) {

		_documentSupplier = () -> {
			try {
				return documentUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A content document element.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ContentDocument document;

	@JsonIgnore
	private Supplier<ContentDocument> _documentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A point determined by latitude and longitude."
	)
	@Valid
	public Geo getGeo() {
		if (_geoSupplier != null) {
			geo = _geoSupplier.get();

			_geoSupplier = null;
		}

		return geo;
	}

	public void setGeo(Geo geo) {
		this.geo = geo;

		_geoSupplier = null;
	}

	@JsonIgnore
	public void setGeo(UnsafeSupplier<Geo, Exception> geoUnsafeSupplier) {
		_geoSupplier = () -> {
			try {
				return geoUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A point determined by latitude and longitude.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Geo geo;

	@JsonIgnore
	private Supplier<Geo> _geoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A content document element that stores an image file."
	)
	@Valid
	public ContentDocument getImage() {
		if (_imageSupplier != null) {
			image = _imageSupplier.get();

			_imageSupplier = null;
		}

		return image;
	}

	public void setImage(ContentDocument image) {
		this.image = image;

		_imageSupplier = null;
	}

	@JsonIgnore
	public void setImage(
		UnsafeSupplier<ContentDocument, Exception> imageUnsafeSupplier) {

		_imageSupplier = () -> {
			try {
				return imageUnsafeSupplier.get();
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
		description = "A content document element that stores an image file."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ContentDocument image;

	@JsonIgnore
	private Supplier<ContentDocument> _imageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A link to a page on the server."
	)
	public String getLink() {
		if (_linkSupplier != null) {
			link = _linkSupplier.get();

			_linkSupplier = null;
		}

		return link;
	}

	public void setLink(String link) {
		this.link = link;

		_linkSupplier = null;
	}

	@JsonIgnore
	public void setLink(UnsafeSupplier<String, Exception> linkUnsafeSupplier) {
		_linkSupplier = () -> {
			try {
				return linkUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A link to a page on the server.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String link;

	@JsonIgnore
	private Supplier<String> _linkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A link to structured content on the server."
	)
	@Valid
	public StructuredContentLink getStructuredContentLink() {
		if (_structuredContentLinkSupplier != null) {
			structuredContentLink = _structuredContentLinkSupplier.get();

			_structuredContentLinkSupplier = null;
		}

		return structuredContentLink;
	}

	public void setStructuredContentLink(
		StructuredContentLink structuredContentLink) {

		this.structuredContentLink = structuredContentLink;

		_structuredContentLinkSupplier = null;
	}

	@JsonIgnore
	public void setStructuredContentLink(
		UnsafeSupplier<StructuredContentLink, Exception>
			structuredContentLinkUnsafeSupplier) {

		_structuredContentLinkSupplier = () -> {
			try {
				return structuredContentLinkUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A link to structured content on the server.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected StructuredContentLink structuredContentLink;

	@JsonIgnore
	private Supplier<StructuredContentLink> _structuredContentLinkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's visible value"
	)
	public String getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(String value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The field's visible value")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String value;

	@JsonIgnore
	private Supplier<String> _valueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentFieldValue)) {
			return false;
		}

		ContentFieldValue contentFieldValue = (ContentFieldValue)object;

		return Objects.equals(toString(), contentFieldValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		ContentDocument document = getDocument();

		if (document != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"document\": ");

			sb.append(String.valueOf(document));
		}

		Geo geo = getGeo();

		if (geo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"geo\": ");

			sb.append(String.valueOf(geo));
		}

		ContentDocument image = getImage();

		if (image != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append(String.valueOf(image));
		}

		String link = getLink();

		if (link != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link\": ");

			sb.append("\"");

			sb.append(_escape(link));

			sb.append("\"");
		}

		StructuredContentLink structuredContentLink =
			getStructuredContentLink();

		if (structuredContentLink != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"structuredContentLink\": ");

			sb.append(String.valueOf(structuredContentLink));
		}

		String value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(value));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.ContentFieldValue",
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