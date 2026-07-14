/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public class MonitorConfig {

	public MonitorConfig(
		long cadence, String id, Map<String, String> parameters,
		Severity severity, Map<String, String> thresholds, long timeout,
		String type) {

		_cadence = cadence;
		_id = id;
		_parameters = _newUnmodifiableMap(parameters);
		_severity = severity;
		_thresholds = _newUnmodifiableMap(thresholds);
		_timeout = timeout;
		_type = type;
	}

	public long getCadence() {
		return _cadence;
	}

	public String getId() {
		return _id;
	}

	public Map<String, String> getParameters() {
		return _parameters;
	}

	public Severity getSeverity() {
		return _severity;
	}

	public Map<String, String> getThresholds() {
		return _thresholds;
	}

	public long getTimeout() {
		return _timeout;
	}

	public String getType() {
		return _type;
	}

	public enum Severity {

		HIGH, LOW, MEDIUM

	}

	private Map<String, String> _newUnmodifiableMap(Map<String, String> map) {
		if (map == null) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(new LinkedHashMap<>(map));
	}

	private final long _cadence;
	private final String _id;
	private final Map<String, String> _parameters;
	private final Severity _severity;
	private final Map<String, String> _thresholds;
	private final long _timeout;
	private final String _type;

}