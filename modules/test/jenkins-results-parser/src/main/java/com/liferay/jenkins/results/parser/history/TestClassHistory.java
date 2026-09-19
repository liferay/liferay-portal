/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import java.net.URL;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface TestClassHistory {

	public long getAverageDuration();

	public long getAverageOverheadDuration();

	public BatchHistory getBatchHistory();

	public String getBatchName();

	public int getFailureCount();

	public JSONObject getJSONObject();

	public String getPortalUpstreamBranchName();

	public int getStatusChanges();

	public String getTestClassName();

	public long getTestCount();

	public TestTaskHistory getTestTaskHistory();

	public String getTestTaskName();

	public URL getTestrayCaseURL();

	public boolean isFlaky();

}