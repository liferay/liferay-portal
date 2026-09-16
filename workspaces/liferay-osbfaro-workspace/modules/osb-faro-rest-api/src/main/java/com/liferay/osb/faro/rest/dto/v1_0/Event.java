/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A single tracked analytics event captured — page views, custom events, form submissions, etc. Each event carries free-form `attributes` (a name-value map of contextual properties) plus any matching page metadata. Use `getWorkspaceGroupChannelEventsPage` to retrieve a list of analytics events.",
	value = "Event"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single tracked analytics event captured — page views, custom events, form submissions, etc. Each event carries free-form `attributes` (a name-value map of contextual properties) plus any matching page metadata. Use `getWorkspaceGroupChannelEventsPage` to retrieve a list of analytics events."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Event")
public class Event implements Serializable {

	public static Event toDTO(String json) {
		return ObjectMapperUtil.readValue(Event.class, json);
	}

	public static Event unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Event.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the application that emitted the event."
	)
	public String getApplicationId() {
		if (_applicationIdSupplier != null) {
			applicationId = _applicationIdSupplier.get();

			_applicationIdSupplier = null;
		}

		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;

		_applicationIdSupplier = null;
	}

	@JsonIgnore
	public void setApplicationId(
		UnsafeSupplier<String, Exception> applicationIdUnsafeSupplier) {

		_applicationIdSupplier = () -> {
			try {
				return applicationIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "ID of the application that emitted the event.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String applicationId;

	@JsonIgnore
	private Supplier<String> _applicationIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Title of the asset associated with the event."
	)
	public String getAssetTitle() {
		if (_assetTitleSupplier != null) {
			assetTitle = _assetTitleSupplier.get();

			_assetTitleSupplier = null;
		}

		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;

		_assetTitleSupplier = null;
	}

	@JsonIgnore
	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		_assetTitleSupplier = () -> {
			try {
				return assetTitleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Title of the asset associated with the event.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Free-form event properties as a key-value map. Available keys depend on the event `name` (e.g. a `pageViewed` event carries different attributes than `formSubmitted`)."
	)
	@Valid
	public Map<String, String> getAttributes() {
		if (_attributesSupplier != null) {
			attributes = _attributesSupplier.get();

			_attributesSupplier = null;
		}

		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;

		_attributesSupplier = null;
	}

	@JsonIgnore
	public void setAttributes(
		UnsafeSupplier<Map<String, String>, Exception>
			attributesUnsafeSupplier) {

		_attributesSupplier = () -> {
			try {
				return attributesUnsafeSupplier.get();
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
		description = "Free-form event properties as a key-value map. Available keys depend on the event `name` (e.g. a `pageViewed` event carries different attributes than `formSubmitted`)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> attributes;

	@JsonIgnore
	private Supplier<Map<String, String>> _attributesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Canonical URL of the page where the event was emitted."
	)
	public String getCanonicalUrl() {
		if (_canonicalUrlSupplier != null) {
			canonicalUrl = _canonicalUrlSupplier.get();

			_canonicalUrlSupplier = null;
		}

		return canonicalUrl;
	}

	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;

		_canonicalUrlSupplier = null;
	}

	@JsonIgnore
	public void setCanonicalUrl(
		UnsafeSupplier<String, Exception> canonicalUrlUnsafeSupplier) {

		_canonicalUrlSupplier = () -> {
			try {
				return canonicalUrlUnsafeSupplier.get();
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
		description = "Canonical URL of the page where the event was emitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String canonicalUrl;

	@JsonIgnore
	private Supplier<String> _canonicalUrlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Timestamp of when the event was recorded."
	)
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Timestamp of when the event was recorded.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the individual that emitted the event. Null for anonymous individuals. Use with `getWorkspaceGroupIndividual` to fetch the individual."
	)
	public String getIndividualId() {
		if (_individualIdSupplier != null) {
			individualId = _individualIdSupplier.get();

			_individualIdSupplier = null;
		}

		return individualId;
	}

	public void setIndividualId(String individualId) {
		this.individualId = individualId;

		_individualIdSupplier = null;
	}

	@JsonIgnore
	public void setIndividualId(
		UnsafeSupplier<String, Exception> individualIdUnsafeSupplier) {

		_individualIdSupplier = () -> {
			try {
				return individualIdUnsafeSupplier.get();
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
		description = "ID of the individual that emitted the event. Null for anonymous individuals. Use with `getWorkspaceGroupIndividual` to fetch the individual."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String individualId;

	@JsonIgnore
	private Supplier<String> _individualIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the event type (e.g. 'pageViewed', 'formSubmitted', etc.). The set of available names varies per channel and per integration."
	)
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

	@GraphQLField(
		description = "Name of the event type (e.g. 'pageViewed', 'formSubmitted', etc.). The set of available names varies per channel and per integration."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Meta description of the page that emitted the event."
	)
	public String getPageDescription() {
		if (_pageDescriptionSupplier != null) {
			pageDescription = _pageDescriptionSupplier.get();

			_pageDescriptionSupplier = null;
		}

		return pageDescription;
	}

	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;

		_pageDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setPageDescription(
		UnsafeSupplier<String, Exception> pageDescriptionUnsafeSupplier) {

		_pageDescriptionSupplier = () -> {
			try {
				return pageDescriptionUnsafeSupplier.get();
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
		description = "Meta description of the page that emitted the event."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String pageDescription;

	@JsonIgnore
	private Supplier<String> _pageDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Meta keywords of the page that emitted the event. Comma-separated when there are multiple."
	)
	public String getPageKeywords() {
		if (_pageKeywordsSupplier != null) {
			pageKeywords = _pageKeywordsSupplier.get();

			_pageKeywordsSupplier = null;
		}

		return pageKeywords;
	}

	public void setPageKeywords(String pageKeywords) {
		this.pageKeywords = pageKeywords;

		_pageKeywordsSupplier = null;
	}

	@JsonIgnore
	public void setPageKeywords(
		UnsafeSupplier<String, Exception> pageKeywordsUnsafeSupplier) {

		_pageKeywordsSupplier = () -> {
			try {
				return pageKeywordsUnsafeSupplier.get();
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
		description = "Meta keywords of the page that emitted the event. Comma-separated when there are multiple."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String pageKeywords;

	@JsonIgnore
	private Supplier<String> _pageKeywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Title of the page that event occurred on."
	)
	public String getPageTitle() {
		if (_pageTitleSupplier != null) {
			pageTitle = _pageTitleSupplier.get();

			_pageTitleSupplier = null;
		}

		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;

		_pageTitleSupplier = null;
	}

	@JsonIgnore
	public void setPageTitle(
		UnsafeSupplier<String, Exception> pageTitleUnsafeSupplier) {

		_pageTitleSupplier = () -> {
			try {
				return pageTitleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Title of the page that event occurred on.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String pageTitle;

	@JsonIgnore
	private Supplier<String> _pageTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Referring URL the visitor arrived from."
	)
	public String getReferrer() {
		if (_referrerSupplier != null) {
			referrer = _referrerSupplier.get();

			_referrerSupplier = null;
		}

		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;

		_referrerSupplier = null;
	}

	@JsonIgnore
	public void setReferrer(
		UnsafeSupplier<String, Exception> referrerUnsafeSupplier) {

		_referrerSupplier = () -> {
			try {
				return referrerUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Referring URL the visitor arrived from.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String referrer;

	@JsonIgnore
	private Supplier<String> _referrerSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Full URL of the page where the event was emitted."
	)
	public String getUrl() {
		if (_urlSupplier != null) {
			url = _urlSupplier.get();

			_urlSupplier = null;
		}

		return url;
	}

	public void setUrl(String url) {
		this.url = url;

		_urlSupplier = null;
	}

	@JsonIgnore
	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		_urlSupplier = () -> {
			try {
				return urlUnsafeSupplier.get();
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
		description = "Full URL of the page where the event was emitted."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String url;

	@JsonIgnore
	private Supplier<String> _urlSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Event)) {
			return false;
		}

		Event event = (Event)object;

		return Objects.equals(toString(), event.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		String applicationId = getApplicationId();

		if (applicationId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"applicationId\": ");

			sb.append("\"");

			sb.append(_escape(applicationId));

			sb.append("\"");
		}

		String assetTitle = getAssetTitle();

		if (assetTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(assetTitle));

			sb.append("\"");
		}

		Map<String, String> attributes = getAttributes();

		if (attributes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append(_toJSON(attributes));
		}

		String canonicalUrl = getCanonicalUrl();

		if (canonicalUrl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalUrl\": ");

			sb.append("\"");

			sb.append(_escape(canonicalUrl));

			sb.append("\"");
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

			sb.append("\"");
		}

		String individualId = getIndividualId();

		if (individualId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualId\": ");

			sb.append("\"");

			sb.append(_escape(individualId));

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

		String pageDescription = getPageDescription();

		if (pageDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageDescription\": ");

			sb.append("\"");

			sb.append(_escape(pageDescription));

			sb.append("\"");
		}

		String pageKeywords = getPageKeywords();

		if (pageKeywords != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageKeywords\": ");

			sb.append("\"");

			sb.append(_escape(pageKeywords));

			sb.append("\"");
		}

		String pageTitle = getPageTitle();

		if (pageTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTitle\": ");

			sb.append("\"");

			sb.append(_escape(pageTitle));

			sb.append("\"");
		}

		String referrer = getReferrer();

		if (referrer != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"referrer\": ");

			sb.append("\"");

			sb.append(_escape(referrer));

			sb.append("\"");
		}

		String url = getUrl();

		if (url != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(url));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.Event",
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
// LIFERAY-REST-BUILDER-HASH:-202593843