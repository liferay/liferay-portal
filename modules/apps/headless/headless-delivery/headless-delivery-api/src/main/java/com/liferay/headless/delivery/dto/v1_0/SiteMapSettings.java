/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents settings related with the site map.",
	value = "SiteMapSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SiteMapSettings")
public class SiteMapSettings implements Serializable {

	public static SiteMapSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(SiteMapSettings.class, json);
	}

	public static SiteMapSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SiteMapSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Indicates how often a page is updated."
	)
	@JsonGetter("changeFrequency")
	@Valid
	public ChangeFrequency getChangeFrequency() {
		if (_changeFrequencySupplier != null) {
			changeFrequency = _changeFrequencySupplier.get();

			_changeFrequencySupplier = null;
		}

		return changeFrequency;
	}

	@JsonIgnore
	public String getChangeFrequencyAsString() {
		ChangeFrequency changeFrequency = getChangeFrequency();

		if (changeFrequency == null) {
			return null;
		}

		return changeFrequency.toString();
	}

	public void setChangeFrequency(ChangeFrequency changeFrequency) {
		this.changeFrequency = changeFrequency;

		_changeFrequencySupplier = null;
	}

	@JsonIgnore
	public void setChangeFrequency(
		UnsafeSupplier<ChangeFrequency, Exception>
			changeFrequencyUnsafeSupplier) {

		_changeFrequencySupplier = () -> {
			try {
				return changeFrequencyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Indicates how often a page is updated.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ChangeFrequency changeFrequency;

	@JsonIgnore
	private Supplier<ChangeFrequency> _changeFrequencySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether search engines should crawl and index the page."
	)
	public Boolean getInclude() {
		if (_includeSupplier != null) {
			include = _includeSupplier.get();

			_includeSupplier = null;
		}

		return include;
	}

	public void setInclude(Boolean include) {
		this.include = include;

		_includeSupplier = null;
	}

	@JsonIgnore
	public void setInclude(
		UnsafeSupplier<Boolean, Exception> includeUnsafeSupplier) {

		_includeSupplier = () -> {
			try {
				return includeUnsafeSupplier.get();
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
		description = "Whether search engines should crawl and index the page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean include;

	@JsonIgnore
	private Supplier<Boolean> _includeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether search engines should crawl and index the child pages."
	)
	public Boolean getIncludeChildSitePages() {
		if (_includeChildSitePagesSupplier != null) {
			includeChildSitePages = _includeChildSitePagesSupplier.get();

			_includeChildSitePagesSupplier = null;
		}

		return includeChildSitePages;
	}

	public void setIncludeChildSitePages(Boolean includeChildSitePages) {
		this.includeChildSitePages = includeChildSitePages;

		_includeChildSitePagesSupplier = null;
	}

	@JsonIgnore
	public void setIncludeChildSitePages(
		UnsafeSupplier<Boolean, Exception>
			includeChildSitePagesUnsafeSupplier) {

		_includeChildSitePagesSupplier = () -> {
			try {
				return includeChildSitePagesUnsafeSupplier.get();
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
		description = "Whether search engines should crawl and index the child pages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean includeChildSitePages;

	@JsonIgnore
	private Supplier<Boolean> _includeChildSitePagesSupplier;

	@DecimalMax("1")
	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "How the page should be prioritized relative to other pages."
	)
	public Double getPagePriority() {
		if (_pagePrioritySupplier != null) {
			pagePriority = _pagePrioritySupplier.get();

			_pagePrioritySupplier = null;
		}

		return pagePriority;
	}

	public void setPagePriority(Double pagePriority) {
		this.pagePriority = pagePriority;

		_pagePrioritySupplier = null;
	}

	@JsonIgnore
	public void setPagePriority(
		UnsafeSupplier<Double, Exception> pagePriorityUnsafeSupplier) {

		_pagePrioritySupplier = () -> {
			try {
				return pagePriorityUnsafeSupplier.get();
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
		description = "How the page should be prioritized relative to other pages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double pagePriority;

	@JsonIgnore
	private Supplier<Double> _pagePrioritySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SiteMapSettings)) {
			return false;
		}

		SiteMapSettings siteMapSettings = (SiteMapSettings)object;

		return Objects.equals(toString(), siteMapSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ChangeFrequency changeFrequency = getChangeFrequency();

		if (changeFrequency != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changeFrequency\": ");

			sb.append("\"");

			sb.append(changeFrequency);

			sb.append("\"");
		}

		Boolean include = getInclude();

		if (include != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"include\": ");

			sb.append(include);
		}

		Boolean includeChildSitePages = getIncludeChildSitePages();

		if (includeChildSitePages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includeChildSitePages\": ");

			sb.append(includeChildSitePages);
		}

		Double pagePriority = getPagePriority();

		if (pagePriority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pagePriority\": ");

			sb.append(pagePriority);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.SiteMapSettings",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ChangeFrequency")
	public static enum ChangeFrequency {

		ALWAYS("Always"), HOURLY("Hourly"), DAILY("Daily"), WEEKLY("Weekly"),
		MONTHLY("Monthly"), YEARLY("Yearly"), NEVER("Never");

		@JsonCreator
		public static ChangeFrequency create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ChangeFrequency changeFrequency : values()) {
				if (Objects.equals(changeFrequency.getValue(), value)) {
					return changeFrequency;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ChangeFrequency(String value) {
			_value = value;
		}

		private final String _value;

	}

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