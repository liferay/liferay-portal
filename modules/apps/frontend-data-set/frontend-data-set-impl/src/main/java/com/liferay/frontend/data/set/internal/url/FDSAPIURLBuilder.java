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
import com.liferay.portal.kernel.json.JSONObject;
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
		return build(true);
	}

	public String build(boolean interpolate) {
		StringBundler sb = new StringBundler(
			3 + (_queryStringItems.size() * 2));

		sb.append("/o");
		sb.append(
			StringUtil.replaceLast(
				_restApplication, "/v1.0", StringPool.BLANK));
		sb.append(_restEndpoint);

		_appendParameters(true, sb);

		String apiURL = sb.toString();

		if (!interpolate) {
			return apiURL;
		}

		return _interpolateTokens(apiURL);
	}

	public String buildQueryString() {
		return buildQueryString(true);
	}

	public String buildQueryString(boolean interpolate) {
		StringBundler sb = new StringBundler(_queryStringItems.size() * 2);

		_appendParameters(false, sb);

		String query = sb.toString();

		if (Validator.isNull(query)) {
			return null;
		}

		if (!interpolate) {
			return query;
		}

		return _interpolateTokens(query);
	}

	public FDSAPIURLBuilder setTokenResolutions(
		JSONObject tokenResolutionsJSONObject) {

		_tokenResolutionsJSONObject = tokenResolutionsJSONObject;

		return this;
	}

	private void _appendParameters(
		boolean includeQuestionMark, StringBundler sb) {

		if (_queryStringItems.isEmpty()) {
			return;
		}

		if (includeQuestionMark) {
			sb.append(CharPool.QUESTION);
		}

		int count = 0;

		for (String parameter : _queryStringItems) {
			sb.append(parameter);

			count++;

			if (count < _queryStringItems.size()) {
				sb.append(CharPool.AMPERSAND);
			}
		}
	}

	private String _interpolateTokens(String text) {

		// Interpolate using provided resolved tokens

		if (_tokenResolutionsJSONObject != null) {
			for (String key : _tokenResolutionsJSONObject.keySet()) {
				text = StringUtil.replace(
					text, "{" + key + "}",
					_tokenResolutionsJSONObject.getString(key));
			}
		}

		// Interpolate using registered resolvers

		FDSAPIURLResolver fdsAPIURLResolver =
			_fdsAPIURLResolverRegistry.getFDSAPIURLResolver(
				_restApplication, _restSchema);

		if (fdsAPIURLResolver != null) {
			try {
				text = fdsAPIURLResolver.resolve(text, _httpServletRequest);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		// Interpolate using default tokens

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		text = StringUtil.replace(
			text, "{scopeKey}", String.valueOf(themeDisplay.getScopeGroupId()));
		text = StringUtil.replace(
			text, "{siteId}", String.valueOf(themeDisplay.getScopeGroupId()));
		text = StringUtil.replace(
			text, "{userId}", String.valueOf(themeDisplay.getUserId()));

		if (StringUtil.contains(text, "{") && _log.isWarnEnabled()) {
			_log.warn("Unresolved token in API URL: " + text);
		}

		return text;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSAPIURLBuilder.class);

	private final FDSAPIURLResolverRegistry _fdsAPIURLResolverRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final List<String> _queryStringItems = new LinkedList<>();
	private final String _restApplication;
	private final String _restEndpoint;
	private final String _restSchema;
	private JSONObject _tokenResolutionsJSONObject;

}