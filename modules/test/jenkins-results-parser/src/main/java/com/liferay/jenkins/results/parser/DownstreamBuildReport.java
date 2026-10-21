/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import java.util.List;

/**
 * @author Michael Hashimoto
 */
public interface DownstreamBuildReport extends BuildReport {

	public String getAxisName();

	public String getBatchName();

	public int getFailCount();

	public String getJobVariant();

	public int getPassCount();

	public int getSkipCount();

	public TestClassReport getTestClassReport(String testClassName);

	public List<TestClassReport> getTestClassReports();

	public List<TestReport> getTestReports();

	public URL getTestrayCaseResultURL();

	public TopLevelBuildReport getTopLevelBuildReport();

	public boolean isBuildCached();

	public boolean isBuildTimedOut();

}