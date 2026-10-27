/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.http.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import java.io.InputStream;

import java.net.InetSocketAddress;
import java.net.URI;

import java.security.KeyStore;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.impl.pool.BasicPoolEntry;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 */
public class HttpImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public String[] stripURLAnchor(String url, String separator) {
					return new String[] {url, StringPool.BLANK};
				}

			});
	}

	@Before
	public void setUp() {
		_httpImpl.activate(Collections.emptyMap());

		DCLSingleton<PoolingHttpClientConnectionManager>
			poolingHttpClientConnectionManagerDCLSingleton =
				ReflectionTestUtil.getFieldValue(
					_httpImpl,
					"_poolingHttpClientConnectionManagerDCLSingleton");

		_poolingHttpClientConnectionManager =
			poolingHttpClientConnectionManagerDCLSingleton.getSingleton(
				() -> ReflectionTestUtil.invoke(
					_httpImpl, "_createPoolingHttpClientConnectionManager",
					new Class<?>[0]));

		DCLSingleton<CloseableHttpClient> closeableHttpClientDCLSingleton =
			ReflectionTestUtil.getFieldValue(
				_httpImpl, "_closeableHttpClientDCLSingleton");

		_closeableHttpClient = closeableHttpClientDCLSingleton.getSingleton(
			() -> ReflectionTestUtil.invoke(
				_httpImpl, "_createCloseableHttpClient",
				new Class<?>[] {PoolingHttpClientConnectionManager.class},
				_poolingHttpClientConnectionManager));
	}

	@Test
	public void testGetRequestConfigBuilder() {
		RequestConfig.Builder requestConfigBuilder = ReflectionTestUtil.invoke(
			_httpImpl, "_getRequestConfigBuilder",
			new Class<?>[] {URI.class, int.class},
			URI.create("http://" + RandomTestUtil.randomString()), 1000);

		RequestConfig requestConfig = requestConfigBuilder.build();

		Assert.assertEquals(1000, requestConfig.getConnectTimeout());
		Assert.assertEquals(1000, requestConfig.getConnectionRequestTimeout());
		Assert.assertEquals(1000, requestConfig.getSocketTimeout());

		requestConfigBuilder = ReflectionTestUtil.invoke(
			_httpImpl, "_getRequestConfigBuilder",
			new Class<?>[] {URI.class, int.class},
			URI.create("http://" + RandomTestUtil.randomString()), 0);

		requestConfig = requestConfigBuilder.build();

		int defaultTimeout = ReflectionTestUtil.getFieldValue(
			_httpImpl, "_TIMEOUT");

		Assert.assertEquals(
			defaultTimeout, requestConfig.getConnectionRequestTimeout());
		Assert.assertEquals(defaultTimeout, requestConfig.getConnectTimeout());
		Assert.assertEquals(defaultTimeout, requestConfig.getSocketTimeout());
	}

	@Test
	public void testHttpKeepAlive() {
		_httpImpl.modified(Collections.singletonMap("keepAliveTimeout", -1));

		_testHttpKeepAlive(true, Long.MAX_VALUE, -1);
		_testHttpKeepAlive(true, Long.MAX_VALUE, 0);
		_testHttpKeepAlive(true, 300000, 300);

		_httpImpl.modified(Collections.singletonMap("keepAliveTimeout", 0));

		_testHttpKeepAlive(true, Long.MAX_VALUE, -1);
		_testHttpKeepAlive(true, Long.MAX_VALUE, 0);
		_testHttpKeepAlive(true, 300000, 300);

		_httpImpl.modified(Collections.singletonMap("keepAliveTimeout", 600));

		_testHttpKeepAlive(true, 600000, -1);
		_testHttpKeepAlive(true, 600000, 0);
		_testHttpKeepAlive(true, 300000, 300);
	}

	@Test
	public void testHttpKeepAliveWithRequestClose() {
		HttpRequest httpRequest = new BasicHttpRequest("GET", "/");

		httpRequest.setHeader(
			HttpHeaders.CONNECTION, HttpHeaders.CONNECTION_CLOSE_VALUE);

		_httpContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httpRequest);

		_httpResponse.setHeaders(
			new Header[] {
				new BasicHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE),
				new BasicHeader(HttpHeaders.CONTENT_LENGTH, "10")
			});

		_testHttpKeepAlive(false, -1);
	}

	@Test
	public void testHttpKeepAliveWithResponseClose() {
		HttpRequest httpRequest = new BasicHttpRequest("GET", "/");

		httpRequest.setHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE);

		_httpContext.setAttribute(HttpCoreContext.HTTP_REQUEST, httpRequest);

		_httpResponse.setHeaders(
			new Header[] {
				new BasicHeader(
					HttpHeaders.CONNECTION, HttpHeaders.CONNECTION_CLOSE_VALUE),
				new BasicHeader(HttpHeaders.CONTENT_LENGTH, "10")
			});

		_testHttpKeepAlive(false, -1);
	}

	@Test
	public void testIsNonProxyHost() throws Exception {
		String domain = "foo.com";
		String ipAddress = "192.168.0.250";
		String ipAddressWithStarWildcard = "182.*.0.250";

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					HttpImpl.class, "_NON_PROXY_HOSTS",
					new String[] {
						domain, ipAddress, ipAddressWithStarWildcard
					})) {

			Assert.assertTrue(_httpImpl.isNonProxyHost(domain));
			Assert.assertTrue(_httpImpl.isNonProxyHost(ipAddress));
			Assert.assertFalse(_httpImpl.isNonProxyHost("182.100.1.250"));
			Assert.assertTrue(_httpImpl.isNonProxyHost("182.123.0.250"));
			Assert.assertFalse(_httpImpl.isNonProxyHost("google.com"));
		}
	}

	@Test
	public void testTCPKeepAlive() {
		_testTCPKeepAlive(false);

		_httpImpl.modified(
			Collections.singletonMap("tcpKeepAliveEnabled", true));

		_testTCPKeepAlive(true);
	}

	@Test
	public void testUntrustedCertificate() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		char[] password = "liferay".toCharArray();

		try (InputStream inputStream = HttpImplTest.class.getResourceAsStream(
				"dependencies/localhost.p12")) {

			keyStore.load(inputStream, password);
		}

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
			KeyManagerFactory.getDefaultAlgorithm());

		keyManagerFactory.init(keyStore, password);

		SSLContext sslContext = SSLContext.getInstance("TLS");

		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

		HttpsServer httpsServer = HttpsServer.create(
			new InetSocketAddress(0), 0);

		httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));

		httpsServer.start();

		try {
			InetSocketAddress inetSocketAddress = httpsServer.getAddress();

			Assert.assertThrows(
				SSLHandshakeException.class,
				() -> _closeableHttpClient.execute(
					new HttpGet(
						"https://localhost:" + inetSocketAddress.getPort())));
		}
		finally {
			httpsServer.stop(0);
		}
	}

	private Tuple _getTuple() {
		ClientExecChain clientExecChain = ReflectionTestUtil.getFieldValue(
			_closeableHttpClient, "execChain");

		while (true) {
			clientExecChain = ReflectionTestUtil.getFieldValue(
				clientExecChain, "requestExecutor");

			if (clientExecChain instanceof MainClientExec) {
				return new Tuple(
					ReflectionTestUtil.getFieldValue(
						clientExecChain, "keepAliveStrategy"),
					ReflectionTestUtil.getFieldValue(
						clientExecChain, "reuseStrategy"));
			}
		}
	}

	private void _testHttpKeepAlive(
		boolean expectedKeepAlive,
		long expectedKeepAliveTimeoutInMilliseconds) {

		Tuple tuple = _getTuple();

		ConnectionReuseStrategy connectionReuseStrategy =
			(ConnectionReuseStrategy)tuple.getObject(1);

		Assert.assertEquals(
			expectedKeepAlive,
			connectionReuseStrategy.keepAlive(_httpResponse, _httpContext));

		long keepAliveTimeout = -1;

		if (expectedKeepAlive) {
			ConnectionKeepAliveStrategy connectionKeepAliveStrategy =
				(ConnectionKeepAliveStrategy)tuple.getObject(0);

			BasicPoolEntry basicPoolEntry = new BasicPoolEntry(
				"id", new HttpHost("localhost", 8080),
				new DefaultManagedHttpClientConnection("id", 8 * 1024));

			basicPoolEntry.updateExpiry(
				connectionKeepAliveStrategy.getKeepAliveDuration(
					_httpResponse, new BasicHttpContext(null)),
				TimeUnit.MILLISECONDS);

			keepAliveTimeout = basicPoolEntry.getExpiry();

			if (keepAliveTimeout != Long.MAX_VALUE) {
				keepAliveTimeout -= basicPoolEntry.getUpdated();
			}
		}

		Assert.assertEquals(
			expectedKeepAliveTimeoutInMilliseconds, keepAliveTimeout);
	}

	private void _testHttpKeepAlive(
		boolean expectedKeepAlive, long expectedKeepAliveTimeoutInMilliseconds,
		long keepAliveTimeoutHeaderValue) {

		_httpResponse.setHeaders(
			new Header[] {
				new BasicHeader(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE),
				new BasicHeader(HttpHeaders.CONTENT_LENGTH, "10")
			});

		if (keepAliveTimeoutHeaderValue > -1) {
			_httpResponse.setHeader(
				HttpHeaders.KEEP_ALIVE,
				"timeout=" + keepAliveTimeoutHeaderValue);
		}

		_testHttpKeepAlive(
			expectedKeepAlive, expectedKeepAliveTimeoutInMilliseconds);
	}

	private void _testTCPKeepAlive(boolean expectedEnabledTCPKeepAlive) {
		SocketConfig socketConfig = ReflectionTestUtil.invoke(
			_poolingHttpClientConnectionManager, "resolveSocketConfig",
			new Class<?>[] {HttpHost.class}, new Object[] {_httpHost});

		Assert.assertEquals(
			expectedEnabledTCPKeepAlive, socketConfig.isSoKeepAlive());
	}

	private CloseableHttpClient _closeableHttpClient;
	private final HttpContext _httpContext = new BasicHttpContext(null);
	private final HttpHost _httpHost = new HttpHost("localhost", 8080);
	private final HttpImpl _httpImpl = new HttpImpl();
	private final HttpResponse _httpResponse = new BasicHttpResponse(
		new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
	private PoolingHttpClientConnectionManager
		_poolingHttpClientConnectionManager;

}