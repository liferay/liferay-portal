/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "An aggregated set of analytics metrics for a single tracked asset (page, blog, document, form, journal, object entry, etc.) over the selected date range. Trend percentages compare metrics on the selected date range to the previous date range. Use `getWorkspaceGroupChannelAssetSummariesPage` to list these sets of metrics.",
	value = "AssetSummaryMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "An aggregated set of analytics metrics for a single tracked asset (page, blog, document, form, journal, object entry, etc.) over the selected date range. Trend percentages compare metrics on the selected date range to the previous date range. Use `getWorkspaceGroupChannelAssetSummariesPage` to list these sets of metrics."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssetSummaryMetric")
public class AssetSummaryMetric implements Serializable {

	public static AssetSummaryMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(AssetSummaryMetric.class, json);
	}

	public static AssetSummaryMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AssetSummaryMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "ID of the tracked asset."
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

	@GraphQLField(description = "ID of the tracked asset.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetId;

	@JsonIgnore
	private Supplier<String> _assetIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display title of the asset (e.g. the page title or blog post title)."
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

	@GraphQLField(
		description = "Display title of the asset (e.g. the page title or blog post title)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "Asset category.")
	@JsonGetter("assetType")
	@Valid
	public AssetType getAssetType() {
		if (_assetTypeSupplier != null) {
			assetType = _assetTypeSupplier.get();

			_assetTypeSupplier = null;
		}

		return assetType;
	}

	@JsonIgnore
	public String getAssetTypeAsString() {
		AssetType assetType = getAssetType();

		if (assetType == null) {
			return null;
		}

		return assetType.toString();
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;

		_assetTypeSupplier = null;
	}

	@JsonIgnore
	public void setAssetType(
		UnsafeSupplier<AssetType, Exception> assetTypeUnsafeSupplier) {

		_assetTypeSupplier = () -> {
			try {
				return assetTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Asset category.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected AssetType assetType;

	@JsonIgnore
	private Supplier<AssetType> _assetTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Download count over the selected date range. Null when the asset type doesn't track downloads."
	)
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

	@GraphQLField(
		description = "Download count over the selected date range. Null when the asset type doesn't track downloads."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double downloads;

	@JsonIgnore
	private Supplier<Double> _downloadsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage change in downloads compared to the previous date range."
	)
	public Double getDownloadsTrendPercentage() {
		if (_downloadsTrendPercentageSupplier != null) {
			downloadsTrendPercentage = _downloadsTrendPercentageSupplier.get();

			_downloadsTrendPercentageSupplier = null;
		}

		return downloadsTrendPercentage;
	}

	public void setDownloadsTrendPercentage(Double downloadsTrendPercentage) {
		this.downloadsTrendPercentage = downloadsTrendPercentage;

		_downloadsTrendPercentageSupplier = null;
	}

	@JsonIgnore
	public void setDownloadsTrendPercentage(
		UnsafeSupplier<Double, Exception>
			downloadsTrendPercentageUnsafeSupplier) {

		_downloadsTrendPercentageSupplier = () -> {
			try {
				return downloadsTrendPercentageUnsafeSupplier.get();
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
		description = "Percentage change in downloads compared to the previous date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double downloadsTrendPercentage;

	@JsonIgnore
	private Supplier<Double> _downloadsTrendPercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Impression count (asset rendered into a viewport but not necessarily interacted with)."
	)
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

	@GraphQLField(
		description = "Impression count (asset rendered into a viewport but not necessarily interacted with)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double impressions;

	@JsonIgnore
	private Supplier<Double> _impressionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage change in impressions compared to the previous date range."
	)
	public Double getImpressionsTrendPercentage() {
		if (_impressionsTrendPercentageSupplier != null) {
			impressionsTrendPercentage =
				_impressionsTrendPercentageSupplier.get();

			_impressionsTrendPercentageSupplier = null;
		}

		return impressionsTrendPercentage;
	}

	public void setImpressionsTrendPercentage(
		Double impressionsTrendPercentage) {

		this.impressionsTrendPercentage = impressionsTrendPercentage;

		_impressionsTrendPercentageSupplier = null;
	}

	@JsonIgnore
	public void setImpressionsTrendPercentage(
		UnsafeSupplier<Double, Exception>
			impressionsTrendPercentageUnsafeSupplier) {

		_impressionsTrendPercentageSupplier = () -> {
			try {
				return impressionsTrendPercentageUnsafeSupplier.get();
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
		description = "Percentage change in impressions compared to the previous date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double impressionsTrendPercentage;

	@JsonIgnore
	private Supplier<Double> _impressionsTrendPercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Read count (full-content engagement, distinct from impressions)."
	)
	public Double getReads() {
		if (_readsSupplier != null) {
			reads = _readsSupplier.get();

			_readsSupplier = null;
		}

		return reads;
	}

	public void setReads(Double reads) {
		this.reads = reads;

		_readsSupplier = null;
	}

	@JsonIgnore
	public void setReads(
		UnsafeSupplier<Double, Exception> readsUnsafeSupplier) {

		_readsSupplier = () -> {
			try {
				return readsUnsafeSupplier.get();
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
		description = "Read count (full-content engagement, distinct from impressions)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double reads;

	@JsonIgnore
	private Supplier<Double> _readsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage change in reads compared to the previous date range."
	)
	public Double getReadsTrendPercentage() {
		if (_readsTrendPercentageSupplier != null) {
			readsTrendPercentage = _readsTrendPercentageSupplier.get();

			_readsTrendPercentageSupplier = null;
		}

		return readsTrendPercentage;
	}

	public void setReadsTrendPercentage(Double readsTrendPercentage) {
		this.readsTrendPercentage = readsTrendPercentage;

		_readsTrendPercentageSupplier = null;
	}

	@JsonIgnore
	public void setReadsTrendPercentage(
		UnsafeSupplier<Double, Exception> readsTrendPercentageUnsafeSupplier) {

		_readsTrendPercentageSupplier = () -> {
			try {
				return readsTrendPercentageUnsafeSupplier.get();
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
		description = "Percentage change in reads compared to the previous date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double readsTrendPercentage;

	@JsonIgnore
	private Supplier<Double> _readsTrendPercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "View count (asset opened/loaded by an individual)."
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
		description = "View count (asset opened/loaded by an individual)."
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

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetSummaryMetric)) {
			return false;
		}

		AssetSummaryMetric assetSummaryMetric = (AssetSummaryMetric)object;

		return Objects.equals(toString(), assetSummaryMetric.toString());
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

		AssetType assetType = getAssetType();

		if (assetType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");
			sb.append(assetType);
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

		Double downloadsTrendPercentage = getDownloadsTrendPercentage();

		if (downloadsTrendPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloadsTrendPercentage\": ");

			sb.append(downloadsTrendPercentage);
		}

		Double impressions = getImpressions();

		if (impressions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressions\": ");

			sb.append(impressions);
		}

		Double impressionsTrendPercentage = getImpressionsTrendPercentage();

		if (impressionsTrendPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressionsTrendPercentage\": ");

			sb.append(impressionsTrendPercentage);
		}

		Double reads = getReads();

		if (reads != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reads\": ");

			sb.append(reads);
		}

		Double readsTrendPercentage = getReadsTrendPercentage();

		if (readsTrendPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readsTrendPercentage\": ");

			sb.append(readsTrendPercentage);
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.AssetSummaryMetric",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("AssetType")
	public static enum AssetType {

		BLOG("BLOG"), DOCUMENT("DOCUMENT"), FORM("FORM"), JOURNAL("JOURNAL"),
		OBJECT_ENTRY("OBJECT_ENTRY"), PAGE("PAGE");

		@JsonCreator
		public static AssetType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (AssetType assetType : values()) {
				if (Objects.equals(assetType.getValue(), value)) {
					return assetType;
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

		private AssetType(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-715899407