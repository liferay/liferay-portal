/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Michael Hashimoto
 */
public interface TestReport {

	public DownstreamBuildReport getDownstreamBuildReport();

	public long getDuration();

	public String getErrorDetails();

	public String getErrorStackTrace();

	public String getModuleAppPath();

	public String getStatus();

	public String getTestClassName();

	public String getTestName();

	public String getTestTaskName();

	public boolean isFailing();

	public boolean isSkipped();

}