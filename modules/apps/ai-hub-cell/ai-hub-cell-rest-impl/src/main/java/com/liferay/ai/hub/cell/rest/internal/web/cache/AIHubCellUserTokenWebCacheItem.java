/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.web.cache;

import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;

/**
 * @author Rafael Praxedes
 */
public class AIHubCellUserTokenWebCacheItem extends BaseWebCacheItem {

	public static String get(
		LocalOAuthClient localOAuthClient, OAuth2Application oAuth2Application,
		long userId) {

		String key = StringBundler.concat(
			AIHubCellUserTokenWebCacheItem.class.getName(), StringPool.POUND,
			oAuth2Application.getCompanyId(), StringPool.POUND,
			oAuth2Application.getOAuth2ApplicationId(), StringPool.POUND,
			oAuth2Application.getOAuth2ApplicationScopeAliasesId(),
			StringPool.POUND, userId);

		String accessToken = (String)WebCachePoolUtil.get(
			key,
			new AIHubCellUserTokenWebCacheItem(
				localOAuthClient, oAuth2Application, userId));

		if (!isExpired(accessToken)) {
			return accessToken;
		}

		WebCachePoolUtil.remove(key);

		return (String)WebCachePoolUtil.get(
			key,
			new AIHubCellUserTokenWebCacheItem(
				localOAuthClient, oAuth2Application, userId));
	}

	public AIHubCellUserTokenWebCacheItem(
		LocalOAuthClient localOAuthClient, OAuth2Application oAuth2Application,
		long userId) {

		_localOAuthClient = localOAuthClient;
		_oAuth2Application = oAuth2Application;
		_userId = userId;
	}

	@Override
	public Object convert(String key) {
		try {
			String responseJSON = TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> _localOAuthClient.requestTokens(
					_oAuth2Application, _userId));

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				responseJSON);

			long expirationTime = getExpirationTime(
				jsonObject.getString("access_token"));

			_refreshTime =
				(long)((expirationTime - System.currentTimeMillis()) * 0.8);

			return jsonObject.getString("access_token");
		}
		catch (Throwable throwable) {
			_log.error(throwable);

			return null;
		}
	}

	@Override
	public long getRefreshTime() {
		return _refreshTime;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubCellUserTokenWebCacheItem.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	private final LocalOAuthClient _localOAuthClient;
	private final OAuth2Application _oAuth2Application;
	private long _refreshTime;
	private final long _userId;

}