/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.connection;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyStore;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.opensearch.client.transport.httpclient5.internal.Node;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class OpenSearchConnection {

	public OpenSearchConnection(OpenSearchConnection openSearchConnectionImpl) {
		_active = openSearchConnectionImpl._active;
		_authenticationEnabled =
			openSearchConnectionImpl._authenticationEnabled;
		_compressionEnabled = openSearchConnectionImpl._compressionEnabled;
		_connectionId = openSearchConnectionImpl._connectionId;
		_httpSSLEnabled = openSearchConnectionImpl._httpSSLEnabled;
		_maxConnections = openSearchConnectionImpl._maxConnections;
		_maxConnectionsPerRoute =
			openSearchConnectionImpl._maxConnectionsPerRoute;
		_networkHostAddresses = openSearchConnectionImpl._networkHostAddresses;
		_password = openSearchConnectionImpl._password;
		_postCloseRunnable = openSearchConnectionImpl._postCloseRunnable;
		_preConnectOpenSearchConnectionConsumer =
			openSearchConnectionImpl._preConnectOpenSearchConnectionConsumer;
		_proxyConfig = openSearchConnectionImpl._proxyConfig;
		_truststorePassword = openSearchConnectionImpl._truststorePassword;
		_truststorePath = openSearchConnectionImpl._truststorePath;
		_truststoreType = openSearchConnectionImpl._truststoreType;
		_userName = openSearchConnectionImpl._userName;
	}

	public void close() {
		try {
			if (_openSearchClient == null) {
				return;
			}

			try {
				_openSearchTransport.close();
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			_openSearchClient = null;
		}
		finally {
			if (_postCloseRunnable != null) {
				_postCloseRunnable.run();
			}
		}
	}

	public void connect() {
		if (!_active) {
			if (_log.isWarnEnabled()) {
				_log.warn("Connecting inactive connection");
			}
		}

		if (_preConnectOpenSearchConnectionConsumer != null) {
			_preConnectOpenSearchConnectionConsumer.accept(this);
		}

		_openSearchClient = _createOpenSearchClient();
	}

	public String getConnectionId() {
		return _connectionId;
	}

	public JsonpMapper getJsonpMapper() {
		return _openSearchTransport.jsonpMapper();
	}

	public OpenSearchClient getOpenSearchClient() {
		return _openSearchClient;
	}

	public boolean isActive() {
		return _active;
	}

	public boolean isConnected() {
		if (_openSearchClient != null) {
			return true;
		}

		return false;
	}

	public static class Builder {

		public Builder active(boolean active) {
			_openSearchConnection._active = active;

			return this;
		}

		public Builder authenticationEnabled(boolean authenticationEnabled) {
			_openSearchConnection._authenticationEnabled =
				authenticationEnabled;

			return this;
		}

		public OpenSearchConnection build() {
			return new OpenSearchConnection(_openSearchConnection);
		}

		public Builder compressionEnabled(boolean compressionEnabled) {
			_openSearchConnection._compressionEnabled = compressionEnabled;

			return this;
		}

		public Builder connectionId(String connectionId) {
			_openSearchConnection._connectionId = connectionId;

			return this;
		}

		public Builder httpSSLEnabled(boolean httpSSLEnabled) {
			_openSearchConnection._httpSSLEnabled = httpSSLEnabled;

			return this;
		}

		public Builder maxConnections(int maxConnections) {
			_openSearchConnection._maxConnections = maxConnections;

			return this;
		}

		public Builder maxConnectionsPerRoute(int maxConnectionsPerRoute) {
			_openSearchConnection._maxConnectionsPerRoute =
				maxConnectionsPerRoute;

			return this;
		}

		public Builder networkHostAddresses(String[] networkHostAddresses) {
			_openSearchConnection._networkHostAddresses = networkHostAddresses;

			return this;
		}

		public Builder password(String password) {
			_openSearchConnection._password = password;

			return this;
		}

		public Builder postCloseRunnable(Runnable postCloseRunnable) {
			_openSearchConnection._postCloseRunnable = postCloseRunnable;

			return this;
		}

		public Builder preConnectOpenSearchConnectionConsumer(
			Consumer<OpenSearchConnection>
				preConnectOpenSearchConnectionConsumer) {

			_openSearchConnection._preConnectOpenSearchConnectionConsumer =
				preConnectOpenSearchConnectionConsumer;

			return this;
		}

		public Builder proxyConfig(ProxyConfig proxyConfig) {
			_openSearchConnection._proxyConfig = proxyConfig;

			return this;
		}

		public Builder truststorePassword(String truststorePassword) {
			_openSearchConnection._truststorePassword = truststorePassword;

			return this;
		}

		public Builder truststorePath(String truststorePath) {
			_openSearchConnection._truststorePath = truststorePath;

			return this;
		}

		public Builder truststoreType(String truststoreType) {
			_openSearchConnection._truststoreType = truststoreType;

			return this;
		}

		public Builder userName(String userName) {
			_openSearchConnection._userName = userName;

			return this;
		}

		private final OpenSearchConnection _openSearchConnection =
			new OpenSearchConnection();

	}

	private OpenSearchConnection() {
	}

	private CredentialsProvider _createCredentialsProvider() {
		BasicCredentialsProvider basicCredentialsProvider =
			new BasicCredentialsProvider();

		if (_proxyConfig.shouldApplyCredentials()) {
			String password = _proxyConfig.getPassword();

			basicCredentialsProvider.setCredentials(
				new AuthScope(_proxyConfig.getHost(), _proxyConfig.getPort()),
				new UsernamePasswordCredentials(
					_proxyConfig.getUserName(), password.toCharArray()));
		}

		basicCredentialsProvider.setCredentials(
			new AuthScope(null, null, -1, null, null),
			new UsernamePasswordCredentials(
				_userName, _password.toCharArray()));

		return basicCredentialsProvider;
	}

	private OpenSearchClient _createOpenSearchClient() {
		_openSearchTransport = _createTransport();

		return new OpenSearchClient(_openSearchTransport);
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

	private OpenSearchTransport _createTransport() {
		return ApacheHttpClient5TransportBuilder.builder(
			_getHttpHosts()
		).setFailureListener(
			new ApacheHttpClient5Transport.FailureListener() {

				@Override
				public void onFailure(Node node) {
					if (_log.isWarnEnabled()) {
						_log.warn("Marked node as unavailable: " + node);
					}
				}

			}
		).setCompressionEnabled(
			_compressionEnabled
		).setMapper(
			new JacksonJsonpMapper()
		).setHttpClientConfigCallback(
			httpClientBuilder -> {
				if (_authenticationEnabled) {
					httpClientBuilder.setDefaultCredentialsProvider(
						_createCredentialsProvider());
				}

				PoolingAsyncClientConnectionManagerBuilder
					poolingAsyncClientConnectionManagerBuilder =
						PoolingAsyncClientConnectionManagerBuilder.create();

				if (_httpSSLEnabled) {
					poolingAsyncClientConnectionManagerBuilder.setTlsStrategy(
						ClientTlsStrategyBuilder.create(
						).setSslContext(
							_createSSLContext()
						).build());
				}

				if ((_proxyConfig != null) &&
					_proxyConfig.shouldApplyConfig()) {

					httpClientBuilder.setProxy(
						new HttpHost(
							"http", _proxyConfig.getHost(),
							_proxyConfig.getPort()));
				}

				return httpClientBuilder.setConnectionManager(
					poolingAsyncClientConnectionManagerBuilder.build());
			}
		).setRequestConfigCallback(
			this::_customizeRequestConfig
		).build();
	}

	private RequestConfig.Builder _customizeRequestConfig(
		RequestConfig.Builder requestConfigBuilder) {

		return requestConfigBuilder.setResponseTimeout(
			120000, TimeUnit.MILLISECONDS);
	}

	private HttpHost[] _getHttpHosts() {
		return TransformUtil.transform(
			_networkHostAddresses, HttpHost::create, HttpHost.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchConnection.class);

	private boolean _active;
	private boolean _authenticationEnabled;
	private boolean _compressionEnabled;
	private String _connectionId;
	private boolean _httpSSLEnabled;
	private int _maxConnections;
	private int _maxConnectionsPerRoute;
	private String[] _networkHostAddresses;
	private OpenSearchClient _openSearchClient;
	private OpenSearchTransport _openSearchTransport;
	private String _password;
	private Runnable _postCloseRunnable;
	private Consumer<OpenSearchConnection>
		_preConnectOpenSearchConnectionConsumer;
	private ProxyConfig _proxyConfig;
	private String _truststorePassword;
	private String _truststorePath;
	private String _truststoreType;
	private String _userName;

}