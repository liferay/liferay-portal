/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.List;

/**
 * @author Michael Hashimoto
 */
public interface TestClassReport {

	public void addTestReport(TestReport testReport);

	public DownstreamBuildReport getDownstreamBuildReport();

	public long getDuration();

	public String getModuleAppPath();

	public long getOverheadDuration();

	public String getStatus();

	public String getTestClassName();

	public List<TestReport> getTestReports();

	public String getTestTaskName();

	public boolean isFailing();

	public boolean isSkipped();

}