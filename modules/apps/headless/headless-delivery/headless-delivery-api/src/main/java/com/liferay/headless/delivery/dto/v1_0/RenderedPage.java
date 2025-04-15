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
	description = "A list of rendered pages, which results from using a page template and the appropriate viewport to process the page and return HTML.",
	value = "RenderedPage"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RenderedPage")
public class RenderedPage implements Serializable {

	public static RenderedPage toDTO(String json) {
		return ObjectMapperUtil.readValue(RenderedPage.class, json);
	}

	public static RenderedPage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(RenderedPage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the master page used to render the content."
	)
	public String getMasterPageId() {
		if (_masterPageIdSupplier != null) {
			masterPageId = _masterPageIdSupplier.get();

			_masterPageIdSupplier = null;
		}

		return masterPageId;
	}

	public void setMasterPageId(String masterPageId) {
		this.masterPageId = masterPageId;

		_masterPageIdSupplier = null;
	}

	@JsonIgnore
	public void setMasterPageId(
		UnsafeSupplier<String, Exception> masterPageIdUnsafeSupplier) {

		_masterPageIdSupplier = () -> {
			try {
				return masterPageIdUnsafeSupplier.get();
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
		description = "The ID of the master page used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String masterPageId;

	@JsonIgnore
	private Supplier<String> _masterPageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the master page used to render the content."
	)
	public String getMasterPageName() {
		if (_masterPageNameSupplier != null) {
			masterPageName = _masterPageNameSupplier.get();

			_masterPageNameSupplier = null;
		}

		return masterPageName;
	}

	public void setMasterPageName(String masterPageName) {
		this.masterPageName = masterPageName;

		_masterPageNameSupplier = null;
	}

	@JsonIgnore
	public void setMasterPageName(
		UnsafeSupplier<String, Exception> masterPageNameUnsafeSupplier) {

		_masterPageNameSupplier = () -> {
			try {
				return masterPageNameUnsafeSupplier.get();
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
		description = "The name of the master page used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String masterPageName;

	@JsonIgnore
	private Supplier<String> _masterPageNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the template used to render the content."
	)
	public String getPageTemplateId() {
		if (_pageTemplateIdSupplier != null) {
			pageTemplateId = _pageTemplateIdSupplier.get();

			_pageTemplateIdSupplier = null;
		}

		return pageTemplateId;
	}

	public void setPageTemplateId(String pageTemplateId) {
		this.pageTemplateId = pageTemplateId;

		_pageTemplateIdSupplier = null;
	}

	@JsonIgnore
	public void setPageTemplateId(
		UnsafeSupplier<String, Exception> pageTemplateIdUnsafeSupplier) {

		_pageTemplateIdSupplier = () -> {
			try {
				return pageTemplateIdUnsafeSupplier.get();
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
		description = "The ID of the template used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String pageTemplateId;

	@JsonIgnore
	private Supplier<String> _pageTemplateIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the template used to render the content."
	)
	public String getPageTemplateName() {
		if (_pageTemplateNameSupplier != null) {
			pageTemplateName = _pageTemplateNameSupplier.get();

			_pageTemplateNameSupplier = null;
		}

		return pageTemplateName;
	}

	public void setPageTemplateName(String pageTemplateName) {
		this.pageTemplateName = pageTemplateName;

		_pageTemplateNameSupplier = null;
	}

	@JsonIgnore
	public void setPageTemplateName(
		UnsafeSupplier<String, Exception> pageTemplateNameUnsafeSupplier) {

		_pageTemplateNameSupplier = () -> {
			try {
				return pageTemplateNameUnsafeSupplier.get();
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
		description = "The name of the template used to render the content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String pageTemplateName;

	@JsonIgnore
	private Supplier<String> _pageTemplateNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "An absolute URL to the rendered page."
	)
	public String getRenderedPageURL() {
		if (_renderedPageURLSupplier != null) {
			renderedPageURL = _renderedPageURLSupplier.get();

			_renderedPageURLSupplier = null;
		}

		return renderedPageURL;
	}

	public void setRenderedPageURL(String renderedPageURL) {
		this.renderedPageURL = renderedPageURL;

		_renderedPageURLSupplier = null;
	}

	@JsonIgnore
	public void setRenderedPageURL(
		UnsafeSupplier<String, Exception> renderedPageURLUnsafeSupplier) {

		_renderedPageURLSupplier = () -> {
			try {
				return renderedPageURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "An absolute URL to the rendered page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String renderedPageURL;

	@JsonIgnore
	private Supplier<String> _renderedPageURLSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RenderedPage)) {
			return false;
		}

		RenderedPage renderedPage = (RenderedPage)object;

		return Objects.equals(toString(), renderedPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String masterPageId = getMasterPageId();

		if (masterPageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"masterPageId\": ");

			sb.append("\"");

			sb.append(_escape(masterPageId));

			sb.append("\"");
		}

		String masterPageName = getMasterPageName();

		if (masterPageName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"masterPageName\": ");

			sb.append("\"");

			sb.append(_escape(masterPageName));

			sb.append("\"");
		}

		String pageTemplateId = getPageTemplateId();

		if (pageTemplateId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateId\": ");

			sb.append("\"");

			sb.append(_escape(pageTemplateId));

			sb.append("\"");
		}

		String pageTemplateName = getPageTemplateName();

		if (pageTemplateName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateName\": ");

			sb.append("\"");

			sb.append(_escape(pageTemplateName));

			sb.append("\"");
		}

		String renderedPageURL = getRenderedPageURL();

		if (renderedPageURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedPageURL\": ");

			sb.append("\"");

			sb.append(_escape(renderedPageURL));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.RenderedPage",
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