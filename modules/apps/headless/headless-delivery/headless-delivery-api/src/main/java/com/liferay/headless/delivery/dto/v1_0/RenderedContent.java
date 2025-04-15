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
	description = "Rendered content, which results from using a template or display page to process the content and return HTML.",
	value = "RenderedContent"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RenderedContent")
public class RenderedContent implements Serializable {

	public static RenderedContent toDTO(String json) {
		return ObjectMapperUtil.readValue(RenderedContent.class, json);
	}

	public static RenderedContent unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(RenderedContent.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the template or display page used to render the content."
	)
	public String getContentTemplateId() {
		if (_contentTemplateIdSupplier != null) {
			contentTemplateId = _contentTemplateIdSupplier.get();

			_contentTemplateIdSupplier = null;
		}

		return contentTemplateId;
	}

	public void setContentTemplateId(String contentTemplateId) {
		this.contentTemplateId = contentTemplateId;

		_contentTemplateIdSupplier = null;
	}

	@JsonIgnore
	public void setContentTemplateId(
		UnsafeSupplier<String, Exception> contentTemplateIdUnsafeSupplier) {

		_contentTemplateIdSupplier = () -> {
			try {
				return contentTemplateIdUnsafeSupplier.get();
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
		description = "The ID of the template or display page used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String contentTemplateId;

	@JsonIgnore
	private Supplier<String> _contentTemplateIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the template or display page used to render the content."
	)
	public String getContentTemplateName() {
		if (_contentTemplateNameSupplier != null) {
			contentTemplateName = _contentTemplateNameSupplier.get();

			_contentTemplateNameSupplier = null;
		}

		return contentTemplateName;
	}

	public void setContentTemplateName(String contentTemplateName) {
		this.contentTemplateName = contentTemplateName;

		_contentTemplateNameSupplier = null;
	}

	@JsonIgnore
	public void setContentTemplateName(
		UnsafeSupplier<String, Exception> contentTemplateNameUnsafeSupplier) {

		_contentTemplateNameSupplier = () -> {
			try {
				return contentTemplateNameUnsafeSupplier.get();
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
		description = "The name of the template or display page used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String contentTemplateName;

	@JsonIgnore
	private Supplier<String> _contentTemplateNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized names of the template or display page used to render the content."
	)
	@Valid
	public Map<String, String> getContentTemplateName_i18n() {
		if (_contentTemplateName_i18nSupplier != null) {
			contentTemplateName_i18n = _contentTemplateName_i18nSupplier.get();

			_contentTemplateName_i18nSupplier = null;
		}

		return contentTemplateName_i18n;
	}

	public void setContentTemplateName_i18n(
		Map<String, String> contentTemplateName_i18n) {

		this.contentTemplateName_i18n = contentTemplateName_i18n;

		_contentTemplateName_i18nSupplier = null;
	}

	@JsonIgnore
	public void setContentTemplateName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			contentTemplateName_i18nUnsafeSupplier) {

		_contentTemplateName_i18nSupplier = () -> {
			try {
				return contentTemplateName_i18nUnsafeSupplier.get();
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
		description = "The localized names of the template or display page used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> contentTemplateName_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _contentTemplateName_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Specifies if the template or display page are marked as default to display the content."
	)
	public Boolean getMarkedAsDefault() {
		if (_markedAsDefaultSupplier != null) {
			markedAsDefault = _markedAsDefaultSupplier.get();

			_markedAsDefaultSupplier = null;
		}

		return markedAsDefault;
	}

	public void setMarkedAsDefault(Boolean markedAsDefault) {
		this.markedAsDefault = markedAsDefault;

		_markedAsDefaultSupplier = null;
	}

	@JsonIgnore
	public void setMarkedAsDefault(
		UnsafeSupplier<Boolean, Exception> markedAsDefaultUnsafeSupplier) {

		_markedAsDefaultSupplier = () -> {
			try {
				return markedAsDefaultUnsafeSupplier.get();
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
		description = "Specifies if the template or display page are marked as default to display the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean markedAsDefault;

	@JsonIgnore
	private Supplier<Boolean> _markedAsDefaultSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "An absolute URL to the rendered content."
	)
	public String getRenderedContentURL() {
		if (_renderedContentURLSupplier != null) {
			renderedContentURL = _renderedContentURLSupplier.get();

			_renderedContentURLSupplier = null;
		}

		return renderedContentURL;
	}

	public void setRenderedContentURL(String renderedContentURL) {
		this.renderedContentURL = renderedContentURL;

		_renderedContentURLSupplier = null;
	}

	@JsonIgnore
	public void setRenderedContentURL(
		UnsafeSupplier<String, Exception> renderedContentURLUnsafeSupplier) {

		_renderedContentURLSupplier = () -> {
			try {
				return renderedContentURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "An absolute URL to the rendered content.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String renderedContentURL;

	@JsonIgnore
	private Supplier<String> _renderedContentURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional field with the rendered content, can be embedded with nestedFields."
	)
	public String getRenderedContentValue() {
		if (_renderedContentValueSupplier != null) {
			renderedContentValue = _renderedContentValueSupplier.get();

			_renderedContentValueSupplier = null;
		}

		return renderedContentValue;
	}

	public void setRenderedContentValue(String renderedContentValue) {
		this.renderedContentValue = renderedContentValue;

		_renderedContentValueSupplier = null;
	}

	@JsonIgnore
	public void setRenderedContentValue(
		UnsafeSupplier<String, Exception> renderedContentValueUnsafeSupplier) {

		_renderedContentValueSupplier = () -> {
			try {
				return renderedContentValueUnsafeSupplier.get();
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
		description = "Optional field with the rendered content, can be embedded with nestedFields."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String renderedContentValue;

	@JsonIgnore
	private Supplier<String> _renderedContentValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RenderedContent)) {
			return false;
		}

		RenderedContent renderedContent = (RenderedContent)object;

		return Objects.equals(toString(), renderedContent.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String contentTemplateId = getContentTemplateId();

		if (contentTemplateId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentTemplateId\": ");

			sb.append("\"");

			sb.append(_escape(contentTemplateId));

			sb.append("\"");
		}

		String contentTemplateName = getContentTemplateName();

		if (contentTemplateName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentTemplateName\": ");

			sb.append("\"");

			sb.append(_escape(contentTemplateName));

			sb.append("\"");
		}

		Map<String, String> contentTemplateName_i18n =
			getContentTemplateName_i18n();

		if (contentTemplateName_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentTemplateName_i18n\": ");

			sb.append(_toJSON(contentTemplateName_i18n));
		}

		Boolean markedAsDefault = getMarkedAsDefault();

		if (markedAsDefault != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markedAsDefault\": ");

			sb.append(markedAsDefault);
		}

		String renderedContentURL = getRenderedContentURL();

		if (renderedContentURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedContentURL\": ");

			sb.append("\"");

			sb.append(_escape(renderedContentURL));

			sb.append("\"");
		}

		String renderedContentValue = getRenderedContentValue();

		if (renderedContentValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedContentValue\": ");

			sb.append("\"");

			sb.append(_escape(renderedContentValue));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.RenderedContent",
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