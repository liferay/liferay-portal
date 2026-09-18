/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.sugarcrm.internal.web.cache;

import com.liferay.object.storage.sugarcrm.configuration.SugarCRMConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import java.net.HttpURLConnection;

/**
 * @author Maurice Sepe
 */
public class SugarCRMAccessTokenWebCacheItem implements WebCacheItem {

	public static JSONObject get(
		long companyId, SugarCRMConfiguration sugarCRMConfiguration) {

		return (JSONObject)WebCachePoolUtil.get(
			StringBundler.concat(
				SugarCRMAccessTokenWebCacheItem.class.getName(),
				StringPool.POUND, companyId, StringPool.POUND,
				sugarCRMConfiguration.accessTokenURL(), StringPool.POUND,
				sugarCRMConfiguration.baseURL(), StringPool.POUND,
				sugarCRMConfiguration.clientId(), StringPool.POUND,
				sugarCRMConfiguration.grantType(), StringPool.POUND,
				sugarCRMConfiguration.password(), StringPool.POUND,
				sugarCRMConfiguration.username()),
			new SugarCRMAccessTokenWebCacheItem(
				companyId, sugarCRMConfiguration));
	}

	public SugarCRMAccessTokenWebCacheItem(
		long companyId, SugarCRMConfiguration sugarCRMConfiguration) {

		_companyId = companyId;
		_sugarCRMConfiguration = sugarCRMConfiguration;
	}

	@Override
	public JSONObject convert(String key) {
		try {
			Http.Options options = new Http.Options();

			options.setCookieSpec(Http.CookieSpec.STANDARD);
			options.setParts(
				HashMapBuilder.put(
					"client_id", _sugarCRMConfiguration.clientId()
				).put(
					"grant_type", _sugarCRMConfiguration.grantType()
				).put(
					"password",
					SecretResolverUtil.resolve(
						_companyId, _sugarCRMConfiguration.password())
				).put(
					"username", _sugarCRMConfiguration.username()
				).build());
			options.setLocation(_sugarCRMConfiguration.accessTokenURL());
			options.setPost(true);

			String responseJSON = HttpUtil.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Response code ", response.getResponseCode(), ": ",
							responseJSON));
				}

				return null;
			}

			return JSONFactoryUtil.createJSONObject(responseJSON);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public long getRefreshTime() {
		return _REFRESH_TIME;
	}

	private static final long _REFRESH_TIME = Time.MINUTE * 60;

	private static final Log _log = LogFactoryUtil.getLog(
		SugarCRMAccessTokenWebCacheItem.class);

	private final long _companyId;
	private final SugarCRMConfiguration _sugarCRMConfiguration;

}