/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.web.cache;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

/**
 * @author Manuele Castro
 */
public class AIHubCellAccessTokenWebCacheItem extends BaseWebCacheItem {

	public static JSONObject get(
		AIHubCellConfiguration aiHubCellConfiguration, long companyId) {

		String key = StringBundler.concat(
			AIHubCellAccessTokenWebCacheItem.class.getName(), StringPool.POUND,
			companyId, StringPool.POUND, aiHubCellConfiguration.clientId(),
			StringPool.POUND, aiHubCellConfiguration.serviceURL());

		JSONObject jsonObject = (JSONObject)WebCachePoolUtil.get(
			key,
			new AIHubCellAccessTokenWebCacheItem(
				aiHubCellConfiguration, companyId));

		if (!isExpired(jsonObject.getString("access_token"))) {
			return jsonObject;
		}

		WebCachePoolUtil.remove(key);

		return (JSONObject)WebCachePoolUtil.get(
			key,
			new AIHubCellAccessTokenWebCacheItem(
				aiHubCellConfiguration, companyId));
	}

	public AIHubCellAccessTokenWebCacheItem(
		AIHubCellConfiguration aiHubCellConfiguration, long companyId) {

		_aiHubCellConfiguration = aiHubCellConfiguration;
		_companyId = companyId;
	}

	@Override
	public Object convert(String key) {
		try {
			Http.Options options = new Http.Options();

			options.addPart("client_id", _aiHubCellConfiguration.clientId());
			options.addPart(
				"client_secret",
				SecretResolverUtil.resolve(
					_companyId, _aiHubCellConfiguration.clientSecret()));
			options.addPart("grant_type", "client_credentials");
			options.setLocation(
				_aiHubCellConfiguration.serviceURL() + "/o/oauth2/token");
			options.setPost(true);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				HttpUtil.URLtoString(options));

			long expirationTime = getExpirationTime(
				jsonObject.getString("access_token"));

			_refreshTime =
				(long)((expirationTime - System.currentTimeMillis()) * 0.8);

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
		AIHubCellAccessTokenWebCacheItem.class);

	private final AIHubCellConfiguration _aiHubCellConfiguration;
	private final long _companyId;
	private long _refreshTime;

}