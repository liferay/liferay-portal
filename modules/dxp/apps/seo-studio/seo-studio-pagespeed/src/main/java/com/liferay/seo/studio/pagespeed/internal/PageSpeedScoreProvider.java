/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.HttpURLConnection;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScoreProvider {

	public PageSpeedScoreProvider(String apiKey, String strategy) {
		_apiKey = apiKey;
		_strategy = strategy;
	}

	public PageSpeedScores getScores(String url)
		throws PageSpeedScoreProviderException {

		try {
			return _getScores(url);
		}
		catch (PageSpeedScoreProviderException
					pageSpeedScoreProviderException) {

			throw pageSpeedScoreProviderException;
		}
		catch (Exception exception) {
			throw new PageSpeedScoreProviderException(exception);
		}
	}

	public boolean isValidConnection() {
		return Validator.isNotNull(_apiKey);
	}

	public static class PageSpeedScoreProviderException
		extends PortalException {

		public PageSpeedScoreProviderException(Exception exception) {
			super(exception);
		}

		public PageSpeedScoreProviderException(
			JSONObject googlePageSpeedErrorJSONObject, String message) {

			super(message);

			_googlePageSpeedErrorJSONObject = googlePageSpeedErrorJSONObject;
		}

		public PageSpeedScoreProviderException(String message) {
			super(message);
		}

		public JSONObject getGooglePageSpeedErrorJSONObject() {
			return _googlePageSpeedErrorJSONObject;
		}

		public boolean isQuotaExceeded() {
			JSONObject errorJSONObject = _googlePageSpeedErrorJSONObject;

			if (errorJSONObject == null) {
				return false;
			}

			JSONObject errorDetailJSONObject = errorJSONObject.getJSONObject(
				"error");

			if (errorDetailJSONObject == null) {
				return false;
			}

			if (errorDetailJSONObject.getInt("code", -1) == 429) {
				return true;
			}

			return false;
		}

		private JSONObject _googlePageSpeedErrorJSONObject;

	}

	private String _buildGooglePageSpeedURL(String url) {
		String googlePageSpeedURL =
			"https://content-pagespeedonline.googleapis.com/pagespeedonline" +
				"/v5/runPagespeed";

		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "category", "ACCESSIBILITY");
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "category", "BEST_PRACTICES");
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "category", "PERFORMANCE");
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "category", "SEO");
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "fields",
			"lighthouseResult/categories/*/score");
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "key", _apiKey);
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "strategy", _strategy);
		googlePageSpeedURL = HttpComponentsUtil.addParameter(
			googlePageSpeedURL, "url", url);

		return googlePageSpeedURL;
	}

	private int _getScore(JSONObject categoriesJSONObject, String category) {
		JSONObject categoryJSONObject = categoriesJSONObject.getJSONObject(
			category);

		if (categoryJSONObject == null) {
			return 0;
		}

		double score = categoryJSONObject.getDouble("score", 0);

		return (int)Math.round(score * 100);
	}

	private PageSpeedScores _getScores(String url) throws Exception {
		if (!isValidConnection()) {
			throw new PageSpeedScoreProviderException("Invalid Connection");
		}

		Http.Options options = new Http.Options();

		String googlePageSpeedURL = _buildGooglePageSpeedURL(url);

		options.setLocation(googlePageSpeedURL);

		options.setTimeout(120000);

		String responseJSON = HttpUtil.URLtoString(options);

		Http.Response response = options.getResponse();

		int responseCode = response.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			JSONObject errorJSONObject = null;

			try {
				errorJSONObject = JSONFactoryUtil.createJSONObject(
					responseJSON);
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse error response as JSON",
						jsonException);
				}
			}

			throw new PageSpeedScoreProviderException(
				errorJSONObject,
				StringBundler.concat(
					"Response code ", responseCode, ": ", responseJSON));
		}

		return _parseScores(responseJSON);
	}

	private PageSpeedScores _parseScores(String responseJSON) throws Exception {
		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			responseJSON);

		JSONObject lighthouseResultJSONObject =
			responseJSONObject.getJSONObject("lighthouseResult");

		if (lighthouseResultJSONObject == null) {
			throw new PageSpeedScoreProviderException(
				"Missing \"lighthouseResult\" in response");
		}

		JSONObject categoriesJSONObject =
			lighthouseResultJSONObject.getJSONObject("categories");

		if (categoriesJSONObject == null) {
			throw new PageSpeedScoreProviderException(
				"Missing \"categories\" in \"lighthouseResult\"");
		}

		return new PageSpeedScores(
			_getScore(categoriesJSONObject, "accessibility"),
			_getScore(categoriesJSONObject, "best-practices"),
			_getScore(categoriesJSONObject, "performance"),
			_getScore(categoriesJSONObject, "seo"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageSpeedScoreProvider.class);

	private final String _apiKey;
	private final String _strategy;

}