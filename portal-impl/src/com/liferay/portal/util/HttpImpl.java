/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.util;

import aQute.bnd.annotation.ProviderType;

import com.liferay.petra.memory.FinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncFilterInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.Reference;

import java.net.InetAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.EntityUtils;

/**
 * @author Brian Wing Shun Chan
 * @author Hugo Huijser
 * @author Shuyang Zhou
 */
@DoPrivileged
@ProviderType
public class HttpImpl implements Http {

	public HttpImpl() {

		// Mimic behavior found in
		// http://java.sun.com/j2se/1.5.0/docs/guide/net/properties.html

		if (Validator.isNotNull(_NON_PROXY_HOSTS)) {
			String nonProxyHostsRegEx = _NON_PROXY_HOSTS;

			nonProxyHostsRegEx = nonProxyHostsRegEx.replaceAll("\\.", "\\\\.");
			nonProxyHostsRegEx = nonProxyHostsRegEx.replaceAll("\\*", ".*?");
			nonProxyHostsRegEx = nonProxyHostsRegEx.replaceAll("\\|", ")|(");

			nonProxyHostsRegEx = "(" + nonProxyHostsRegEx + ")";

			_nonProxyHostsPattern = Pattern.compile(nonProxyHostsRegEx);
		}
		else {
			_nonProxyHostsPattern = null;
		}

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		_poolingHttpClientConnectionManager =
			new PoolingHttpClientConnectionManager();

		_poolingHttpClientConnectionManager.setDefaultMaxPerRoute(
			_MAX_CONNECTIONS_PER_HOST);
		_poolingHttpClientConnectionManager.setMaxTotal(_MAX_TOTAL_CONNECTIONS);

		httpClientBuilder.setConnectionManager(
			_poolingHttpClientConnectionManager);

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

		requestConfigBuilder = requestConfigBuilder.setConnectTimeout(_TIMEOUT);
		requestConfigBuilder = requestConfigBuilder.setConnectionRequestTimeout(
			_TIMEOUT);

		RequestConfig requestConfig = requestConfigBuilder.build();

		httpClientBuilder.setDefaultRequestConfig(requestConfig);

		SystemDefaultRoutePlanner systemDefaultRoutePlanner =
			new SystemDefaultRoutePlanner(ProxySelector.getDefault());

		httpClientBuilder.setRoutePlanner(systemDefaultRoutePlanner);

		_closeableHttpClient = httpClientBuilder.build();

		if (!hasProxyConfig() || Validator.isNull(_PROXY_USERNAME)) {
			_proxyCredentials = null;

			_proxyCloseableHttpClient = _closeableHttpClient;

			return;
		}

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

		HttpClientBuilder proxyHttpClientBuilder = HttpClientBuilder.create();

		proxyHttpClientBuilder.setRoutePlanner(systemDefaultRoutePlanner);

		proxyHttpClientBuilder.setConnectionManager(
			_poolingHttpClientConnectionManager);

		requestConfigBuilder.setProxy(new HttpHost(_PROXY_HOST, _PROXY_PORT));
		requestConfigBuilder.setProxyPreferredAuthSchemes(_proxyAuthPrefs);

		RequestConfig proxyRequestConfig = requestConfigBuilder.build();

		proxyHttpClientBuilder.setDefaultRequestConfig(proxyRequestConfig);

		_proxyCloseableHttpClient = proxyHttpClientBuilder.build();
	}

	@Override
	public String addParameter(String url, String name, boolean value) {
		return addParameter(url, name, String.valueOf(value));
	}

	@Override
	public String addParameter(String url, String name, double value) {
		return addParameter(url, name, String.valueOf(value));
	}

	@Override
	public String addParameter(String url, String name, int value) {
		return addParameter(url, name, String.valueOf(value));
	}

	@Override
	public String addParameter(String url, String name, long value) {
		return addParameter(url, name, String.valueOf(value));
	}

	@Override
	public String addParameter(String url, String name, short value) {
		return addParameter(url, name, String.valueOf(value));
	}

	@Override
	public String addParameter(String url, String name, String value) {
		if (url == null) {
			return null;
		}

		String[] urlArray = PortalUtil.stripURLAnchor(url, StringPool.POUND);

		url = urlArray[0];

		String anchor = urlArray[1];

		StringBundler sb = new StringBundler(6);

		sb.append(url);

		if (url.indexOf(CharPool.QUESTION) == -1) {
			sb.append(StringPool.QUESTION);
		}
		else if (!url.endsWith(StringPool.QUESTION) &&
				 !url.endsWith(StringPool.AMPERSAND)) {

			sb.append(StringPool.AMPERSAND);
		}

		sb.append(name);
		sb.append(StringPool.EQUAL);
		sb.append(URLCodec.encodeURL(value));
		sb.append(anchor);

		return shortenURL(sb.toString());
	}

	@Override
	public String decodePath(String path) {
		if (Validator.isNull(path)) {
			return path;
		}

		path = StringUtil.replace(path, CharPool.SLASH, _TEMP_SLASH);
		path = decodeURL(path, true);
		path = StringUtil.replace(path, _TEMP_SLASH, StringPool.SLASH);

		return path;
	}

	@Override
	public String decodeURL(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		try {
			return URLCodec.decodeURL(url, StringPool.UTF8);
		}
		catch (IllegalArgumentException iae) {
			_log.error(iae.getMessage());
		}

		return StringPool.BLANK;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #decodeURL(String)}
	 */
	@Deprecated
	@Override
	public String decodeURL(String url, boolean unescapeSpaces) {
		return decodeURL(url);
	}

	public void destroy() {
		int retry = 0;

		while (retry < 10) {
			PoolStats poolStats =
				_poolingHttpClientConnectionManager.getTotalStats();

			int availableConnections = poolStats.getAvailable();

			if (availableConnections <= 0) {
				break;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						toString(), " is waiting on ",
						String.valueOf(availableConnections), " connections"));
			}

			_poolingHttpClientConnectionManager.closeIdleConnections(
				200, TimeUnit.MILLISECONDS);

			try {
				Thread.sleep(500);
			}
			catch (InterruptedException ie) {
			}

			retry++;
		}

		_poolingHttpClientConnectionManager.shutdown();
	}

	@Override
	public String encodeParameters(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		String queryString = getQueryString(url);

		if (Validator.isNull(queryString)) {
			return url;
		}

		String encodedQueryString = parameterMapToString(
			parameterMapFromString(queryString), false);

		return StringUtil.replace(url, queryString, encodedQueryString);
	}

	@Override
	public String encodePath(String path) {
		if (Validator.isNull(path)) {
			return path;
		}

		path = StringUtil.replace(path, CharPool.SLASH, _TEMP_SLASH);
		path = URLCodec.encodeURL(path, true);
		path = StringUtil.replace(path, _TEMP_SLASH, StringPool.SLASH);

		return path;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link URLCodec#encodeURL(String)}
	 */
	@Deprecated
	@Override
	public String encodeURL(String url) {
		return encodeURL(url, false);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link URLCodec#encodeURL(String,
	 *             boolean)}
	 */
	@Deprecated
	@Override
	public String encodeURL(String url, boolean escapeSpaces) {
		return URLCodec.encodeURL(url, StringPool.UTF8, escapeSpaces);
	}

	@Override
	public String fixPath(String path) {
		return fixPath(path, true, true);
	}

	@Override
	public String fixPath(String path, boolean leading, boolean trailing) {
		if (path == null) {
			return StringPool.BLANK;
		}

		int leadingSlashCount = 0;
		int trailingSlashCount = 0;

		if (leading) {
			for (int i = 0; i < path.length(); i++) {
				if (path.charAt(i) == CharPool.SLASH) {
					leadingSlashCount++;
				}
				else {
					break;
				}
			}
		}

		if (trailing) {
			for (int i = path.length() - 1; i >= 0; i--) {
				if (path.charAt(i) == CharPool.SLASH) {
					trailingSlashCount++;
				}
				else {
					break;
				}
			}
		}

		int slashCount = leadingSlashCount + trailingSlashCount;

		if (slashCount > path.length()) {
			return StringPool.BLANK;
		}

		if (slashCount > 0) {
			path = path.substring(
				leadingSlashCount, path.length() - trailingSlashCount);
		}

		return path;
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public HttpClient getClient(HostConfiguration hostConfiguration) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCompleteURL(HttpServletRequest request) {
		StringBuffer sb = request.getRequestURL();

		if (sb == null) {
			sb = new StringBuffer();
		}

		if (request.getQueryString() != null) {
			sb.append(StringPool.QUESTION);
			sb.append(request.getQueryString());
		}

		String proxyPath = PortalUtil.getPathProxy();

		if (Validator.isNotNull(proxyPath)) {
			int x =
				sb.indexOf(Http.PROTOCOL_DELIMITER) +
					Http.PROTOCOL_DELIMITER.length();

			int y = sb.indexOf(StringPool.SLASH, x);

			sb.insert(y, proxyPath);
		}

		String completeURL = sb.toString();

		if (request.isRequestedSessionIdFromURL()) {
			HttpSession session = request.getSession();

			String sessionId = session.getId();

			completeURL = PortalUtil.getURLWithSessionId(
				completeURL, sessionId);
		}

		if (_log.isWarnEnabled()) {
			if (completeURL.contains("?&")) {
				_log.warn("Invalid url " + completeURL);
			}
		}

		return completeURL;
	}

	@Override
	public Cookie[] getCookies() {
		return _cookies.get();
	}

	@Override
	public String getDomain(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		url = url.trim();

		if (!hasProtocol(url)) {
			url = Http.HTTPS_WITH_SLASH + url;
		}

		try {
			URI uri = new URI(url);

			String host = uri.getHost();

			if (host == null) {
				return StringPool.BLANK;
			}

			return host;
		}
		catch (URISyntaxException urise) {
			return StringPool.BLANK;
		}
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public HostConfiguration getHostConfiguration(String location)
		throws IOException {

		throw new UnsupportedOperationException();
	}

	@Override
	public String getIpAddress(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		try {
			URL urlObj = new URL(url);

			InetAddress address = InetAddress.getByName(urlObj.getHost());

			return address.getHostAddress();
		}
		catch (Exception e) {
			return url;
		}
	}

	@Override
	public String getParameter(String url, String name) {
		return getParameter(url, name, true);
	}

	@Override
	public String getParameter(String url, String name, boolean escaped) {
		if (Validator.isNull(url) || Validator.isNull(name)) {
			return StringPool.BLANK;
		}

		String[] parts = StringUtil.split(url, CharPool.QUESTION);

		if (parts.length == 2) {
			String[] params = null;

			if (escaped) {
				params = StringUtil.split(parts[1], "&amp;");
			}
			else {
				params = StringUtil.split(parts[1], CharPool.AMPERSAND);
			}

			for (String param : params) {
				String[] kvp = StringUtil.split(param, CharPool.EQUAL);

				if ((kvp.length == 2) && kvp[0].equals(name)) {
					return kvp[1];
				}
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public Map<String, String[]> getParameterMap(String queryString) {
		return parameterMapFromString(queryString);
	}

	@Override
	public String getPath(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		if (url.startsWith(Http.HTTP)) {
			int pos = url.indexOf(
				CharPool.SLASH, Http.HTTPS_WITH_SLASH.length());

			url = url.substring(pos);
		}

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos == -1) {
			return url;
		}

		return url.substring(0, pos);
	}

	@Override
	public String getProtocol(ActionRequest actionRequest) {
		return getProtocol(actionRequest.isSecure());
	}

	@Override
	public String getProtocol(boolean secure) {
		if (!secure) {
			return Http.HTTP;
		}

		return Http.HTTPS;
	}

	@Override
	public String getProtocol(HttpServletRequest request) {
		return getProtocol(request.isSecure());
	}

	@Override
	public String getProtocol(RenderRequest renderRequest) {
		return getProtocol(renderRequest.isSecure());
	}

	@Override
	public String getProtocol(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		url = url.trim();

		int pos = url.indexOf(Http.PROTOCOL_DELIMITER);

		if (pos != -1) {
			return url.substring(0, pos);
		}

		return Http.HTTP;
	}

	@Override
	public String getQueryString(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos == -1) {
			return StringPool.BLANK;
		}

		return url.substring(pos + 1);
	}

	@Override
	public String getRequestURL(HttpServletRequest request) {
		return String.valueOf(request.getRequestURL());
	}

	@Override
	public boolean hasDomain(String url) {
		if (Validator.isNull(url)) {
			return false;
		}

		return Validator.isNotNull(getDomain(url));
	}

	@Override
	public boolean hasProtocol(String url) {
		if (Validator.isNull(url)) {
			return false;
		}

		int pos = url.indexOf(Http.PROTOCOL_DELIMITER);

		if (pos != -1) {
			return true;
		}

		return false;
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

		if (_nonProxyHostsPattern != null) {
			Matcher matcher = _nonProxyHostsPattern.matcher(host);

			if (matcher.matches()) {
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
	public boolean isSecure(String url) {
		String protocol = getProtocol(url);

		return StringUtil.equalsIgnoreCase(protocol, Http.HTTPS);
	}

	@Override
	public String normalizePath(String uri) {
		if (Validator.isNull(uri)) {
			return uri;
		}

		uri = removePathParameters(uri);

		for (int i = 0; i < uri.length(); i++) {
			char c = uri.charAt(i);

			if ((c == CharPool.PERCENT) || (c == CharPool.PERIOD) ||
				((c == CharPool.SLASH) && ((i + 1) < uri.length()) &&
				 (uri.charAt(i + 1) == CharPool.SLASH))) {

				break;
			}

			if (i == (uri.length() - 1)) {
				if (c == CharPool.QUESTION) {
					return uri.substring(0, uri.length() - 1);
				}

				return uri;
			}
		}

		String path = null;
		String queryString = null;

		int pos = uri.indexOf('?');

		if (pos != -1) {
			path = uri.substring(0, pos);
			queryString = uri.substring(pos + 1);
		}
		else {
			path = uri;
		}

		String[] uriParts = StringUtil.split(path.substring(1), CharPool.SLASH);

		List<String> parts = new ArrayList<>(uriParts.length);

		String prevUriPart = null;

		for (String uriPart : uriParts) {
			String curUriPart = URLCodec.decodeURL(uriPart);

			if (curUriPart.equals(StringPool.DOUBLE_PERIOD)) {
				if ((prevUriPart != null) &&
					!prevUriPart.equals(StringPool.PERIOD)) {

					parts.remove(parts.size() - 1);
				}
			}
			else if ((curUriPart.length() > 0) &&
					 !curUriPart.equals(StringPool.PERIOD)) {

				parts.add(URLCodec.encodeURL(curUriPart));
			}

			prevUriPart = curUriPart;
		}

		if (parts.isEmpty()) {
			return StringPool.SLASH;
		}

		StringBundler sb = new StringBundler(parts.size() * 2 + 2);

		for (String part : parts) {
			sb.append(StringPool.SLASH);
			sb.append(part);
		}

		if (Validator.isNotNull(queryString)) {
			sb.append(StringPool.QUESTION);
			sb.append(queryString);
		}

		return sb.toString();
	}

	@Override
	public Map<String, String[]> parameterMapFromString(String queryString) {
		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		if (Validator.isNull(queryString)) {
			return parameterMap;
		}

		String[] parameters = StringUtil.split(queryString, CharPool.AMPERSAND);

		for (String parameter : parameters) {
			if (parameter.length() > 0) {
				String[] kvp = StringUtil.split(parameter, CharPool.EQUAL);

				if (kvp.length == 0) {
					continue;
				}

				String key = kvp[0];

				String value = StringPool.BLANK;

				if (kvp.length > 1) {
					try {
						value = decodeURL(kvp[1]);
					}
					catch (IllegalArgumentException iae) {
						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Skipping parameter with key ", key,
									" because of invalid value ", kvp[1]),
								iae);
						}

						continue;
					}
				}

				String[] values = parameterMap.get(key);

				if (values == null) {
					parameterMap.put(key, new String[] {value});
				}
				else {
					parameterMap.put(key, ArrayUtil.append(values, value));
				}
			}
		}

		return parameterMap;
	}

	@Override
	public String parameterMapToString(Map<String, String[]> parameterMap) {
		return parameterMapToString(parameterMap, true);
	}

	@Override
	public String parameterMapToString(
		Map<String, String[]> parameterMap, boolean addQuestion) {

		if (parameterMap.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		if (addQuestion) {
			sb.append(StringPool.QUESTION);
		}

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();

			for (String value : values) {
				sb.append(name);
				sb.append(StringPool.EQUAL);
				sb.append(URLCodec.encodeURL(value));
				sb.append(StringPool.AMPERSAND);
			}
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	@Override
	public String protocolize(String url, ActionRequest actionRequest) {
		return protocolize(url, actionRequest.isSecure());
	}

	@Override
	public String protocolize(String url, boolean secure) {
		return protocolize(url, -1, secure);
	}

	@Override
	public String protocolize(String url, HttpServletRequest request) {
		return protocolize(url, request.isSecure());
	}

	@Override
	public String protocolize(String url, int port, boolean secure) {
		if (Validator.isNull(url)) {
			return url;
		}

		try {
			URL urlObj = new URL(url);

			String protocol = Http.HTTP;

			if (secure) {
				protocol = Http.HTTPS;
			}

			if (port == -1) {
				port = urlObj.getPort();
			}

			urlObj = new URL(
				protocol, urlObj.getHost(), port, urlObj.getFile());

			return urlObj.toString();
		}
		catch (Exception e) {
			return url;
		}
	}

	@Override
	public String protocolize(String url, RenderRequest renderRequest) {
		return protocolize(url, renderRequest.isSecure());
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	public void proxifyState(
		HttpState httpState, HostConfiguration hostConfiguration) {
	}

	@Override
	public String removeDomain(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		url = removeProtocol(url);

		int pos = url.indexOf(CharPool.SLASH);

		if (pos > 0) {
			return url.substring(pos);
		}

		return url;
	}

	@Override
	public String removeParameter(String url, String name) {
		if (Validator.isNull(url) || Validator.isNull(name)) {
			return url;
		}

		int pos = url.indexOf(CharPool.QUESTION);

		if (pos == -1) {
			return url;
		}

		String[] array = PortalUtil.stripURLAnchor(url, StringPool.POUND);

		url = array[0];

		String anchor = array[1];

		StringBundler sb = new StringBundler();

		sb.append(url.substring(0, pos + 1));

		String[] parameters = StringUtil.split(
			url.substring(pos + 1), CharPool.AMPERSAND);

		for (String parameter : parameters) {
			if (parameter.length() > 0) {
				String[] kvp = StringUtil.split(parameter, CharPool.EQUAL);

				String key = kvp[0];

				String value = StringPool.BLANK;

				if (kvp.length > 1) {
					value = kvp[1];
				}

				if (!key.equals(name)) {
					sb.append(key);
					sb.append(StringPool.EQUAL);
					sb.append(value);
					sb.append(StringPool.AMPERSAND);
				}
			}
		}

		url = StringUtil.replace(
			sb.toString(), StringPool.AMPERSAND + StringPool.AMPERSAND,
			StringPool.AMPERSAND);

		if (url.endsWith(StringPool.AMPERSAND)) {
			url = url.substring(0, url.length() - 1);
		}

		if (url.endsWith(StringPool.QUESTION)) {
			url = url.substring(0, url.length() - 1);
		}

		return url + anchor;
	}

	@Override
	public String removePathParameters(String uri) {
		if (Validator.isNull(uri)) {
			return uri;
		}

		int pos = uri.indexOf(CharPool.SEMICOLON);

		if (pos == -1) {
			return uri;
		}

		if (pos == 0) {
			throw new IllegalArgumentException("Unable to handle URI: " + uri);
		}

		String[] uriParts = StringUtil.split(uri.substring(1), CharPool.SLASH);

		StringBundler sb = new StringBundler(uriParts.length * 2);

		for (String uriPart : uriParts) {
			pos = uriPart.indexOf(CharPool.SEMICOLON);

			if (pos == -1) {
				sb.append(StringPool.SLASH);
				sb.append(uriPart);
			}
			else if (pos == 0) {
				continue;
			}
			else {
				sb.append(StringPool.SLASH);
				sb.append(uriPart.substring(0, pos));
			}
		}

		if (sb.length() == 0) {
			return StringPool.SLASH;
		}

		return sb.toString();
	}

	@Override
	public String removeProtocol(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		url = url.trim();

		if (url.startsWith(Http.HTTP_WITH_SLASH)) {
			return url.substring(Http.HTTP_WITH_SLASH.length());
		}
		else if (url.startsWith(Http.HTTPS_WITH_SLASH)) {
			return url.substring(Http.HTTPS_WITH_SLASH.length());
		}
		else {
			return url;
		}
	}

	@Override
	public String sanitizeHeader(String header) {
		if (header == null) {
			return null;
		}

		StringBuilder sb = null;

		for (int i = 0; i < header.length(); i++) {
			char c = header.charAt(i);

			if (((c <= 31) && (c != 9)) || (c == 127) || (c > 255)) {
				if (sb == null) {
					sb = new StringBuilder(header);
				}

				sb.setCharAt(i, CharPool.SPACE);
			}
		}

		if (sb != null) {
			header = sb.toString();
		}

		return header;
	}

	@Override
	public String setParameter(String url, String name, boolean value) {
		return setParameter(url, name, String.valueOf(value));
	}

	@Override
	public String setParameter(String url, String name, double value) {
		return setParameter(url, name, String.valueOf(value));
	}

	@Override
	public String setParameter(String url, String name, int value) {
		return setParameter(url, name, String.valueOf(value));
	}

	@Override
	public String setParameter(String url, String name, long value) {
		return setParameter(url, name, String.valueOf(value));
	}

	@Override
	public String setParameter(String url, String name, short value) {
		return setParameter(url, name, String.valueOf(value));
	}

	@Override
	public String setParameter(String url, String name, String value) {
		if (Validator.isNull(url) || Validator.isNull(name)) {
			return url;
		}

		url = removeParameter(url, name);

		return addParameter(url, name, value);
	}

	@Override
	public String shortenURL(String url) {
		if (url.length() <= URL_MAXIMUM_LENGTH) {
			return url;
		}

		return _shortenURL(
			url, 0, StringPool.QUESTION, StringPool.AMPERSAND,
			StringPool.EQUAL);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #shortenURL(String)}
	 */
	@Deprecated
	@Override
	public String shortenURL(String url, int count) {
		return shortenURL(url);
	}

	@Override
	public byte[] URLtoByteArray(Http.Options options) throws IOException {
		return URLtoByteArray(
			options.getLocation(), options.getMethod(), options.getHeaders(),
			options.getCookies(), options.getAuth(), options.getBody(),
			options.getFileParts(), options.getParts(), options.getResponse(),
			options.isFollowRedirects(), options.getTimeout());
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
			options.getCookies(), options.getAuth(), options.getBody(),
			options.getFileParts(), options.getParts(), options.getResponse(),
			options.isFollowRedirects(), options.getTimeout());
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
		return new String(URLtoByteArray(options));
	}

	@Override
	public String URLtoString(String location) throws IOException {
		return new String(URLtoByteArray(location));
	}

	@Override
	public String URLtoString(String location, boolean post)
		throws IOException {

		return new String(URLtoByteArray(location, post));
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
		String xml = null;

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

	protected void addProxyCredentials(
		URI uri, HttpClientContext httpClientContext) {

		String host = uri.getHost();

		if (!isProxyHost(host) || (_proxyCredentials == null)) {
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

	protected CloseableHttpClient getCloseableHttpClient(HttpHost proxyHost) {
		if (proxyHost != null) {
			return _proxyCloseableHttpClient;
		}

		return _closeableHttpClient;
	}

	protected RequestConfig.Builder getRequestConfigBuilder(
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

		int maxConnectionsPerHost = GetterUtil.getInteger(
			PropsUtil.get(
				HttpImpl.class.getName() + ".max.connections.per.host",
				new Filter(uri.getHost())));

		if ((maxConnectionsPerHost > 0) &&
			(maxConnectionsPerHost != _MAX_CONNECTIONS_PER_HOST)) {

			HttpRoute httpRoute = new HttpRoute(
				new HttpHost(uri.getHost(), uri.getPort()));

			_poolingHttpClientConnectionManager.setMaxPerRoute(
				httpRoute, maxConnectionsPerHost);
		}

		if (timeout == 0) {
			timeout = GetterUtil.getInteger(
				PropsUtil.get(
					HttpImpl.class.getName() + ".timeout",
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

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected boolean hasRequestHeader(HttpMethod httpMethod, String name) {
		throw new UnsupportedOperationException();
	}

	protected boolean hasRequestHeader(
		RequestBuilder requestBuilder, String name) {

		Header[] headers = requestBuilder.getHeaders(name);

		if (ArrayUtil.isEmpty(headers)) {
			return false;
		}

		return true;
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected void processPostMethod(
		PostMethod postMethod, List<Http.FilePart> fileParts,
		Map<String, String> parts) {

		throw new UnsupportedOperationException();
	}

	protected void processPostMethod(
		RequestBuilder requestBuilder, List<Http.FilePart> fileParts,
		Map<String, String> parts) {

		if ((fileParts == null) || fileParts.isEmpty()) {
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

			for (Http.FilePart filePart : fileParts) {
				ByteArrayBody byteArrayBody = new ByteArrayBody(
					filePart.getValue(), ContentType.DEFAULT_BINARY,
					filePart.getFileName());

				multipartEntityBuilder.addPart(
					filePart.getName(), byteArrayBody);
			}

			requestBuilder.setEntity(multipartEntityBuilder.build());
		}
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected org.apache.commons.httpclient.Cookie toCommonsCookie(
		Cookie cookie) {

		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected org.apache.commons.httpclient.Cookie[] toCommonsCookies(
		Cookie[] cookie) {

		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected org.apache.commons.httpclient.methods.multipart.FilePart
		toCommonsFilePart(Http.FilePart filePart) {

		throw new UnsupportedOperationException();
	}

	protected org.apache.http.cookie.Cookie toHttpCookie(Cookie cookie) {
		BasicClientCookie basicClientCookie = new BasicClientCookie(
			cookie.getName(), cookie.getValue());

		basicClientCookie.setDomain(cookie.getDomain());

		int maxAge = cookie.getMaxAge();

		if (maxAge > 0) {
			Date expiryDate = new Date(
				System.currentTimeMillis() + maxAge * 1000L);

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

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected Cookie toServletCookie(
		org.apache.commons.httpclient.Cookie commonsCookie) {

		throw new UnsupportedOperationException();
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

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	protected Cookie[] toServletCookies(
		org.apache.commons.httpclient.Cookie[] commonsCookies) {

		throw new UnsupportedOperationException();
	}

	protected byte[] URLtoByteArray(
			String location, Http.Method method, Map<String, String> headers,
			Cookie[] cookies, Http.Auth auth, Http.Body body,
			List<Http.FilePart> fileParts, Map<String, String> parts,
			Http.Response response, boolean followRedirects)
		throws IOException {

		return URLtoByteArray(
			location, method, headers, cookies, auth, body, fileParts, parts,
			response, followRedirects, 0);
	}

	protected byte[] URLtoByteArray(
			String location, Http.Method method, Map<String, String> headers,
			Cookie[] cookies, Http.Auth auth, Http.Body body,
			List<Http.FilePart> fileParts, Map<String, String> parts,
			Http.Response response, boolean followRedirects, int timeout)
		throws IOException {

		InputStream inputStream = URLtoInputStream(
			location, method, headers, cookies, auth, body, fileParts, parts,
			response, followRedirects, timeout);

		if (inputStream == null) {
			return null;
		}

		try {
			long contentLengthLong = response.getContentLengthLong();

			if (contentLengthLong > _MAX_BYTE_ARRAY_LENGTH) {
				StringBundler sb = new StringBundler(5);

				sb.append("Retrieving ");
				sb.append(location);
				sb.append(" yields a file of size ");
				sb.append(contentLengthLong);
				sb.append(
					" bytes that is too large to convert to a byte array");

				throw new OutOfMemoryError(sb.toString());
			}

			return FileUtil.getBytes(inputStream);
		}
		finally {
			inputStream.close();
		}
	}

	protected InputStream URLtoInputStream(
			String location, Http.Method method, Map<String, String> headers,
			Cookie[] cookies, Http.Auth auth, Http.Body body,
			List<Http.FilePart> fileParts, Map<String, String> parts,
			Http.Response response, boolean followRedirects)
		throws IOException {

		return URLtoInputStream(
			location, method, headers, cookies, auth, body, fileParts, parts,
			response, followRedirects, 0);
	}

	protected InputStream URLtoInputStream(
			String location, Http.Method method, Map<String, String> headers,
			Cookie[] cookies, Http.Auth auth, Http.Body body,
			List<Http.FilePart> fileParts, Map<String, String> parts,
			Http.Response response, boolean followRedirects, int timeout)
		throws IOException {

		URI uri = null;

		try {
			uri = new URI(location);
		}
		catch (URISyntaxException urise) {
			throw new IOException("Invalid URI: " + location, urise);
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

				uri = new URI(location);
			}

			HttpHost targetHttpHost = new HttpHost(
				uri.getHost(), uri.getPort(), uri.getScheme());

			RequestConfig.Builder requestConfigBuilder =
				getRequestConfigBuilder(uri, timeout);

			RequestConfig requestConfig = requestConfigBuilder.build();

			CloseableHttpClient httpClient = getCloseableHttpClient(
				requestConfig.getProxy());

			HttpClientContext httpClientContext = HttpClientContext.create();

			RequestBuilder requestBuilder = null;

			if (method.equals(Http.Method.POST) ||
				method.equals(Http.Method.PUT)) {

				if (method.equals(Http.Method.POST)) {
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

						_poolingHttpClientConnectionManager.setConnectionConfig(
							targetHttpHost, connectionConfigBuilder.build());
					}

					processPostMethod(requestBuilder, fileParts, parts);
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

			if ((method.equals(Http.Method.POST) ||
				 method.equals(Http.Method.PUT)) &&
				((body != null) ||
				 ((fileParts != null) && !fileParts.isEmpty()) ||
				 ((parts != null) && !parts.isEmpty())) &&
				!hasRequestHeader(requestBuilder, HttpHeaders.CONTENT_TYPE)) {

				requestBuilder.addHeader(
					HttpHeaders.CONTENT_TYPE,
					ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED_UTF8);
			}

			if (!hasRequestHeader(requestBuilder, HttpHeaders.USER_AGENT)) {
				requestBuilder.addHeader(
					HttpHeaders.USER_AGENT, _DEFAULT_USER_AGENT);
			}

			if (ArrayUtil.isNotEmpty(cookies)) {
				basicCookieStore = new BasicCookieStore();

				org.apache.http.cookie.Cookie[] httpCookies = toHttpCookies(
					cookies);

				basicCookieStore.addCookies(httpCookies);

				httpClientContext.setCookieStore(basicCookieStore);

				requestConfigBuilder.setCookieSpec(CookieSpecs.DEFAULT);
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
							cookies, auth, body, fileParts, parts, response,
							followRedirects, timeout);
					}
					else {
						response.setRedirect(locationHeaderValue);
					}
				}
			}

			long contentLengthLong = 0;

			Header contentLengthHeader = closeableHttpResponse.getFirstHeader(
				HttpHeaders.CONTENT_LENGTH);

			if (contentLengthHeader != null) {
				contentLengthLong = GetterUtil.getLong(
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
						catch (IOException ioe) {
							if (_log.isDebugEnabled()) {
								_log.debug("Unable to close response", ioe);
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
		catch (Exception e) {
			if (httpEntity != null) {
				EntityUtils.consumeQuietly(httpEntity);
			}

			if (closeableHttpResponse != null) {
				try {
					closeableHttpResponse.close();
				}
				catch (IOException ioe) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to close response", e);
					}
				}
			}

			throw new IOException(e);
		}
		finally {
			try {
				if (basicCookieStore != null) {
					_cookies.set(
						toServletCookies(basicCookieStore.getCookies()));
				}
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}
	}

	private boolean _isLetterOrNumber(char c) {
		if (((CharPool.NUMBER_0 <= c) && (c <= CharPool.NUMBER_9)) ||
			((CharPool.UPPER_CASE_A <= c) && (c <= CharPool.UPPER_CASE_Z)) ||
			((CharPool.LOWER_CASE_A <= c) && (c <= CharPool.LOWER_CASE_Z))) {

			return true;
		}

		return false;
	}

	private String _shortenURL(
		String encodedURL, int currentLength, String encodedQuestion,
		String encodedAmpersand, String encodedEqual) {

		if ((currentLength + encodedURL.length()) <= Http.URL_MAXIMUM_LENGTH) {
			return encodedURL;
		}

		int index = encodedURL.indexOf(encodedQuestion);

		if (index == -1) {
			return encodedURL;
		}

		StringBundler sb = new StringBundler();

		sb.append(encodedURL.substring(0, index));
		sb.append(encodedQuestion);

		String queryString = encodedURL.substring(
			index + encodedQuestion.length());

		String[] params = StringUtil.split(queryString, encodedAmpersand);

		params = ArrayUtil.unique(params);

		List<String> encodedRedirectParams = new ArrayList<>();

		for (String param : params) {
			if (param.contains("_backURL" + encodedEqual) ||
				param.contains("_redirect" + encodedEqual) ||
				param.contains("_returnToFullPageURL" + encodedEqual) ||
				(param.startsWith("redirect") &&
				 (param.indexOf(encodedEqual) != -1))) {

				encodedRedirectParams.add(param);
			}
			else {
				sb.append(param);
				sb.append(encodedAmpersand);
			}
		}

		if ((currentLength + sb.length()) > URL_MAXIMUM_LENGTH) {
			sb.setIndex(sb.index() - 1);

			return sb.toString();
		}

		for (String encodedRedirectParam : encodedRedirectParams) {
			int pos = encodedRedirectParam.indexOf(encodedEqual);

			String key = encodedRedirectParam.substring(0, pos);

			String redirect = encodedRedirectParam.substring(
				pos + encodedEqual.length());

			sb.append(key);
			sb.append(encodedEqual);

			int newLength = sb.length();

			redirect = _shortenURL(
				redirect, currentLength + newLength,
				URLCodec.encodeURL(encodedQuestion),
				URLCodec.encodeURL(encodedAmpersand),
				URLCodec.encodeURL(encodedEqual));

			newLength += redirect.length();

			if ((currentLength + newLength) > URL_MAXIMUM_LENGTH) {
				sb.setIndex(sb.index() - 2);
			}
			else {
				sb.append(redirect);
				sb.append(encodedAmpersand);
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final String _DEFAULT_USER_AGENT =
		"Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv 11.0) like Gecko";

	private static final int _MAX_BYTE_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

	private static final int _MAX_CONNECTIONS_PER_HOST = GetterUtil.getInteger(
		PropsUtil.get(HttpImpl.class.getName() + ".max.connections.per.host"),
		2);

	private static final int _MAX_TOTAL_CONNECTIONS = GetterUtil.getInteger(
		PropsUtil.get(HttpImpl.class.getName() + ".max.total.connections"), 20);

	private static final String _NON_PROXY_HOSTS = SystemProperties.get(
		"http.nonProxyHosts");

	private static final String _PROXY_AUTH_TYPE = GetterUtil.getString(
		PropsUtil.get(HttpImpl.class.getName() + ".proxy.auth.type"));

	private static final String _PROXY_HOST = GetterUtil.getString(
		SystemProperties.get("http.proxyHost"));

	private static final String _PROXY_NTLM_DOMAIN = GetterUtil.getString(
		PropsUtil.get(HttpImpl.class.getName() + ".proxy.ntlm.domain"));

	private static final String _PROXY_NTLM_HOST = GetterUtil.getString(
		PropsUtil.get(HttpImpl.class.getName() + ".proxy.ntlm.host"));

	private static final String _PROXY_PASSWORD = GetterUtil.getString(
		PropsUtil.get(HttpImpl.class.getName() + ".proxy.password"));

	private static final int _PROXY_PORT = GetterUtil.getInteger(
		SystemProperties.get("http.proxyPort"));

	private static final String _PROXY_USERNAME = GetterUtil.getString(
		PropsUtil.get(HttpImpl.class.getName() + ".proxy.username"));

	private static final String _TEMP_SLASH = "_LIFERAY_TEMP_SLASH_";

	private static final int _TIMEOUT = GetterUtil.getInteger(
		PropsUtil.get(HttpImpl.class.getName() + ".timeout"), 5000);

	private static final Log _log = LogFactoryUtil.getLog(HttpImpl.class);

	private static final ThreadLocal<Cookie[]> _cookies = new ThreadLocal<>();

	private final CloseableHttpClient _closeableHttpClient;
	private final Pattern _nonProxyHostsPattern;
	private final PoolingHttpClientConnectionManager
		_poolingHttpClientConnectionManager;
	private final List<String> _proxyAuthPrefs = new ArrayList<>();
	private final CloseableHttpClient _proxyCloseableHttpClient;
	private final Credentials _proxyCredentials;

}