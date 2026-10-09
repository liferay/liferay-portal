/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Peter Yoo
 */
public class DefaultTopLevelBuild extends BaseTopLevelBuild {

	public DefaultTopLevelBuild(String url) {
		super(url);
	}

	public DefaultTopLevelBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

	@Override
	public String getResult() {
		String result = super.getResult();

		if (hasDownstreamBuilds() && (result == null)) {
			boolean hasFailure = false;

			for (Build downstreamBuild : getDownstreamBuilds()) {
				String downstreamBuildResult = downstreamBuild.getResult();

				if (downstreamBuildResult == null) {
					setResult(null);

					return null;
				}

				if (downstreamBuildResult.equals("SUCCESS")) {
					continue;
				}

				hasFailure = true;
			}

			if (hasFailure) {
				return "FAILURE";
			}

			return "SUCCESS";
		}

		return result;
	}

}