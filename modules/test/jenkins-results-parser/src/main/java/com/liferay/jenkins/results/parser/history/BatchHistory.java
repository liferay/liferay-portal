/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface BatchHistory {

	public long getAverageDuration();

	public String getBatchName();

	public JSONObject getJSONObject();

	public JobHistory getJobHistory();

	public String getPortalUpstreamBranchName();

	public List<TestClassHistory> getTestClassHistories();

	public TestClassHistory getTestClassHistory(String key);

	public List<TestTaskHistory> getTestTaskHistories();

	public TestTaskHistory getTestTaskHistory(String key);

}