/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.internal.client.exception.DataSourceConnectionException;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsChannel;
import com.liferay.analytics.settings.rest.internal.client.model.AnalyticsDataSource;
import com.liferay.analytics.settings.rest.internal.client.pagination.Page;
import com.liferay.analytics.settings.rest.internal.client.pagination.Pagination;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Riccardo Ferrari
 */
public class AnalyticsCloudClient {

	public AnalyticsCloudClient(Http http) {
		_http = http;
	}

	public AnalyticsChannel addAnalyticsChannel(
			AnalyticsConfiguration analyticsConfiguration, String name)
		throws Exception {

		Http.Options options = _getOptions(analyticsConfiguration);

		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(
			JSONUtil.put(
				"name", name
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(
			analyticsConfiguration.liferayAnalyticsFaroBackendURL() +
				"/api/1.0/channels");
		options.setPost(true);

		String content = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
			TypeFactory typeFactory = TypeFactory.defaultInstance();

			ObjectReader objectReader =
				ObjectMapperHolder._objectMapper.readerFor(
					typeFactory.constructCollectionType(
						ArrayList.class, AnalyticsChannel.class));

			List<AnalyticsChannel> analyticsChannels = objectReader.readValue(
				content);

			return analyticsChannels.get(0);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Response code " + response.getResponseCode());
		}

		throw new PortalException("Unable to add Channel");
	}

	public Map<String, Object> connectAnalyticsDataSource(
			Company company, String connectionToken)
		throws Exception {

		JSONObject connectionTokenJSONObject = _decodeToken(connectionToken);

		_validateConnectionTokenURL(connectionTokenJSONObject.getString("url"));

		Http.Options options = new Http.Options();

		options.addPart("name", company.getName());
		options.addPart("portalURL", company.getPortalURL(0));
		options.addPart("token", connectionTokenJSONObject.getString("token"));
		options.setLocation(connectionTokenJSONObject.getString("url"));
		options.setPost(true);

		String content = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new DataSourceConnectionException(
				"Unable to connect analytics data source");
		}

		JSONObject contentJSONObject = JSONFactoryUtil.createJSONObject(
			content);

		_connectionProperties.putAll(contentJSONObject.toMap());

		return _connectionProperties;
	}

	public AnalyticsDataSource disconnectAnalyticsDataSource(
			AnalyticsConfiguration analyticsConfiguration, Company company)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setBody(
				JSONUtil.put(
					"url", company.getPortalURL(0)
				).toString(),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setLocation(
				String.format(
					"%s/api/1.0/data-sources/%s/disconnect",
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					analyticsConfiguration.liferayAnalyticsDataSourceId()));
			options.setPost(true);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				_connectionProperties.clear();

				return ObjectMapperHolder._objectMapper.readValue(
					content, AnalyticsDataSource.class);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException(
				"Unable to disconnect analytics data source");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to disconnect analytics data source", exception);
		}
	}

	public Page<AnalyticsChannel> getAnalyticsChannelsPage(
			AnalyticsConfiguration analyticsConfiguration, String keywords,
			int page, int size, Sort[] sorts)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			String liferayAnalyticsFaroBackendURL = GetterUtil.getString(
				_connectionProperties.get("liferayAnalyticsFaroBackendURL"),
				analyticsConfiguration.liferayAnalyticsFaroBackendURL());

			String url = liferayAnalyticsFaroBackendURL + "/api/1.0/channels";

			if (Validator.isNotNull(keywords)) {
				url = HttpComponentsUtil.addParameter(url, "filter", keywords);
			}

			url = HttpComponentsUtil.addParameter(url, "page", page);
			url = HttpComponentsUtil.addParameter(url, "size", size);

			if (ArrayUtil.isNotEmpty(sorts)) {
				StringBundler sb = new StringBundler(sorts.length * 3);

				for (Sort sort : sorts) {
					sb.append(sort.getFieldName());
					sb.append(StringPool.COMMA);

					if (sort.isReverse()) {
						sb.append("desc");
					}
					else {
						sb.append("asc");
					}
				}

				url = HttpComponentsUtil.addParameter(
					url, "sort", sb.toString());
			}

			options.setLocation(url);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				List<AnalyticsChannel> analyticsChannels =
					Collections.emptyList();

				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				JsonNode embeddedJsonNode = jsonNode.get("_embedded");

				if (embeddedJsonNode != null) {
					TypeFactory typeFactory = TypeFactory.defaultInstance();

					ObjectReader objectReader =
						ObjectMapperHolder._objectMapper.readerFor(
							typeFactory.constructCollectionType(
								ArrayList.class, AnalyticsChannel.class));

					analyticsChannels = objectReader.readValue(
						embeddedJsonNode.get("channels"));
				}

				JsonNode pageJsonNode = jsonNode.get("page");

				JsonNode totalElementsJsonNode = pageJsonNode.get(
					"totalElements");

				return Page.of(
					analyticsChannels, Pagination.of(page, size),
					totalElementsJsonNode.asLong());
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to get analytics channels page");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to get analytics channels page", exception);
		}
	}

	public AnalyticsChannel updateAnalyticsChannel(
			String analyticsChannelId, Group[] commerceChannelGroups,
			AnalyticsConfiguration analyticsConfiguration, String dataSourceId,
			Locale locale, Group[] siteGroups)
		throws Exception {

		try {
			if (!dataSourceId.equals(
					analyticsConfiguration.liferayAnalyticsDataSourceId())) {

				throw new IllegalArgumentException("Unknown data source ID");
			}

			Http.Options options = _getOptions(analyticsConfiguration);

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setBody(
				JSONUtil.put(
					"commerceChannels",
					_getGroupsJSONArray(commerceChannelGroups, locale)
				).put(
					"dataSourceId", dataSourceId
				).put(
					"groups", _getGroupsJSONArray(siteGroups, locale)
				).toString(),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setLocation(
				String.format(
					"%s/api/1.0/channels/%s",
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					analyticsChannelId));
			options.setPatch(true);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				JsonNode jsonNode = ObjectMapperHolder._objectMapper.readTree(
					content);

				ObjectReader objectReader =
					ObjectMapperHolder._objectMapper.readerFor(
						AnalyticsChannel.class);

				return objectReader.readValue(jsonNode.get("channel"));
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException("Unable to update analytics channel");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to update analytics channels", exception);
		}
	}

	public AnalyticsDataSource updateAnalyticsDataSourceDetails(
			AnalyticsConfiguration analyticsConfiguration,
			Boolean contentRecommenderMostPopularItemsEnabled,
			Boolean contentRecommenderUserPersonalizationEnabled)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setBody(
				JSONUtil.put(
					"contentRecommenderMostPopularItemsEnabled",
					contentRecommenderMostPopularItemsEnabled
				).put(
					"contentRecommenderUserPersonalizationEnabled",
					contentRecommenderUserPersonalizationEnabled
				).toString(),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setLocation(
				String.format(
					"%s/api/1.0/data-sources/%s/details",
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					analyticsConfiguration.liferayAnalyticsDataSourceId()));
			options.setPut(true);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return ObjectMapperHolder._objectMapper.readValue(
					content, AnalyticsDataSource.class);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException(
				"Unable to update analytics data source content recommender " +
					"details");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to update analytics data source content recommender " +
					"details",
				exception);
		}
	}

	public AnalyticsDataSource updateAnalyticsDataSourceDetails(
			Boolean accountsSelected,
			AnalyticsConfiguration analyticsConfiguration,
			Boolean commerceChannelsSelected, Boolean contactsSelected,
			Boolean sitesSelected)
		throws Exception {

		try {
			Http.Options options = _getOptions(analyticsConfiguration);

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setBody(
				JSONUtil.put(
					"accountsSelected", accountsSelected
				).put(
					"commerceChannelsSelected", commerceChannelsSelected
				).put(
					"contactsSelected", contactsSelected
				).put(
					"sitesSelected", sitesSelected
				).toString(),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setLocation(
				String.format(
					"%s/api/1.0/data-sources/%s/details",
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					analyticsConfiguration.liferayAnalyticsDataSourceId()));
			options.setPut(true);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return ObjectMapperHolder._objectMapper.readValue(
					content, AnalyticsDataSource.class);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + response.getResponseCode());
			}

			throw new PortalException(
				"Unable to update analytics data source details");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to update analytics data source details", exception);
		}
	}

	private JSONObject _decodeToken(String connectionToken) throws Exception {
		try {
			if (Validator.isBlank(connectionToken)) {
				throw new IllegalArgumentException();
			}

			return JSONFactoryUtil.createJSONObject(
				new String(Base64.decode(connectionToken)));
		}
		catch (Exception exception) {
			_log.error("Unable to decode token", exception);

			throw new PortalException("Unable to decode token", exception);
		}
	}

	private JSONArray _getGroupsJSONArray(Group[] groups, Locale locale)
		throws Exception {

		return JSONUtil.toJSONArray(
			groups,
			group -> {
				if (group == null) {
					return null;
				}

				return JSONUtil.put(
					"id",
					() -> {
						if (!Objects.equals(
								group.getClassNameId(),
								PortalUtil.getClassNameId(Group.class)) &&
							!Objects.equals(
								group.getClassNameId(),
								PortalUtil.getClassNameId(
									Organization.class))) {

							return String.valueOf(group.getClassPK());
						}

						return String.valueOf(group.getGroupId());
					}
				).put(
					"name",
					() -> {
						try {
							return group.getDescriptiveName(locale);
						}
						catch (PortalException portalException) {
							_log.error(portalException);

							return LanguageUtil.get(locale, "unknown");
						}
					}
				);
			});
	}

	private Http.Options _getOptions(
			AnalyticsConfiguration analyticsConfiguration)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			GetterUtil.getString(
				_connectionProperties.get(
					"liferayAnalyticsFaroBackendSecuritySignature"),
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature()));
		options.addHeader(
			"OSB-Asah-Project-ID",
			GetterUtil.getString(
				_connectionProperties.get("liferayAnalyticsProjectId"),
				analyticsConfiguration.liferayAnalyticsProjectId()));

		return options;
	}

	private void _validateConnectionTokenURL(String url) throws Exception {
		String analyticsCloudDomainAllowed = PropsUtil.get(
			PropsKeys.ANALYTICS_CLOUD_DOMAIN_ALLOWED);

		if (StringUtil.equals(analyticsCloudDomainAllowed, StringPool.STAR)) {
			return;
		}

		String domain = HttpComponentsUtil.getDomain(url);

		if (InetAddressUtil.isLocalInetAddress(
				InetAddressUtil.getInetAddressByName(domain)) ||
			!StringUtil.endsWith(domain, analyticsCloudDomainAllowed)) {

			throw new DataSourceConnectionException("Invalid URL domain");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsCloudClient.class);

	private static final Map<String, Object> _connectionProperties =
		new ConcurrentHashMap<>();

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