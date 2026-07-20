/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.model.PageSpeedReport;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.time.Duration;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Kiana Suetani
 */
@Component
public class PageSpeedReportService {

	public PageSpeedReport getPageSpeedReport(
			String apiKey, String strategy, String url)
		throws InterruptedException, IOException {

		HttpResponse<String> httpResponse = _httpClient.send(
			HttpRequest.newBuilder(
			).uri(
				URI.create(_buildReportURL(apiKey, strategy, url))
			).timeout(
				Duration.ofSeconds(60)
			).GET(
			).build(),
			HttpResponse.BodyHandlers.ofString());

		if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException(
				"Unable to fetch PageSpeed report, HTTP " +
					httpResponse.statusCode());
		}

		return _parsePageSpeedReport(httpResponse.body());
	}

	public boolean isValidAPIKey(String apiKey) throws InterruptedException {
		try {
			HttpResponse<String> httpResponse = _httpClient.send(
				HttpRequest.newBuilder(
				).uri(
					URI.create(_buildValidationURL(apiKey))
				).timeout(
					Duration.ofSeconds(30)
				).GET(
				).build(),
				HttpResponse.BodyHandlers.ofString());

			JSONObject responseJSONObject = new JSONObject(httpResponse.body());

			JSONObject errorJSONObject = responseJSONObject.optJSONObject(
				"error");

			if (errorJSONObject == null) {
				return true;
			}

			if (Objects.equals(
					errorJSONObject.optString("status"), "PERMISSION_DENIED")) {

				return false;
			}

			JSONArray detailsJSONArray = errorJSONObject.optJSONArray(
				"details");

			if (detailsJSONArray != null) {
				for (Object object : detailsJSONArray) {
					JSONObject detailJSONObject = (JSONObject)object;

					if (Validator.isNotNull(
							detailJSONObject.optString("reason"))) {

						return false;
					}
				}
			}

			return true;
		}
		catch (IOException | JSONException exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to validate the PageSpeed API key", exception);
			}

			return false;
		}
	}

	private String _buildReportURL(String apiKey, String strategy, String url) {
		StringBundler sb = new StringBundler(10);

		sb.append("https://content-pagespeedonline.googleapis.com");
		sb.append("/pagespeedonline/v5/runPagespeed");
		sb.append("?category=ACCESSIBILITY&category=BEST_PRACTICES");
		sb.append("&category=PERFORMANCE&category=SEO&fields=");
		sb.append("lighthouseResult/categories/*/score&key=");
		sb.append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
		sb.append("&strategy=");
		sb.append(strategy);
		sb.append("&url=");
		sb.append(URLEncoder.encode(url, StandardCharsets.UTF_8));

		return sb.toString();
	}

	private String _buildValidationURL(String apiKey) {
		StringBundler sb = new StringBundler(3);

		sb.append("https://content-pagespeedonline.googleapis.com");
		sb.append("/pagespeedonline/v5/runPagespeed?url=invalid_url&key=");
		sb.append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8));

		return sb.toString();
	}

	private int _getPageSpeedScore(JSONObject jsonObject) {
		return (int)Math.round(jsonObject.getDouble("score") * 100);
	}

	private PageSpeedReport _parsePageSpeedReport(String responseJSON) {
		JSONObject responseJSONObject = new JSONObject(responseJSON);

		JSONObject lighthouseResultJSONObject =
			responseJSONObject.getJSONObject("lighthouseResult");

		JSONObject categoriesJSONObject =
			lighthouseResultJSONObject.getJSONObject("categories");

		return new PageSpeedReport(
			_getPageSpeedScore(
				categoriesJSONObject.getJSONObject("accessibility")),
			_getPageSpeedScore(
				categoriesJSONObject.getJSONObject("best-practices")),
			_getPageSpeedScore(
				categoriesJSONObject.getJSONObject("performance")),
			_getPageSpeedScore(categoriesJSONObject.getJSONObject("seo")));
	}

	private static final Log _log = LogFactory.getLog(
		PageSpeedReportService.class);

	private final HttpClient _httpClient = HttpClient.newBuilder(
	).connectTimeout(
		Duration.ofSeconds(5)
	).build();

}