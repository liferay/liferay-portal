/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.client;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NestableRuntimeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import org.apache.http.HttpStatus;

/**
 * @author David Arques
 */
public class AsahFaroBackendClient {

	public AsahFaroBackendClient(
		AnalyticsSettingsManager analyticsSettingsManager, Http http) {

		_analyticsSettingsManager = analyticsSettingsManager;
		_http = http;
	}

	public String doGet(long companyId, String path) {
		try {
			AnalyticsConfiguration analyticsConfiguration =
				_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

			return _getResponse(
				companyId,
				String.format(
					"%s/%s",
					analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
					path));
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				"Request to Asah Faro backend failed", exception);
		}
	}

	public boolean isValidConnection(long companyId) throws Exception {
		if (!_analyticsSettingsManager.isAnalyticsEnabled(companyId)) {
			return false;
		}

		try {
			AnalyticsConfiguration analyticsConfiguration =
				_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

			doGet(
				companyId,
				"api/1.0/data-sources/" +
					analyticsConfiguration.liferayAnalyticsDataSourceId());

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Invalid Asah Faro backend connection", exception);
			}

			return false;
		}
	}

	private String _getResponse(long companyId, String url) throws Exception {
		Http.Options options = new Http.Options();

		options.addHeader("Accept", "application/json");

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			SecretResolverUtil.resolve(
				companyId,
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature()));

		String projectId = analyticsConfiguration.liferayAnalyticsProjectId();

		if (projectId != null) {
			options.addHeader("OSB-Asah-Project-ID", projectId);
		}

		options.setLocation(url);

		String response = _http.URLtoString(options);

		Http.Response httpResponse = options.getResponse();

		if (httpResponse.getResponseCode() != HttpStatus.SC_OK) {
			throw new NestableRuntimeException(
				StringBundler.concat(
					"Unexpected response status ",
					httpResponse.getResponseCode(), " with response message: ",
					response));
		}

		return response;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AsahFaroBackendClient.class);

	private final AnalyticsSettingsManager _analyticsSettingsManager;
	private final Http _http;

}