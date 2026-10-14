/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportURL {

	public StaticSiteExportURL(Set<String> portalHostNames, String url) {
		url = StringUtil.trim(url);

		String uriFragment = StringPool.BLANK;

		int poundIndex = url.indexOf(CharPool.POUND);

		if (poundIndex != -1) {
			uriFragment = url.substring(poundIndex);

			url = url.substring(0, poundIndex);
		}

		String urlWithoutURIFragment = url;

		String queryString = null;

		int questionIndex = url.indexOf(CharPool.QUESTION);

		if (questionIndex != -1) {
			queryString = url.substring(questionIndex + 1);

			url = url.substring(0, questionIndex);
		}

		String scheme = _getScheme(url);

		if (scheme != null) {
			url = url.substring(scheme.length() + 1);
		}

		String host = null;
		String path = url;

		if (url.startsWith(StringPool.DOUBLE_SLASH)) {
			url = url.substring(StringPool.DOUBLE_SLASH.length());

			int slashIndex = url.indexOf(CharPool.SLASH);

			if (slashIndex == -1) {
				host = url;
				path = StringPool.BLANK;
			}
			else {
				host = url.substring(0, slashIndex);
				path = url.substring(slashIndex);
			}
		}

		if ((host != null) && portalHostNames.contains(_getHostName(host))) {
			host = null;
			scheme = null;

			if (Validator.isNull(path)) {
				path = StringPool.SLASH;
			}

			if (queryString == null) {
				urlWithoutURIFragment = path;
			}
			else {
				urlWithoutURIFragment =
					path + StringPool.QUESTION + queryString;
			}
		}

		_portalHostNames = portalHostNames;

		_host = host;
		_path = path;
		_queryString = queryString;
		_scheme = scheme;
		_uriFragment = uriFragment;
		_url = urlWithoutURIFragment;
	}

	public StaticSiteExportURL(String url) {
		this(Collections.emptySet(), url);
	}

	public String getArchivePath(String extension) {
		String path = StringUtil.removeFirst(_path, StringPool.SLASH);

		if (Validator.isNotNull(path)) {
			path = _addExtension(extension, path);
		}

		if (_host != null) {
			String host = StringUtil.replace(
				_host, CharPool.COLON, CharPool.UNDERLINE);

			String hostPath = "external/" + host;

			if (Validator.isNull(path)) {
				path = hostPath;
			}
			else {
				path = hostPath + StringPool.SLASH + path;
			}
		}

		if (Validator.isNull(_queryString)) {
			return path;
		}

		String fileName = _getFileName(path);

		int periodIndex = fileName.lastIndexOf(CharPool.PERIOD);

		String digest = DigesterUtil.digestHex(
			DigesterUtil.SHA_256, _queryString);

		if (periodIndex == -1) {
			return path + StringPool.PERIOD + digest;
		}

		String directory = path.substring(0, path.length() - fileName.length());

		return StringBundler.concat(
			directory, fileName.substring(0, periodIndex), StringPool.PERIOD,
			digest, fileName.substring(periodIndex));
	}

	public String getDispatchPath() {
		String path = HttpComponentsUtil.decodePath(_path);

		String moduleName = getModuleName();

		if (moduleName == null) {
			return path;
		}

		return StringUtil.removeFirst(path, _MODULE_PATH_PREFIX + moduleName);
	}

	public String getHostName() {
		if (_host == null) {
			return null;
		}

		return _getHostName(_host);
	}

	public String getModuleName() {
		if (!_path.startsWith(_MODULE_PATH_PREFIX)) {
			return null;
		}

		int slashIndex = _path.indexOf(
			CharPool.SLASH, _MODULE_PATH_PREFIX.length());

		if (slashIndex == -1) {
			return null;
		}

		return _path.substring(_MODULE_PATH_PREFIX.length(), slashIndex);
	}

	public String getPathInfo() {
		String dispatchPath = getDispatchPath();

		int slashIndex = dispatchPath.indexOf(CharPool.SLASH, 1);

		if (slashIndex == -1) {
			return null;
		}

		return dispatchPath.substring(slashIndex);
	}

	public String getQueryString() {
		return _queryString;
	}

	public String getScheme() {
		return _scheme;
	}

	public String getServletPath() {
		String dispatchPath = getDispatchPath();

		int slashIndex = dispatchPath.indexOf(CharPool.SLASH, 1);

		if (slashIndex == -1) {
			return dispatchPath;
		}

		return dispatchPath.substring(0, slashIndex);
	}

	public String getURIFragment() {
		return _uriFragment;
	}

	public String getURL() {
		return _url;
	}

	public boolean hasScheme() {
		if (_scheme != null) {
			return true;
		}

		return false;
	}

	public boolean isExternal() {
		if (_host != null) {
			return true;
		}

		return false;
	}

	public boolean isResource() {
		if (isExternal() || hasScheme()) {
			return false;
		}

		for (String resourcePathPrefix : _RESOURCE_PATH_PREFIXES) {
			if (_path.startsWith(resourcePathPrefix)) {
				return true;
			}
		}

		return false;
	}

	public StaticSiteExportURL resolve(String relativeURL) {
		if (Validator.isNull(relativeURL)) {
			return null;
		}

		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
			_portalHostNames, relativeURL);

		if (Validator.isNull(staticSiteExportURL.getURL())) {
			return null;
		}

		if (staticSiteExportURL.isExternal()) {
			return staticSiteExportURL;
		}

		if (staticSiteExportURL.hasScheme()) {
			return null;
		}

		if (staticSiteExportURL._path.startsWith(StringPool.SLASH)) {
			return _resolve(staticSiteExportURL.getURL());
		}

		int slashIndex = _path.lastIndexOf(CharPool.SLASH);

		if (slashIndex == -1) {
			return null;
		}

		List<String> names = new ArrayList<>();

		for (String name :
				StringUtil.split(
					_path.substring(0, slashIndex), CharPool.SLASH)) {

			if (Validator.isNotNull(name)) {
				names.add(name);
			}
		}

		for (String name :
				StringUtil.split(
					staticSiteExportURL.getURL(), CharPool.SLASH)) {

			if (Objects.equals(name, StringPool.PERIOD)) {
				continue;
			}

			if (Objects.equals(name, StringPool.DOUBLE_PERIOD)) {
				if (names.isEmpty()) {
					return null;
				}

				names.remove(names.size() - 1);

				continue;
			}

			names.add(name);
		}

		return _resolve(
			StringPool.SLASH + StringUtil.merge(names, StringPool.SLASH));
	}

	private String _addExtension(String extension, String path) {
		String fileName = _getFileName(path);

		if (fileName.indexOf(CharPool.PERIOD) != -1) {
			return path;
		}

		if (extension == null) {
			extension = _getPathExtension(path);
		}

		if (extension == null) {
			extension = _getQueryStringExtension();
		}

		if (extension == null) {
			return path;
		}

		return path + StringPool.PERIOD + extension;
	}

	private String _getExtension(String name) {
		int periodIndex = name.lastIndexOf(CharPool.PERIOD);

		if (periodIndex == -1) {
			return null;
		}

		return name.substring(periodIndex + 1);
	}

	private String _getFileName(String path) {
		return path.substring(path.lastIndexOf(CharPool.SLASH) + 1);
	}

	private String _getHostName(String host) {
		String hostName = host;

		int atIndex = hostName.lastIndexOf(CharPool.AT);

		if (atIndex != -1) {
			hostName = hostName.substring(atIndex + 1);
		}

		int colonIndex = hostName.indexOf(CharPool.COLON);

		if (colonIndex != -1) {
			hostName = hostName.substring(0, colonIndex);
		}

		return StringUtil.toLowerCase(hostName);
	}

	private String _getPathExtension(String path) {
		int slashIndex = path.lastIndexOf(CharPool.SLASH);

		if (slashIndex == -1) {
			return null;
		}

		String[] names = StringUtil.split(
			path.substring(0, slashIndex), CharPool.SLASH);

		for (int i = names.length - 1; i >= 0; i--) {
			String extension = _getExtension(names[i]);

			if (extension == null) {
				continue;
			}

			if (!_isExtension(extension)) {
				return null;
			}

			return extension;
		}

		return null;
	}

	private String _getQueryStringExtension() {
		if (Validator.isNull(_queryString)) {
			return null;
		}

		String extension = null;

		for (String parameter :
				StringUtil.split(_queryString, CharPool.AMPERSAND)) {

			if (parameter.startsWith(StringPool.SLASH)) {
				String parameterExtension = _getExtension(parameter);

				if (parameterExtension != null) {
					extension = parameterExtension;
				}
			}
		}

		if (!_isExtension(extension)) {
			return null;
		}

		return extension;
	}

	private String _getScheme(String url) {
		int colonIndex = url.indexOf(CharPool.COLON);

		if (colonIndex == -1) {
			return null;
		}

		int slashIndex = url.indexOf(CharPool.SLASH);

		if ((slashIndex != -1) && (slashIndex < colonIndex)) {
			return null;
		}

		return url.substring(0, colonIndex);
	}

	private boolean _isExtension(String extension) {
		if (Validator.isNull(extension) || (extension.length() > 5)) {
			return false;
		}

		for (char c : extension.toCharArray()) {
			if (!Validator.isChar(c) && !Validator.isDigit(c)) {
				return false;
			}
		}

		return true;
	}

	private StaticSiteExportURL _resolve(String path) {
		if (_host == null) {
			return new StaticSiteExportURL(_portalHostNames, path);
		}

		String scheme = _scheme;

		if (scheme == null) {
			scheme = Http.HTTPS;
		}

		return new StaticSiteExportURL(
			_portalHostNames,
			StringBundler.concat(
				scheme, StringPool.COLON, StringPool.DOUBLE_SLASH, _host,
				path));
	}

	private static final String _MODULE_PATH_PREFIX = "/o/";

	private static final String[] _RESOURCE_PATH_PREFIXES = {
		"/combo", "/documents/", "/image/", "/o/", "/webserver/"
	};

	private final String _host;
	private final String _path;
	private final Set<String> _portalHostNames;
	private final String _queryString;
	private final String _scheme;
	private final String _uriFragment;
	private final String _url;

}