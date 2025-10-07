/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.osb.faro.engine.client.cache.FaroCache;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.http.client.AuditClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.http.client.AuthenticationClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.http.client.AuthorClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.http.client.CacheClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.http.client.LoggingClientHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.http.client.SSLHandshakeExceptionHttpRequestInterceptor;
import com.liferay.osb.faro.engine.client.mixin.ResourceMixin;
import com.liferay.osb.faro.engine.client.model.BulkRequest;
import com.liferay.osb.faro.engine.client.model.Rels;
import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.engine.client.web.client.ResponseErrorHandler;
import com.liferay.osb.faro.engine.client.web.util.UriTemplateHandler;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Matthew Kong
 * @author Shinn Lok
 */
public abstract class BaseEngineClient {

	public void setEngineURL(String engineURL) {
		_engineURL = engineURL;

		_urlPaths.clear();
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> bulk(
		FaroProject faroProject, String type, HttpMethod httpMethod,
		List<Object> bodies, TypeReference<T> typeReference,
		List<Map<String, Object>> uriVariablesList) {

		List<T> responses = new ArrayList<>();

		BulkRequest bulkRequest = new BulkRequest();

		List<BulkRequest.Operation> operations = new ArrayList<>();

		String templatedURL = getTemplatedURL(faroProject, type);

		RestTemplate restTemplate = getRestTemplate(faroProject);

		UriTemplateHandler uriTemplateHandler =
			(UriTemplateHandler)restTemplate.getUriTemplateHandler();

		for (int i = 0; i < uriVariablesList.size(); i++) {
			URI uri = uriTemplateHandler.expand(
				templatedURL, uriVariablesList.get(i));

			Object body = null;

			if (ListUtil.isNotEmpty(bodies)) {
				body = bodies.get(i);
			}

			String url = uri.getPath();

			if (Validator.isNotNull(uri.getQuery())) {
				url += StringPool.QUESTION + uri.getQuery();
			}

			operations.add(
				new BulkRequest.Operation(body, httpMethod.name(), url));
		}

		bulkRequest.setOperations(operations);

		ResponseEntity<Object> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, Rels.BULK), HttpMethod.POST,
			new HttpEntity<>(bulkRequest), Object.class,
			getUriVariables(faroProject));

		Map<String, Object> responseBody =
			(Map<String, Object>)responseEntity.getBody();

		if (responseBody != null) {
			for (Object result : (List<Object>)responseBody.get("responses")) {
				try {
					String resultString = objectMapper.writeValueAsString(
						result);

					if (resultString.startsWith(StringPool.OPEN_BRACKET)) {
						responses.add(
							objectMapper.readValue(
								resultString, typeReference));

						continue;
					}

					Map<String, Map<String, Object>> resultMap =
						(Map<String, Map<String, Object>>)result;

					Map<String, Object> embeddedObject = resultMap.get(
						"_embedded");

					if (embeddedObject == null) {
						responses.add(
							objectMapper.readValue(
								resultString, typeReference));

						continue;
					}

					Collection<Object> collection = embeddedObject.values();

					Iterator<Object> iterator = collection.iterator();

					if (!iterator.hasNext()) {
						continue;
					}

					responses.add(
						objectMapper.readValue(
							objectMapper.writeValueAsString(iterator.next()),
							typeReference));
				}
				catch (Exception exception) {
					_log.error(exception);

					responses.add(null);
				}
			}
		}

		return responses;
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> bulk(
		FaroProject faroProject, String type, HttpMethod httpMethod,
		TypeReference<T> typeReference,
		List<Map<String, Object>> uriVariablesList) {

		return bulk(
			faroProject, type, httpMethod, new ArrayList<>(), typeReference,
			uriVariablesList);
	}

	protected HttpHeaders createHttpHeaders(Map<String, String> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			httpHeaders.set(entry.getKey(), entry.getValue());
		}

		return httpHeaders;
	}

	protected void delete(
			FaroProject faroProject, Map<String, List<String>> queryParameters)
		throws Exception {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		restTemplate.delete(
			getUriString(
				faroProject, "/projects/" + faroProject.getProjectId(),
				queryParameters));
	}

	protected void delete(
			FaroProject faroProject, String type, List<String> ids)
		throws FaroEngineClientException {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		restTemplate.exchange(
			getTemplatedURL(faroProject, type), HttpMethod.DELETE,
			new HttpEntity<>(ids), String.class, getUriVariables(faroProject));
	}

	protected void delete(
			FaroProject faroProject, String type,
			Map<String, Object> uriVariables)
		throws FaroEngineClientException {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		restTemplate.delete(getTemplatedURL(faroProject, type), uriVariables);
	}

	protected void delete(
			FaroProject faroProject, String type, Object object,
			Map<String, Object> uriVariables)
		throws FaroEngineClientException {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		restTemplate.exchange(
			getTemplatedURL(faroProject, type), HttpMethod.DELETE,
			new HttpEntity<>(object), String.class, uriVariables);
	}

	protected void delete(FaroProject faroProject, String type, String id)
		throws FaroEngineClientException {

		if (Validator.isNull(id)) {
			throw new FaroEngineClientException();
		}

		RestTemplate restTemplate = getRestTemplate(faroProject);

		restTemplate.delete(
			getTemplatedURL(faroProject, type),
			getUriVariables(faroProject, id));
	}

	protected <T> T get(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Class<T> responseType,
			Map<String, Object> uriVariables)
		throws Exception {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.exchange(
			getUriString(faroProject, path, queryParameters), HttpMethod.GET,
			new HttpEntity(createHttpHeaders(headers)), responseType,
			uriVariables);

		return responseEntity.getBody();
	}

	protected <T> T get(
		FaroProject faroProject, String type,
		ParameterizedTypeReference<T> parameterizedTypeReference,
		Map<String, Object> uriVariables) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, type), HttpMethod.GET,
			HttpEntity.EMPTY, parameterizedTypeReference, uriVariables);

		return responseEntity.getBody();
	}

	protected <T> T get(
			FaroProject faroProject, String type, String id,
			Class<T> responseType)
		throws FaroEngineClientException {

		return get(
			faroProject, type, id, responseType,
			getUriVariables(faroProject, id));
	}

	protected <T> T get(
			FaroProject faroProject, String type, String id,
			Class<T> responseType, Map<String, Object> uriVariables)
		throws FaroEngineClientException {

		if (Validator.isNull(id)) {
			throw new FaroEngineClientException();
		}

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.getForEntity(
			getTemplatedURL(faroProject, type), responseType, uriVariables);

		return responseEntity.getBody();
	}

	protected Cache getCache() {
		if (!FaroThreadLocal.isCacheEnabled()) {
			return null;
		}

		Cache cache = (Cache)FaroThreadLocal.getCache();

		if (cache != null) {
			return cache;
		}

		cache = new FaroCache();

		FaroThreadLocal.setCache(cache);

		return cache;
	}

	protected String getEngineURL(FaroProject faroProject) {
		if (Validator.isNotNull(_engineURL)) {
			return _engineURL;
		}

		try {
			return EngineServiceURLUtil.getBackendExternalURL(faroProject);
		}
		catch (URISyntaxException uriSyntaxException) {
			_log.error(uriSyntaxException);
		}

		return FaroPropsValues.OSB_ASAH_BACKEND_URL;
	}

	protected RestTemplate getRestTemplate(FaroProject faroProject) {
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

		restTemplateBuilder = restTemplateBuilder.uriTemplateHandler(
			new UriTemplateHandler());

		MappingJackson2HttpMessageConverter
			mappingJackson2HttpMessageConverter =
				new MappingJackson2HttpMessageConverter();

		mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
			Arrays.asList(MediaType.APPLICATION_JSON, MediaTypes.HAL_JSON));

		restTemplateBuilder = restTemplateBuilder.messageConverters(
			mappingJackson2HttpMessageConverter);

		RestTemplate restTemplate = restTemplateBuilder.build();

		restTemplate.setErrorHandler(new ResponseErrorHandler());

		List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors =
			new ArrayList<>();

		clientHttpRequestInterceptors.add(
			new AuditClientHttpRequestInterceptor());
		clientHttpRequestInterceptors.add(
			new AuthenticationClientHttpRequestInterceptor(faroProject));
		clientHttpRequestInterceptors.add(
			new AuthorClientHttpRequestInterceptor());

		Cache cache = getCache();

		if (cache != null) {
			clientHttpRequestInterceptors.add(
				new CacheClientHttpRequestInterceptor(cache));
		}

		clientHttpRequestInterceptors.add(
			new LoggingClientHttpRequestInterceptor());
		clientHttpRequestInterceptors.add(
			new SSLHandshakeExceptionHttpRequestInterceptor());

		restTemplate.setInterceptors(clientHttpRequestInterceptors);

		restTemplate.setRequestFactory(
			new HttpComponentsClientHttpRequestFactory() {

				@Override
				protected HttpUriRequest createHttpUriRequest(
					HttpMethod httpMethod, URI uri) {

					if (httpMethod == HttpMethod.GET) {
						return new HttpEntityEnclosingRequestBase() {
							{
								setURI(uri);
							}

							@Override
							public String getMethod() {
								return HttpMethod.GET.name();
							}

						};
					}

					return super.createHttpUriRequest(httpMethod, uri);
				}

			});

		return restTemplate;
	}

	protected String getTemplatedURL(FaroProject faroProject, String type) {
		String engineURL = getEngineURL(faroProject);

		if (_urlPaths.containsKey(type)) {
			return engineURL + _urlPaths.get(type);
		}

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<EntityModel<?>> responseEntity = restTemplate.exchange(
			engineURL, HttpMethod.GET, null,
			new ParameterizedTypeReference<EntityModel<?>>() {
			},
			getUriVariables(faroProject));

		if (responseEntity.getStatusCodeValue() != HttpStatus.SC_OK) {
			throw new IllegalStateException("Invalid url: " + engineURL);
		}

		EntityModel<?> resource = responseEntity.getBody();

		if (resource == null) {
			return null;
		}

		Link link = resource.getRequiredLink(type);

		if (link == null) {
			throw new IllegalArgumentException(
				"URL does not exist for type: " + type);
		}

		String href = link.getHref();

		_urlPaths.put(
			type,
			StringUtil.removeSubstring(
				href,
				StringUtil.replace(
					engineURL, "{weDeployKey}", faroProject.getWeDeployKey())));

		return href;
	}

	protected String getUriString(
			FaroProject faroProject, String path,
			Map<String, List<String>> queryParameters)
		throws Exception {

		UriComponentsBuilder uriComponentsBuilder =
			UriComponentsBuilder.fromHttpUrl(
				EngineServiceURLUtil.getBackendURL(faroProject, path));

		for (Map.Entry<String, List<String>> entry :
				queryParameters.entrySet()) {

			List<String> value = entry.getValue();

			uriComponentsBuilder.queryParam(
				entry.getKey(), value.toArray(new String[0]));
		}

		return uriComponentsBuilder.toUriString();
	}

	protected Map<String, Object> getUriVariables(FaroProject faroProject) {
		return HashMapBuilder.<String, Object>put(
			"weDeployKey", faroProject.getWeDeployKey()
		).build();
	}

	protected Map<String, Object> getUriVariables(
		FaroProject faroProject, int start, int end) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("end", end);
		uriVariables.put("start", start);

		return uriVariables;
	}

	protected Map<String, Object> getUriVariables(
		FaroProject faroProject, int cur, int delta,
		List<OrderByField> orderByFields) {

		return getUriVariables(
			faroProject, cur, delta, null, orderByFields, null);
	}

	protected Map<String, Object> getUriVariables(
		FaroProject faroProject, int cur, int delta,
		List<OrderByField> orderByFields, String fieldNameContext) {

		return getUriVariables(
			faroProject, cur, delta, null, orderByFields, fieldNameContext);
	}

	protected Map<String, Object> getUriVariables(
		FaroProject faroProject, int cur, int delta, List<String> ids,
		List<OrderByField> orderByFields) {

		return getUriVariables(
			faroProject, cur, delta, ids, orderByFields, null);
	}

	protected Map<String, Object> getUriVariables(
		FaroProject faroProject, int cur, int delta, List<String> ids,
		List<OrderByField> orderByFields, String fieldNameContext) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		int page = cur - 1;

		if (page < 0) {
			page = 0;
		}

		uriVariables.put("page", page);

		uriVariables.put("size", delta);

		if (ids != null) {
			uriVariables.put("ids", ids);
		}

		if (orderByFields != null) {
			uriVariables.put(
				"sort",
				TransformUtil.transform(
					orderByFields,
					orderByField -> {
						String fieldName = orderByField.getFieldName();

						if (!orderByField.isSystem() &&
							(fieldNameContext != null)) {

							fieldName = StringUtil.replace(
								fieldNameContext, CharPool.QUESTION, fieldName);
						}

						return fieldName + StringPool.COMMA +
							orderByField.getOrderBy();
					}));
		}

		return uriVariables;
	}

	protected Map<String, Object> getUriVariables(
		FaroProject faroProject, String id) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("id", id);

		return uriVariables;
	}

	protected <T> T patch(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Object requestBody,
			Class<T> responseType, Map<String, Object> uriVariables)
		throws Exception {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.exchange(
			getUriString(faroProject, path, queryParameters), HttpMethod.PATCH,
			new HttpEntity<>(requestBody, createHttpHeaders(headers)),
			responseType, uriVariables);

		return responseEntity.getBody();
	}

	protected <T> T patch(
		FaroProject faroProject, String type, Object object,
		Class<T> responseType, Map<String, Object> uriVariables) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		return restTemplate.patchForObject(
			getTemplatedURL(faroProject, type), object, responseType,
			uriVariables);
	}

	protected <T> T patch(
		FaroProject faroProject, String type, String id, Object object,
		Class<T> responseType) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		return restTemplate.patchForObject(
			getTemplatedURL(faroProject, type), object, responseType,
			getUriVariables(faroProject, id));
	}

	protected <T> T post(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Object requestBody,
			Class<T> responseType, Map<String, Object> uriVariables)
		throws Exception {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.exchange(
			getUriString(faroProject, path, queryParameters), HttpMethod.POST,
			new HttpEntity<>(requestBody, createHttpHeaders(headers)),
			responseType, uriVariables);

		return responseEntity.getBody();
	}

	protected <T> T post(
		FaroProject faroProject, String type, Object object,
		Class<T> responseType) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.postForEntity(
			getTemplatedURL(faroProject, type), object, responseType,
			getUriVariables(faroProject));

		return responseEntity.getBody();
	}

	protected <T> T post(
		FaroProject faroProject, String type, Object object,
		Class<T> responseType, Map<String, Object> uriVariables) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.postForEntity(
			getTemplatedURL(faroProject, type), object, responseType,
			uriVariables);

		return responseEntity.getBody();
	}

	protected <T> T post(
		FaroProject faroProject, String type, Object object,
		ParameterizedTypeReference<T> responseType,
		Map<String, Object> uriVariables) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, type), HttpMethod.POST,
			new HttpEntity<>((T)object), responseType, uriVariables);

		return responseEntity.getBody();
	}

	protected <T> T post(
			FaroProject faroProject, String type, String id,
			Class<T> responseType)
		throws FaroEngineClientException {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.postForEntity(
			getTemplatedURL(faroProject, type), null, responseType,
			getUriVariables(faroProject, id));

		return responseEntity.getBody();
	}

	protected <T> T put(
		FaroProject faroProject, String type, Object object,
		Class<T> responseType, Map<String, Object> uriVariables) {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<T> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, type), HttpMethod.PUT,
			new HttpEntity<>((T)object), responseType, uriVariables);

		return responseEntity.getBody();
	}

	protected static final ObjectMapper objectMapper = new ObjectMapper() {
		{
			addMixIn(EntityModel.class, ResourceMixin.class);
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			registerModule(new Jackson2HalModule());
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		BaseEngineClient.class);

	private static final Map<String, String> _urlPaths = new HashMap<>();

	private String _engineURL;

}