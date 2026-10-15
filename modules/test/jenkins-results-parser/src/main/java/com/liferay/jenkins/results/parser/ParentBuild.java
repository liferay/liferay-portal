/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public interface ParentBuild extends Build {

	public void addCachedDownstreamBuild(Build build);

	public void addDownstreamBuild(Build build);

	public void addDownstreamBuilds(Map<String, String> urlAxisNames);

	public void addDownstreamBuilds(String... urls);

	public int getDownstreamBuildCount(String status);

	public int getDownstreamBuildCount(String result, String status);

	public List<Build> getDownstreamBuilds();

	public List<Build> getDownstreamBuilds(String status);

	public List<Build> getDownstreamBuilds(String result, String status);

	public Long getLatestStartTimestamp();

	public Build getLongestDelayedDownstreamBuild();

	public Build getLongestRunningDownstreamBuild();

	public List<Build> getModifiedDownstreamBuilds();

	public List<Build> getModifiedDownstreamBuildsByStatus(String status);

	public long getTotalDuration();

	public int getTotalSlavesUsedCount();

	public int getTotalSlavesUsedCount(
		String status, boolean modifiedBuildsOnly);

	public int getTotalSlavesUsedCount(
		String status, boolean modifiedBuildsOnly, boolean ignoreCurrentBuild);

	public boolean hasModifiedDownstreamBuilds();

	public void removeDownstreamBuild(Build build);

	public void waitForDownstreamTestrayCaseResultURLs();

}