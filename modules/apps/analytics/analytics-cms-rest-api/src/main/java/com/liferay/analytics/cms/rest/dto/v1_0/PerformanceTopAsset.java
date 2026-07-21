/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.dto.v1_0;

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

import jakarta.validation.Valid;

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
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
@GraphQLName("PerformanceTopAsset")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PerformanceTopAsset")
public class PerformanceTopAsset implements Serializable {

	public static PerformanceTopAsset toDTO(String json) {
		return ObjectMapperUtil.readValue(PerformanceTopAsset.class, json);
	}

	public static PerformanceTopAsset unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PerformanceTopAsset.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getClassName() {
		if (_classNameSupplier != null) {
			className = _classNameSupplier.get();

			_classNameSupplier = null;
		}

		return className;
	}

	public void setClassName(String className) {
		this.className = className;

		_classNameSupplier = null;
	}

	@JsonIgnore
	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		_classNameSupplier = () -> {
			try {
				return classNameUnsafeSupplier.get();
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
	protected String className;

	@JsonIgnore
	private Supplier<String> _classNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getDownloads() {
		if (_downloadsSupplier != null) {
			downloads = _downloadsSupplier.get();

			_downloadsSupplier = null;
		}

		return downloads;
	}

	public void setDownloads(Double downloads) {
		this.downloads = downloads;

		_downloadsSupplier = null;
	}

	@JsonIgnore
	public void setDownloads(
		UnsafeSupplier<Double, Exception> downloadsUnsafeSupplier) {

		_downloadsSupplier = () -> {
			try {
				return downloadsUnsafeSupplier.get();
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
	protected Double downloads;

	@JsonIgnore
	private Supplier<Double> _downloadsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getEmbedded() {
		if (_embeddedSupplier != null) {
			embedded = _embeddedSupplier.get();

			_embeddedSupplier = null;
		}

		return embedded;
	}

	public void setEmbedded(Object embedded) {
		this.embedded = embedded;

		_embeddedSupplier = null;
	}

	@JsonIgnore
	public void setEmbedded(
		UnsafeSupplier<Object, Exception> embeddedUnsafeSupplier) {

		_embeddedSupplier = () -> {
			try {
				return embeddedUnsafeSupplier.get();
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
	protected Object embedded;

	@JsonIgnore
	private Supplier<Object> _embeddedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getEngagement() {
		if (_engagementSupplier != null) {
			engagement = _engagementSupplier.get();

			_engagementSupplier = null;
		}

		return engagement;
	}

	public void setEngagement(Double engagement) {
		this.engagement = engagement;

		_engagementSupplier = null;
	}

	@JsonIgnore
	public void setEngagement(
		UnsafeSupplier<Double, Exception> engagementUnsafeSupplier) {

		_engagementSupplier = () -> {
			try {
				return engagementUnsafeSupplier.get();
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
	protected Double engagement;

	@JsonIgnore
	private Supplier<Double> _engagementSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getImpressions() {
		if (_impressionsSupplier != null) {
			impressions = _impressionsSupplier.get();

			_impressionsSupplier = null;
		}

		return impressions;
	}

	public void setImpressions(Double impressions) {
		this.impressions = impressions;

		_impressionsSupplier = null;
	}

	@JsonIgnore
	public void setImpressions(
		UnsafeSupplier<Double, Exception> impressionsUnsafeSupplier) {

		_impressionsSupplier = () -> {
			try {
				return impressionsUnsafeSupplier.get();
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
	protected Double impressions;

	@JsonIgnore
	private Supplier<Double> _impressionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(String title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
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
	protected String title;

	@JsonIgnore
	private Supplier<String> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Trend getTrend() {
		if (_trendSupplier != null) {
			trend = _trendSupplier.get();

			_trendSupplier = null;
		}

		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;

		_trendSupplier = null;
	}

	@JsonIgnore
	public void setTrend(UnsafeSupplier<Trend, Exception> trendUnsafeSupplier) {
		_trendSupplier = () -> {
			try {
				return trendUnsafeSupplier.get();
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
	protected Trend trend;

	@JsonIgnore
	private Supplier<Trend> _trendSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
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
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getViews() {
		if (_viewsSupplier != null) {
			views = _viewsSupplier.get();

			_viewsSupplier = null;
		}

		return views;
	}

	public void setViews(Double views) {
		this.views = views;

		_viewsSupplier = null;
	}

	@JsonIgnore
	public void setViews(
		UnsafeSupplier<Double, Exception> viewsUnsafeSupplier) {

		_viewsSupplier = () -> {
			try {
				return viewsUnsafeSupplier.get();
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
	protected Double views;

	@JsonIgnore
	private Supplier<Double> _viewsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PerformanceTopAsset)) {
			return false;
		}

		PerformanceTopAsset performanceTopAsset = (PerformanceTopAsset)object;

		return Objects.equals(toString(), performanceTopAsset.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String className = getClassName();

		if (className != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(className));

			sb.append("\"");
		}

		Double downloads = getDownloads();

		if (downloads != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloads\": ");

			sb.append(downloads);
		}

		Object embedded = getEmbedded();

		if (embedded != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embedded\": ");

			if (embedded instanceof Collection) {
				sb.append(
					JSONFactoryUtil.createJSONArray((Collection<?>)embedded));
			}
			else if (embedded instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)embedded));
			}
			else if (embedded instanceof Object[]) {
				sb.append(
					JSONFactoryUtil.createJSONArray(
						Arrays.asList((Object[])embedded)));
			}
			else if (embedded instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)embedded));
				sb.append("\"");
			}
			else {
				sb.append(embedded);
			}
		}

		Double engagement = getEngagement();

		if (engagement != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"engagement\": ");

			sb.append(engagement);
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Double impressions = getImpressions();

		if (impressions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressions\": ");

			sb.append(impressions);
		}

		String title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(title));

			sb.append("\"");
		}

		Trend trend = getTrend();

		if (trend != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trend\": ");

			sb.append(String.valueOf(trend));
		}

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		Double views = getViews();

		if (views != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"views\": ");

			sb.append(views);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.cms.rest.dto.v1_0.PerformanceTopAsset",
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
// LIFERAY-REST-BUILDER-HASH:-1171444181