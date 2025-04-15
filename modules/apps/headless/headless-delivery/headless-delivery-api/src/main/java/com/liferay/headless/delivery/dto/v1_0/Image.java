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
@GraphQLName(description = "The blog post's cover image.", value = "Image")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Image")
public class Image implements Serializable {

	public static Image toDTO(String json) {
		return ObjectMapperUtil.readValue(Image.class, json);
	}

	public static Image unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Image.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The text describing the image."
	)
	public String getCaption() {
		if (_captionSupplier != null) {
			caption = _captionSupplier.get();

			_captionSupplier = null;
		}

		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;

		_captionSupplier = null;
	}

	@JsonIgnore
	public void setCaption(
		UnsafeSupplier<String, Exception> captionUnsafeSupplier) {

		_captionSupplier = () -> {
			try {
				return captionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The text describing the image.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String caption;

	@JsonIgnore
	private Supplier<String> _captionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The image's relative URL."
	)
	public String getContentUrl() {
		if (_contentUrlSupplier != null) {
			contentUrl = _contentUrlSupplier.get();

			_contentUrlSupplier = null;
		}

		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;

		_contentUrlSupplier = null;
	}

	@JsonIgnore
	public void setContentUrl(
		UnsafeSupplier<String, Exception> contentUrlUnsafeSupplier) {

		_contentUrlSupplier = () -> {
			try {
				return contentUrlUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The image's relative URL.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String contentUrl;

	@JsonIgnore
	private Supplier<String> _contentUrlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "optional field with the content of the image in Base64, can be embedded with nestedFields"
	)
	public String getContentValue() {
		if (_contentValueSupplier != null) {
			contentValue = _contentValueSupplier.get();

			_contentValueSupplier = null;
		}

		return contentValue;
	}

	public void setContentValue(String contentValue) {
		this.contentValue = contentValue;

		_contentValueSupplier = null;
	}

	@JsonIgnore
	public void setContentValue(
		UnsafeSupplier<String, Exception> contentValueUnsafeSupplier) {

		_contentValueSupplier = () -> {
			try {
				return contentValueUnsafeSupplier.get();
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
		description = "optional field with the content of the image in Base64, can be embedded with nestedFields"
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String contentValue;

	@JsonIgnore
	private Supplier<String> _contentValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The image's ID. This can be used to retrieve more information in the `Document` API."
	)
	public Long getImageId() {
		if (_imageIdSupplier != null) {
			imageId = _imageIdSupplier.get();

			_imageIdSupplier = null;
		}

		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;

		_imageIdSupplier = null;
	}

	@JsonIgnore
	public void setImageId(
		UnsafeSupplier<Long, Exception> imageIdUnsafeSupplier) {

		_imageIdSupplier = () -> {
			try {
				return imageIdUnsafeSupplier.get();
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
		description = "The image's ID. This can be used to retrieve more information in the `Document` API."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long imageId;

	@JsonIgnore
	private Supplier<Long> _imageIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Image)) {
			return false;
		}

		Image image = (Image)object;

		return Objects.equals(toString(), image.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String caption = getCaption();

		if (caption != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"caption\": ");

			sb.append("\"");

			sb.append(_escape(caption));

			sb.append("\"");
		}

		String contentUrl = getContentUrl();

		if (contentUrl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(contentUrl));

			sb.append("\"");
		}

		String contentValue = getContentValue();

		if (contentValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(contentValue));

			sb.append("\"");
		}

		Long imageId = getImageId();

		if (imageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(imageId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.Image",
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