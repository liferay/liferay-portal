/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.web.cache;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.search.internal.configuration.AsahConfiguration;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import java.net.HttpURLConnection;

/**
 * @author Gustavo Lima
 */
public class AsahWebCacheItem implements WebCacheItem {

	public static JSONObject get(
		AnalyticsConfiguration analyticsConfiguration,
		AsahConfiguration asahConfiguration, long companyId, String url,
		String webCacheItem) {

		try {
			return (JSONObject)WebCachePoolUtil.get(
				AsahWebCacheItem.class.getName() + webCacheItem,
				new AsahWebCacheItem(
					analyticsConfiguration, asahConfiguration, companyId, url));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	public AsahWebCacheItem(
		AnalyticsConfiguration analyticsConfiguration,
		AsahConfiguration asahConfiguration, long companyId, String url) {

		_analyticsConfiguration = analyticsConfiguration;
		_asahConfiguration = asahConfiguration;
		_companyId = companyId;
		_url = url;
	}

	@Override
	public JSONObject convert(String key) {
		try {
			Http.Options options = new Http.Options();

			options.addHeader(
				"OSB-Asah-Faro-Backend-Security-Signature",
				SecretResolverUtil.resolve(
					_companyId,
					_analyticsConfiguration.
						liferayAnalyticsFaroBackendSecuritySignature()));
			options.addHeader(
				"OSB-Asah-Project-ID",
				_analyticsConfiguration.liferayAnalyticsProjectId());

			if (Validator.isBlank(_url)) {
				return JSONFactoryUtil.createJSONObject();
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Reading " + _url);
			}

			options.setLocation(_url);

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				HttpUtil.URLtoString(options));

			Http.Response response = options.getResponse();

			if ((response.getResponseCode() != HttpURLConnection.HTTP_OK) ||
				!jsonObject.has("_embedded")) {

				throw new RuntimeException(
					StringBundler.concat(
						"Response body: ", jsonObject, "\nResponse code: ",
						response.getResponseCode()));
			}

			return jsonObject;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public long getRefreshTime() {
		return _asahConfiguration.cacheTimeout();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AsahWebCacheItem.class);

	private final AnalyticsConfiguration _analyticsConfiguration;
	private final AsahConfiguration _asahConfiguration;
	private final long _companyId;
	private final String _url;

}