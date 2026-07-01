/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.liferay.analytics.cms.rest.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryAcquisitionChannel;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryHistogramMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryTopPages;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceAssetConsumption;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceAssetConsumptionItem;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceOverviewMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceTopAsset;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceTopAssetItem;
import com.liferay.analytics.cms.rest.dto.v1_0.Trend;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.InputStream;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Rachael Koestartyo
 */
public class AnalyticsCloudClient {

	public AnalyticsCloudClient(Http http) {
		this(http, null);
	}

	public AnalyticsCloudClient(
		Http http, ObjectDefinitionLocalService objectDefinitionLocalService) {

		_http = http;
		_objectDefinitionLocalService = objectDefinitionLocalService;
	}

	public InputStream getInputStream(
			AnalyticsConfiguration analyticsConfiguration, String filterString,
			List<Long> groupIds, String metricType, String path,
			Integer rangeKey, Sort[] sorts)
		throws PortalException {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.addHeader("Accept", "application/octet-stream, */*");

			options.setLocation(
				_getLocation(
					null, analyticsConfiguration.liferayAnalyticsDataSourceId(),
					null, filterString, null, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					metricType, null, null, path, rangeKey, null, null, sorts,
					null, null));

			InputStream inputStream = _http.URLtoInputStream(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (inputStream != null) {
					inputStream.close();
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Response code " + response.getResponseCode());
				}

				throw new PortalException("Unable to get input stream " + path);
			}

			return inputStream;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get input stream " + path, exception);
		}
	}

	public List<ObjectEntryAcquisitionChannel>
			getObjectEntryAcquisitionChannels(
				AnalyticsConfiguration analyticsConfiguration,
				String externalReferenceCode, Long groupId, Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			List<Long> groupIds = new ArrayList<>();

			if (groupId != null) {
				groupIds.add(groupId);
			}

			options.setLocation(
				_getLocation(
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					externalReferenceCode, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/acquisition-channels", rangeKey, null));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (_log.isDebugEnabled()) {
					_log.debug("Response code " + response.getResponseCode());
				}

				throw new PortalException(
					"Unable to get object entry acquisition channels");
			}

			ObjectMapper objectMapper = ObjectMapperHolder._objectMapper;

			JsonNode jsonNode = objectMapper.readTree(content);

			if (jsonNode == null) {
				return null;
			}

			TypeFactory typeFactory = TypeFactory.defaultInstance();

			ObjectReader objectReader = objectMapper.readerFor(
				typeFactory.constructCollectionType(
					List.class, ObjectEntryAcquisitionChannel.class));

			return objectReader.readValue(jsonNode);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get object entry acquisition channels", exception);
		}
	}

	public ObjectEntryHistogramMetric getObjectEntryHistogramMetric(
			AnalyticsConfiguration analyticsConfiguration,
			String externalReferenceCode, Long groupId, Integer rangeKey,
			String[] selectedMetrics)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			List<Long> groupIds = new ArrayList<>();

			if (groupId != null) {
				groupIds.add(groupId);
			}

			options.setLocation(
				_getLocation(
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					externalReferenceCode, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/overview/histogram", rangeKey, selectedMetrics));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				ObjectEntryHistogramMetric objectEntryHistogramMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(
								ObjectEntryHistogramMetric.class));

					objectEntryHistogramMetric = objectReader.readValue(
						jsonNode);
				}

				return objectEntryHistogramMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException(
				"Unable to get object entry histogram metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get object entry histogram metric", exception);
		}
	}

	public ObjectEntryMetric getObjectEntryMetric(
			AnalyticsConfiguration analyticsConfiguration,
			String externalReferenceCode, Long groupId, Integer rangeKey,
			String[] selectedMetrics)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			List<Long> groupIds = new ArrayList<>();

			if (groupId != null) {
				groupIds.add(groupId);
			}

			options.setLocation(
				_getLocation(
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					externalReferenceCode, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/overview", rangeKey, selectedMetrics));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				ObjectEntryMetric objectEntryMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					_renameKey(
						jsonNode, "classification", "trendClassification");

					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(ObjectEntryMetric.class));

					objectEntryMetric = objectReader.readValue(jsonNode);
				}

				return objectEntryMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get object entry metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get object entry metric", exception);
		}
	}

	public ObjectEntryTopPages getObjectEntryTopPages(
			AnalyticsConfiguration analyticsConfiguration,
			String externalReferenceCode, Long groupId, Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			List<Long> groupIds = new ArrayList<>();

			if (groupId != null) {
				groupIds.add(groupId);
			}

			options.setLocation(
				_getLocation(
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					externalReferenceCode, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/appears-on", rangeKey, null));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				ObjectEntryTopPages objectEntryTopPages = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					_renameKey(jsonNode, "topPages", "pages");

					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(
								ObjectEntryTopPages.class));

					objectEntryTopPages = objectReader.readValue(jsonNode);
				}

				return objectEntryTopPages;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get object entry top pages");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get object entry top pages", exception);
		}
	}

	public PerformanceAssetConsumption getPerformanceAssetConsumption(
			AnalyticsConfiguration analyticsConfiguration, Long categoryId,
			String groupBy, List<Long> groupIds, Locale locale,
			String metricType, String objectType, int page, Integer rangeKey,
			int size, Long tagId, Long vocabularyId)
		throws PortalException {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getLocation(
					categoryId,
					analyticsConfiguration.liferayAnalyticsDataSourceId(), null,
					null, groupBy, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					metricType, objectType, page, "/asset-consumption",
					rangeKey, null, size, null, tagId, vocabularyId));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				PerformanceAssetConsumption performanceAssetConsumption = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					_renameKey(
						jsonNode, "performanceAssetConsumptionItems",
						"metrics");
					_renameKey(
						jsonNode, "performanceAssetConsumptionItemsCount",
						"total");

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							PerformanceAssetConsumption.class);

					performanceAssetConsumption = objectReader.readValue(
						jsonNode);

					if (StringUtil.equalsIgnoreCase(groupBy, "structure")) {
						_updatePerformanceAssetConsumptionFromObjectDefinition(
							locale, performanceAssetConsumption);
					}
				}

				return performanceAssetConsumption;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException(
				"Unable to get performance asset consumption");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get performance asset consumption", exception);
		}
	}

	public PerformanceMetric getPerformanceMetric(
			AnalyticsConfiguration analyticsConfiguration, List<Long> groupIds,
			String metricType, String path, Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getLocation(
					analyticsConfiguration.liferayAnalyticsDataSourceId(),
					groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					metricType, path, rangeKey));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				PerformanceMetric performanceMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					performanceMetric = new PerformanceMetric();

					performanceMetric.setMetricType(() -> metricType);

					Metric[] metrics;

					JsonNode metricsJsonNode = jsonNode.get("metrics");

					if ((metricsJsonNode == null) ||
						!metricsJsonNode.isArray()) {

						metrics = new Metric[0];
					}
					else {
						ObjectReader objectReader =
							ObjectMapperHolder._objectMapper.readerFor(
								Metric[].class);

						metrics = objectReader.readValue(metricsJsonNode);
					}

					performanceMetric.setMetrics(() -> metrics);
				}

				return performanceMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get performance metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get performance metric", exception);
		}
	}

	public PerformanceOverviewMetric getPerformanceOverviewMetric(
			AnalyticsConfiguration analyticsConfiguration, List<Long> groupIds,
			Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getLocation(
					analyticsConfiguration.liferayAnalyticsDataSourceId(), null,
					groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/performance-overview-metric", rangeKey, null));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				PerformanceOverviewMetric performanceOverviewMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					_renameKey(
						jsonNode, "classification", "trendClassification");

					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructCollectionType(
								List.class, Metric.class));

					performanceOverviewMetric = _getPerformanceOverviewMetric(
						objectReader.readValue(jsonNode));
				}

				return performanceOverviewMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException(
				"Unable to get performance overview metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get performance overview metric", exception);
		}
	}

	public PerformanceTopAsset getPerformanceTopAsset(
			AnalyticsConfiguration analyticsConfiguration, String filterString,
			List<Long> groupIds, int page, Integer rangeKey, int size,
			Sort[] sorts)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getLocation(
					null, analyticsConfiguration.liferayAnalyticsDataSourceId(),
					null, filterString, null, groupIds,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					null, null, page, "/summaries", rangeKey, null, size, sorts,
					null, null));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				PerformanceTopAsset performanceTopAsset = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					performanceTopAsset = _getPerformanceTopAsset(jsonNode);
				}

				return performanceTopAsset;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get performance top asset");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get performance top asset", exception);
		}
	}

	private String _getLocation(
		Long categoryId, String dataSourceId, String externalReferenceCode,
		String filterString, String groupBy, List<Long> groupIds,
		String liferayAnalyticsFaroBackendURL, String metricType,
		String objectType, Integer page, String path, Integer rangeKey,
		String[] selectedMetrics, Integer size, Sort[] sorts, Long tagId,
		Long vocabularyId) {

		String location = String.join(
			StringPool.BLANK, liferayAnalyticsFaroBackendURL,
			"/api/1.0/asset-metric/objectEntry", path);

		if (categoryId != null) {
			location = HttpComponentsUtil.addParameter(
				location, "categoryId", categoryId);
		}

		if (Validator.isNotNull(dataSourceId)) {
			location = HttpComponentsUtil.addParameter(
				location, "dataSourceId", dataSourceId);
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			location = HttpComponentsUtil.addParameter(
				location, "externalReferenceCode", externalReferenceCode);
		}

		if (Validator.isNotNull(filterString)) {
			location = HttpComponentsUtil.addParameter(
				location, "filter", filterString);
		}

		if (Validator.isNotNull(groupBy)) {
			location = HttpComponentsUtil.addParameter(
				location, "groupBy", groupBy);
		}

		if (!groupIds.isEmpty()) {
			location = HttpComponentsUtil.addParameter(
				location, "groupIds",
				StringUtil.merge(groupIds, StringPool.COMMA));
		}

		if (Validator.isNotNull(metricType)) {
			location = HttpComponentsUtil.addParameter(
				location, "assetSummaryMetricType", metricType);
		}

		if (Validator.isNotNull(objectType)) {
			location = HttpComponentsUtil.addParameter(
				location, "objectType", objectType);
		}

		if (page != null) {
			location = HttpComponentsUtil.addParameter(location, "page", page);
		}

		if (rangeKey != null) {
			location = HttpComponentsUtil.addParameter(
				location, "rangeKey", rangeKey);
		}

		if (ArrayUtil.isNotEmpty(selectedMetrics)) {
			location = HttpComponentsUtil.addParameter(
				location, "selectedMetrics",
				StringUtil.merge(selectedMetrics, StringPool.COMMA));
		}

		if (size != null) {
			location = HttpComponentsUtil.addParameter(location, "size", size);
		}

		if (ArrayUtil.isNotEmpty(sorts)) {
			List<String> sortStrings = new ArrayList<>();

			for (Sort sort : sorts) {
				sortStrings.add(sort.getFieldName());
				sortStrings.add(sort.isReverse() ? "desc" : "asc");
			}

			location = HttpComponentsUtil.addParameter(
				location, "sort",
				StringUtil.merge(sortStrings, StringPool.COMMA));
		}

		if (tagId != null) {
			location = HttpComponentsUtil.addParameter(
				location, "tagId", tagId);
		}

		if (vocabularyId != null) {
			location = HttpComponentsUtil.addParameter(
				location, "vocabularyId", vocabularyId);
		}

		return location;
	}

	private String _getLocation(
		String dataSourceId, List<Long> groupIds,
		String liferayAnalyticsFaroBackendURL, String metricType, String path,
		Integer rangeKey) {

		return _getLocation(
			null, dataSourceId, null, null, null, groupIds,
			liferayAnalyticsFaroBackendURL, metricType, null, null, path,
			rangeKey, null, null, null, null, null);
	}

	private String _getLocation(
		String dataSourceId, String externalReferenceCode, List<Long> groupIds,
		String liferayAnalyticsFaroBackendURL, String path, Integer rangeKey,
		String[] selectedMetrics) {

		return _getLocation(
			null, dataSourceId, externalReferenceCode, null, null, groupIds,
			liferayAnalyticsFaroBackendURL, null, null, null, path, rangeKey,
			selectedMetrics, null, null, null, null);
	}

	private Double _getMetricValue(JsonNode jsonNode, String metricName) {
		JsonNode metricJsonNode = jsonNode.get(metricName);

		if (metricJsonNode == null) {
			return null;
		}

		JsonNode valueJsonNode = metricJsonNode.get("value");

		if (valueJsonNode == null) {
			return null;
		}

		return valueJsonNode.asDouble();
	}

	private Http.Options _getOptions(
			AnalyticsConfiguration analyticsConfiguration)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			analyticsConfiguration.
				liferayAnalyticsFaroBackendSecuritySignature());
		options.addHeader(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId());

		return options;
	}

	private PerformanceOverviewMetric _getPerformanceOverviewMetric(
		List<Metric> metrics) {

		PerformanceOverviewMetric performanceOverviewMetric =
			new PerformanceOverviewMetric();

		for (Metric metric : metrics) {
			String metricType = metric.getMetricType();

			if (StringUtil.equals(metricType, "downloadsMetric")) {
				performanceOverviewMetric.setDownloadsMetric(() -> metric);
			}
			else if (StringUtil.equals(metricType, "impressionsMetric")) {
				performanceOverviewMetric.setImpressionsMetric(() -> metric);
			}
			else if (StringUtil.equals(metricType, "readsMetric")) {
				performanceOverviewMetric.setReadsMetric(() -> metric);
			}
			else if (StringUtil.equals(metricType, "viewsMetric")) {
				performanceOverviewMetric.setViewsMetric(() -> metric);
			}
		}

		return performanceOverviewMetric;
	}

	private PerformanceTopAsset _getPerformanceTopAsset(JsonNode jsonNode) {
		PerformanceTopAsset performanceTopAsset = new PerformanceTopAsset();

		JsonNode pageJsonNode = jsonNode.get("page");

		if (pageJsonNode != null) {
			JsonNode lastPageJsonNode = pageJsonNode.get("totalPages");

			if (lastPageJsonNode != null) {
				performanceTopAsset.setLastPage(lastPageJsonNode::asLong);
			}

			JsonNode pageNumberJsonNode = pageJsonNode.get("number");

			if (pageNumberJsonNode != null) {
				performanceTopAsset.setPage(pageNumberJsonNode::asLong);
			}

			JsonNode pageSizeJsonNode = pageJsonNode.get("size");

			if (pageSizeJsonNode != null) {
				performanceTopAsset.setPageSize(pageSizeJsonNode::asLong);
			}

			JsonNode totalCountJsonNode = pageJsonNode.get("totalElements");

			if (totalCountJsonNode != null) {
				performanceTopAsset.setTotalCount(totalCountJsonNode::asLong);
			}
		}

		List<PerformanceTopAssetItem> performanceTopAssetItems =
			new ArrayList<>();

		JsonNode embeddedJsonNode = jsonNode.get("_embedded");

		if (embeddedJsonNode != null) {
			JsonNode assetSummaryMetricsJsonNode = embeddedJsonNode.get(
				"assetSummaryMetrics");

			if (assetSummaryMetricsJsonNode != null) {
				for (JsonNode assetSummaryMetricJsonNode :
						assetSummaryMetricsJsonNode) {

					performanceTopAssetItems.add(
						_getPerformanceTopAssetItem(
							assetSummaryMetricJsonNode));
				}
			}
		}

		performanceTopAsset.setPerformanceTopAssetItems(
			() -> performanceTopAssetItems.toArray(
				new PerformanceTopAssetItem[0]));

		return performanceTopAsset;
	}

	private PerformanceTopAssetItem _getPerformanceTopAssetItem(
		JsonNode jsonNode) {

		PerformanceTopAssetItem performanceTopAssetItem =
			new PerformanceTopAssetItem();

		performanceTopAssetItem.setDownloads(
			() -> _getMetricValue(jsonNode, "downloadsMetric"));
		performanceTopAssetItem.setEngagement(
			() -> _getMetricValue(jsonNode, "engagementMetric"));
		performanceTopAssetItem.setImpressions(
			() -> _getMetricValue(jsonNode, "impressionsMetric"));

		JsonNode mimeTypeJsonNode = jsonNode.get("assetType");

		if (mimeTypeJsonNode != null) {
			performanceTopAssetItem.setMimeType(mimeTypeJsonNode::asText);
		}

		JsonNode titleJsonNode = jsonNode.get("assetTitle");

		if (titleJsonNode != null) {
			performanceTopAssetItem.setTitle(titleJsonNode::asText);
		}

		JsonNode engagementMetricJsonNode = jsonNode.get("engagementMetric");

		if (engagementMetricJsonNode != null) {
			JsonNode trendJsonNode = engagementMetricJsonNode.get("trend");

			if (trendJsonNode != null) {
				performanceTopAssetItem.setTrend(
					() -> _getTrend(trendJsonNode));
			}
		}

		performanceTopAssetItem.setViews(
			() -> _getMetricValue(jsonNode, "viewsMetric"));

		return performanceTopAssetItem;
	}

	private Trend _getTrend(JsonNode jsonNode) {
		Trend trend = new Trend();

		JsonNode trendClassificationJsonNode = jsonNode.get(
			"trendClassification");

		if (trendClassificationJsonNode != null) {
			trend.setClassification(
				() -> Trend.Classification.create(
					trendClassificationJsonNode.asText()));
		}

		JsonNode percentageJsonNode = jsonNode.get("percentage");

		if (percentageJsonNode != null) {
			trend.setPercentage(percentageJsonNode::asDouble);
		}

		return trend;
	}

	private void _renameKey(JsonNode jsonNode, String newKey, String oldKey) {
		if (jsonNode == null) {
			return;
		}

		if (jsonNode.isObject()) {
			ObjectNode objectNode = (ObjectNode)jsonNode;

			if (objectNode.has(oldKey)) {
				JsonNode objectJsonNode = objectNode.remove(oldKey);

				objectNode.set(newKey, objectJsonNode);
			}

			Iterator<Map.Entry<String, JsonNode>> iterator =
				objectNode.fields();

			iterator.forEachRemaining(
				entry -> _renameKey(entry.getValue(), newKey, oldKey));
		}
		else if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode)jsonNode;

			for (JsonNode arrayJsonNode : arrayNode) {
				_renameKey(arrayJsonNode, newKey, oldKey);
			}
		}
	}

	private void _updatePerformanceAssetConsumptionFromObjectDefinition(
		Locale locale,
		PerformanceAssetConsumption performanceAssetConsumption) {

		PerformanceAssetConsumptionItem[] performanceAssetConsumptionItems =
			performanceAssetConsumption.getPerformanceAssetConsumptionItems();

		if (ArrayUtil.isEmpty(performanceAssetConsumptionItems)) {
			return;
		}

		List<String> names = TransformUtil.transformToList(
			performanceAssetConsumptionItems,
			PerformanceAssetConsumptionItem::getTitle);

		Map<String, ObjectDefinition> objectDefinitionsMap = new HashMap<>();

		for (ObjectDefinition objectDefinition :
				(List<ObjectDefinition>)_objectDefinitionLocalService.dslQuery(
					DSLQueryFactoryUtil.select(
						ObjectDefinitionTable.INSTANCE
					).from(
						ObjectDefinitionTable.INSTANCE
					).where(
						ObjectDefinitionTable.INSTANCE.companyId.eq(
							CompanyThreadLocal.getCompanyId()
						).and(
							ObjectDefinitionTable.INSTANCE.name.in(
								names.toArray(new String[0]))
						)
					))) {

			objectDefinitionsMap.put(
				objectDefinition.getName(), objectDefinition);
		}

		for (PerformanceAssetConsumptionItem performanceAssetConsumptionItem :
				performanceAssetConsumptionItems) {

			ObjectDefinition objectDefinition = objectDefinitionsMap.get(
				performanceAssetConsumptionItem.getTitle());

			if (objectDefinition != null) {
				performanceAssetConsumptionItem.setKey(
					objectDefinition::getExternalReferenceCode);
				performanceAssetConsumptionItem.setTitle(
					() -> objectDefinition.getLabel(locale));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsCloudClient.class);

	private final Http _http;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static class ObjectMapperHolder {

		private static final ObjectMapper _objectMapper = new ObjectMapper() {
			{
				configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			}
		};

	}

}