/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microsoft.translator.internal;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Time;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Hugo Huijser
 */
public class MicrosoftTranslatorAuthenticator {

	public MicrosoftTranslatorAuthenticator(String subscriptionKey) {
		_subscriptionKey = subscriptionKey;

		init(true);
	}

	public String getAccessToken() {
		init(false);

		return _accessToken;
	}

	public String getError() {
		return _error;
	}

	public void init(boolean manual) {
		if (manual || _isStale()) {
			_init();
		}
	}

	private void _init() {
		_accessToken = null;
		_error = null;

		try {
			Http.Options options = new Http.Options();

			options.addHeader(HttpHeaders.CONTENT_LENGTH, "0");
			options.addHeader("Ocp-Apim-Subscription-Key", _subscriptionKey);

			options.setLocation(_URL);
			options.setPost(true);

			String content = HttpUtil.URLtoString(options);

			Http.Response response = options.getResponse();

			int responseCode = response.getResponseCode();

			if (responseCode != HttpServletResponse.SC_OK) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					content);

				_error = jsonObject.getString("message");

				if (_log.isInfoEnabled()) {
					_log.info("Unable to initialize access token: " + content);
				}
			}
			else {
				_accessToken = content;
			}

			if (_log.isInfoEnabled() && (_accessToken != null)) {
				_log.info("Access token " + _accessToken);
			}

			_initTime = System.currentTimeMillis();
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to initialize authentication token", exception);
			}
		}
	}

	private boolean _isStale() {
		if ((_initTime + _EXPIRE_TIME) > System.currentTimeMillis()) {
			return false;
		}

		return true;
	}

	private static final long _EXPIRE_TIME = 10 * Time.MINUTE;

	private static final String _URL =
		"https://api.cognitive.microsoft.com/sts/v1.0/issueToken";

	private static final Log _log = LogFactoryUtil.getLog(
		MicrosoftTranslatorAuthenticator.class);

	private String _accessToken;
	private String _error;
	private long _initTime;
	private final String _subscriptionKey;

}