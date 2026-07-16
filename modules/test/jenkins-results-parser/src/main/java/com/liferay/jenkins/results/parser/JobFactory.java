/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JobFactory {

	public static String getKey(Job job) {
		List<String> projectNames = null;

		if (job instanceof QAWebsitesGitRepositoryJob) {
			QAWebsitesGitRepositoryJob qaWebsitesGitRepositoryJob =
				(QAWebsitesGitRepositoryJob)job;

			projectNames = qaWebsitesGitRepositoryJob.getProjectNames();
		}

		String repositoryName = null;

		if (job instanceof GitRepositoryJob) {
			GitRepositoryJob gitRepositoryJob = (GitRepositoryJob)job;

			repositoryName = gitRepositoryJob.getRepositoryName();
		}

		String testSuiteName = "default";

		if (job instanceof TestSuiteJob) {
			TestSuiteJob testSuiteJob = (TestSuiteJob)job;

			testSuiteName = testSuiteJob.getTestSuiteName();
		}

		String upstreamBranchName = null;

		if (job instanceof GitRepositoryJob) {
			GitRepositoryJob gitRepositoryJob = (GitRepositoryJob)job;

			upstreamBranchName = gitRepositoryJob.getUpstreamBranchName();
		}

		String portalUpstreamBranchName = null;

		if (job instanceof PortalTestClassJob) {
			PortalTestClassJob portalTestClassJob = (PortalTestClassJob)job;

			PortalGitWorkingDirectory portalGitWorkingDirectory =
				portalTestClassJob.getPortalGitWorkingDirectory();

			portalUpstreamBranchName =
				portalGitWorkingDirectory.getUpstreamBranchName();

			if (JenkinsResultsParserUtil.isNullOrEmpty(repositoryName)) {
				repositoryName =
					portalGitWorkingDirectory.getGitRepositoryName();
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
				upstreamBranchName = portalUpstreamBranchName;
			}
		}
		else if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
			portalUpstreamBranchName = upstreamBranchName;
		}

		return getKey(
			job.getBuildProfile(), job.getJobName(), null,
			portalUpstreamBranchName, projectNames, repositoryName,
			testSuiteName, upstreamBranchName);
	}

	public static String getKey(
		Job.BuildProfile buildProfile, String jobName,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String portalUpstreamBranchName, List<String> projectNames,
		String repositoryName, String testSuiteName,
		String upstreamBranchName) {

		StringBuilder sb = new StringBuilder();

		if (buildProfile == null) {
			buildProfile = Job.BuildProfile.DXP;
		}

		sb.append(buildProfile);
		sb.append("_");

		sb.append(jobName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			if (portalGitWorkingDirectory != null) {
				portalUpstreamBranchName =
					portalGitWorkingDirectory.getUpstreamBranchName();
			}
			else if (JenkinsResultsParserUtil.isNullOrEmpty(
						upstreamBranchName)) {

				portalUpstreamBranchName = upstreamBranchName;
			}
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			sb.append("_");
			sb.append(portalUpstreamBranchName);
		}

		if ((projectNames != null) && !projectNames.isEmpty()) {
			Collections.sort(projectNames);

			sb.append("_");
			sb.append(JenkinsResultsParserUtil.join("_", projectNames));
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(repositoryName)) {
			sb.append("_");
			sb.append(repositoryName);
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
			testSuiteName = "default";
		}

		sb.append("_");
		sb.append(testSuiteName);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
			sb.append("_");
			sb.append(upstreamBranchName);
		}

		return sb.toString();
	}

	public static String getKey(JSONObject jsonObject) {
		Job.BuildProfile buildProfile = Job.BuildProfile.DXP;

		if (jsonObject.has("build_profile")) {
			buildProfile = Job.BuildProfile.getByString(
				jsonObject.getString("build_profile"));
		}

		List<String> projectNames = null;

		JSONArray projectNamesJSONArray = jsonObject.optJSONArray(
			"project_names");

		if ((projectNamesJSONArray != null) &&
			!projectNamesJSONArray.isEmpty()) {

			projectNames = new ArrayList<>();

			for (int i = 0; i < projectNamesJSONArray.length(); i++) {
				projectNames.add(projectNamesJSONArray.getString(i));
			}
		}

		return getKey(
			buildProfile, jsonObject.getString("job_name"), null,
			jsonObject.optString("portal_upstream_branch_name"), projectNames,
			jsonObject.optString("repository_name"),
			jsonObject.optString("test_suite_name"),
			jsonObject.optString("upstream_branch_name"));
	}

	public static Job newJob(Build build) {
		TopLevelBuild topLevelBuild = build.getTopLevelBuild();

		PortalHotfixRelease portalHotfixRelease = null;

		if (build instanceof PortalHotfixReleaseBuild) {
			PortalHotfixReleaseBuild portalHotfixReleaseBuild =
				(PortalHotfixReleaseBuild)build;

			portalHotfixRelease =
				portalHotfixReleaseBuild.getPortalHotfixRelease();
		}

		String portalUpstreamBranchName = topLevelBuild.getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			portalUpstreamBranchName = topLevelBuild.getBranchName();
		}

		PortalGitWorkingDirectory portalGitWorkingDirectory = null;

		if (JenkinsResultsParserUtil.isCINode()) {
			portalGitWorkingDirectory =
				GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					portalUpstreamBranchName);
		}

		return _newJob(
			topLevelBuild.getBuildProfile(), topLevelBuild.getJobName(), null,
			portalGitWorkingDirectory, portalHotfixRelease,
			portalUpstreamBranchName, topLevelBuild.getProjectNames(),
			topLevelBuild.getBaseGitRepositoryName(),
			topLevelBuild.getTestSuiteName(), topLevelBuild.getBranchName());
	}

	public static Job newJob(BuildData buildData) {
		Job.BuildProfile buildProfile = null;
		String portalUpstreamBranchName = null;
		String repositoryName = null;
		String upstreamBranchName = null;

		if (buildData instanceof PortalBuildData) {
			PortalBuildData portalBuildData = (PortalBuildData)buildData;

			buildProfile = portalBuildData.getBuildProfile();
			portalUpstreamBranchName =
				portalBuildData.getPortalUpstreamBranchName();
			repositoryName = portalBuildData.getPortalGitHubRepositoryName();
			upstreamBranchName = portalBuildData.getPortalUpstreamBranchName();
		}

		return _newJob(
			buildProfile, buildData.getJobName(), null, null, null,
			portalUpstreamBranchName, null, repositoryName, null,
			upstreamBranchName);
	}

	public static Job newJob(
		Job.BuildProfile buildProfile, String jobName, JSONObject jsonObject,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		PortalHotfixRelease portalHotfixRelease,
		String portalUpstreamBranchName, List<String> projectNames,
		String repositoryName, String testSuiteName,
		String upstreamBranchName) {

		return _newJob(
			buildProfile, jobName, jsonObject, portalGitWorkingDirectory,
			portalHotfixRelease, portalUpstreamBranchName, projectNames,
			repositoryName, testSuiteName, upstreamBranchName);
	}

	public static Job newJob(
		Job.BuildProfile buildProfile, String jobName, JSONObject jsonObject,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String portalUpstreamBranchName, List<String> projectNames,
		String repositoryName, String testSuiteName,
		String upstreamBranchName) {

		return _newJob(
			buildProfile, jobName, jsonObject, portalGitWorkingDirectory, null,
			portalUpstreamBranchName, projectNames, repositoryName,
			testSuiteName, upstreamBranchName);
	}

	public static Job newJob(
		Job.BuildProfile buildProfile, String jobName, String testSuiteName) {

		return _newJob(
			buildProfile, jobName, null, null, null, null, null, null,
			testSuiteName, null);
	}

	public static Job newJob(JSONObject jsonObject) {
		return _newJob(
			null, null, jsonObject, null, null, null, null, null, null, null);
	}

	public static Job newJob(String jobName) {
		return _newJob(
			null, jobName, null, null, null, null, null, null, null, null);
	}

	private static Job _newJob(
		Job.BuildProfile buildProfile, String jobName, JSONObject jsonObject,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		PortalHotfixRelease portalHotfixRelease,
		String portalUpstreamBranchName, List<String> projectNames,
		String repositoryName, String testSuiteName,
		String upstreamBranchName) {

		String key = null;

		if ((jsonObject == null) &&
			JenkinsResultsParserUtil.isBuildCachingEnabled(
				jobName, testSuiteName)) {

			jsonObject = JobCacheUtil.getCachedJobJSONObject(
				jobName, portalGitWorkingDirectory, testSuiteName);
		}

		if (jsonObject != null) {
			jobName = jsonObject.getString("job_name");

			key = getKey(jsonObject);
		}
		else {
			key = getKey(
				buildProfile, jobName, portalGitWorkingDirectory,
				portalUpstreamBranchName, projectNames, repositoryName,
				testSuiteName, upstreamBranchName);
		}

		if (jsonObject == null) {
			BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

			if (buildDatabase.hasJob(key)) {
				return buildDatabase.getJob(key);
			}
		}

		synchronized (_jobs) {
			Job job = _jobs.get(key);

			if (job != null) {
				return job;
			}

			if (jobName.equals("js-test-csv-report") ||
				jobName.equals("junit-test-csv-report")) {

				if (portalGitWorkingDirectory == null) {
					File gitWorkingDir =
						JenkinsResultsParserUtil.getGitWorkingDir(
							new File(System.getProperty("user.dir")));

					Properties buildProperties =
						JenkinsResultsParserUtil.getProperties(
							new File(gitWorkingDir, "build.properties"));

					String gitWorkingBranchName =
						JenkinsResultsParserUtil.getProperty(
							buildProperties, "git.working.branch.name");

					String gitRepositoryName = "liferay-portal";

					if (!gitWorkingBranchName.equals("master")) {
						gitRepositoryName += "-ee";
					}

					GitWorkingDirectory gitWorkingDirectory =
						GitWorkingDirectoryFactory.newGitWorkingDirectory(
							gitWorkingBranchName, gitWorkingDir,
							gitRepositoryName);

					if (gitWorkingDirectory instanceof
							PortalGitWorkingDirectory) {

						portalGitWorkingDirectory =
							(PortalGitWorkingDirectory)gitWorkingDirectory;
					}
				}

				if (jsonObject != null) {
					job = new PortalAcceptancePullRequestJob(jsonObject);
				}
				else {
					job = new PortalAcceptancePullRequestJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.equals("root-cause-analysis-tool")) {
				if (jsonObject != null) {
					job = new RootCauseAnalysisToolJob(jsonObject);
				}
				else {
					job = new RootCauseAnalysisToolJob(
						buildProfile, jobName, upstreamBranchName);
				}
			}

			if (jobName.equals("root-cause-analysis-tool-batch")) {
				if (jsonObject != null) {
					job = new RootCauseAnalysisToolBatchJob(jsonObject);
				}
				else {
					job = new RootCauseAnalysisToolBatchJob(
						buildProfile, jobName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-fixpack-builder-pullrequest")) {
				if (jsonObject != null) {
					job = new FixPackBuilderGitRepositoryJob(jsonObject);
				}
				else {
					job = new FixPackBuilderGitRepositoryJob(
						buildProfile, jobName, testSuiteName,
						upstreamBranchName);
				}
			}

			if (jobName.equals("test-jenkins-acceptance-pullrequest")) {
				if (jsonObject != null) {
					job = new JenkinsGitRepositoryJob(jsonObject);
				}
				else {
					job = new JenkinsGitRepositoryJob(
						buildProfile, jobName, testSuiteName);
				}
			}

			if (jobName.startsWith("test-plugins-acceptance-pullrequest(")) {
				if (jsonObject != null) {
					job = new PluginsAcceptancePullRequestJob(jsonObject);
				}
				else {
					job = new PluginsAcceptancePullRequestJob(
						buildProfile, jobName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-plugins-extraapps")) {
				if (jsonObject != null) {
					job = new PluginsExtraAppsJob(jsonObject);
				}
				else {
					job = new PluginsExtraAppsJob(
						buildProfile, jobName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-plugins-marketplaceapp")) {
				if (jsonObject != null) {
					job = new PluginsMarketplaceAppJob(jsonObject);
				}
				else {
					job = new PluginsMarketplaceAppJob(
						buildProfile, jobName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-plugins-release")) {
				if (jsonObject != null) {
					job = new PluginsReleaseJob(jsonObject);
				}
				else {
					job = new PluginsReleaseJob(
						buildProfile, jobName, testSuiteName,
						upstreamBranchName);
				}
			}

			if (jobName.equals("test-plugins-upstream")) {
				if (jsonObject != null) {
					job = new PluginsUpstreamJob(jsonObject);
				}
				else {
					job = new PluginsUpstreamJob(
						buildProfile, jobName, testSuiteName,
						upstreamBranchName);
				}
			}

			if (jobName.startsWith("test-portal-acceptance-pullrequest(")) {
				if (jsonObject != null) {
					job = new PortalAcceptancePullRequestJob(jsonObject);
				}
				else {
					if (upstreamBranchName.contains("release")) {
						String githubUpstreamBranchName = Environment.get(
							"GITHUB_UPSTREAM_BRANCH_NAME");

						if (!JenkinsResultsParserUtil.isNullOrEmpty(
								githubUpstreamBranchName)) {

							upstreamBranchName = githubUpstreamBranchName;
						}
					}

					job = new PortalAcceptancePullRequestJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.startsWith("test-portal-acceptance-upstream")) {
				if (jsonObject != null) {
					job = new PortalAcceptanceUpstreamJob(jsonObject);
				}
				else {
					job = new PortalAcceptanceUpstreamJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-portal-app-release")) {
				if (jsonObject != null) {
					job = new PortalAppReleaseJob(jsonObject);
				}
				else {
					job = new PortalAppReleaseJob(
						buildProfile, jobName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-portal-fixpack-release")) {
				if (jsonObject != null) {
					job = new PortalFixpackReleaseJob(jsonObject);
				}
				else {
					job = new PortalFixpackReleaseJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-portal-hotfix-release")) {
				if (jsonObject != null) {
					job = new PortalHotfixReleaseJob(jsonObject);
				}
				else {
					job = new PortalHotfixReleaseJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						portalHotfixRelease, testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-portal-release")) {
				if (jsonObject != null) {
					job = new PortalReleaseJob(jsonObject);
				}
				else {
					job = new PortalReleaseJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-portal-source-format")) {
				if (jsonObject != null) {
					job = new PortalAcceptancePullRequestJob(jsonObject);
				}
				else {
					job = new PortalAcceptancePullRequestJob(
						buildProfile, jobName, portalGitWorkingDirectory, "sf",
						upstreamBranchName);
				}
			}

			if (jobName.startsWith("test-portal-testsuite-upstream(")) {
				if (jsonObject != null) {
					job = new PortalTestSuiteUpstreamJob(jsonObject);
				}
				else {
					job = new PortalTestSuiteUpstreamJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.equals("test-portal-upstream")) {
				if (jsonObject != null) {
					job = new PortalUpstreamJob(jsonObject);
				}
				else {
					job = new PortalUpstreamJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName, upstreamBranchName);
				}
			}

			if (jobName.startsWith("generate-reports") ||
				jobName.startsWith(
					"test-portal-testsuite-upstream-controller(") ||
				jobName.startsWith("test-portal-upstream-controller(") ||
				jobName.equals("test-poshi-release") ||
				jobName.startsWith(
					"test-qa-websites-functional-daily-controller(") ||
				jobName.startsWith(
					"test-qa-websites-functional-weekly-controller(")) {

				if (jsonObject != null) {
					job = new SimpleJob(jsonObject);
				}
				else {
					job = new SimpleJob(buildProfile, jobName);
				}
			}

			if (jobName.equals("test-qa-websites-functional-daily") ||
				jobName.equals("test-qa-websites-functional-environment") ||
				jobName.equals("test-qa-websites-functional-weekly")) {

				if (jsonObject != null) {
					job = new QAWebsitesGitRepositoryJob(jsonObject);
				}
				else {
					job = new QAWebsitesGitRepositoryJob(
						buildProfile, jobName, projectNames, testSuiteName,
						upstreamBranchName);
				}
			}

			if (jobName.startsWith(
					"test-subrepository-acceptance-pullrequest")) {

				if (jsonObject != null) {
					job = new SubrepositoryAcceptancePullRequestJob(jsonObject);
				}
				else {
					job = new SubrepositoryAcceptancePullRequestJob(
						buildProfile, jobName, portalUpstreamBranchName,
						repositoryName, testSuiteName, upstreamBranchName);
				}
			}

			if (job == null) {
				if (jsonObject != null) {
					job = new DefaultPortalJob(jsonObject);
				}
				else {
					job = new DefaultPortalJob(
						buildProfile, jobName, portalGitWorkingDirectory,
						testSuiteName);
				}
			}

			_jobs.put(key, job);

			return job;
		}
	}

	private static final Map<String, Job> _jobs = new ConcurrentHashMap<>();

}