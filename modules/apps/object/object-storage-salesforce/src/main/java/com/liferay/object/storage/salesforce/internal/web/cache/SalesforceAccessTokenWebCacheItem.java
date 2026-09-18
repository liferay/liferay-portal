/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.internal.web.cache;

import com.liferay.object.storage.salesforce.configuration.SalesforceConfiguration;
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
 * @author Brian Wing Shun Chan
 */
public class SalesforceAccessTokenWebCacheItem implements WebCacheItem {

	public static JSONObject get(
		long companyId, SalesforceConfiguration salesforceConfiguration) {

		return (JSONObject)WebCachePoolUtil.get(
			StringBundler.concat(
				SalesforceAccessTokenWebCacheItem.class.getName(),
				StringPool.POUND, companyId, StringPool.POUND,
				salesforceConfiguration.consumerKey(), StringPool.POUND,
				salesforceConfiguration.consumerSecret(), StringPool.POUND,
				salesforceConfiguration.password(), StringPool.POUND,
				salesforceConfiguration.username()),
			new SalesforceAccessTokenWebCacheItem(
				companyId, salesforceConfiguration));
	}

	public SalesforceAccessTokenWebCacheItem(
		long companyId, SalesforceConfiguration salesforceConfiguration) {

		_companyId = companyId;
		_salesforceConfiguration = salesforceConfiguration;
	}

	@Override
	public JSONObject convert(String key) {
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Get Salesforce access token for consumer key " +
						_salesforceConfiguration.consumerKey());
			}

			Http.Options options = new Http.Options();

			options.setParts(
				HashMapBuilder.put(
					"client_id", _salesforceConfiguration.consumerKey()
				).put(
					"client_secret",
					SecretResolverUtil.resolve(
						_companyId, _salesforceConfiguration.consumerSecret())
				).put(
					"grant_type", "password"
				).put(
					"password",
					SecretResolverUtil.resolve(
						_companyId, _salesforceConfiguration.password())
				).put(
					"username", _salesforceConfiguration.username()
				).build());
			options.setLocation(
				_salesforceConfiguration.loginURL() + "/services/oauth2/token");
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

	private static final long _REFRESH_TIME = Time.MINUTE * 45;

	private static final Log _log = LogFactoryUtil.getLog(
		SalesforceAccessTokenWebCacheItem.class);

	private final long _companyId;
	private final SalesforceConfiguration _salesforceConfiguration;

}