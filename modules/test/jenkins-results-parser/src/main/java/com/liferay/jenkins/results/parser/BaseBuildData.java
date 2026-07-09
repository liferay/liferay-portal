/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseBuildData implements BuildData {

	public static String getJobName(String buildURL) {
		if (buildURL == null) {
			return null;
		}

		Matcher matcher = _buildURLPattern.matcher(buildURL);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("jobName");
	}

	@Override
	public File getArtifactDir() {
		return new File(getWorkspaceDir(), getRunId());
	}

	@Override
	public String getBuildDescription() {
		return getString("build_description");
	}

	@Override
	public Long getBuildDuration() {
		return optLong("build_duration", Long.valueOf(0));
	}

	@Override
	public String getBuildDurationString() {
		return JenkinsResultsParserUtil.toDurationString(getBuildDuration());
	}

	@Override
	public Integer getBuildNumber() {
		return optInt("build_number");
	}

	@Override
	public String getBuildParameter(String key) {
		Map<String, String> buildParameters = getBuildParameters();

		return buildParameters.get(key);
	}

	@Override
	public Map<String, String> getBuildParameters() {
		if (_buildParameters != null) {
			return _buildParameters;
		}

		JSONObject buildURLJSONObject = _getBuildURLJSONObject();

		if (buildURLJSONObject == null) {
			throw new RuntimeException("Please set the build URL");
		}

		if (!buildURLJSONObject.has("actions")) {
			return null;
		}

		JSONArray actionsJSONArray = buildURLJSONObject.getJSONArray("actions");

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			JSONObject actionsJSONObject = actionsJSONArray.getJSONObject(i);

			if ((actionsJSONObject == null) ||
				!actionsJSONObject.has("parameters")) {

				continue;
			}

			JSONArray parametersJSONArray = actionsJSONObject.getJSONArray(
				"parameters");

			_buildParameters = new HashMap<>();

			for (int j = 0; j < parametersJSONArray.length(); j++) {
				JSONObject parameterJSONObject =
					parametersJSONArray.getJSONObject(j);

				if ((parameterJSONObject == null) ||
					!parameterJSONObject.has("name") ||
					!parameterJSONObject.has("value")) {

					continue;
				}

				_buildParameters.put(
					parameterJSONObject.getString("name"),
					parameterJSONObject.getString("value"));
			}

			return _buildParameters;
		}

		return null;
	}

	@Override
	public String getBuildResult() {
		return optString("build_result", "");
	}

	@Override
	public String getBuildStatus() {
		return optString("build_status", "");
	}

	@Override
	public String getBuildURL() {
		return optString("build_url");
	}

	@Override
	public String getCohortName() {
		return optString("cohort_name");
	}

	@Override
	public Host getHost() {
		if (_host != null) {
			return _host;
		}

		String hostname = getHostname();

		if (hostname == null) {
			return null;
		}

		_host = HostFactory.newHost(hostname);

		return _host;
	}

	@Override
	public String getHostname() {
		return optString("hostname");
	}

	@Override
	public String getJenkinsGitHubBranchName() {
		return getGitHubBranchName(getJenkinsGitHubURL());
	}

	@Override
	public String getJenkinsGitHubRepositoryName() {
		return getGitHubRepositoryName(getJenkinsGitHubURL());
	}

	@Override
	public String getJenkinsGitHubURL() {
		return getString("jenkins_github_url");
	}

	@Override
	public String getJenkinsGitHubUsername() {
		return getGitHubUsername(getJenkinsGitHubURL());
	}

	@Override
	public String getJobName() {
		return optString("job_name");
	}

	@Override
	public String getJobURL() {
		return optString("job_url");
	}

	@Override
	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	@Override
	public String getMasterHostname() {
		return optString("master_hostname");
	}

	@Override
	public String getRunId() {
		return getString("run_id");
	}

	@Override
	public Long getStartTime() {
		return getLong("start_time");
	}

	@Override
	public String getStartTimeString() {
		return getFormattedDate(getStartTime());
	}

	@Override
	public String getUserContentRelativePath() {
		return JenkinsResultsParserUtil.combine(
			"jobs/", getTopLevelJobName(), "/builds/",
			String.valueOf(getTopLevelBuildNumber()), "/");
	}

	@Override
	public File getWorkspaceDir() {
		return getFile("workspace_dir");
	}

	@Override
	public void setBuildDescription(String buildDescription) {
		put("build_description", buildDescription);
	}

	@Override
	public void setBuildDuration(Long buildDuration) {
		put("build_duration", buildDuration);
	}

	@Override
	public void setBuildResult(String buildResult) {
		put("build_result", buildResult);
	}

	@Override
	public void setBuildStatus(String buildStatus) {
		put("build_status", buildStatus);
	}

	@Override
	public void setBuildURL(String buildURL) {
		Matcher matcher = _buildURLPattern.matcher(buildURL);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid build URL " + buildURL);
		}

		put("build_number", Integer.valueOf(matcher.group("buildNumber")));
		put("build_url", buildURL);
		put("cohort_name", matcher.group("cohortName"));
		put("hostname", _getHostname());
		put("job_url", matcher.group("jobURL"));
		put("master_hostname", matcher.group("masterHostname"));
		put("start_time", _getStartTime());
		put("type", getType());
	}

	@Override
	public void setInvocationTime(Long invocationTime) {
		put("invocation_time", invocationTime);
	}

	@Override
	public void setJenkinsGitHubURL(String jenkinsGitHubURL) {
		put("jenkins_github_url", jenkinsGitHubURL);
	}

	@Override
	public void setWorkspaceDir(File workspaceDir) {
		put(
			"workspace_dir",
			JenkinsResultsParserUtil.getCanonicalPath(workspaceDir));
	}

	protected static boolean isValidJSONObject(
		JSONObject jsonObject, String type) {

		if ((jsonObject == null) || (type == null)) {
			return false;
		}

		if (jsonObject.has("type") &&
			type.equals(jsonObject.getString("type"))) {

			return true;
		}

		return false;
	}

	protected BaseBuildData(String runId, String jobName, String buildURL) {
		JSONObject jsonObject = buildDatabase.getBuildDataJSONObject(runId);

		String json = jsonObject.toString();

		if (json.equals("{}") && (buildURL != null)) {
			try {
				jsonObject = buildDatabase.getBuildDataJSONObject(
					new URL(buildURL));

				if (jsonObject.has("run_id")) {
					runId = jsonObject.getString("run_id");
				}
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		_jsonObject = jsonObject;

		put("run_id", runId);

		if (jobName == null) {
			throw new RuntimeException("Please set a job name");
		}

		put("job_name", jobName);

		if (buildURL == null) {
			return;
		}

		setBuildURL(buildURL);

		if (!has("build_description")) {
			setBuildDescription(_getDefaultBuildDescription());
		}

		setJenkinsGitHubURL(JenkinsResultsParserUtil.getJenkinsGitHubURL());
		setWorkspaceDir(DIR_WORKSPACE_DEFAULT);

		validateKeys(_KEYS_REQUIRED);
	}

	protected File getFile(String key) {
		return new File(getString(key));
	}

	protected String getFormattedDate(Long timestamp) {
		return JenkinsResultsParserUtil.toDateString(
			new Date(timestamp), "MMM dd, yyyy h:mm:ss a z", "US/Pacific");
	}

	protected String getGitHubBranchName(String gitHubBranchURL) {
		Matcher matcher = _gitHubBranchURLPattern.matcher(gitHubBranchURL);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Invalid GitHub Branch URL " + gitHubBranchURL);
		}

		return matcher.group("branchName");
	}

	protected String getGitHubRepositoryName(String gitHubBranchURL) {
		Matcher matcher = _gitHubBranchURLPattern.matcher(gitHubBranchURL);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Invalid GitHub Branch URL " + gitHubBranchURL);
		}

		return matcher.group("repositoryName");
	}

	protected String getGitHubUsername(String gitHubBranchURL) {
		Matcher matcher = _gitHubBranchURLPattern.matcher(gitHubBranchURL);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Invalid GitHub Branch URL " + gitHubBranchURL);
		}

		return matcher.group("username");
	}

	protected JSONArray getJSONArray(String key) {
		return _jsonObject.getJSONArray(key);
	}

	protected JSONObject getJSONObject(String key) {
		return _jsonObject.getJSONObject(key);
	}

	protected List<String> getList(String key) {
		JSONArray jsonArray = getJSONArray(key);

		List<String> list = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}

		return list;
	}

	protected Long getLong(String key) {
		return _jsonObject.getLong(key);
	}

	protected String getString(String key) {
		return _jsonObject.getString(key);
	}

	protected abstract String getType();

	protected boolean has(String key) {
		return _jsonObject.has(key);
	}

	protected Integer optInt(String key) {
		return _jsonObject.optInt(key);
	}

	protected Long optLong(String key, Long defaultValue) {
		return _jsonObject.optLong(key, defaultValue);
	}

	protected String optString(String key) {
		return _jsonObject.optString(key);
	}

	protected String optString(String key, String defaultValue) {
		return _jsonObject.optString(key, defaultValue);
	}

	protected void put(String key, Object value) {
		_jsonObject.put(key, value);

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		buildDatabase.putBuildData(getRunId(), this);
	}

	protected void validateKeys(String[] requiredKeys) {
		for (String requiredKey : requiredKeys) {
			if (!has(requiredKey)) {
				throw new RuntimeException("Missing " + requiredKey);
			}
		}
	}

	protected static final BuildDatabase buildDatabase =
		BuildDatabaseUtil.getBuildDatabase();

	private JSONObject _getBuildURLJSONObject() {
		String buildURL = getBuildURL();

		if (buildURL == null) {
			return null;
		}

		try {
			return JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(buildURL + "/api/json"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getDefaultBuildDescription() {
		return JenkinsResultsParserUtil.combine(
			"<a href=\"https://", getTopLevelMasterHostname(),
			".liferay.com/userContent/", getUserContentRelativePath(),
			"jenkins-report.html\">Jenkins Report</a>");
	}

	private String _getHostname() {
		JSONObject buildURLJSONObject = _getBuildURLJSONObject();

		if (buildURLJSONObject == null) {
			throw new RuntimeException("Please set the build URL");
		}

		return buildURLJSONObject.getString("builtOn");
	}

	private long _getStartTime() {
		JSONObject buildURLJSONObject = _getBuildURLJSONObject();

		if (buildURLJSONObject == null) {
			throw new RuntimeException("Please set the build URL");
		}

		return buildURLJSONObject.getLong("timestamp");
	}

	private static final String[] _KEYS_REQUIRED = {
		"build_description", "build_number", "build_url", "cohort_name",
		"hostname", "jenkins_github_url", "job_name", "master_hostname",
		"run_id", "workspace_dir"
	};

	private static final Pattern _buildURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"(?<jobURL>https?://(?<masterHostname>",
			"(?<cohortName>test-\\d+)-\\d+)(-aws)?(\\.liferay\\.com)?/job/",
			"(?<jobName>[^/]+)/(.*/)?)(?<buildNumber>\\d+)/?"));
	private static final Pattern _gitHubBranchURLPattern = Pattern.compile(
		"https://github.com/(?<username>[^/]+)/(?<repositoryName>[^/]+)/tree/" +
			"(?<branchName>.+)");

	private Map<String, String> _buildParameters;
	private Host _host;
	private final JSONObject _jsonObject;

}