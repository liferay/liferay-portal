/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseBuildRunner<T extends BuildData>
	implements BuildRunner<T> {

	@Override
	public T getBuildData() {
		return _buildData;
	}

	@Override
	public void run() {
		updateBuildDescription();

		setUpWorkspace();
	}

	@Override
	public void setUp() {
	}

	@Override
	public void tearDown() {
		cleanUpHostServices();

		tearDownWorkspace();
	}

	protected BaseBuildRunner(T buildData) {
		_buildData = buildData;
	}

	protected void cleanUpHostServices() {
		Host host = _buildData.getHost();

		host.cleanUpServices();
	}

	protected long getAllowedBuildStartTime() {
		long allowedBuildAge = 7 * _MILLISECONDS_PER_DAY;

		String allowedBuildAgeInDays = Environment.get(
			"ALLOWED_BUILD_AGE_IN_DAYS");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(allowedBuildAgeInDays)) {
			allowedBuildAge =
				Integer.parseInt(allowedBuildAgeInDays) * _MILLISECONDS_PER_DAY;
		}

		return JenkinsResultsParserUtil.getCurrentTimeMillis() -
			allowedBuildAge;
	}

	protected Job getJob() {
		if (_job != null) {
			return _job;
		}

		_job = JobFactory.newJob(_buildData);

		return _job;
	}

	protected List<JSONObject> getPreviousBuildJSONObjects() {
		if (_previousBuildJSONObjects != null) {
			return _previousBuildJSONObjects;
		}

		_previousBuildJSONObjects = new ArrayList<>();

		BuildData buildData = getBuildData();

		try {
			JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(buildData.getJobURL()) +
					"api/json");

			JSONArray buildsJSONArray = jsonObject.getJSONArray("builds");

			for (int i = 0; i < buildsJSONArray.length(); i++) {
				JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

				_previousBuildJSONObjects.add(
					JenkinsResultsParserUtil.toJSONObject(
						JenkinsResultsParserUtil.getLocalURL(
							buildJSONObject.getString("url") + "api/json")));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _previousBuildJSONObjects;
	}

	protected void keepJenkinsBuild(boolean keepLogs) {
		JenkinsResultsParserUtil.keepJenkinsBuild(
			keepLogs, _buildData.getBuildNumber(), _buildData.getJobName(),
			_buildData.getMasterHostname());
	}

	protected void publishToUserContentDir(File file) {
		if (!JenkinsResultsParserUtil.isCINode()) {
			return;
		}

		String userContentRelativePath =
			_buildData.getUserContentRelativePath();

		userContentRelativePath = userContentRelativePath.replace(")", "\\)");
		userContentRelativePath = userContentRelativePath.replace("(", "\\(");

		RemoteExecutor remoteExecutor = new RemoteExecutor();

		int returnCode = remoteExecutor.execute(
			1, new String[] {_buildData.getMasterHostname()},
			new String[] {
				"mkdir -p /opt/java/jenkins/userContent/" +
					userContentRelativePath
			});

		if (returnCode != 0) {
			throw new RuntimeException("Unable to create target directory");
		}

		int maxRetries = 3;
		int retries = 0;

		while (retries < maxRetries) {
			try {
				retries++;

				String command = JenkinsResultsParserUtil.combine(
					"time timeout 1200 rsync -Ipqrs --chmod=go=rx ",
					JenkinsResultsParserUtil.getCanonicalPath(file), " ",
					_buildData.getTopLevelMasterHostname(),
					":/opt/java/jenkins/userContent/", userContentRelativePath);

				JenkinsResultsParserUtil.executeBashCommands(command);

				break;
			}
			catch (IOException | TimeoutException exception) {
				if (retries == maxRetries) {
					throw new RuntimeException(
						"Unable to send " + file.getName(), exception);
				}

				System.out.println(
					"Unable to execute bash commands, retrying... ");

				exception.printStackTrace();

				JenkinsResultsParserUtil.sleep(3000);
			}
		}
	}

	protected void retirePreviousBuilds() {
		long allowedBuildStartTime = getAllowedBuildStartTime();

		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			long previousBuildStartTime = previousBuildJSONObject.getLong(
				"timestamp");

			if (previousBuildStartTime > allowedBuildStartTime) {
				continue;
			}

			try {
				JSONObject injectedEnvVarsJSONObject =
					JenkinsResultsParserUtil.toJSONObject(
						JenkinsResultsParserUtil.getLocalURL(
							previousBuildJSONObject.getString("url") +
								"/injectedEnvVars/api/json"));

				JSONObject envMapJSONObject =
					injectedEnvVarsJSONObject.getJSONObject("envMap");

				if (envMapJSONObject.isEmpty()) {
					return;
				}

				JenkinsResultsParserUtil.keepJenkinsBuild(
					false,
					Integer.valueOf(envMapJSONObject.getString("BUILD_NUMBER")),
					envMapJSONObject.getString("JOB_NAME"),
					envMapJSONObject.getString("HOSTNAME"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	protected void setUpWorkspace() {
		Workspace workspace = getWorkspace();

		workspace.setUp();
	}

	protected void tearDownWorkspace() {
		Workspace workspace = getWorkspace();

		workspace.tearDown();
	}

	protected void updateBuildDescription() {
		JenkinsResultsParserUtil.updateBuildDescription(
			_buildData.getBuildDescription(), _buildData.getBuildNumber(),
			_buildData.getJobName(), _buildData.getMasterHostname());
	}

	private static final long _MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

	private final T _buildData;
	private Job _job;
	private List<JSONObject> _previousBuildJSONObjects;

}