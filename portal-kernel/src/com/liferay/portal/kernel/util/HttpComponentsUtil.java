/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tina Tian
 */
public class HttpComponentsUtil {

	public static String addParameter(String url, String name, boolean value) {
		return addParameter(url, name, String.valueOf(value));
	}

	public static String addParameter(String url, String name, double value) {
		return addParameter(url, name, String.valueOf(value));
	}

	public static String addParameter(String url, String name, int value) {
		return addParameter(url, name, String.valueOf(value));
	}

	public static String addParameter(String url, String name, long value) {
		return addParameter(url, name, String.valueOf(value));
	}

	public static String addParameter(String url, String name, short value) {
		return addParameter(url, name, String.valueOf(value));
	}

	public static String addParameter(String url, String name, String value) {
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

	public static String addParameters(String url, Object... parameters) {
		if ((parameters.length % 2) != 0) {
			throw new IllegalArgumentException(
				"Parameters length is not an even number");
		}

		for (int i = 0; i < parameters.length; i += 2) {
			String name = String.valueOf(parameters[i]);
			String value = String.valueOf(parameters[i + 1]);

			url = addParameter(url, name, value);
		}

		return url;
	}

	public static String decodePath(String path) {
		return decodeURL(path);
	}

	public static String decodeURL(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		try {
			return URLCodec.decodeURL(url, StringPool.UTF8);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isWarnEnabled()) {
				_log.warn(illegalArgumentException);
			}
		}

		return StringPool.BLANK;
	}

	public static String encodeParameters(String url) {
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

	public static String encodePath(String path) {
		if (Validator.isNull(path)) {
			return path;
		}

		path = StringUtil.replace(
			path, new char[] {CharPool.PLUS, CharPool.SLASH, CharPool.TILDE},
			new String[] {_TEMP_PLUS, _TEMP_SLASH, _TEMP_TILDE});
		path = URLCodec.encodeURL(path, true);
		path = StringUtil.replace(
			path, new String[] {_TEMP_PLUS, _TEMP_SLASH, _TEMP_TILDE},
			new String[] {StringPool.PLUS, StringPool.SLASH, StringPool.TILDE});

		return path;
	}

	public static String fixPath(String path) {
		return fixPath(path, true, true);
	}

	public static String fixPath(
		String path, boolean leading, boolean trailing) {

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

	public static String getCompleteURL(HttpServletRequest httpServletRequest) {
		StringBuffer sb = httpServletRequest.getRequestURL();

		if (sb == null) {
			sb = new StringBuffer();
		}

		if (httpServletRequest.getQueryString() != null) {
			sb.append(StringPool.QUESTION);
			sb.append(httpServletRequest.getQueryString());
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

		if (httpServletRequest.isRequestedSessionIdFromURL()) {
			HttpSession httpSession = httpServletRequest.getSession();

			String sessionId = httpSession.getId();

			completeURL = PortalUtil.getURLWithSessionId(
				completeURL, sessionId);
		}

		if (_log.isWarnEnabled() && completeURL.contains("?&")) {
			_log.warn("Invalid url " + completeURL);
		}

		return completeURL;
	}

	public static String getDomain(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		URI uri = null;

		try {
			uri = getURI(url);
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}

		if (uri == null) {
			return StringPool.BLANK;
		}

		String host = uri.getHost();

		if (host == null) {
			return StringPool.BLANK;
		}

		return host;
	}

	public static String getIpAddress(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		try {
			URL urlObj = new URL(url);

			InetAddress address = InetAddressUtil.getInetAddressByName(
				urlObj.getHost());

			return address.getHostAddress();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return url;
		}
	}

	public static String getParameter(String url, String name) {
		return getParameter(url, name, true);
	}

	public static String getParameter(
		String url, String name, boolean escaped) {

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

	public static Map<String, String[]> getParameterMap(String queryString) {
		return parameterMapFromString(queryString);
	}

	public static String getPath(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		URI uri = null;

		try {
			uri = getURI(url);
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}

		if (uri == null) {
			return StringPool.BLANK;
		}

		String path = uri.getPath();

		if (path == null) {
			return StringPool.BLANK;
		}

		return path;
	}

	public static String getProtocol(ActionRequest actionRequest) {
		return getProtocol(actionRequest.isSecure());
	}

	public static String getProtocol(boolean secure) {
		if (!secure) {
			return Http.HTTP;
		}

		return Http.HTTPS;
	}

	public static String getProtocol(HttpServletRequest httpServletRequest) {
		return getProtocol(httpServletRequest.isSecure());
	}

	public static String getProtocol(RenderRequest renderRequest) {
		return getProtocol(renderRequest.isSecure());
	}

	public static String getProtocol(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		URI uri = null;

		try {
			uri = getURI(url);
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}

		if (uri == null) {
			return StringPool.BLANK;
		}

		String scheme = uri.getScheme();

		if (scheme == null) {
			return StringPool.BLANK;
		}

		return scheme;
	}

	public static String getQueryString(HttpServletRequest httpServletRequest) {
		if (isForwarded(httpServletRequest)) {
			return GetterUtil.getString(
				httpServletRequest.getAttribute(
					JavaConstants.JAVAX_SERVLET_FORWARD_QUERY_STRING));
		}

		return httpServletRequest.getQueryString();
	}

	public static String getQueryString(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		URI uri = null;

		try {
			uri = getURI(url);
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}
		}

		if (uri == null) {
			return StringPool.BLANK;
		}

		String queryString = uri.getRawQuery();

		if (queryString == null) {
			return StringPool.BLANK;
		}

		return queryString;
	}

	public static String getRequestURL(HttpServletRequest httpServletRequest) {
		return String.valueOf(httpServletRequest.getRequestURL());
	}

	public static URI getURI(String uriString) throws URISyntaxException {
		Map<String, URI> uris = _uris.get();

		uriString = uriString.trim();

		URI uri = uris.get(uriString);

		if (uri == null) {
			uri = new URI(uriString);

			uris.put(uriString, uri);
		}

		return uri;
	}

	public static boolean hasDomain(String url) {
		if (Validator.isNull(url)) {
			return false;
		}

		return Validator.isNotNull(getDomain(url));
	}

	public static boolean hasProtocol(String url) {
		if (Validator.isNull(url) || (url.indexOf(CharPool.COLON) == -1)) {
			return false;
		}

		return Validator.isNotNull(getProtocol(url));
	}

	public static boolean isForwarded(HttpServletRequest httpServletRequest) {
		String forwardedRequestURI = (String)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI);

		if (forwardedRequestURI != null) {
			return true;
		}

		return false;
	}

	public static boolean isSecure(String url) {
		return StringUtil.equalsIgnoreCase(getProtocol(url), Http.HTTPS);
	}

	public static String normalizePath(String uri) {
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

		StringBundler sb = new StringBundler((parts.size() * 2) + 2);

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

	public static Map<String, String[]> parameterMapFromString(
		String queryString) {

		Map<String, String[]> parameterMap = new LinkedHashMap<>();

		if (Validator.isNull(queryString)) {
			return parameterMap;
		}

		String[] parameters = StringUtil.split(queryString, CharPool.AMPERSAND);

		for (String parameter : parameters) {
			if (parameter.length() == 0) {
				continue;
			}

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
				catch (IllegalArgumentException illegalArgumentException) {
					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Skipping parameter with key ", key,
								" because of invalid value ", kvp[1]),
							illegalArgumentException);
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

		return parameterMap;
	}

	public static String parameterMapToString(
		Map<String, String[]> parameterMap) {

		return parameterMapToString(parameterMap, true);
	}

	public static String parameterMapToString(
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

	public static String protocolize(String url, ActionRequest actionRequest) {
		return protocolize(url, actionRequest.isSecure());
	}

	public static String protocolize(String url, boolean secure) {
		return protocolize(url, -1, secure);
	}

	public static String protocolize(
		String url, HttpServletRequest httpServletRequest) {

		return protocolize(url, httpServletRequest.isSecure());
	}

	public static String protocolize(String url, int port, boolean secure) {
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
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return url;
		}
	}

	public static String protocolize(String url, RenderRequest renderRequest) {
		return protocolize(url, renderRequest.isSecure());
	}

	public static String removeDomain(String url) {
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

	public static String removeParameter(String url, String name) {
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

	public static String removePathParameters(String uri) {
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

				continue;
			}

			if (pos != 0) {
				sb.append(StringPool.SLASH);
				sb.append(uriPart.substring(0, pos));
			}
		}

		if (sb.length() == 0) {
			return StringPool.SLASH;
		}

		return sb.toString();
	}

	public static String removeProtocol(String url) {
		String protocol = getProtocol(url);

		if (Validator.isNotNull(protocol)) {
			url = url.trim();

			if (url.regionMatches(
					protocol.length(), Http.PROTOCOL_DELIMITER, 0,
					Http.PROTOCOL_DELIMITER.length())) {

				return url.substring(
					protocol.length() + Http.PROTOCOL_DELIMITER.length());
			}

			return url.substring(protocol.length() + StringPool.COLON.length());
		}

		return url;
	}

	public static String sanitizeHeader(String header) {
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

	public static String setParameter(String url, String name, boolean value) {
		return setParameter(url, name, String.valueOf(value));
	}

	public static String setParameter(String url, String name, double value) {
		return setParameter(url, name, String.valueOf(value));
	}

	public static String setParameter(String url, String name, int value) {
		return setParameter(url, name, String.valueOf(value));
	}

	public static String setParameter(String url, String name, long value) {
		return setParameter(url, name, String.valueOf(value));
	}

	public static String setParameter(String url, String name, short value) {
		return setParameter(url, name, String.valueOf(value));
	}

	public static String setParameter(String url, String name, String value) {
		if (Validator.isNull(url) || Validator.isNull(name)) {
			return url;
		}

		url = removeParameter(url, name);

		return addParameter(url, name, value);
	}

	public static String shortenURL(String url) {
		if (url.length() <= Http.URL_MAXIMUM_LENGTH) {
			return url;
		}

		return _shortenURL(
			url, 0, StringPool.QUESTION, StringPool.AMPERSAND,
			StringPool.EQUAL);
	}

	private static String _shortenURL(
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

		if ((currentLength + sb.length()) > Http.URL_MAXIMUM_LENGTH) {
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

			if ((currentLength + newLength) > Http.URL_MAXIMUM_LENGTH) {
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

	private static final String _TEMP_PLUS = "_LIFERAY_TEMP_PLUS_";

	private static final String _TEMP_SLASH = "_LIFERAY_TEMP_SLASH_";

	private static final String _TEMP_TILDE = "_LIFERAY_TEMP_TILDE_";

	private static final Log _log = LogFactoryUtil.getLog(
		HttpComponentsUtil.class);

	private static final ThreadLocal<Map<String, URI>> _uris =
		new CentralizedThreadLocal<>(
			HttpComponentsUtil.class + "._uris", HashMap::new);

}