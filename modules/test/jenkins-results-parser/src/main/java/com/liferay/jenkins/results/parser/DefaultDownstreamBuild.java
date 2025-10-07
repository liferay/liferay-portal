/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Michael Hashimoto
 */
public class DefaultDownstreamBuild extends BaseDownstreamBuild {

	@Override
	public boolean isUniqueFailure() {
		if (!isFailing()) {
			return false;
		}

		if (!isCompareToUpstream()) {
			return true;
		}

		String currentFailure = JenkinsResultsParserUtil.combine(
			getBatchName(), ",", getResult());

		for (String upstreamFailure :
				UpstreamFailureUtil.getUpstreamJobFailures(
					"build", getTopLevelBuild())) {

			if (upstreamFailure.equals(currentFailure)) {
				return false;
			}
		}

		return true;
	}

	protected DefaultDownstreamBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

}