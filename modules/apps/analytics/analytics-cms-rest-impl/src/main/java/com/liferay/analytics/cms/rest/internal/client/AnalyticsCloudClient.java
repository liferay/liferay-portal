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

import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryAcquisitionChannel;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryHistogramMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryTopPages;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Rachael Koestartyo
 */
public class AnalyticsCloudClient {

	public AnalyticsCloudClient(Http http) {
		_http = http;
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
				_getUrl(
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
				_getUrl(
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
				_getUrl(
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
				_getUrl(
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

	private String _getUrl(
		String dataSourceId, String externalReferenceCode, List<Long> groupIds,
		String liferayAnalyticsFaroBackendURL, String path, Integer rangeKey,
		String[] selectedMetrics) {

		String url = String.join(
			StringPool.BLANK, liferayAnalyticsFaroBackendURL,
			"/api/1.0/asset-metric/objectEntry", path);

		url = HttpComponentsUtil.addParameter(
			url, "dataSourceId", dataSourceId);
		url = HttpComponentsUtil.addParameter(
			url, "externalReferenceCode", externalReferenceCode);

		if (!groupIds.isEmpty()) {
			url = HttpComponentsUtil.addParameter(
				url, "groupIds", StringUtil.merge(groupIds, StringPool.COMMA));
		}

		if (rangeKey != null) {
			url = HttpComponentsUtil.addParameter(url, "rangeKey", rangeKey);
		}

		if (ArrayUtil.isNotEmpty(selectedMetrics)) {
			return HttpComponentsUtil.addParameter(
				url, "selectedMetrics",
				StringUtil.merge(selectedMetrics, StringPool.COMMA));
		}

		return url;
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

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsCloudClient.class);

	private final Http _http;

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