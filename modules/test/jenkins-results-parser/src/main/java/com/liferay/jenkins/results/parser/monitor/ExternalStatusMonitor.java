/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class ExternalStatusMonitor extends BaseMonitor {

	public ExternalStatusMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		Map<String, String> parameters = monitorConfig.getParameters();

		_componentNames = _getComponentNames(parameters);
		_statusPageURL = getRequiredURLParameter("url", parameters, "https://");
	}

	@Override
	public MonitorResult execute() {
		Map<String, String> componentStatuses = null;

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			componentStatuses = _getComponentStatuses();
		}
		catch (Exception exception) {
			return new MonitorResult(
				"Unable to read status page " + _statusPageURL, null,
				MonitorResult.Status.UNKNOWN, currentTimeMillis);
		}

		if (componentStatuses == null) {
			return new MonitorResult(
				"Unable to determine the component statuses from status page " +
					_statusPageURL,
				null, MonitorResult.Status.UNKNOWN, currentTimeMillis);
		}

		List<String> componentMessages = new ArrayList<>();
		List<MonitorResult.Status> statuses = new ArrayList<>();

		for (String componentName : _componentNames) {
			String statusString = componentStatuses.getOrDefault(
				componentName, "missing");

			MonitorResult.Status status = _getStatus(statusString);

			if (status == MonitorResult.Status.OK) {
				continue;
			}

			componentMessages.add(
				JenkinsResultsParserUtil.combine(
					componentName, ": ", statusString));
			statuses.add(status);
		}

		if (componentMessages.isEmpty()) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Status page ", _statusPageURL, " is OK"),
				null, MonitorResult.Status.OK, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.combine(
				"Status page ", _statusPageURL, " reports ",
				JenkinsResultsParserUtil.join(", ", componentMessages)),
			null, MonitorResult.Status.getMostSevere(statuses),
			currentTimeMillis);
	}

	@Override
	protected String getDefaultLinkURL() {
		return _statusPageURL;
	}

	private Set<String> _getComponentNames(Map<String, String> parameters) {
		Set<String> componentNames = new TreeSet<>();

		String components = getRequiredParameter("components", parameters);

		for (String componentName : components.split(",")) {
			componentName = componentName.trim();

			if (componentName.isEmpty()) {
				throw new IllegalArgumentException(
					getInvalidValueMessage(
						"parameter", "components", components));
			}

			componentNames.add(componentName);
		}

		return componentNames;
	}

	private Map<String, String> _getComponentStatuses() throws IOException {
		JSONObject statusPageJSONObject = JenkinsResultsParserUtil.toJSONObject(
			_statusPageURL, false, _RETRIES_SIZE_MAX, null, null,
			_SECONDS_RETRY_PERIOD, getAttemptTimeoutMillis(_RETRIES_SIZE_MAX),
			null);

		JSONArray componentsJSONArray = statusPageJSONObject.optJSONArray(
			"components");

		if (componentsJSONArray == null) {
			return null;
		}

		Map<String, String> componentStatuses = new HashMap<>();

		for (int i = 0; i < componentsJSONArray.length(); i++) {
			JSONObject componentJSONObject = componentsJSONArray.optJSONObject(
				i);

			if (componentJSONObject == null) {
				continue;
			}

			String statusString = componentJSONObject.optString("status");

			if (statusString.isEmpty()) {
				continue;
			}

			String componentName = componentJSONObject.optString("name");

			String previousStatusString = componentStatuses.get(componentName);

			if (previousStatusString != null) {
				MonitorResult.Status previousStatus = _getStatus(
					previousStatusString);
				MonitorResult.Status status = _getStatus(statusString);

				if (previousStatus.getSeverityRank() >=
						status.getSeverityRank()) {

					continue;
				}
			}

			componentStatuses.put(componentName, statusString);
		}

		return componentStatuses;
	}

	private MonitorResult.Status _getStatus(String statusString) {
		if (statusString.equals("major_outage") ||
			statusString.equals("partial_outage")) {

			return MonitorResult.Status.CRITICAL;
		}

		if (statusString.equals("degraded_performance") ||
			statusString.equals("under_maintenance")) {

			return MonitorResult.Status.WARN;
		}

		if (statusString.equals("operational")) {
			return MonitorResult.Status.OK;
		}

		return MonitorResult.Status.UNKNOWN;
	}

	private static final int _RETRIES_SIZE_MAX = 1;

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private final Set<String> _componentNames;
	private final String _statusPageURL;

}