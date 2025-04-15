/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.display.context;

import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class FriendlyURLSeparatorCompanyConfigurationDisplayContext {

	public FriendlyURLSeparatorCompanyConfigurationDisplayContext(
		FriendlyURLSeparatorConfigurationManager
			friendlyURLSeparatorConfigurationManager,
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		Language language, Portal portal) {

		_friendlyURLSeparatorConfigurationManager =
			friendlyURLSeparatorConfigurationManager;
		_httpServletRequest = httpServletRequest;
		_jsonFactory = jsonFactory;
		_language = language;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public JSONArray getConfigurableFriendlyURLSeparatorsJSONArray()
		throws Exception {

		if (_configurableFriendlyURLSeparatorsJSONArray != null) {
			return _configurableFriendlyURLSeparatorsJSONArray;
		}

		JSONObject configuredFriendlyURLSeparatorsJSONObject =
			_getConfiguredFriendlyURLSeparatorsJSONObject();
		String namespace = _portal.getPortletNamespace(_themeDisplay.getPpid());
		Map<String, String[]> parameters =
			_httpServletRequest.getParameterMap();

		List<JSONObject> list = TransformUtil.transform(
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolversAsCollection(),
			friendlyURLResolver -> {
				if (!friendlyURLResolver.isURLSeparatorConfigurable() ||
					Validator.isNull(friendlyURLResolver.getKey())) {

					return null;
				}

				return JSONUtil.put(
					"defaultValue",
					StringUtil.removeSubstring(
						friendlyURLResolver.getDefaultURLSeparator(),
						StringPool.SLASH)
				).put(
					"label",
					_language.get(
						_themeDisplay.getLocale(),
						friendlyURLResolver.getKey() + "-url-separator")
				).put(
					"name", namespace + friendlyURLResolver.getKey()
				).put(
					"value",
					() -> {
						if (parameters.containsKey(
								friendlyURLResolver.getKey())) {

							return ParamUtil.getString(
								_httpServletRequest,
								friendlyURLResolver.getKey());
						}

						String friendlyURLSeparator =
							configuredFriendlyURLSeparatorsJSONObject.getString(
								friendlyURLResolver.getKey());

						if (Validator.isNull(friendlyURLSeparator)) {
							friendlyURLSeparator =
								friendlyURLResolver.getDefaultURLSeparator();
						}

						return StringUtil.removeSubstring(
							friendlyURLSeparator, StringPool.SLASH);
					}
				);
			});

		Collections.sort(
			list,
			Comparator.comparing(jsonObject -> jsonObject.getString("label")));

		_configurableFriendlyURLSeparatorsJSONArray = JSONUtil.toJSONArray(
			list, jsonObject -> jsonObject);

		return _configurableFriendlyURLSeparatorsJSONArray;
	}

	public JSONObject getErrorsJSONObject() {
		String errors = ParamUtil.getString(_httpServletRequest, "errors");

		try {
			if (Validator.isNotNull(errors)) {
				return _jsonFactory.createJSONObject(errors);
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return _jsonFactory.createJSONObject();
	}

	public String getSampleURL() {
		return _SAMPLE_URL;
	}

	public Map<String, Object> getSeparatorFieldsProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"errors", getErrorsJSONObject()
		).put(
			"fields", getConfigurableFriendlyURLSeparatorsJSONArray()
		).put(
			"url", getSampleURL()
		).build();
	}

	private JSONObject _getConfiguredFriendlyURLSeparatorsJSONObject() {
		try {
			return _friendlyURLSeparatorConfigurationManager.
				getFriendlyURLSeparatorsJSONObject(
					_themeDisplay.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _jsonFactory.createJSONObject();
	}

	private static final String _SAMPLE_URL = "http://www.sitename.com";

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLSeparatorCompanyConfigurationDisplayContext.class.getName());

	private JSONArray _configurableFriendlyURLSeparatorsJSONArray;
	private final FriendlyURLSeparatorConfigurationManager
		_friendlyURLSeparatorConfigurationManager;
	private final HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory;
	private final Language _language;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}