/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.connection;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyStore;

import java.util.Arrays;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * @author André de Oliveira
 */
public class RestClientTransportFactory {

	public RestClientTransport newRestClientTransport() {
		RestClientBuilder restClientBuilder = RestClient.builder(
			_httpHosts
		).setFailureListener(
			new RestClient.FailureListener() {

				@Override
				public void onFailure(Node node) {
					if (_log.isWarnEnabled()) {
						_log.warn("Marked node as unavailable: " + node);
					}
				}

			}
		).setCompressionEnabled(
			_compressionEnabled
		).setHttpClientConfigCallback(
			this::_customizeHttpClient
		).setRequestConfigCallback(
			this::_customizeRequestConfig
		);

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				getClass().getClassLoader())) {

			return new RestClientTransport(
				restClientBuilder.build(), new JacksonJsonpMapper());
		}
	}

	public static class Builder {

		public Builder(String[] networkHostAddresses) {
			_httpHosts = TransformUtil.transform(
				networkHostAddresses, HttpHost::create, HttpHost.class);
		}

		public Builder authenticationEnabled(boolean authenticationEnabled) {
			_authenticationEnabled = authenticationEnabled;

			return this;
		}

		public RestClientTransportFactory build() {
			return new RestClientTransportFactory(this);
		}

		public Builder compressionEnabled(boolean compressionEnabled) {
			_compressionEnabled = compressionEnabled;

			return this;
		}

		public Builder httpSSLEnabled(boolean httpSSLEnabled) {
			_httpSSLEnabled = httpSSLEnabled;

			return this;
		}

		public Builder maxConnections(int maxConnections) {
			_maxConnections = maxConnections;

			return this;
		}

		public Builder maxConnectionsPerRoute(int maxConnectionsPerRoute) {
			_maxConnectionsPerRoute = maxConnectionsPerRoute;

			return this;
		}

		public Builder password(String password) {
			_password = password;

			return this;
		}

		public Builder proxyConfig(ProxyConfig proxyConfig) {
			_proxyConfig = proxyConfig;

			return this;
		}

		public Builder truststorePassword(String truststorePassword) {
			_truststorePassword = truststorePassword;

			return this;
		}

		public Builder truststorePath(String truststorePath) {
			_truststorePath = truststorePath;

			return this;
		}

		public Builder truststoreType(String truststoreType) {
			_truststoreType = truststoreType;

			return this;
		}

		public Builder userName(String userName) {
			_userName = userName;

			return this;
		}

		private boolean _authenticationEnabled;
		private boolean _compressionEnabled;
		private HttpHost[] _httpHosts;
		private boolean _httpSSLEnabled;
		private int _maxConnections;
		private int _maxConnectionsPerRoute;
		private String _password;
		private ProxyConfig _proxyConfig;
		private String _truststorePassword;
		private String _truststorePath;
		private String _truststoreType;
		private String _userName;

	}

	private RestClientTransportFactory(Builder builder) {
		_authenticationEnabled = builder._authenticationEnabled;
		_compressionEnabled = builder._compressionEnabled;
		_httpSSLEnabled = builder._httpSSLEnabled;
		_maxConnections = builder._maxConnections;
		_maxConnectionsPerRoute = builder._maxConnectionsPerRoute;
		_httpHosts = builder._httpHosts;
		_password = builder._password;
		_proxyConfig = builder._proxyConfig;
		_truststorePassword = builder._truststorePassword;
		_truststorePath = builder._truststorePath;
		_truststoreType = builder._truststoreType;
		_userName = builder._userName;
	}

	private CredentialsProvider _createCredentialsProvider() {
		CredentialsProvider credentialsProvider =
			new BasicCredentialsProvider();

		if (_proxyConfig.shouldApplyCredentials()) {
			credentialsProvider.setCredentials(
				new AuthScope(_proxyConfig.getHost(), _proxyConfig.getPort()),
				new UsernamePasswordCredentials(
					_proxyConfig.getUserName(), _proxyConfig.getPassword()));
		}

		credentialsProvider.setCredentials(
			AuthScope.ANY,
			new UsernamePasswordCredentials(_userName, _password));

		return credentialsProvider;
	}

	private SSLContext _createSSLContext() {
		char[] truststorePasswordChars = _truststorePassword.toCharArray();

		try {
			Path path = Paths.get(_truststorePath);

			InputStream inputStream = Files.newInputStream(path);

			KeyStore keyStore = KeyStore.getInstance(_truststoreType);

			keyStore.load(inputStream, truststorePasswordChars);

			SSLContextBuilder sslContextBuilder = SSLContexts.custom();

			sslContextBuilder.loadKeyMaterial(
				keyStore, truststorePasswordChars);
			sslContextBuilder.loadTrustMaterial(keyStore, null);

			return sslContextBuilder.build();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
		finally {
			Arrays.fill(truststorePasswordChars, '\0');
		}
	}

	private HttpAsyncClientBuilder _customizeHttpClient(
		HttpAsyncClientBuilder httpAsyncClientBuilder) {

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		if (_authenticationEnabled) {
			httpClientBuilder.setDefaultCredentialsProvider(
				_createCredentialsProvider());
		}

		if (_httpSSLEnabled) {
			httpClientBuilder.setSSLContext(_createSSLContext());
		}

		if ((_proxyConfig != null) && _proxyConfig.shouldApplyConfig()) {
			httpClientBuilder.setProxy(
				new HttpHost(
					_proxyConfig.getHost(), _proxyConfig.getPort(), "http"));
		}

		httpClientBuilder.disableAutomaticRetries();
		httpClientBuilder.disableConnectionState();
		httpClientBuilder.disableContentCompression();
		httpClientBuilder.disableCookieManagement();
		httpClientBuilder.disableDefaultUserAgent();
		httpClientBuilder.disableRedirectHandling();

		httpClientBuilder.setMaxConnPerRoute(_maxConnectionsPerRoute);
		httpClientBuilder.setMaxConnTotal(_maxConnections);

		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		CloseableHttpAsyncClient closeableHttpAsyncClient =
			new CloseableHttpAsyncClient() {

				@Override
				public void close() throws IOException {
					closeableHttpClient.close();
				}

				@Override
				public <T> Future<T> execute(
					HttpAsyncRequestProducer httpAsyncRequestProducer,
					HttpAsyncResponseConsumer<T> httpAsyncResponseConsumer,
					HttpContext httpContext, FutureCallback<T> futureCallback) {

					BasicFuture<T> basicFuture = new BasicFuture<>(
						futureCallback);

					try (CloseableHttpResponse closeableHttpResponse =
							closeableHttpClient.execute(
								httpAsyncRequestProducer.getTarget(),
								httpAsyncRequestProducer.generateRequest(),
								httpContext)) {

						HttpEntity httpEntity =
							closeableHttpResponse.getEntity();

						if (httpEntity != null) {
							closeableHttpResponse.setEntity(
								new BufferedHttpEntity(httpEntity));
						}

						basicFuture.completed((T)closeableHttpResponse);
					}
					catch (Exception exception) {
						basicFuture.failed(exception);
					}

					return basicFuture;
				}

				@Override
				public boolean isRunning() {
					return true;
				}

				@Override
				public void start() {
				}

			};

		return new HttpAsyncClientBuilder() {

			@Override
			public CloseableHttpAsyncClient build() {
				return closeableHttpAsyncClient;
			}

		};
	}

	private RequestConfig.Builder _customizeRequestConfig(
		RequestConfig.Builder requestConfigBuilder) {

		return requestConfigBuilder.setSocketTimeout(120000);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RestClientTransportFactory.class);

	private final boolean _authenticationEnabled;
	private final boolean _compressionEnabled;
	private final HttpHost[] _httpHosts;
	private final boolean _httpSSLEnabled;
	private final int _maxConnections;
	private final int _maxConnectionsPerRoute;
	private final String _password;
	private final ProxyConfig _proxyConfig;
	private final String _truststorePassword;
	private final String _truststorePath;
	private final String _truststoreType;
	private final String _userName;

}