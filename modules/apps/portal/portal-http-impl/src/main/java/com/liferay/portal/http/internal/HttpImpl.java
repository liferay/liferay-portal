/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.http.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.memory.FinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.http.internal.configuration.HttpConfiguration;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncFilterInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.Reference;

import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Brian Wing Shun Chan
 * @author Hugo Huijser
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.http.internal.configuration.HttpConfiguration",
	service = Http.class
)
public class HttpImpl implements Http {

	@Override
	public Cookie[] getCookies() {
		return _cookies.get();
	}

	@Override
	public boolean hasProxyConfig() {
		if (Validator.isNotNull(_PROXY_HOST) && (_PROXY_PORT > 0)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isNonProxyHost(String host) {
		if (Validator.isNull(host)) {
			return false;
		}

		for (String nonProxyHost : _NON_PROXY_HOSTS) {
			if (nonProxyHost.equals(host) ||
				(nonProxyHost.contains(StringPool.STAR) &&
				 StringUtil.wildcardMatches(
					 host, nonProxyHost, (char)0, CharPool.STAR, (char)0,
					 false))) {

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isProxyHost(String host) {
		if (Validator.isNull(host)) {
			return false;
		}

		if (hasProxyConfig() && !isNonProxyHost(host)) {
			return true;
		}

		return false;
	}

	@Override
	public byte[] URLtoByteArray(Http.Options options) throws IOException {
		return URLtoByteArray(
			options.getLocation(), options.getMethod(), options.getHeaders(),
			options.getCookieSpec(), options.getCookies(), options.getAuth(),
			options.getBody(), options.getFileParts(),
			options.getInputStreamParts(), options.getParts(),
			options.getResponse(), options.isFollowRedirects(),
			options.isNormalizeURI(), options.getTimeout());
	}

	@Override
	public byte[] URLtoByteArray(String location) throws IOException {
		Http.Options options = new Http.Options();

		options.setLocation(location);

		return URLtoByteArray(options);
	}

	@Override
	public byte[] URLtoByteArray(String location, boolean post)
		throws IOException {

		Http.Options options = new Http.Options();

		options.setLocation(location);
		options.setPost(post);

		return URLtoByteArray(options);
	}

	@Override
	public InputStream URLtoInputStream(Http.Options options)
		throws IOException {

		return URLtoInputStream(
			options.getLocation(), options.getMethod(), options.getHeaders(),
			options.getCookieSpec(), options.getCookies(), options.getAuth(),
			options.getBody(), options.getFileParts(),
			options.getInputStreamParts(), options.getParts(),
			options.getResponse(), options.isFollowRedirects(),
			options.isNormalizeURI(), options.getTimeout());
	}

	@Override
	public InputStream URLtoInputStream(String location) throws IOException {
		Http.Options options = new Http.Options();

		options.setLocation(location);

		return URLtoInputStream(options);
	}

	@Override
	public InputStream URLtoInputStream(String location, boolean post)
		throws IOException {

		Http.Options options = new Http.Options();

		options.setLocation(location);
		options.setPost(post);

		return URLtoInputStream(options);
	}

	@Override
	public String URLtoString(Http.Options options) throws IOException {
		byte[] bytes = URLtoByteArray(options);

		if (bytes == null) {
			return null;
		}

		return new String(bytes);
	}

	@Override
	public String URLtoString(String location) throws IOException {
		byte[] bytes = URLtoByteArray(location);

		if (bytes == null) {
			return null;
		}

		return new String(bytes);
	}

	@Override
	public String URLtoString(String location, boolean post)
		throws IOException {

		byte[] bytes = URLtoByteArray(location, post);

		if (bytes == null) {
			return null;
		}

		return new String(bytes);
	}

	/**
	 * This method only uses the default Commons HttpClient implementation when
	 * the URL object represents a HTTP resource. The URL object could also
	 * represent a file or some JNDI resource. In that case, the default Java
	 * implementation is used.
	 *
	 * @param  url the URL
	 * @return A string representation of the resource referenced by the URL
	 *         object
	 * @throws IOException if an IO exception occurred
	 */
	@Override
	public String URLtoString(URL url) throws IOException {
		if (url == null) {
			return null;
		}

		String protocol = StringUtil.toLowerCase(url.getProtocol());

		if (protocol.startsWith(Http.HTTP) || protocol.startsWith(Http.HTTPS)) {
			return URLtoString(url.toString());
		}

		URLConnection urlConnection = url.openConnection();

		if (urlConnection == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to open a connection to " + url);
			}

			return null;
		}

		String xml = null;

		try (InputStream inputStream = urlConnection.getInputStream();
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			byte[] bytes = new byte[512];

			for (int i = inputStream.read(bytes, 0, 512); i != -1;
				 i = inputStream.read(bytes, 0, 512)) {

				unsyncByteArrayOutputStream.write(bytes, 0, i);
			}

			xml = new String(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}

		return xml;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_httpConfiguration = ConfigurableUtil.createConfigurable(
			HttpConfiguration.class, properties);

		_proxyAuthPrefs.add(AuthSchemes.BASIC);
		_proxyAuthPrefs.add(AuthSchemes.DIGEST);

		if (_PROXY_AUTH_TYPE.equals("username-password")) {
			_proxyCredentials = new UsernamePasswordCredentials(
				_PROXY_USERNAME, _PROXY_PASSWORD);

			_proxyAuthPrefs.add(AuthSchemes.NTLM);
		}
		else if (_PROXY_AUTH_TYPE.equals("ntlm")) {
			_proxyCredentials = new NTCredentials(
				_PROXY_USERNAME, _PROXY_PASSWORD, _PROXY_NTLM_HOST,
				_PROXY_NTLM_DOMAIN);

			_proxyAuthPrefs.add(0, AuthSchemes.NTLM);
		}
		else {
			_proxyCredentials = null;
		}
	}

	protected void addProxyCredentials(
		URI uri, HttpClientContext httpClientContext) {

		if (!isProxyHost(uri.getHost()) || (_proxyCredentials == null)) {
			return;
		}

		CredentialsProvider credentialsProvider =
			httpClientContext.getCredentialsProvider();

		if (credentialsProvider == null) {
			credentialsProvider = new BasicCredentialsProvider();

			httpClientContext.setCredentialsProvider(credentialsProvider);
		}

		credentialsProvider.setCredentials(
			new AuthScope(_PROXY_HOST, _PROXY_PORT), _proxyCredentials);
	}

	@Deactivate
	protected void deactivate() {
		_poolingHttpClientConnectionManagerDCLSingleton.destroy(
			HttpImpl::_destroyPoolingHttpClientConnectionManager);
	}

	protected boolean hasRequestHeader(
		RequestBuilder requestBuilder, String name) {

		return ArrayUtil.isNotEmpty(requestBuilder.getHeaders(name));
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_httpConfiguration = ConfigurableUtil.createConfigurable(
			HttpConfiguration.class, properties);

		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
			_poolingHttpClientConnectionManagerDCLSingleton.getSingleton(
				this::_createPoolingHttpClientConnectionManager);

		poolingHttpClientConnectionManager.setDefaultSocketConfig(
			SocketConfig.custom(
			).setSoKeepAlive(
				_httpConfiguration.tcpKeepAliveEnabled()
			).build());
	}

	protected void processPostMethod(
		RequestBuilder requestBuilder, Map<String, String> headers,
		List<Http.FilePart> fileParts,
		List<Http.InputStreamPart> inputStreamParts,
		Map<String, String> parts) {

		if (ListUtil.isEmpty(fileParts) && ListUtil.isEmpty(inputStreamParts)) {
			if (parts != null) {
				for (Map.Entry<String, String> entry : parts.entrySet()) {
					String value = entry.getValue();

					if (value != null) {
						requestBuilder.addParameter(entry.getKey(), value);
					}
				}
			}
		}
		else {
			MultipartEntityBuilder multipartEntityBuilder =
				MultipartEntityBuilder.create();

			if (headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
				ContentType contentType = ContentType.parse(
					headers.get(HttpHeaders.CONTENT_TYPE));

				String boundary = contentType.getParameter("boundary");

				if (boundary != null) {
					multipartEntityBuilder.setBoundary(boundary);
				}
			}

			if (parts != null) {
				for (Map.Entry<String, String> entry : parts.entrySet()) {
					String value = entry.getValue();

					if (value != null) {
						multipartEntityBuilder.addPart(
							entry.getKey(),
							new StringBody(
								value,
								ContentType.create(
									"text/plain", StringPool.UTF8)));
					}
				}
			}

			if (fileParts != null) {
				for (Http.FilePart filePart : fileParts) {
					ByteArrayBody byteArrayBody = new ByteArrayBody(
						filePart.getValue(), ContentType.DEFAULT_BINARY,
						filePart.getFileName());

					multipartEntityBuilder.addPart(
						filePart.getName(), byteArrayBody);
				}
			}

			if (inputStreamParts != null) {
				for (Http.InputStreamPart inputStreamPart : inputStreamParts) {
					ContentType contentType = ContentType.DEFAULT_BINARY;

					if (inputStreamPart.getContentType() != null) {
						contentType = ContentType.create(
							inputStreamPart.getContentType());
					}

					multipartEntityBuilder.addPart(
						inputStreamPart.getName(),
						new InputStreamBody(
							inputStreamPart.getInputStream(), contentType,
							inputStreamPart.getInputStreamName()));
				}
			}

			requestBuilder.setEntity(multipartEntityBuilder.build());
		}
	}

	protected org.apache.http.cookie.Cookie toHttpCookie(Cookie cookie) {
		BasicClientCookie basicClientCookie = new BasicClientCookie(
			cookie.getName(), cookie.getValue());

		basicClientCookie.setDomain(cookie.getDomain());

		int maxAge = cookie.getMaxAge();

		if (maxAge > 0) {
			Date expiryDate = new Date(
				System.currentTimeMillis() + (maxAge * 1000L));

			basicClientCookie.setExpiryDate(expiryDate);

			basicClientCookie.setAttribute(
				ClientCookie.MAX_AGE_ATTR, String.valueOf(maxAge));
		}

		basicClientCookie.setPath(cookie.getPath());
		basicClientCookie.setSecure(cookie.getSecure());
		basicClientCookie.setVersion(cookie.getVersion());

		return basicClientCookie;
	}

	protected org.apache.http.cookie.Cookie[] toHttpCookies(Cookie[] cookies) {
		if (cookies == null) {
			return null;
		}

		org.apache.http.cookie.Cookie[] httpCookies =
			new org.apache.http.cookie.Cookie[cookies.length];

		for (int i = 0; i < cookies.length; i++) {
			httpCookies[i] = toHttpCookie(cookies[i]);
		}

		return httpCookies;
	}

	protected Cookie toServletCookie(org.apache.http.cookie.Cookie httpCookie) {
		Cookie cookie = new Cookie(httpCookie.getName(), httpCookie.getValue());

		if (!PropsValues.SESSION_COOKIE_USE_FULL_HOSTNAME) {
			String domain = httpCookie.getDomain();

			if (Validator.isNotNull(domain)) {
				cookie.setDomain(domain);
			}
		}

		Date expiryDate = httpCookie.getExpiryDate();

		if (expiryDate != null) {
			int maxAge =
				(int)(expiryDate.getTime() - System.currentTimeMillis());

			maxAge = maxAge / 1000;

			if (maxAge > -1) {
				cookie.setMaxAge(maxAge);
			}
		}

		String path = httpCookie.getPath();

		if (Validator.isNotNull(path)) {
			cookie.setPath(path);
		}

		cookie.setSecure(httpCookie.isSecure());
		cookie.setVersion(httpCookie.getVersion());

		return cookie;
	}

	protected Cookie[] toServletCookies(
		List<org.apache.http.cookie.Cookie> httpCookies) {

		if (httpCookies == null) {
			return null;
		}

		Cookie[] cookies = new Cookie[httpCookies.size()];

		for (int i = 0; i < httpCookies.size(); i++) {
			cookies[i] = toServletCookie(httpCookies.get(i));
		}

		return cookies;
	}

	protected byte[] URLtoByteArray(
			String location, Http.Method method, Map<String, String> headers,
			Http.CookieSpec cookieSpec, Cookie[] cookies, Http.Auth auth,
			Http.Body body, List<Http.FilePart> fileParts,
			List<Http.InputStreamPart> inputStreamParts,
			Map<String, String> parts, Http.Response response,
			boolean followRedirects, boolean normalizeURI, int timeout)
		throws IOException {

		try (InputStream inputStream = URLtoInputStream(
				location, method, headers, cookieSpec, cookies, auth, body,
				fileParts, inputStreamParts, parts, response, followRedirects,
				normalizeURI, timeout)) {

			if (inputStream == null) {
				return null;
			}

			long contentLengthLong = response.getContentLengthLong();

			if (contentLengthLong > _MAX_BYTE_ARRAY_LENGTH) {
				throw new OutOfMemoryError(
					StringBundler.concat(
						"Retrieving ", location, " yields a file of size ",
						contentLengthLong,
						" bytes that is too large to convert to a byte array"));
			}

			return FileUtil.getBytes(inputStream);
		}
	}

	protected InputStream URLtoInputStream(
			String location, Http.Method method, Map<String, String> headers,
			Http.CookieSpec cookieSpec, Cookie[] cookies, Http.Auth auth,
			Http.Body body, List<Http.FilePart> fileParts,
			List<Http.InputStreamPart> inputStreamParts,
			Map<String, String> parts, Http.Response response,
			boolean followRedirects, boolean normalizeURI, int timeout)
		throws IOException {

		URI uri = null;

		try {
			uri = HttpComponentsUtil.getURI(location);
		}
		catch (URISyntaxException uriSyntaxException) {
			throw new IOException(
				"Invalid URI: " + location, uriSyntaxException);
		}

		BasicCookieStore basicCookieStore = null;
		CloseableHttpResponse closeableHttpResponse = null;
		HttpEntity httpEntity = null;

		try {
			_cookies.set(null);

			if (location == null) {
				return null;
			}
			else if (!location.startsWith(Http.HTTP_WITH_SLASH) &&
					 !location.startsWith(Http.HTTPS_WITH_SLASH)) {

				location = Http.HTTP_WITH_SLASH + location;

				uri = HttpComponentsUtil.getURI(location);
			}

			HttpHost targetHttpHost = new HttpHost(
				uri.getHost(), uri.getPort(), uri.getScheme());

			RequestConfig.Builder requestConfigBuilder =
				_getRequestConfigBuilder(uri, timeout);

			int maxConnectionsPerHost = GetterUtil.getInteger(
				PropsUtil.get(
					Http.class.getName() + ".max.connections.per.host",
					new Filter(uri.getHost())));

			PoolingHttpClientConnectionManager
				poolingHttpClientConnectionManager =
					_poolingHttpClientConnectionManagerDCLSingleton.
						getSingleton(
							this::_createPoolingHttpClientConnectionManager);

			if ((maxConnectionsPerHost > 0) &&
				(maxConnectionsPerHost != _MAX_CONNECTIONS_PER_HOST)) {

				poolingHttpClientConnectionManager.setMaxPerRoute(
					new HttpRoute(new HttpHost(uri.getHost(), uri.getPort())),
					maxConnectionsPerHost);
			}

			RequestConfig requestConfig = requestConfigBuilder.build();

			CloseableHttpClient httpClient = null;

			if ((requestConfig.getProxy() != null) && hasProxyConfig() &&
				Validator.isNotNull(_PROXY_USERNAME)) {

				httpClient = _proxyCloseableHttpClientDCLSingleton.getSingleton(
					() -> _createCloseableHttpClient(
						poolingHttpClientConnectionManager,
						new HttpHost(_PROXY_HOST, _PROXY_PORT),
						_proxyAuthPrefs));
			}
			else {
				httpClient = _closeableHttpClientDCLSingleton.getSingleton(
					() -> _createCloseableHttpClient(
						poolingHttpClientConnectionManager, null, null));
			}

			HttpClientContext httpClientContext = HttpClientContext.create();

			RequestBuilder requestBuilder = null;

			if (method.equals(Http.Method.PATCH) ||
				method.equals(Http.Method.POST) ||
				method.equals(Http.Method.PUT)) {

				if (method.equals(Http.Method.PATCH)) {
					requestBuilder = RequestBuilder.patch(location);
				}
				else if (method.equals(Http.Method.POST)) {
					requestBuilder = RequestBuilder.post(location);
				}
				else {
					requestBuilder = RequestBuilder.put(location);
				}

				if (body != null) {
					StringEntity stringEntity = new StringEntity(
						body.getContent(), body.getCharset());

					stringEntity.setContentType(body.getContentType());

					requestBuilder.setEntity(stringEntity);
				}
				else if (method.equals(Http.Method.POST)) {
					if (!hasRequestHeader(
							requestBuilder, HttpHeaders.CONTENT_TYPE)) {

						ConnectionConfig.Builder connectionConfigBuilder =
							ConnectionConfig.custom();

						connectionConfigBuilder.setCharset(
							Charset.forName(StringPool.UTF8));

						poolingHttpClientConnectionManager.setConnectionConfig(
							targetHttpHost, connectionConfigBuilder.build());
					}

					processPostMethod(
						requestBuilder, headers, fileParts, inputStreamParts,
						parts);
				}
			}
			else if (method.equals(Http.Method.DELETE)) {
				requestBuilder = RequestBuilder.delete(location);
			}
			else if (method.equals(Http.Method.HEAD)) {
				requestBuilder = RequestBuilder.head(location);
			}
			else {
				requestBuilder = RequestBuilder.get(location);
			}

			if (headers != null) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					requestBuilder.addHeader(
						header.getKey(), header.getValue());
				}
			}

			if ((method.equals(Http.Method.PATCH) ||
				 method.equals(Http.Method.POST) ||
				 method.equals(Http.Method.PUT)) &&
				((body != null) || ListUtil.isNotEmpty(fileParts) ||
				 ListUtil.isNotEmpty(inputStreamParts) ||
				 MapUtil.isNotEmpty(parts)) &&
				!hasRequestHeader(requestBuilder, HttpHeaders.CONTENT_TYPE)) {

				requestBuilder.addHeader(
					HttpHeaders.CONTENT_TYPE,
					ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED_UTF8);
			}

			if (!hasRequestHeader(requestBuilder, HttpHeaders.USER_AGENT)) {
				requestBuilder.addHeader(
					HttpHeaders.USER_AGENT, _DEFAULT_USER_AGENT);
			}

			if (cookieSpec != null) {
				if (cookieSpec.equals(CookieSpec.DEFAULT)) {
					requestConfigBuilder.setCookieSpec(CookieSpecs.DEFAULT);
				}
				else if (cookieSpec.equals(CookieSpec.IGNORE_COOKIES)) {
					requestConfigBuilder.setCookieSpec(
						CookieSpecs.IGNORE_COOKIES);
				}
				else if (cookieSpec.equals(CookieSpec.NETSCAPE)) {
					requestConfigBuilder.setCookieSpec(CookieSpecs.NETSCAPE);
				}
				else if (cookieSpec.equals(CookieSpec.STANDARD)) {
					requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD);
				}
				else if (cookieSpec.equals(CookieSpec.STANDARD_STRICT)) {
					requestConfigBuilder.setCookieSpec(
						CookieSpecs.STANDARD_STRICT);
				}
			}
			else {
				requestConfigBuilder.setCookieSpec(CookieSpecs.DEFAULT);
			}

			if (ArrayUtil.isNotEmpty(cookies)) {
				basicCookieStore = new BasicCookieStore();

				org.apache.http.cookie.Cookie[] httpCookies = toHttpCookies(
					cookies);

				basicCookieStore.addCookies(httpCookies);

				httpClientContext.setCookieStore(basicCookieStore);
			}

			if (auth != null) {
				requestConfigBuilder.setAuthenticationEnabled(true);

				CredentialsProvider credentialsProvider =
					new BasicCredentialsProvider();

				httpClientContext.setCredentialsProvider(credentialsProvider);

				credentialsProvider.setCredentials(
					new AuthScope(
						auth.getHost(), auth.getPort(), auth.getRealm()),
					new UsernamePasswordCredentials(
						auth.getUsername(), auth.getPassword()));
			}

			requestConfigBuilder.setNormalizeUri(normalizeURI);

			if (!followRedirects) {
				requestConfigBuilder.setRedirectsEnabled(false);
			}

			addProxyCredentials(uri, httpClientContext);

			requestBuilder.setConfig(requestConfigBuilder.build());

			closeableHttpResponse = httpClient.execute(
				targetHttpHost, requestBuilder.build(), httpClientContext);

			httpEntity = closeableHttpResponse.getEntity();

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			response.setResponseCode(statusLine.getStatusCode());

			Header locationHeader = closeableHttpResponse.getFirstHeader(
				"location");

			if (locationHeader != null) {
				String locationHeaderValue = locationHeader.getValue();

				if (!locationHeaderValue.equals(location)) {
					if (followRedirects) {
						EntityUtils.consumeQuietly(httpEntity);

						closeableHttpResponse.close();

						return URLtoInputStream(
							locationHeaderValue, Http.Method.GET, headers,
							cookieSpec, cookies, auth, body, fileParts,
							inputStreamParts, parts, response, followRedirects,
							normalizeURI, timeout);
					}

					response.setRedirect(locationHeaderValue);
				}
			}

			Header contentLengthHeader = closeableHttpResponse.getFirstHeader(
				HttpHeaders.CONTENT_LENGTH);

			if (contentLengthHeader != null) {
				long contentLengthLong = GetterUtil.getLong(
					contentLengthHeader.getValue());

				response.setContentLengthLong(contentLengthLong);

				if (contentLengthLong > _MAX_BYTE_ARRAY_LENGTH) {
					response.setContentLength(-1);
				}
				else {
					int contentLength = (int)contentLengthLong;

					response.setContentLength(contentLength);
				}
			}

			Header contentTypeHeader = closeableHttpResponse.getFirstHeader(
				HttpHeaders.CONTENT_TYPE);

			if (contentTypeHeader != null) {
				response.setContentType(contentTypeHeader.getValue());
			}

			for (Header header : closeableHttpResponse.getAllHeaders()) {
				response.addHeader(header.getName(), header.getValue());
			}

			if (httpEntity == null) {
				return null;
			}

			InputStream inputStream = httpEntity.getContent();

			final CloseableHttpResponse referenceCloseableHttpResponse =
				closeableHttpResponse;

			final Reference<InputStream> reference = FinalizeManager.register(
				inputStream,
				new FinalizeAction() {

					@Override
					public void doFinalize(Reference<?> reference) {
						try {
							referenceCloseableHttpResponse.close();
						}
						catch (IOException ioException) {
							if (_log.isDebugEnabled()) {
								_log.debug(
									"Unable to close response", ioException);
							}
						}
					}

				},
				FinalizeManager.WEAK_REFERENCE_FACTORY);

			return new UnsyncFilterInputStream(inputStream) {

				@Override
				public void close() throws IOException {
					super.close();

					referenceCloseableHttpResponse.close();

					reference.clear();
				}

			};
		}
		catch (Exception exception) {
			if (httpEntity != null) {
				EntityUtils.consumeQuietly(httpEntity);
			}

			if (closeableHttpResponse != null) {
				try {
					closeableHttpResponse.close();
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to close response", ioException);
					}
				}
			}

			throw new IOException(exception);
		}
		finally {
			try {
				if (basicCookieStore != null) {
					_cookies.set(
						toServletCookies(basicCookieStore.getCookies()));
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private static void _destroyPoolingHttpClientConnectionManager(
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {

		int retry = 0;

		while (retry < 10) {
			PoolStats poolStats =
				poolingHttpClientConnectionManager.getTotalStats();

			int availableConnections = poolStats.getAvailable();

			if (availableConnections <= 0) {
				break;
			}

			poolingHttpClientConnectionManager.closeIdleConnections(
				200, TimeUnit.MILLISECONDS);

			try {
				Thread.sleep(500);
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}
			}

			retry++;
		}

		poolingHttpClientConnectionManager.shutdown();
	}

	private CloseableHttpClient _createCloseableHttpClient(
		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
		HttpHost httpHost, List<String> proxyAuthPrefs) {

		// Mimic behavior found in
		// http://java.sun.com/j2se/1.5.0/docs/guide/net/properties.html

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		httpClientBuilder.setConnectionManager(
			poolingHttpClientConnectionManager);

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

		requestConfigBuilder = requestConfigBuilder.setConnectTimeout(_TIMEOUT);
		requestConfigBuilder = requestConfigBuilder.setConnectionRequestTimeout(
			_TIMEOUT);

		if (httpHost != null) {
			requestConfigBuilder.setProxy(httpHost);
		}

		if (proxyAuthPrefs != null) {
			requestConfigBuilder.setProxyPreferredAuthSchemes(proxyAuthPrefs);
		}

		httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

		httpClientBuilder.setKeepAliveStrategy(
			new DefaultConnectionKeepAliveStrategy() {

				@Override
				public long getKeepAliveDuration(
					HttpResponse httpResponse, HttpContext httpContext) {

					long keepAliveDuration = super.getKeepAliveDuration(
						httpResponse, httpContext);

					if (keepAliveDuration > 0) {
						return keepAliveDuration;
					}

					return _httpConfiguration.keepAliveTimeout() * 1000L;
				}

			});
		httpClientBuilder.setRoutePlanner(
			new SystemDefaultRoutePlanner(ProxySelector.getDefault()));

		return httpClientBuilder.build();
	}

	private PoolingHttpClientConnectionManager
		_createPoolingHttpClientConnectionManager() {

		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
			new PoolingHttpClientConnectionManager(
				RegistryBuilder.<ConnectionSocketFactory>create(
				).register(
					Http.HTTP, PlainConnectionSocketFactory.getSocketFactory()
				).register(
					Http.HTTPS,
					SSLConnectionSocketFactory.getSystemSocketFactory()
				).build());

		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(
			_MAX_CONNECTIONS_PER_HOST);
		poolingHttpClientConnectionManager.setDefaultSocketConfig(
			SocketConfig.custom(
			).setSoKeepAlive(
				_httpConfiguration.tcpKeepAliveEnabled()
			).build());
		poolingHttpClientConnectionManager.setMaxTotal(_MAX_TOTAL_CONNECTIONS);

		return poolingHttpClientConnectionManager;
	}

	private RequestConfig.Builder _getRequestConfigBuilder(
		URI uri, int timeout) {

		if (_log.isDebugEnabled()) {
			_log.debug("Location is " + uri.toString());
		}

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

		if (isProxyHost(uri.getHost())) {
			HttpHost proxy = new HttpHost(_PROXY_HOST, _PROXY_PORT);

			requestConfigBuilder.setProxy(proxy);

			if (_proxyCredentials != null) {
				requestConfigBuilder.setProxyPreferredAuthSchemes(
					_proxyAuthPrefs);
			}
		}

		if (timeout == 0) {
			timeout = GetterUtil.getInteger(
				PropsUtil.get(
					Http.class.getName() + ".timeout",
					new Filter(uri.getHost())));
		}

		if (timeout > 0) {
			requestConfigBuilder = requestConfigBuilder.setConnectTimeout(
				timeout);

			requestConfigBuilder =
				requestConfigBuilder.setConnectionRequestTimeout(timeout);
		}

		return requestConfigBuilder;
	}

	private static final String _DEFAULT_USER_AGENT =
		"Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv 11.0) like Gecko";

	private static final int _MAX_BYTE_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

	private static final int _MAX_CONNECTIONS_PER_HOST = GetterUtil.getInteger(
		PropsUtil.get(Http.class.getName() + ".max.connections.per.host"), 2);

	private static final int _MAX_TOTAL_CONNECTIONS = GetterUtil.getInteger(
		PropsUtil.get(Http.class.getName() + ".max.total.connections"), 20);

	private static final String[] _NON_PROXY_HOSTS = StringUtil.split(
		SystemProperties.get("http.nonProxyHosts"), StringPool.PIPE);

	private static final String _PROXY_AUTH_TYPE = GetterUtil.getString(
		PropsUtil.get(Http.class.getName() + ".proxy.auth.type"));

	private static final String _PROXY_HOST = GetterUtil.getString(
		SystemProperties.get("http.proxyHost"));

	private static final String _PROXY_NTLM_DOMAIN = GetterUtil.getString(
		PropsUtil.get(Http.class.getName() + ".proxy.ntlm.domain"));

	private static final String _PROXY_NTLM_HOST = GetterUtil.getString(
		PropsUtil.get(Http.class.getName() + ".proxy.ntlm.host"));

	private static final String _PROXY_PASSWORD = GetterUtil.getString(
		PropsUtil.get(Http.class.getName() + ".proxy.password"));

	private static final int _PROXY_PORT = GetterUtil.getInteger(
		SystemProperties.get("http.proxyPort"));

	private static final String _PROXY_USERNAME = GetterUtil.getString(
		PropsUtil.get(Http.class.getName() + ".proxy.username"));

	private static final int _TIMEOUT = GetterUtil.getInteger(
		PropsUtil.get(Http.class.getName() + ".timeout"), 5000);

	private static final Log _log = LogFactoryUtil.getLog(HttpImpl.class);

	private static final ThreadLocal<Cookie[]> _cookies = new ThreadLocal<>();

	private final DCLSingleton<CloseableHttpClient>
		_closeableHttpClientDCLSingleton = new DCLSingleton<>();
	private volatile HttpConfiguration _httpConfiguration;
	private final DCLSingleton<PoolingHttpClientConnectionManager>
		_poolingHttpClientConnectionManagerDCLSingleton = new DCLSingleton<>();
	private final List<String> _proxyAuthPrefs = new ArrayList<>();
	private final DCLSingleton<CloseableHttpClient>
		_proxyCloseableHttpClientDCLSingleton = new DCLSingleton<>();
	private Credentials _proxyCredentials;

}