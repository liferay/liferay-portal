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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An array of images in several resolutions and sizes, created by the Adaptive Media framework.",
	value = "AdaptedImage"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AdaptedImage")
public class AdaptedImage implements Serializable {

	public static AdaptedImage toDTO(String json) {
		return ObjectMapperUtil.readValue(AdaptedImage.class, json);
	}

	public static AdaptedImage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AdaptedImage.class, json);
	}

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentUrl;

	@JsonIgnore
	private Supplier<String> _contentUrlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the content of the image in Base64, can be embedded with nestedFields."
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
		description = "Optional field with the content of the image in Base64, can be embedded with nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String contentValue;

	@JsonIgnore
	private Supplier<String> _contentValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The image's height in pixels."
	)
	public Integer getHeight() {
		if (_heightSupplier != null) {
			height = _heightSupplier.get();

			_heightSupplier = null;
		}

		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;

		_heightSupplier = null;
	}

	@JsonIgnore
	public void setHeight(
		UnsafeSupplier<Integer, Exception> heightUnsafeSupplier) {

		_heightSupplier = () -> {
			try {
				return heightUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The image's height in pixels.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer height;

	@JsonIgnore
	private Supplier<Integer> _heightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the image's Adaptive Media image resolution."
	)
	public String getResolutionName() {
		if (_resolutionNameSupplier != null) {
			resolutionName = _resolutionNameSupplier.get();

			_resolutionNameSupplier = null;
		}

		return resolutionName;
	}

	public void setResolutionName(String resolutionName) {
		this.resolutionName = resolutionName;

		_resolutionNameSupplier = null;
	}

	@JsonIgnore
	public void setResolutionName(
		UnsafeSupplier<String, Exception> resolutionNameUnsafeSupplier) {

		_resolutionNameSupplier = () -> {
			try {
				return resolutionNameUnsafeSupplier.get();
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
		description = "The name of the image's Adaptive Media image resolution."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String resolutionName;

	@JsonIgnore
	private Supplier<String> _resolutionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The image's size in bytes."
	)
	public Long getSizeInBytes() {
		if (_sizeInBytesSupplier != null) {
			sizeInBytes = _sizeInBytesSupplier.get();

			_sizeInBytesSupplier = null;
		}

		return sizeInBytes;
	}

	public void setSizeInBytes(Long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;

		_sizeInBytesSupplier = null;
	}

	@JsonIgnore
	public void setSizeInBytes(
		UnsafeSupplier<Long, Exception> sizeInBytesUnsafeSupplier) {

		_sizeInBytesSupplier = () -> {
			try {
				return sizeInBytesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The image's size in bytes.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long sizeInBytes;

	@JsonIgnore
	private Supplier<Long> _sizeInBytesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The image's width in pixels."
	)
	public Integer getWidth() {
		if (_widthSupplier != null) {
			width = _widthSupplier.get();

			_widthSupplier = null;
		}

		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;

		_widthSupplier = null;
	}

	@JsonIgnore
	public void setWidth(
		UnsafeSupplier<Integer, Exception> widthUnsafeSupplier) {

		_widthSupplier = () -> {
			try {
				return widthUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The image's width in pixels.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer width;

	@JsonIgnore
	private Supplier<Integer> _widthSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AdaptedImage)) {
			return false;
		}

		AdaptedImage adaptedImage = (AdaptedImage)object;

		return Objects.equals(toString(), adaptedImage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

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

		Integer height = getHeight();

		if (height != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append(height);
		}

		String resolutionName = getResolutionName();

		if (resolutionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resolutionName\": ");

			sb.append("\"");

			sb.append(_escape(resolutionName));

			sb.append("\"");
		}

		Long sizeInBytes = getSizeInBytes();

		if (sizeInBytes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sizeInBytes\": ");

			sb.append(sizeInBytes);
		}

		Integer width = getWidth();

		if (width != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append(width);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.AdaptedImage",
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