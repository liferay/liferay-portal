/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.internal.rest;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderException;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInstanceSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderOutputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponseStatus;
import com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration;
import com.liferay.dynamic.data.mapping.data.provider.settings.DDMDataProviderSettingsProvider;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.json.web.service.client.JSONWebServiceClient;
import com.liferay.portal.json.web.service.client.JSONWebServiceClientFactory;
import com.liferay.portal.json.web.service.client.JSONWebServiceException;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.ByteArrayInputStream;

import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.security.KeyStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	configurationPid = "com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration",
	property = "ddm.data.provider.type=rest", service = DDMDataProvider.class
)
public class DDMRESTDataProvider implements DDMDataProvider {

	@Override
	public DDMDataProviderResponse getData(
			DDMDataProviderRequest ddmDataProviderRequest)
		throws DDMDataProviderException {

		try {
			DDMDataProviderInstance ddmDataProviderInstance =
				_ddmDataProviderInstanceService.fetchDataProviderInstanceByUuid(
					ddmDataProviderRequest.getDDMDataProviderId());

			if ((ddmDataProviderInstance == null) &&
				Validator.isNumber(
					ddmDataProviderRequest.getDDMDataProviderId())) {

				ddmDataProviderInstance =
					_ddmDataProviderInstanceService.fetchDataProviderInstance(
						GetterUtil.getLong(
							ddmDataProviderRequest.getDDMDataProviderId()));
			}

			if (ddmDataProviderInstance == null) {
				return DDMDataProviderResponse.Builder.newBuilder(
				).withStatus(
					DDMDataProviderResponseStatus.SERVICE_UNAVAILABLE
				).build();
			}

			DDMRESTDataProviderSettings ddmRESTDataProviderSettings =
				_ddmDataProviderInstanceSettings.getSettings(
					ddmDataProviderInstance, DDMRESTDataProviderSettings.class);

			try {
				return _getData(
					ddmDataProviderInstance, ddmDataProviderRequest,
					ddmRESTDataProviderSettings);
			}
			catch (JSONWebServiceException jsonWebServiceException) {
				if (_log.isDebugEnabled()) {
					_log.debug(jsonWebServiceException);
				}
				else if (_log.isWarnEnabled()) {
					_log.warn(
						"The data provider was not able to connect to the " +
							"web service. " + jsonWebServiceException);
				}
			}

			return _createDDMDataProviderResponse(
				JsonPath.parse("{}"), ddmDataProviderRequest,
				DDMDataProviderResponseStatus.SERVICE_UNAVAILABLE,
				ddmRESTDataProviderSettings);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new DDMDataProviderException(exception);
		}
	}

	@Override
	public Class<?> getSettings() {
		return _ddmDataProviderSettingsProvider.getSettings();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ddmDataProviderConfiguration = ConfigurableUtil.createConfigurable(
			DDMDataProviderConfiguration.class, properties);
		_portalCache =
			(PortalCache<String, DDMDataProviderResponse>)
				_multiVMPool.getPortalCache(
					DDMRESTDataProvider.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(DDMRESTDataProvider.class.getName());
	}

	protected Map<String, Object> getProxySettingsMap(String host) {
		if (_http.isNonProxyHost(host)) {
			return Collections.emptyMap();
		}

		String proxyHost = SystemProperties.get("http.proxyHost");
		String proxyPort = SystemProperties.get("http.proxyPort");

		if (Validator.isNull(proxyHost) || Validator.isNull(proxyPort)) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"proxyHostName", proxyHost
		).put(
			"proxyHostPort", GetterUtil.getInteger(proxyPort)
		).build();
	}

	private void _addParameter(
		DDMDataProviderRequest ddmDataProviderRequest,
		String ddmDataProviderRequestParameterName, String parameterName,
		Map<String, List<String>> parametersMap) {

		_addParameter(
			parameterName, parametersMap,
			ddmDataProviderRequest.getParameter(
				ddmDataProviderRequestParameterName, String.class));
	}

	private void _addParameter(
		String parameterName, Map<String, List<String>> parametersMap,
		Object parameterValue) {

		if (parameterValue == null) {
			return;
		}

		parametersMap.put(
			parameterName,
			Collections.singletonList(GetterUtil.getString(parameterValue)));
	}

	private DDMDataProviderResponse _createDDMDataProviderResponse(
		DocumentContext documentContext,
		DDMDataProviderRequest ddmDataProviderRequest,
		DDMDataProviderResponseStatus ddmDataProviderResponseStatus,
		DDMRESTDataProviderSettings ddmRESTDataProviderSettings) {

		DDMDataProviderResponse.Builder builder =
			DDMDataProviderResponse.Builder.newBuilder(
			).withStatus(
				ddmDataProviderResponseStatus
			);

		if (ArrayUtil.isEmpty(ddmRESTDataProviderSettings.outputParameters())) {
			return builder.build();
		}

		for (DDMDataProviderOutputParametersSettings
				ddmDataProviderOutputParametersSettings :
					ddmRESTDataProviderSettings.outputParameters()) {

			String outputParameterId =
				ddmDataProviderOutputParametersSettings.outputParameterId();
			String outputParameterPath =
				ddmDataProviderOutputParametersSettings.outputParameterPath();
			String outputParameterType =
				ddmDataProviderOutputParametersSettings.outputParameterType();

			if (StringUtil.equals(outputParameterType, "number") ||
				StringUtil.equals(outputParameterType, "text")) {

				Class<?> clazz = String.class;

				if (StringUtil.equals(outputParameterType, "number")) {
					clazz = Number.class;
				}

				builder = builder.withOutput(
					outputParameterId,
					documentContext.read(
						_normalizePath(outputParameterPath), clazz));

				continue;
			}

			String[] paths = StringUtil.split(
				outputParameterPath, CharPool.SEMICOLON);

			List<?> values = documentContext.read(
				_normalizePath(paths[0]), List.class);

			if (values == null) {
				continue;
			}

			List<?> keys = values;

			if (paths.length >= 2) {
				keys = documentContext.read(_normalizePath(paths[1]));
			}

			List<KeyValuePair> keyValuePairs = new ArrayList<>();

			for (int i = 0; i < values.size(); i++) {
				keyValuePairs.add(
					new KeyValuePair(
						String.valueOf(keys.get(i)),
						String.valueOf(values.get(i))));
			}

			if (!ddmRESTDataProviderSettings.pagination()) {
				builder = builder.withOutput(outputParameterId, keyValuePairs);

				continue;
			}

			int end = GetterUtil.getInteger(
				ddmDataProviderRequest.getParameter(
					"paginationEnd", String.class),
				10);
			int start = GetterUtil.getInteger(
				ddmDataProviderRequest.getParameter(
					"paginationStart", String.class),
				1);

			if (keyValuePairs.size() <= (end - start)) {
				continue;
			}

			builder = builder.withOutput(
				outputParameterId, ListUtil.subList(keyValuePairs, start, end));
		}

		return builder.build();
	}

	private DDMDataProviderResponse _getData(
			DDMDataProviderInstance ddmDataProviderInstance,
			DDMDataProviderRequest ddmDataProviderRequest,
			DDMRESTDataProviderSettings ddmRESTDataProviderSettings)
		throws Exception {

		Map<String, List<String>> parametersMap = new HashMap<>();

		Map<String, Object> ddmDataProviderRequestParameters =
			ddmDataProviderRequest.getParameters();

		for (DDMDataProviderInputParametersSettings
				ddmDataProviderInputParametersSettings :
					ddmRESTDataProviderSettings.inputParameters()) {

			_addParameter(
				ddmDataProviderInputParametersSettings.inputParameterName(),
				parametersMap,
				ddmDataProviderRequestParameters.get(
					ddmDataProviderInputParametersSettings.
						inputParameterName()));
		}

		String url = ddmRESTDataProviderSettings.url();

		Matcher matcher = _pattern.matcher(url);

		while (matcher.find()) {
			String parameterName = matcher.group(1);

			if (!parametersMap.containsKey(parameterName)) {
				continue;
			}

			List<String> parameterValues = parametersMap.get(parameterName);

			url = StringUtil.replaceFirst(
				url, String.format("{%s}", parameterName),
				HtmlUtil.escapeURL(parameterValues.get(0)));

			parametersMap.remove(parameterName);
		}

		Locale locale = ddmDataProviderRequest.getLocale();

		if (locale != null) {
			_addParameter(
				"ddmDataProviderLanguageId", parametersMap,
				LocaleUtil.toLanguageId(locale));
		}

		if (ddmRESTDataProviderSettings.filterable()) {
			_addParameter(
				ddmDataProviderRequest, "filterParameterValue",
				ddmRESTDataProviderSettings.filterParameterName(),
				parametersMap);
		}

		if (ddmRESTDataProviderSettings.pagination()) {
			_addParameter(
				ddmDataProviderRequest, "paginationEnd",
				ddmRESTDataProviderSettings.paginationEndParameterName(),
				parametersMap);
			_addParameter(
				ddmDataProviderRequest, "paginationStart",
				ddmRESTDataProviderSettings.paginationStartParameterName(),
				parametersMap);
		}

		URI uri = new URI(url);

		for (String queryParameter :
				StringUtil.split(uri.getQuery(), StringPool.AMPERSAND)) {

			String[] queryParameterParts = StringUtil.split(
				queryParameter, StringPool.EQUAL);

			String queryParameterValue = StringPool.BLANK;

			if (queryParameterParts.length > 1) {
				queryParameterValue = queryParameterParts[1];
			}

			List<String> queryParameterValues = parametersMap.computeIfAbsent(
				queryParameterParts[0], key -> new ArrayList<>());

			queryParameterValues.add(queryParameterValue);
		}

		String absoluteURL = url;

		if (uri.getQuery() != null) {
			absoluteURL = StringUtil.removeLast(
				absoluteURL, StringPool.QUESTION + uri.getQuery());
		}

		String portalCacheKey = String.format(
			"%s@%s?", ddmDataProviderRequest.getDDMDataProviderId(),
			absoluteURL);

		for (Map.Entry<String, List<String>> entry : parametersMap.entrySet()) {
			for (String value : entry.getValue()) {
				portalCacheKey = portalCacheKey.concat(
					String.format("%s=%s&", entry.getKey(), value));
			}
		}

		portalCacheKey = StringUtil.removeLast(
			portalCacheKey, StringPool.AMPERSAND);

		DDMDataProviderResponse ddmDataProviderResponse = _portalCache.get(
			portalCacheKey);

		if ((ddmDataProviderResponse != null) &&
			ddmRESTDataProviderSettings.cacheable()) {

			return ddmDataProviderResponse;
		}

		String password = _secretResolver.resolve(
			ddmDataProviderInstance.getCompanyId(),
			StringBundler.concat(
				DDMDataProviderInstance.class.getSimpleName(), StringPool.SLASH,
				ddmDataProviderInstance.getCompanyId(), StringPool.SLASH,
				ddmDataProviderInstance.getDataProviderInstanceId(),
				"/password"),
			ddmRESTDataProviderSettings.password());

		JSONWebServiceClient jsonWebServiceClient =
			_jsonWebServiceClientFactory.getInstance(
				HashMapBuilder.<String, Object>put(
					"hostName",
					() -> {
						String host = uri.getHost();

						if (StringUtil.startsWith(host, "www.")) {
							return host.substring(4);
						}

						return host;
					}
				).put(
					"hostPort",
					() -> {
						if (uri.getPort() != -1) {
							return uri.getPort();
						}

						if (StringUtil.equals(uri.getScheme(), Http.HTTPS)) {
							return Http.HTTPS_PORT;
						}

						return Http.HTTP_PORT;
					}
				).put(
					"keyStore",
					() -> {
						KeyStore keyStore = KeyStore.getInstance(
							KeyStore.getDefaultType());

						keyStore.load(null);

						return keyStore;
					}
				).put(
					"login", ddmRESTDataProviderSettings.username()
				).put(
					"password", password
				).put(
					"protocol", uri.getScheme()
				).put(
					"trustSelfSignedCertificates",
					_ddmDataProviderConfiguration.trustSelfSignedCertificates()
				).putAll(
					getProxySettingsMap(uri.getHost())
				).build(),
				false);

		String response;

		try {
			List<String> parameters = new ArrayList<>();

			for (Map.Entry<String, List<String>> entry :
					parametersMap.entrySet()) {

				for (String value : entry.getValue()) {
					parameters.add(entry.getKey());
					parameters.add(value);
				}
			}

			response = jsonWebServiceClient.doGet(
				absoluteURL, ArrayUtil.toStringArray(parameters));
		}
		finally {
			jsonWebServiceClient.destroy();
		}

		ddmDataProviderResponse = _createDDMDataProviderResponse(
			JsonPath.parse(
				IOUtils.toString(
					new BOMInputStream(
						new ByteArrayInputStream(response.getBytes())),
					StandardCharsets.UTF_8)),
			ddmDataProviderRequest, DDMDataProviderResponseStatus.OK,
			ddmRESTDataProviderSettings);

		if (ddmRESTDataProviderSettings.cacheable()) {
			_portalCache.put(portalCacheKey, ddmDataProviderResponse);
		}

		return ddmDataProviderResponse;
	}

	private String _normalizePath(String path) {
		if (StringUtil.startsWith(path, StringPool.DOLLAR) ||
			StringUtil.startsWith(path, StringPool.PERIOD) ||
			StringUtil.startsWith(path, StringPool.STAR)) {

			return path;
		}

		return StringPool.PERIOD.concat(path);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMRESTDataProvider.class);

	private static final Pattern _pattern = Pattern.compile("\\{(.+?)\\}");

	private volatile DDMDataProviderConfiguration _ddmDataProviderConfiguration;

	@Reference
	private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

	@Reference
	private DDMDataProviderInstanceSettings _ddmDataProviderInstanceSettings;

	@Reference(target = "(ddm.data.provider.type=rest)")
	private DDMDataProviderSettingsProvider _ddmDataProviderSettingsProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONWebServiceClientFactory _jsonWebServiceClientFactory;

	@Reference
	private MultiVMPool _multiVMPool;

	private volatile PortalCache<String, DDMDataProviderResponse> _portalCache;

	@Reference
	private SecretResolver _secretResolver;

}