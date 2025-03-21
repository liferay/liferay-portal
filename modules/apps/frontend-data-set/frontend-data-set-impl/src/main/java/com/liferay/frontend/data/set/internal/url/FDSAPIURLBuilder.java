/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.url;

import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.frontend.data.set.url.FDSAPIURLResolverRegistry;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Sanz
 */
public class FDSAPIURLBuilder {

	public FDSAPIURLBuilder(
		FDSAPIURLResolverRegistry fdsAPIURLResolverRegistry,
		HttpServletRequest httpServletRequest, String restApplication,
		String restEndpoint, String restSchema) {

		_fdsAPIURLResolverRegistry = fdsAPIURLResolverRegistry;
		_httpServletRequest = httpServletRequest;
		_restApplication = restApplication;
		_restEndpoint = restEndpoint;
		_restSchema = restSchema;
	}

	public FDSAPIURLBuilder addParameter(String name, String value) {
		if (Validator.isNotNull(name) && Validator.isNotNull(value)) {
			_queryStringItems.add(name + CharPool.EQUAL + value);
		}

		return this;
	}

	public FDSAPIURLBuilder addQueryString(String queryString) {
		if (Validator.isNotNull(queryString)) {
			_queryStringItems.add(queryString);
		}

		return this;
	}

	public String build() {
		StringBundler sb = new StringBundler(
			3 + (_queryStringItems.size() * 2));

		sb.append("/o");
		sb.append(
			StringUtil.replaceLast(
				_restApplication, "/v1.0", StringPool.BLANK));
		sb.append(_restEndpoint);

		_appendParameters(sb);

		return _interpolate(_resolveParameters(sb.toString()));
	}

	private void _appendParameters(StringBundler sb) {
		if (_queryStringItems.isEmpty()) {
			return;
		}

		sb.append(CharPool.QUESTION);

		int count = 0;

		for (String parameter : _queryStringItems) {
			sb.append(parameter);

			count++;

			if (count < _queryStringItems.size()) {
				sb.append(CharPool.AMPERSAND);
			}
		}
	}

	private String _interpolate(String apiURL) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		apiURL = StringUtil.replace(
			apiURL, "{siteId}", String.valueOf(themeDisplay.getScopeGroupId()));
		apiURL = StringUtil.replace(
			apiURL, "{scopeKey}",
			String.valueOf(themeDisplay.getScopeGroupId()));
		apiURL = StringUtil.replace(
			apiURL, "{userId}", String.valueOf(themeDisplay.getUserId()));

		if (StringUtil.contains(apiURL, "{") && _log.isWarnEnabled()) {
			_log.warn("Unsupported parameter in API URL: " + apiURL);
		}

		return apiURL;
	}

	private String _resolveParameters(String apiURL) {
		FDSAPIURLResolver fdsAPIURLResolver =
			_fdsAPIURLResolverRegistry.getFDSAPIURLResolver(
				_restApplication, _restSchema);

		if (fdsAPIURLResolver == null) {
			return apiURL;
		}

		try {
			return fdsAPIURLResolver.resolve(apiURL, _httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return apiURL;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSAPIURLBuilder.class);

	private final FDSAPIURLResolverRegistry _fdsAPIURLResolverRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final List<String> _queryStringItems = new LinkedList<>();
	private final String _restApplication;
	private final String _restEndpoint;
	private final String _restSchema;

}