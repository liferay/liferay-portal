/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.dto.v1_0;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
@GraphQLName("DocumentMetric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DocumentMetric")
public class DocumentMetric implements Serializable {

	public static DocumentMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(DocumentMetric.class, json);
	}

	public static DocumentMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(DocumentMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetId() {
		if (_assetIdSupplier != null) {
			assetId = _assetIdSupplier.get();

			_assetIdSupplier = null;
		}

		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;

		_assetIdSupplier = null;
	}

	@JsonIgnore
	public void setAssetId(
		UnsafeSupplier<String, Exception> assetIdUnsafeSupplier) {

		_assetIdSupplier = () -> {
			try {
				return assetIdUnsafeSupplier.get();
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
	protected String assetId;

	@JsonIgnore
	private Supplier<String> _assetIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MetricValue getCommentsMetric() {
		if (_commentsMetricSupplier != null) {
			commentsMetric = _commentsMetricSupplier.get();

			_commentsMetricSupplier = null;
		}

		return commentsMetric;
	}

	public void setCommentsMetric(MetricValue commentsMetric) {
		this.commentsMetric = commentsMetric;

		_commentsMetricSupplier = null;
	}

	@JsonIgnore
	public void setCommentsMetric(
		UnsafeSupplier<MetricValue, Exception> commentsMetricUnsafeSupplier) {

		_commentsMetricSupplier = () -> {
			try {
				return commentsMetricUnsafeSupplier.get();
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
	protected MetricValue commentsMetric;

	@JsonIgnore
	private Supplier<MetricValue> _commentsMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MetricValue getDownloadsMetric() {
		if (_downloadsMetricSupplier != null) {
			downloadsMetric = _downloadsMetricSupplier.get();

			_downloadsMetricSupplier = null;
		}

		return downloadsMetric;
	}

	public void setDownloadsMetric(MetricValue downloadsMetric) {
		this.downloadsMetric = downloadsMetric;

		_downloadsMetricSupplier = null;
	}

	@JsonIgnore
	public void setDownloadsMetric(
		UnsafeSupplier<MetricValue, Exception> downloadsMetricUnsafeSupplier) {

		_downloadsMetricSupplier = () -> {
			try {
				return downloadsMetricUnsafeSupplier.get();
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
	protected MetricValue downloadsMetric;

	@JsonIgnore
	private Supplier<MetricValue> _downloadsMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MetricValue getImpressionMadeMetric() {
		if (_impressionMadeMetricSupplier != null) {
			impressionMadeMetric = _impressionMadeMetricSupplier.get();

			_impressionMadeMetricSupplier = null;
		}

		return impressionMadeMetric;
	}

	public void setImpressionMadeMetric(MetricValue impressionMadeMetric) {
		this.impressionMadeMetric = impressionMadeMetric;

		_impressionMadeMetricSupplier = null;
	}

	@JsonIgnore
	public void setImpressionMadeMetric(
		UnsafeSupplier<MetricValue, Exception>
			impressionMadeMetricUnsafeSupplier) {

		_impressionMadeMetricSupplier = () -> {
			try {
				return impressionMadeMetricUnsafeSupplier.get();
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
	protected MetricValue impressionMadeMetric;

	@JsonIgnore
	private Supplier<MetricValue> _impressionMadeMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MetricValue getLastViewedMetric() {
		if (_lastViewedMetricSupplier != null) {
			lastViewedMetric = _lastViewedMetricSupplier.get();

			_lastViewedMetricSupplier = null;
		}

		return lastViewedMetric;
	}

	public void setLastViewedMetric(MetricValue lastViewedMetric) {
		this.lastViewedMetric = lastViewedMetric;

		_lastViewedMetricSupplier = null;
	}

	@JsonIgnore
	public void setLastViewedMetric(
		UnsafeSupplier<MetricValue, Exception> lastViewedMetricUnsafeSupplier) {

		_lastViewedMetricSupplier = () -> {
			try {
				return lastViewedMetricUnsafeSupplier.get();
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
	protected MetricValue lastViewedMetric;

	@JsonIgnore
	private Supplier<MetricValue> _lastViewedMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MetricValue getRatingsMetric() {
		if (_ratingsMetricSupplier != null) {
			ratingsMetric = _ratingsMetricSupplier.get();

			_ratingsMetricSupplier = null;
		}

		return ratingsMetric;
	}

	public void setRatingsMetric(MetricValue ratingsMetric) {
		this.ratingsMetric = ratingsMetric;

		_ratingsMetricSupplier = null;
	}

	@JsonIgnore
	public void setRatingsMetric(
		UnsafeSupplier<MetricValue, Exception> ratingsMetricUnsafeSupplier) {

		_ratingsMetricSupplier = () -> {
			try {
				return ratingsMetricUnsafeSupplier.get();
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
	protected MetricValue ratingsMetric;

	@JsonIgnore
	private Supplier<MetricValue> _ratingsMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getUrls() {
		if (_urlsSupplier != null) {
			urls = _urlsSupplier.get();

			_urlsSupplier = null;
		}

		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;

		_urlsSupplier = null;
	}

	@JsonIgnore
	public void setUrls(
		UnsafeSupplier<String[], Exception> urlsUnsafeSupplier) {

		_urlsSupplier = () -> {
			try {
				return urlsUnsafeSupplier.get();
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
	protected String[] urls;

	@JsonIgnore
	private Supplier<String[]> _urlsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MetricValue getUsersInvolvedMetric() {
		if (_usersInvolvedMetricSupplier != null) {
			usersInvolvedMetric = _usersInvolvedMetricSupplier.get();

			_usersInvolvedMetricSupplier = null;
		}

		return usersInvolvedMetric;
	}

	public void setUsersInvolvedMetric(MetricValue usersInvolvedMetric) {
		this.usersInvolvedMetric = usersInvolvedMetric;

		_usersInvolvedMetricSupplier = null;
	}

	@JsonIgnore
	public void setUsersInvolvedMetric(
		UnsafeSupplier<MetricValue, Exception>
			usersInvolvedMetricUnsafeSupplier) {

		_usersInvolvedMetricSupplier = () -> {
			try {
				return usersInvolvedMetricUnsafeSupplier.get();
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
	protected MetricValue usersInvolvedMetric;

	@JsonIgnore
	private Supplier<MetricValue> _usersInvolvedMetricSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DocumentMetric)) {
			return false;
		}

		DocumentMetric documentMetric = (DocumentMetric)object;

		return Objects.equals(toString(), documentMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String assetId = getAssetId();

		if (assetId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetId\": ");

			sb.append("\"");

			sb.append(_escape(assetId));

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

		MetricValue commentsMetric = getCommentsMetric();

		if (commentsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commentsMetric\": ");

			sb.append(String.valueOf(commentsMetric));
		}

		MetricValue downloadsMetric = getDownloadsMetric();

		if (downloadsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloadsMetric\": ");

			sb.append(String.valueOf(downloadsMetric));
		}

		MetricValue impressionMadeMetric = getImpressionMadeMetric();

		if (impressionMadeMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressionMadeMetric\": ");

			sb.append(String.valueOf(impressionMadeMetric));
		}

		MetricValue lastViewedMetric = getLastViewedMetric();

		if (lastViewedMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastViewedMetric\": ");

			sb.append(String.valueOf(lastViewedMetric));
		}

		MetricValue ratingsMetric = getRatingsMetric();

		if (ratingsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingsMetric\": ");

			sb.append(String.valueOf(ratingsMetric));
		}

		String[] urls = getUrls();

		if (urls != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append("[");

			for (int i = 0; i < urls.length; i++) {
				sb.append("\"");

				sb.append(_escape(urls[i]));

				sb.append("\"");

				if ((i + 1) < urls.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		MetricValue usersInvolvedMetric = getUsersInvolvedMetric();

		if (usersInvolvedMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"usersInvolvedMetric\": ");

			sb.append(String.valueOf(usersInvolvedMetric));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.site.dsr.analytics.rest.dto.v1_0.DocumentMetric",
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
// LIFERAY-REST-BUILDER-HASH:-2022624441