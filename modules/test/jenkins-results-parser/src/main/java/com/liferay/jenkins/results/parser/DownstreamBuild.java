/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

/**
 * @author Michael Hashimoto
 */
public interface DownstreamBuild extends Build {

	public void generateBuildReport();

	public long getAverageDuration();

	public long getAverageOverheadDuration();

	public long getAverageTotalTestDuration();

	public String getAxisName();

	public AxisTestClassGroup getAxisTestClassGroup();

	public String getAxisVariable();

	public String getBatchName();

	public DownstreamBuildReport getDownstreamBuildReport();

}