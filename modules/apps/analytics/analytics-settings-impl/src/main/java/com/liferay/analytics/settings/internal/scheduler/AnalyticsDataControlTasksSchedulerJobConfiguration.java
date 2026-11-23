/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.scheduler;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import java.net.HttpURLConnection;

import java.util.Map;
import java.util.Set;

/**
 * @author Marcos Martins
 */
public class AnalyticsDataControlTasksSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	public AnalyticsDataControlTasksSchedulerJobConfiguration(
		AnalyticsConfigurationRegistry analyticsConfigurationRegistry,
		Map<Long, Set<String>> emailAddresses, Http http) {

		_analyticsConfigurationRegistry = analyticsConfigurationRegistry;
		_emailAddresses = emailAddresses;
		_http = http;
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			Map<Long, AnalyticsConfiguration> analyticsConfigurations =
				_analyticsConfigurationRegistry.getAnalyticsConfigurations();

			if (analyticsConfigurations.isEmpty()) {
				return;
			}

			for (Map.Entry<Long, AnalyticsConfiguration> entry :
					analyticsConfigurations.entrySet()) {

				Set<String> emailAddresses = _emailAddresses.remove(
					entry.getKey());

				if (SetUtil.isEmpty(emailAddresses)) {
					continue;
				}

				_createDataControlTasks(
					entry.getValue(), entry.getKey(), emailAddresses,
					SetUtil.fromArray(new String[] {"DELETE", "SUPPRESS"}));
			}
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			1, TimeUnit.MINUTE);
	}

	private void _createDataControlTasks(
			AnalyticsConfiguration analyticsConfiguration, long companyId,
			Set<String> emailAddresses, Set<String> types)
		throws Exception {

		Http.Options options = _getOptions(analyticsConfiguration, companyId);

		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(
			JSONUtil.put(
				"emailAddresses", emailAddresses
			).put(
				"types", types
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(
			analyticsConfiguration.liferayAnalyticsFaroBackendURL() +
				"/api/1.0/data-control-tasks");
		options.setPost(true);

		_http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Response code " + response.getResponseCode());
		}

		throw new PortalException("Unable to add data control tasks");
	}

	private Http.Options _getOptions(
			AnalyticsConfiguration analyticsConfiguration, long companyId)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			"OSB-Asah-Faro-Backend-Security-Signature",
			SecretResolverUtil.resolve(
				companyId,
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature()));
		options.addHeader(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId());

		return options;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsDataControlTasksSchedulerJobConfiguration.class);

	private final AnalyticsConfigurationRegistry
		_analyticsConfigurationRegistry;
	private final Map<Long, Set<String>> _emailAddresses;
	private final Http _http;

}