/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.internal.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.liferay.analytics.reports.rest.dto.v1_0.AssetAppearsOnHistogramMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetDeviceMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetHistogramMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetMetric;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import java.net.HttpURLConnection;

import java.util.List;

/**
 * @author Marcos Martins
 */
public class AnalyticsCloudClient {

	public AnalyticsCloudClient(Http http) {
		_http = http;
	}

	public AssetAppearsOnHistogramMetric getAssetAppearsOnHistogramMetric(
			AnalyticsConfiguration analyticsConfiguration, String assetId,
			String assetType, List<Long> channelIds, String identityType,
			Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getUrl(
					assetId, assetType, channelIds, identityType,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/appears-on/histogram", rangeKey));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric =
					null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(
								AssetAppearsOnHistogramMetric.class));

					assetAppearsOnHistogramMetric = objectReader.readValue(
						jsonNode);
				}

				return assetAppearsOnHistogramMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get asset histogram metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get asset histogram metric", exception);
		}
	}

	public AssetDeviceMetric getAssetDeviceMetric(
			AnalyticsConfiguration analyticsConfiguration, String assetId,
			String assetType, List<Long> channelIds, String identityType,
			Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getUrl(
					assetId, assetType, channelIds, identityType,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/devices", rangeKey));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				AssetDeviceMetric assetDeviceMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(AssetDeviceMetric.class));

					assetDeviceMetric = objectReader.readValue(jsonNode);
				}

				return assetDeviceMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get asset histogram metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get asset device metric", exception);
		}
	}

	public AssetHistogramMetric getAssetHistogramMetric(
			AnalyticsConfiguration analyticsConfiguration, String assetId,
			String assetType, List<Long> channelIds, String identityType,
			Integer rangeKey)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.setLocation(
				_getUrl(
					assetId, assetType, channelIds, identityType,
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					"/histogram", rangeKey));

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				AssetHistogramMetric assetHistogramMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(
								AssetHistogramMetric.class));

					assetHistogramMetric = objectReader.readValue(jsonNode);
				}

				return assetHistogramMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get asset histogram metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get asset histogram metric", exception);
		}
	}

	public AssetMetric getAssetMetric(
			AnalyticsConfiguration analyticsConfiguration, String assetId,
			String assetType, List<Long> channelIds, String identityType,
			Integer rangeKey, String[] selectedMetrics)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			String url = _getUrl(
				assetId, assetType, channelIds, identityType,
				analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
				StringPool.BLANK, rangeKey);

			url = HttpComponentsUtil.addParameter(
				url, "selectedMetrics",
				StringUtil.merge(selectedMetrics, StringPool.COMMA));

			options.setLocation(url);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				AssetMetric assetMetric = null;

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				if (jsonNode != null) {
					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructType(AssetMetric.class));

					assetMetric = objectReader.readValue(jsonNode);
				}

				return assetMetric;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get asset metric");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException("Unable to get asset metric", exception);
		}
	}

	private Http.Options _getOptions(
			AnalyticsConfiguration analyticsConfiguration)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			SecretResolverUtil.resolve(
				CompanyThreadLocal.getCompanyId(),
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature()));
		options.addHeader(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId());

		return options;
	}

	private String _getUrl(
		String assetId, String assetType, List<Long> channelIds,
		String identityType, String liferayAnalyticsFaroBackendURL, String path,
		Integer rangeKey) {

		String url = String.join(
			StringPool.BLANK, liferayAnalyticsFaroBackendURL,
			"/api/1.0/asset-metric/", assetType, path);

		url = HttpComponentsUtil.addParameter(url, "assetId", assetId);
		url = HttpComponentsUtil.addParameter(
			url, "channelIds", StringUtil.merge(channelIds, StringPool.COMMA));

		url = HttpComponentsUtil.addParameter(
			url, "identityType", identityType);

		if (rangeKey != null) {
			url = HttpComponentsUtil.addParameter(url, "rangeKey", rangeKey);
		}

		return url;
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