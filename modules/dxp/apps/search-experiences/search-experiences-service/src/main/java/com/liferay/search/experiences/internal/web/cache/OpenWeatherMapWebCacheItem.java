/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.web.cache;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.search.experiences.blueprint.exception.InvalidWebCacheItemException;
import com.liferay.search.experiences.internal.configuration.OpenWeatherMapConfiguration;

import java.beans.ExceptionListener;

/**
 * @author Brian Wing Shun Chan
 */
public class OpenWeatherMapWebCacheItem implements WebCacheItem {

	public static JSONObject get(
		long companyId, ExceptionListener exceptionListener, String latitude,
		String longitude,
		OpenWeatherMapConfiguration openWeatherMapConfiguration) {

		if (!openWeatherMapConfiguration.enabled()) {
			return JSONFactoryUtil.createJSONObject();
		}

		try {
			return (JSONObject)WebCachePoolUtil.get(
				StringBundler.concat(
					OpenWeatherMapWebCacheItem.class.getName(),
					StringPool.POUND, companyId, StringPool.POUND,
					openWeatherMapConfiguration.apiKey(), StringPool.POUND,
					openWeatherMapConfiguration.apiURL(), StringPool.POUND,
					latitude, StringPool.POUND, longitude),
				new OpenWeatherMapWebCacheItem(
					companyId, latitude, longitude,
					openWeatherMapConfiguration));
		}
		catch (Exception exception) {
			exceptionListener.exceptionThrown(exception);

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	public OpenWeatherMapWebCacheItem(
		long companyId, String latitude, String longitude,
		OpenWeatherMapConfiguration openWeatherMapConfiguration) {

		_companyId = companyId;
		_latitude = latitude;
		_longitude = longitude;
		_openWeatherMapConfiguration = openWeatherMapConfiguration;
	}

	@Override
	public JSONObject convert(String key) {
		try {
			String url = StringBundler.concat(
				_openWeatherMapConfiguration.apiURL(), "?APPID=",
				SecretResolverUtil.resolve(
					_companyId, _openWeatherMapConfiguration.apiKey()),
				"&format=json&lat=", _latitude, "&lon=", _longitude, "&units=",
				_openWeatherMapConfiguration.units());

			if (_log.isDebugEnabled()) {
				_log.debug("Reading " + _openWeatherMapConfiguration.apiURL());
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				HttpUtil.URLtoString(url));

			_validateResponse(jsonObject);

			return jsonObject;
		}
		catch (Exception exception) {
			throw new InvalidWebCacheItemException(exception);
		}
	}

	@Override
	public long getRefreshTime() {
		if (_openWeatherMapConfiguration.enabled()) {
			return _openWeatherMapConfiguration.cacheTimeout();
		}

		return 0;
	}

	private void _validateResponse(JSONObject jsonObject) {
		String cod = jsonObject.getString("cod");

		if (Validator.isNull(cod) || cod.startsWith("2")) {
			return;
		}

		throw new InvalidWebCacheItemException(
			StringBundler.concat(
				"OpenWeatherMap: ",
				JSONUtil.getValueAsString(jsonObject, "Object/message"), " (",
				cod, ")"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenWeatherMapWebCacheItem.class);

	private final long _companyId;
	private final String _latitude;
	private final String _longitude;
	private final OpenWeatherMapConfiguration _openWeatherMapConfiguration;

}