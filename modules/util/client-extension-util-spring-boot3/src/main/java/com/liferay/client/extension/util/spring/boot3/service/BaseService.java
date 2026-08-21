/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util.spring.boot3.service;

import java.net.URI;

import java.time.Duration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.logging.Log;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Nilton Vieira
 */
public abstract class BaseService {

	protected Disposable asyncDelete(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).method(
			HttpMethod.DELETE
		).uri(
			_getAbsoluteURI(uri)
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).bodyValue(
			body
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).subscribe();
	}

	protected Disposable asyncDelete(
		String authorization, String body, URI uri) {

		return asyncDelete(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected Disposable asyncGet(Map<String, String> httpHeadersMap, URI uri) {
		return _getWebClient(
		).get(
		).uri(
			_getAbsoluteURI(uri)
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).subscribe();
	}

	protected Disposable asyncGet(String authorization, URI uri) {
		return asyncGet(
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected Disposable asyncPatch(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).patch(
		).uri(
			_getAbsoluteURI(uri)
		).bodyValue(
			body
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).subscribe();
	}

	protected Disposable asyncPatch(
		String authorization, String body, URI uri) {

		return asyncPatch(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected Disposable asyncPost(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).post(
		).uri(
			_getAbsoluteURI(uri)
		).bodyValue(
			body
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).subscribe();
	}

	protected Disposable asyncPost(String authorization, String body, URI uri) {
		return asyncPost(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected Disposable asyncPut(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).put(
		).uri(
			_getAbsoluteURI(uri)
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).bodyValue(
			body
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).subscribe();
	}

	protected Disposable asyncPut(String authorization, String body, URI uri) {
		return asyncPut(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected String delete(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).method(
			HttpMethod.DELETE
		).uri(
			_getAbsoluteURI(uri)
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).bodyValue(
			body
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).block();
	}

	protected String delete(String authorization, String body, URI uri) {
		return delete(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected String get(Map<String, String> httpHeadersMap, URI uri) {
		return _getWebClient(
		).get(
		).uri(
			_getAbsoluteURI(uri)
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).block();
	}

	protected String get(String authorization, URI uri) {
		return get(
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected ExchangeFilterFunction getWebClientExchangeFilterFunction() {
		return (clientRequest, exchangeFunction) -> exchangeFunction.exchange(
			clientRequest);
	}

	protected void log(Jwt jwt, Log log) {
		if (log.isInfoEnabled()) {
			log.info("JWT Claims: " + jwt.getClaims());
			log.info("JWT ID: " + jwt.getId());
			log.info("JWT Subject: " + jwt.getSubject());
		}
	}

	protected void log(Jwt jwt, Log log, Map<String, String> parameters) {
		if (log.isInfoEnabled()) {
			log.info("JWT Claims: " + jwt.getClaims());
			log.info("JWT ID: " + jwt.getId());
			log.info("JWT Subject: " + jwt.getSubject());
			log.info("Parameters: " + parameters);
		}
	}

	protected void log(Jwt jwt, Log log, String json) {
		if (log.isInfoEnabled()) {
			try {
				JSONObject jsonObject = new JSONObject(json);

				log.info("JSON: " + jsonObject.toString(4));
			}
			catch (Exception exception) {
				log.error("JSON: " + json, exception);
			}

			log.info("JWT Claims: " + jwt.getClaims());
			log.info("JWT ID: " + jwt.getId());
			log.info("JWT Subject: " + jwt.getSubject());
		}
	}

	protected String patch(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).patch(
		).uri(
			_getAbsoluteURI(uri)
		).bodyValue(
			body
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).block();
	}

	protected String patch(String authorization, String body, URI uri) {
		return patch(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected String post(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).post(
		).uri(
			_getAbsoluteURI(uri)
		).bodyValue(
			body
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).block();
	}

	protected String post(String authorization, String body, URI uri) {
		return post(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	protected String put(
		String body, Map<String, String> httpHeadersMap, URI uri) {

		return _getWebClient(
		).put(
		).uri(
			_getAbsoluteURI(uri)
		).headers(
			_getHttpHeadersConsumer(httpHeadersMap)
		).bodyValue(
			body
		).exchangeToMono(
			_getExchangeToMonoFunction()
		).block();
	}

	protected String put(String authorization, String body, URI uri) {
		return put(
			body,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			uri);
	}

	@Value("${com.liferay.lxc.dxp.mainDomain}")
	protected String lxcDXPMainDomain;

	@Value("${com.liferay.lxc.dxp.server.protocol}")
	protected String lxcDXPServerProtocol;

	private URI _getAbsoluteURI(URI uri) {
		if (uri.isAbsolute()) {
			return uri;
		}

		URI baseURI = URI.create(
			lxcDXPServerProtocol + "://" + lxcDXPMainDomain);

		return baseURI.resolve(uri);
	}

	private Function<ClientResponse, Mono<String>>
		_getExchangeToMonoFunction() {

		return clientResponse -> {
			HttpStatusCode httpStatusCode = clientResponse.statusCode();

			if (Objects.equals(httpStatusCode, HttpStatus.NO_CONTENT)) {
				return Mono.just("{}");
			}
			else if (httpStatusCode.is2xxSuccessful()) {
				return clientResponse.bodyToMono(String.class);
			}

			Mono<WebClientResponseException> mono =
				clientResponse.createException();

			return mono.flatMap(Mono::error);
		};
	}

	private Consumer<HttpHeaders> _getHttpHeadersConsumer(
		Map<String, String> httpHeadersMap) {

		if (httpHeadersMap == null) {
			return httpHeadersConsumer -> httpHeadersConsumer.setAll(
				new HashMap<>());
		}

		return httpHeadersConsumer -> httpHeadersConsumer.setAll(
			httpHeadersMap);
	}

	private WebClient _getWebClient() {
		ConnectionProvider connectionProvider = ConnectionProvider.builder(
			"fixed"
		).evictInBackground(
			Duration.ofSeconds(120)
		).maxConnections(
			500
		).maxIdleTime(
			Duration.ofSeconds(20)
		).maxLifeTime(
			Duration.ofSeconds(60)
		).pendingAcquireTimeout(
			Duration.ofSeconds(60)
		).build();

		return WebClient.builder(
		).clientConnector(
			new ReactorClientHttpConnector(
				HttpClient.create(connectionProvider))
		).defaultHeader(
			HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
		).defaultHeader(
			HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
		).exchangeStrategies(
			ExchangeStrategies.builder(
			).codecs(
				clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs(
				).maxInMemorySize(
					16 * 1024 * 1024
				)
			).build()
		).filter(
			getWebClientExchangeFilterFunction()
		).build();
	}

}