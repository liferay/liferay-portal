/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

/**
 * @author Michael Hashimoto
 */
public class SFBatchBuildTestrayCaseResult extends BatchBuildTestrayCaseResult {

	public SFBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);
	}

	@Override
	public BuildReport getBuildReport() {
		return getTopLevelBuildReport();
	}

}