/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.language;

import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.batch.engine.language.LanguageKeyResolver;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vendel Töreki
 */
@Component(service = LanguageKeyResolver.class)
public class LanguageKeyResolverImpl implements LanguageKeyResolver {

	@Override
	public void expand(long companyId, JSONObject jsonObject)
		throws ConfigurationException {

		if (!_isLanguageKeyResolutionEnabled(companyId)) {
			return;
		}

		_expand(jsonObject);
	}

	@Override
	public void expand(long companyId, Map<String, Object> fieldNameValueMap)
		throws ConfigurationException {

		if (!_isLanguageKeyResolutionEnabled(companyId)) {
			return;
		}

		_expand(fieldNameValueMap);
	}

	private void _expand(Object object) {
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)object;

			for (int i = 0; i < jsonArray.length(); i++) {
				_expand(jsonArray.get(i));
			}
		}
		else if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)object;

			for (String key : jsonObject.keySet()) {
				_expand(jsonObject.get(key));
			}

			Object value = jsonObject.get(_FOR_EACH_LANGUAGE_ID);

			if (!(value instanceof String)) {
				return;
			}

			jsonObject.remove(_FOR_EACH_LANGUAGE_ID);

			Map<String, String> translations = _resolve((String)value);

			for (Map.Entry<String, String> entry : translations.entrySet()) {
				if (jsonObject.has(entry.getKey())) {
					continue;
				}

				jsonObject.put(entry.getKey(), entry.getValue());
			}
		}
		else if (object instanceof List) {
			for (Object value : (List<Object>)object) {
				_expand(value);
			}
		}
		else if (object instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)object;

			for (Object value : map.values()) {
				_expand(value);
			}

			Object value = map.get(_FOR_EACH_LANGUAGE_ID);

			if (!(value instanceof String)) {
				return;
			}

			map.remove(_FOR_EACH_LANGUAGE_ID);

			Map<String, String> translations = _resolve((String)value);

			for (Map.Entry<String, String> entry : translations.entrySet()) {
				map.putIfAbsent(entry.getKey(), entry.getValue());
			}
		}
	}

	private boolean _isLanguageKeyResolutionEnabled(long companyId)
		throws ConfigurationException {

		BatchEngineTaskCompanyConfiguration
			batchEngineTaskCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					BatchEngineTaskCompanyConfiguration.class, companyId);

		return batchEngineTaskCompanyConfiguration.
			languageKeyResolutionEnabled();
	}

	private Map<String, String> _resolve(String key) {
		Map<String, String> translations = new LinkedHashMap<>();

		for (Locale locale : _language.getAvailableLocales()) {
			String translation = _language.get(locale, key, null);

			if (Validator.isNull(translation)) {
				continue;
			}

			translations.put(LocaleUtil.toLanguageId(locale), translation);
		}

		if (translations.isEmpty() && _log.isWarnEnabled()) {
			_log.warn("Unable to resolve language key \"" + key + "\"");
		}

		return translations;
	}

	private static final String _FOR_EACH_LANGUAGE_ID =
		"[$FOR_EACH_LANGUAGE_ID$]";

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageKeyResolverImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

}