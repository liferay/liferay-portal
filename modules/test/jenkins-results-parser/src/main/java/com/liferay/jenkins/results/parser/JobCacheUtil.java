/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JobCacheUtil {

	public static void cacheJob(Job job) {
		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			return;
		}

		PortalGitWorkingDirectory portalGitWorkingDirectory = null;

		if (job instanceof PortalGitRepositoryJob) {
			PortalGitRepositoryJob portalGitRepositoryJob =
				(PortalGitRepositoryJob)job;

			portalGitWorkingDirectory =
				portalGitRepositoryJob.getPortalGitWorkingDirectory();
		}

		String testSuiteName = null;

		if (job instanceof TestSuiteJob) {
			TestSuiteJob testSuiteJob = (TestSuiteJob)job;

			testSuiteName = testSuiteJob.getTestSuiteName();
		}

		String cachedJobS3ObjectPath = _getCachedJobS3ObjectPath(
			job.getJobName(), portalGitWorkingDirectory, testSuiteName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(cachedJobS3ObjectPath)) {
			return;
		}

		File jobFile = null;
		File jobGzFile = null;

		try {
			String timeStamp = JenkinsResultsParserUtil.getDistinctTimeStamp();

			jobFile = new File(
				System.getProperty("java.io.tmpdir"),
				JenkinsResultsParserUtil.combine("job-", timeStamp, ".json"));
			jobGzFile = new File(
				System.getProperty("java.io.tmpdir"),
				JenkinsResultsParserUtil.combine(
					"job-", timeStamp, ".json.gz"));

			JenkinsResultsParserUtil.write(
				jobFile, String.valueOf(job.getJSONObject()));

			JenkinsResultsParserUtil.gzip(jobFile, jobGzFile);

			CloudBucketUtil.uploadS3File(cachedJobS3ObjectPath, jobGzFile);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			if (jobFile != null) {
				JenkinsResultsParserUtil.delete(jobFile);
			}

			if (jobGzFile != null) {
				JenkinsResultsParserUtil.delete(jobGzFile);
			}
		}
	}

	public static JSONObject getCachedJobJSONObject(
		String jobName, PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName) {

		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			return null;
		}

		String cachedJobS3ObjectPath = _getCachedJobS3ObjectPath(
			jobName, portalGitWorkingDirectory, testSuiteName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(cachedJobS3ObjectPath)) {
			return null;
		}

		JSONObject jobJSONObject = _jobJSONObjects.get(cachedJobS3ObjectPath);

		if (jobJSONObject != null) {
			return jobJSONObject;
		}

		if (!CloudBucketUtil.isS3ObjectPathAvailable(cachedJobS3ObjectPath)) {
			return null;
		}

		synchronized (cachedJobS3ObjectPath.intern()) {
			jobJSONObject = _jobJSONObjects.get(cachedJobS3ObjectPath);

			if (jobJSONObject != null) {
				return jobJSONObject;
			}

			File file = new File(
				System.getProperty("java.io.tmpdir"),
				JenkinsResultsParserUtil.getDistinctTimeStamp() + ".json.gz");

			try {
				CloudBucketUtil.downloadS3File(file, cachedJobS3ObjectPath);

				String fileContent = JenkinsResultsParserUtil.read(file);

				if (JenkinsResultsParserUtil.isJSONObject(fileContent)) {
					jobJSONObject = new JSONObject(fileContent);
				}
			}
			catch (IOException | JSONException exception) {
				return null;
			}
			finally {
				JenkinsResultsParserUtil.delete(file);
			}

			if (jobJSONObject != null) {
				_jobJSONObjects.put(cachedJobS3ObjectPath, jobJSONObject);
			}

			return jobJSONObject;
		}
	}

	public static boolean isJobCached(
		String jobName, PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName) {

		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			return false;
		}

		JSONObject cachedJobJSONObject = getCachedJobJSONObject(
			jobName, portalGitWorkingDirectory, testSuiteName);

		if (cachedJobJSONObject != null) {
			return true;
		}

		return false;
	}

	private static String _getCachedJobS3ObjectPath(
		String jobName, PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName) {

		if (portalGitWorkingDirectory == null) {
			return null;
		}

		PortalWorkspace portalWorkspace = _getPortalWorkspace();

		if (portalWorkspace == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		try {
			sb.append(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.job.cache.path"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		sb.append("/");
		sb.append(jobName.replaceAll("[\\/\\(\\)]+", "__"));
		sb.append("/");
		sb.append(portalWorkspace.getS3ObjectPathName());
		sb.append("/");

		if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
			testSuiteName = "default";
		}

		sb.append(testSuiteName);

		sb.append(".json.gz");

		return sb.toString();
	}

	private static PortalWorkspace _getPortalWorkspace() {
		List<PortalWorkspace> portalWorkspaces = new ArrayList<>();

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		for (Workspace workspace : buildDatabase.getWorkspaces()) {
			if (!(workspace instanceof PortalWorkspace)) {
				continue;
			}

			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			WorkspaceGitRepository workspaceGitRepository =
				portalWorkspace.getPrimaryWorkspaceGitRepository();

			if (workspaceGitRepository instanceof
					PortalWorkspaceGitRepository) {

				return portalWorkspace;
			}

			portalWorkspaces.add(portalWorkspace);
		}

		if (portalWorkspaces.isEmpty()) {
			return null;
		}

		return portalWorkspaces.get(0);
	}

	private static final Map<String, JSONObject> _jobJSONObjects =
		new ConcurrentHashMap<>();

}