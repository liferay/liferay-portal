/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import java.util.List;

/**
 * @author Marcos Martins
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class ProjectUsageMetricDisplay {

	public ProjectUsageMetricDisplay() {
	}

	public ProjectUsageMetricDisplay(
			String corpProjectName, String corpProjectUuid,
			String lastAccessDateString, String lastAnniversaryDateString,
			boolean offline, List<UsageMetric> usageMetrics, String weDeployKey)
		throws Exception {

		_corpProjectName = corpProjectName;
		_corpProjectUuid = corpProjectUuid;
		_lastAccessDateString = lastAccessDateString;
		_lastAnniversaryDateString = lastAnniversaryDateString;
		_usageMetrics = usageMetrics;
		_weDeployKey = weDeployKey;
	}

	public String getCorpProjectName() {
		return _corpProjectName;
	}

	public String getCorpProjectUuid() {
		return _corpProjectUuid;
	}

	public String getLastAccessDateString() {
		return _lastAccessDateString;
	}

	public String getLastAnniversaryDateString() {
		return _lastAnniversaryDateString;
	}

	public boolean getOffline() {
		return isOffline();
	}

	public List<UsageMetric> getUsageMetrics() {
		return _usageMetrics;
	}

	public String getWeDeployKey() {
		return _weDeployKey;
	}

	public boolean isOffline() {
		return _offline;
	}

	private String _corpProjectName;
	private String _corpProjectUuid;
	private String _lastAccessDateString;
	private String _lastAnniversaryDateString;
	private boolean _offline;
	private List<UsageMetric> _usageMetrics;
	private String _weDeployKey;

}