/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.web.cache;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;

/**
 * @author David Truong
 */
public class SEOStudioInstanceAccessTokenWebCacheItem implements WebCacheItem {

	public static String get(
		String baseURL, String clientId, String clientSecret, long companyId) {

		String key = StringBundler.concat(
			SEOStudioInstanceAccessTokenWebCacheItem.class.getName(),
			StringPool.POUND, companyId, StringPool.POUND, clientId,
			StringPool.POUND, baseURL);

		JSONObject jsonObject = (JSONObject)WebCachePoolUtil.get(
			key,
			new SEOStudioInstanceAccessTokenWebCacheItem(
				baseURL, clientId, clientSecret));

		if (jsonObject == null) {
			return null;
		}

		if (System.currentTimeMillis() < jsonObject.getLong("expirationTime")) {
			return jsonObject.getString("access_token");
		}

		WebCachePoolUtil.remove(key);

		jsonObject = (JSONObject)WebCachePoolUtil.get(
			key,
			new SEOStudioInstanceAccessTokenWebCacheItem(
				baseURL, clientId, clientSecret));

		if (jsonObject == null) {
			return null;
		}

		return jsonObject.getString("access_token");
	}

	public SEOStudioInstanceAccessTokenWebCacheItem(
		String baseURL, String clientId, String clientSecret) {

		_baseURL = baseURL;
		_clientId = clientId;
		_clientSecret = clientSecret;
	}

	@Override
	public Object convert(String key) {
		try {
			Http.Options options = new Http.Options();

			options.addPart("client_id", _clientId);
			options.addPart("client_secret", _clientSecret);
			options.addPart("grant_type", "client_credentials");
			options.setLocation(_baseURL + "/o/oauth2/token");
			options.setPost(true);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				HttpUtil.URLtoString(options));

			long expiresIn = jsonObject.getLong("expires_in");

			jsonObject.put(
				"expirationTime",
				System.currentTimeMillis() + ((expiresIn - 30) * 1000));

			_refreshTime = expiresIn * 800;

			return jsonObject;
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
	}

	@Override
	public long getRefreshTime() {
		return _refreshTime;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SEOStudioInstanceAccessTokenWebCacheItem.class);

	private final String _baseURL;
	private final String _clientId;
	private final String _clientSecret;
	private long _refreshTime;

}