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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
	description = "Analytics metrics for a single tracked page over the selected date range. Use `getWorkspaceGroupChannelPagesPage` to list these metrics for a site.",
	value = "PageMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Analytics metrics for a single tracked page over the selected date range. Use `getWorkspaceGroupChannelPagesPage` to list these metrics for a site."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageMetric")
public class PageMetric implements Serializable {

	public static PageMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(PageMetric.class, json);
	}

	public static PageMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the tracked page (the page's canonical URL)."
	)
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

	@GraphQLField(
		description = "ID of the tracked page (the page's canonical URL)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetId;

	@JsonIgnore
	private Supplier<String> _assetIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display title of the page."
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

	@GraphQLField(description = "Display title of the page.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Average time on page in seconds across all sessions in the date range."
	)
	public Double getAvgTimeOnPage() {
		if (_avgTimeOnPageSupplier != null) {
			avgTimeOnPage = _avgTimeOnPageSupplier.get();

			_avgTimeOnPageSupplier = null;
		}

		return avgTimeOnPage;
	}

	public void setAvgTimeOnPage(Double avgTimeOnPage) {
		this.avgTimeOnPage = avgTimeOnPage;

		_avgTimeOnPageSupplier = null;
	}

	@JsonIgnore
	public void setAvgTimeOnPage(
		UnsafeSupplier<Double, Exception> avgTimeOnPageUnsafeSupplier) {

		_avgTimeOnPageSupplier = () -> {
			try {
				return avgTimeOnPageUnsafeSupplier.get();
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
		description = "Average time on page in seconds across all sessions in the date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double avgTimeOnPage;

	@JsonIgnore
	private Supplier<Double> _avgTimeOnPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Fraction of sessions that landed on this page and exited without further interaction. Expressed as a value between 0 to 1."
	)
	public Double getBounceRate() {
		if (_bounceRateSupplier != null) {
			bounceRate = _bounceRateSupplier.get();

			_bounceRateSupplier = null;
		}

		return bounceRate;
	}

	public void setBounceRate(Double bounceRate) {
		this.bounceRate = bounceRate;

		_bounceRateSupplier = null;
	}

	@JsonIgnore
	public void setBounceRate(
		UnsafeSupplier<Double, Exception> bounceRateUnsafeSupplier) {

		_bounceRateSupplier = () -> {
			try {
				return bounceRateUnsafeSupplier.get();
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
		description = "Fraction of sessions that landed on this page and exited without further interaction. Expressed as a value between 0 to 1."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double bounceRate;

	@JsonIgnore
	private Supplier<Double> _bounceRateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the data source providing this page's analytics events."
	)
	public String getDataSourceId() {
		if (_dataSourceIdSupplier != null) {
			dataSourceId = _dataSourceIdSupplier.get();

			_dataSourceIdSupplier = null;
		}

		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;

		_dataSourceIdSupplier = null;
	}

	@JsonIgnore
	public void setDataSourceId(
		UnsafeSupplier<String, Exception> dataSourceIdUnsafeSupplier) {

		_dataSourceIdSupplier = () -> {
			try {
				return dataSourceIdUnsafeSupplier.get();
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
		description = "ID of the data source providing this page's analytics events."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String dataSourceId;

	@JsonIgnore
	private Supplier<String> _dataSourceIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total count of sessions where this page was the entry point with no referrer over the selected time range."
	)
	public Double getDirectAccess() {
		if (_directAccessSupplier != null) {
			directAccess = _directAccessSupplier.get();

			_directAccessSupplier = null;
		}

		return directAccess;
	}

	public void setDirectAccess(Double directAccess) {
		this.directAccess = directAccess;

		_directAccessSupplier = null;
	}

	@JsonIgnore
	public void setDirectAccess(
		UnsafeSupplier<Double, Exception> directAccessUnsafeSupplier) {

		_directAccessSupplier = () -> {
			try {
				return directAccessUnsafeSupplier.get();
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
		description = "Total count of sessions where this page was the entry point with no referrer over the selected time range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double directAccess;

	@JsonIgnore
	private Supplier<Double> _directAccessSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total count of sessions that started on this page over the selected time range."
	)
	public Double getEntrances() {
		if (_entrancesSupplier != null) {
			entrances = _entrancesSupplier.get();

			_entrancesSupplier = null;
		}

		return entrances;
	}

	public void setEntrances(Double entrances) {
		this.entrances = entrances;

		_entrancesSupplier = null;
	}

	@JsonIgnore
	public void setEntrances(
		UnsafeSupplier<Double, Exception> entrancesUnsafeSupplier) {

		_entrancesSupplier = () -> {
			try {
				return entrancesUnsafeSupplier.get();
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
		description = "Total count of sessions that started on this page over the selected time range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double entrances;

	@JsonIgnore
	private Supplier<Double> _entrancesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Fraction of sessions on which this page was the last page viewed before exit. Expressed as a value between 0 to 1."
	)
	public Double getExitRate() {
		if (_exitRateSupplier != null) {
			exitRate = _exitRateSupplier.get();

			_exitRateSupplier = null;
		}

		return exitRate;
	}

	public void setExitRate(Double exitRate) {
		this.exitRate = exitRate;

		_exitRateSupplier = null;
	}

	@JsonIgnore
	public void setExitRate(
		UnsafeSupplier<Double, Exception> exitRateUnsafeSupplier) {

		_exitRateSupplier = () -> {
			try {
				return exitRateUnsafeSupplier.get();
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
		description = "Fraction of sessions on which this page was the last page viewed before exit. Expressed as a value between 0 to 1."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double exitRate;

	@JsonIgnore
	private Supplier<Double> _exitRateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visit count where this page was reached via a referrer (search engine, link, etc.)."
	)
	public Double getIndirectAccess() {
		if (_indirectAccessSupplier != null) {
			indirectAccess = _indirectAccessSupplier.get();

			_indirectAccessSupplier = null;
		}

		return indirectAccess;
	}

	public void setIndirectAccess(Double indirectAccess) {
		this.indirectAccess = indirectAccess;

		_indirectAccessSupplier = null;
	}

	@JsonIgnore
	public void setIndirectAccess(
		UnsafeSupplier<Double, Exception> indirectAccessUnsafeSupplier) {

		_indirectAccessSupplier = () -> {
			try {
				return indirectAccessUnsafeSupplier.get();
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
		description = "Visit count where this page was reached via a referrer (search engine, link, etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double indirectAccess;

	@JsonIgnore
	private Supplier<Double> _indirectAccessSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "List of distinct URLs that resolve to this page."
	)
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

	@GraphQLField(
		description = "List of distinct URLs that resolve to this page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] urls;

	@JsonIgnore
	private Supplier<String[]> _urlsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total page view count over the selected date range."
	)
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

	@GraphQLField(
		description = "Total page view count over the selected date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double views;

	@JsonIgnore
	private Supplier<Double> _viewsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage change in views compared to the previous date range."
	)
	public Double getViewsTrendPercentage() {
		if (_viewsTrendPercentageSupplier != null) {
			viewsTrendPercentage = _viewsTrendPercentageSupplier.get();

			_viewsTrendPercentageSupplier = null;
		}

		return viewsTrendPercentage;
	}

	public void setViewsTrendPercentage(Double viewsTrendPercentage) {
		this.viewsTrendPercentage = viewsTrendPercentage;

		_viewsTrendPercentageSupplier = null;
	}

	@JsonIgnore
	public void setViewsTrendPercentage(
		UnsafeSupplier<Double, Exception> viewsTrendPercentageUnsafeSupplier) {

		_viewsTrendPercentageSupplier = () -> {
			try {
				return viewsTrendPercentageUnsafeSupplier.get();
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
		description = "Percentage change in views compared to the previous date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double viewsTrendPercentage;

	@JsonIgnore
	private Supplier<Double> _viewsTrendPercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Distinct visitor count over the selected date range."
	)
	public Double getVisitors() {
		if (_visitorsSupplier != null) {
			visitors = _visitorsSupplier.get();

			_visitorsSupplier = null;
		}

		return visitors;
	}

	public void setVisitors(Double visitors) {
		this.visitors = visitors;

		_visitorsSupplier = null;
	}

	@JsonIgnore
	public void setVisitors(
		UnsafeSupplier<Double, Exception> visitorsUnsafeSupplier) {

		_visitorsSupplier = () -> {
			try {
				return visitorsUnsafeSupplier.get();
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
		description = "Distinct visitor count over the selected date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double visitors;

	@JsonIgnore
	private Supplier<Double> _visitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage change in distinct visitors compared to the previous date range."
	)
	public Double getVisitorsTrendPercentage() {
		if (_visitorsTrendPercentageSupplier != null) {
			visitorsTrendPercentage = _visitorsTrendPercentageSupplier.get();

			_visitorsTrendPercentageSupplier = null;
		}

		return visitorsTrendPercentage;
	}

	public void setVisitorsTrendPercentage(Double visitorsTrendPercentage) {
		this.visitorsTrendPercentage = visitorsTrendPercentage;

		_visitorsTrendPercentageSupplier = null;
	}

	@JsonIgnore
	public void setVisitorsTrendPercentage(
		UnsafeSupplier<Double, Exception>
			visitorsTrendPercentageUnsafeSupplier) {

		_visitorsTrendPercentageSupplier = () -> {
			try {
				return visitorsTrendPercentageUnsafeSupplier.get();
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
		description = "Percentage change in distinct visitors compared to the previous date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double visitorsTrendPercentage;

	@JsonIgnore
	private Supplier<Double> _visitorsTrendPercentageSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageMetric)) {
			return false;
		}

		PageMetric pageMetric = (PageMetric)object;

		return Objects.equals(toString(), pageMetric.toString());
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

		Double avgTimeOnPage = getAvgTimeOnPage();

		if (avgTimeOnPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"avgTimeOnPage\": ");

			sb.append(avgTimeOnPage);
		}

		Double bounceRate = getBounceRate();

		if (bounceRate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bounceRate\": ");

			sb.append(bounceRate);
		}

		String dataSourceId = getDataSourceId();

		if (dataSourceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(dataSourceId));

			sb.append("\"");
		}

		Double directAccess = getDirectAccess();

		if (directAccess != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"directAccess\": ");

			sb.append(directAccess);
		}

		Double entrances = getEntrances();

		if (entrances != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entrances\": ");

			sb.append(entrances);
		}

		Double exitRate = getExitRate();

		if (exitRate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exitRate\": ");

			sb.append(exitRate);
		}

		Double indirectAccess = getIndirectAccess();

		if (indirectAccess != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indirectAccess\": ");

			sb.append(indirectAccess);
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

		Double views = getViews();

		if (views != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"views\": ");

			sb.append(views);
		}

		Double viewsTrendPercentage = getViewsTrendPercentage();

		if (viewsTrendPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewsTrendPercentage\": ");

			sb.append(viewsTrendPercentage);
		}

		Double visitors = getVisitors();

		if (visitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitors\": ");

			sb.append(visitors);
		}

		Double visitorsTrendPercentage = getVisitorsTrendPercentage();

		if (visitorsTrendPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitorsTrendPercentage\": ");

			sb.append(visitorsTrendPercentage);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.PageMetric",
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
// LIFERAY-REST-BUILDER-HASH:593040454