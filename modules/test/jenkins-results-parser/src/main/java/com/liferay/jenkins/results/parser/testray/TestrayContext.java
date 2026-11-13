/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalFixpackRelease;
import com.liferay.jenkins.results.parser.PortalHotfixRelease;
import com.liferay.jenkins.results.parser.PortalRelease;
import com.liferay.jenkins.results.parser.PullRequest;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.File;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Hashimoto
 */
public interface TestrayContext {

	public void deleteTestrayCaseResultFile(
		AxisTestClassGroup axisTestClassGroup);

	public BuildDatabase getBuildDatabase();

	public JobProperty getJobProperty(
		String basePropertyName, File testBaseDir);

	public List<Job> getJobs();

	public PortalFixpackRelease getPortalFixpackRelease();

	public PortalHotfixRelease getPortalHotfixRelease();

	public PortalRelease getPortalRelease();

	public PullRequest getPullRequest();

	public TestrayBuild getTestrayBuild();

	public TestrayBuild getTestrayBuild(File testBaseDir);

	public Date getTestrayBuildDate();

	public String getTestrayBuildDescription();

	public String getTestrayBuildSHA();

	public Set<TestrayBuild> getTestrayBuilds();

	public Map<File, TestrayBuild> getTestrayBuildsMap();

	public TestrayProductVersion getTestrayProductVersion();

	public TestrayProductVersion getTestrayProductVersion(File testBaseDir);

	public TestrayProject getTestrayProject();

	public TestrayProject getTestrayProject(File testBaseDir);

	public TestrayRoutine getTestrayRoutine();

	public TestrayRoutine getTestrayRoutine(File testBaseDir);

	public TestrayServer getTestrayServer();

	public TestrayServer getTestrayServer(File testBaseDir);

	public boolean hasTestrayCaseResultFile(
		AxisTestClassGroup axisTestClassGroup);

	public void recordTestrayCaseResultFile(
		AxisTestClassGroup axisTestClassGroup,
		TestrayCaseResult buildTestrayCaseResult);

	public String replace(String string);

	public String replaceSlack(String string, TestrayBuild testrayBuild);

	public void updateTestrayCaseResultFileDurations(
		AxisTestClassGroup axisTestClassGroup);

}