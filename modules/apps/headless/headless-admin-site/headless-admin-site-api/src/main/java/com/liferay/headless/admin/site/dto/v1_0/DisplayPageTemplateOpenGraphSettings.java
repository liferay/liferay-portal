/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents display page template settings related with Open Graph protocol.",
	value = "DisplayPageTemplateOpenGraphSettings"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents display page template settings related with Open Graph protocol."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DisplayPageTemplateOpenGraphSettings")
public class DisplayPageTemplateOpenGraphSettings implements Serializable {

	public static DisplayPageTemplateOpenGraphSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(
			DisplayPageTemplateOpenGraphSettings.class, json);
	}

	public static DisplayPageTemplateOpenGraphSettings unsafeToDTO(
		String json) {

		return ObjectMapperUtil.unsafeReadValue(
			DisplayPageTemplateOpenGraphSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The Open Graph's description template."
	)
	public String getDescriptionTemplate() {
		if (_descriptionTemplateSupplier != null) {
			descriptionTemplate = _descriptionTemplateSupplier.get();

			_descriptionTemplateSupplier = null;
		}

		return descriptionTemplate;
	}

	public void setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;

		_descriptionTemplateSupplier = null;
	}

	@JsonIgnore
	public void setDescriptionTemplate(
		UnsafeSupplier<String, Exception> descriptionTemplateUnsafeSupplier) {

		_descriptionTemplateSupplier = () -> {
			try {
				return descriptionTemplateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The Open Graph's description template.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String descriptionTemplate;

	@JsonIgnore
	private Supplier<String> _descriptionTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The Open Graph's image alt template."
	)
	public String getImageAltTemplate() {
		if (_imageAltTemplateSupplier != null) {
			imageAltTemplate = _imageAltTemplateSupplier.get();

			_imageAltTemplateSupplier = null;
		}

		return imageAltTemplate;
	}

	public void setImageAltTemplate(String imageAltTemplate) {
		this.imageAltTemplate = imageAltTemplate;

		_imageAltTemplateSupplier = null;
	}

	@JsonIgnore
	public void setImageAltTemplate(
		UnsafeSupplier<String, Exception> imageAltTemplateUnsafeSupplier) {

		_imageAltTemplateSupplier = () -> {
			try {
				return imageAltTemplateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The Open Graph's image alt template.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String imageAltTemplate;

	@JsonIgnore
	private Supplier<String> _imageAltTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The Open Graph's image template."
	)
	public String getImageTemplate() {
		if (_imageTemplateSupplier != null) {
			imageTemplate = _imageTemplateSupplier.get();

			_imageTemplateSupplier = null;
		}

		return imageTemplate;
	}

	public void setImageTemplate(String imageTemplate) {
		this.imageTemplate = imageTemplate;

		_imageTemplateSupplier = null;
	}

	@JsonIgnore
	public void setImageTemplate(
		UnsafeSupplier<String, Exception> imageTemplateUnsafeSupplier) {

		_imageTemplateSupplier = () -> {
			try {
				return imageTemplateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The Open Graph's image template.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String imageTemplate;

	@JsonIgnore
	private Supplier<String> _imageTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The Open Graph's title template."
	)
	public String getTitleTemplate() {
		if (_titleTemplateSupplier != null) {
			titleTemplate = _titleTemplateSupplier.get();

			_titleTemplateSupplier = null;
		}

		return titleTemplate;
	}

	public void setTitleTemplate(String titleTemplate) {
		this.titleTemplate = titleTemplate;

		_titleTemplateSupplier = null;
	}

	@JsonIgnore
	public void setTitleTemplate(
		UnsafeSupplier<String, Exception> titleTemplateUnsafeSupplier) {

		_titleTemplateSupplier = () -> {
			try {
				return titleTemplateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The Open Graph's title template.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String titleTemplate;

	@JsonIgnore
	private Supplier<String> _titleTemplateSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageTemplateOpenGraphSettings)) {
			return false;
		}

		DisplayPageTemplateOpenGraphSettings
			displayPageTemplateOpenGraphSettings =
				(DisplayPageTemplateOpenGraphSettings)object;

		return Objects.equals(
			toString(), displayPageTemplateOpenGraphSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String descriptionTemplate = getDescriptionTemplate();

		if (descriptionTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionTemplate\": ");

			sb.append("\"");

			sb.append(_escape(descriptionTemplate));

			sb.append("\"");
		}

		String imageAltTemplate = getImageAltTemplate();

		if (imageAltTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageAltTemplate\": ");

			sb.append("\"");

			sb.append(_escape(imageAltTemplate));

			sb.append("\"");
		}

		String imageTemplate = getImageTemplate();

		if (imageTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageTemplate\": ");

			sb.append("\"");

			sb.append(_escape(imageTemplate));

			sb.append("\"");
		}

		String titleTemplate = getTitleTemplate();

		if (titleTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"titleTemplate\": ");

			sb.append("\"");

			sb.append(_escape(titleTemplate));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateOpenGraphSettings",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-306857113