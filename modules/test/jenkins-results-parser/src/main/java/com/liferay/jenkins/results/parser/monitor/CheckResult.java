/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public class CheckResult {

	public CheckResult(
		Status status, String message, Map<String, String> metrics,
		long timestamp) {

		_status = status;
		_message = message;
		_metrics = _newUnmodifiableMap(metrics);
		_timestamp = timestamp;
	}

	public String getMessage() {
		return _message;
	}

	public Map<String, String> getMetrics() {
		return _metrics;
	}

	public Status getStatus() {
		return _status;
	}

	public long getTimestamp() {
		return _timestamp;
	}

	public enum Status {

		CRITICAL(3), OK(0), UNKNOWN(1), WARN(2);

		public static Status getMostSevere(Collection<Status> statuses) {
			if (statuses.isEmpty()) {
				return UNKNOWN;
			}

			Status mostSevereStatus = OK;

			for (Status status : statuses) {
				if (status._severityRank > mostSevereStatus._severityRank) {
					mostSevereStatus = status;
				}
			}

			return mostSevereStatus;
		}

		private Status(int severityRank) {
			_severityRank = severityRank;
		}

		private final int _severityRank;

	}

	private Map<String, String> _newUnmodifiableMap(Map<String, String> map) {
		if (map == null) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(new LinkedHashMap<>(map));
	}

	private final String _message;
	private final Map<String, String> _metrics;
	private final Status _status;
	private final long _timestamp;

}