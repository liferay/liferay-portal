/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildFactory;
import com.liferay.jenkins.results.parser.Environment;
import com.liferay.jenkins.results.parser.JenkinsAPIUtil;
import com.liferay.jenkins.results.parser.JenkinsCohort;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.ReinvokeRule;
import com.liferay.jenkins.results.parser.SlaveOfflineRule;
import com.liferay.jenkins.results.parser.SubrepositoryWorkspace;
import com.liferay.jenkins.results.parser.TopLevelBuild;
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseBundlePersistentResource
	extends BasePersistentResource {

	@Override
	public String getBaseS3ObjectPath() {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.persistent.resources.archives.path"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		WorkspaceGitRepository bundleWorkspaceGitRepository =
			getBundleWorkspaceGitRepository();

		sb.append("/");
		sb.append(getType());
		sb.append("/");
		sb.append(bundleWorkspaceGitRepository.getName());
		sb.append("/");
		sb.append(bundleWorkspaceGitRepository.getBaseBranchSHA());
		sb.append("/");
		sb.append(bundleWorkspaceGitRepository.getSenderBranchSHA());

		return sb.toString();
	}

	protected BaseBundlePersistentResource(
		BuildDatabase buildDatabase, TopLevelBuild topLevelBuild) {

		super(buildDatabase);

		_topLevelBuild = topLevelBuild;
	}

	@Override
	protected Set<String> getArtifactNames() {
		Set<String> artifactNames = new HashSet<>();

		try {
			String artifactNamesString =
				JenkinsResultsParserUtil.getBuildProperty(
					"persistent.resource.artifact.names[" + getType() + "]",
					getUpstreamBranchName());

			if (JenkinsResultsParserUtil.isNullOrEmpty(artifactNamesString)) {
				return artifactNames;
			}

			Collections.addAll(artifactNames, artifactNamesString.split(","));
		}
		catch (IOException ioException) {
		}

		return artifactNames;
	}

	protected abstract WorkspaceGitRepository getBundleWorkspaceGitRepository();

	@Override
	protected String getCurrentTopLevelBuildURL() {
		if (_topLevelBuild != null) {
			return _topLevelBuild.getBuildURL();
		}

		return super.getCurrentTopLevelBuildURL();
	}

	@Override
	protected String getStatusMessage() {
		Status status = getStatus();

		if (status == Status.FAILED) {
			return "Failed to build bundles at " + getProducerBuildURL();
		}
		else if (status == Status.IN_PROGRESS) {
			if (isController()) {
				return "Building bundles at " + getProducerBuildURL();
			}

			return "Waiting for bundles at " + getProducerBuildURL();
		}
		else if (status == Status.IN_QUEUE) {
			return "In queue at " + _getProducerJobURL();
		}
		else if (status == Status.SUCCESS) {
			return "Completed bundles at " + getProducerBuildURL();
		}

		return "Not started";
	}

	protected String getUpstreamBranchName() {
		WorkspaceGitRepository workspaceGitRepository =
			getBundleWorkspaceGitRepository();

		return workspaceGitRepository.getUpstreamBranchName();
	}

	protected synchronized Workspace getWorkspace() {
		if (_workspace != null) {
			return _workspace;
		}

		String primaryGitDirectoryName = getStartProperty(
			"PRIMARY_GIT_DIRECTORY_NAME");

		BuildDatabase buildDatabase = getBuildDatabase();

		if (!buildDatabase.hasWorkspace(primaryGitDirectoryName)) {
			return null;
		}

		_workspace = buildDatabase.getWorkspace(primaryGitDirectoryName);

		if (_workspace instanceof SubrepositoryWorkspace) {
			String portalUpstreamBranchName = getStartProperty(
				"PORTAL_UPSTREAM_BRANCH_NAME");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalUpstreamBranchName)) {

				SubrepositoryWorkspace subrepositoryWorkspace =
					(SubrepositoryWorkspace)_workspace;

				subrepositoryWorkspace.setPortalUpstreamBranchName(
					portalUpstreamBranchName);
			}
		}

		return _workspace;
	}

	@Override
	protected void populateDataJSONObject(JSONObject dataJSONObject) {
		dataJSONObject.put(
			"redispatch_attempts", _redispatchAttempts
		).put(
			"redispatch_history", _redispatchHistoryJSONArray
		);
	}

	protected void start() {
		_invokeBuild();

		print("Start building bundles at " + _getProducerJobURL());
	}

	@Override
	protected void update() {
		JSONObject dataJSONObject = getDataJSONObject();

		if (dataJSONObject == null) {
			start();

			return;
		}

		if (!isController()) {
			setControllerBuildURL(
				dataJSONObject.optString("controller_build_url"));

			String producerBuildURL = dataJSONObject.optString(
				"producer_build_url");

			setProducerBuildURL(producerBuildURL);

			if (JenkinsResultsParserUtil.isURL(producerBuildURL)) {
				_updateBuild(producerBuildURL);
			}

			setProducerJenkinsMaster(null);

			String producerJenkinsMasterName = dataJSONObject.optString(
				"producer_jenkins_master");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					producerJenkinsMasterName)) {

				setProducerJenkinsMaster(
					JenkinsMaster.getInstance(producerJenkinsMasterName));
			}

			setProducerQueueId(dataJSONObject.optLong("producer_queue_id"));
			setStatus(Status.valueOf(dataJSONObject.getString("status")));

			_redispatchAttempts = dataJSONObject.optInt(
				"redispatch_attempts", 0);

			JSONArray redispatchHistoryJSONArray = dataJSONObject.optJSONArray(
				"redispatch_history");

			if (redispatchHistoryJSONArray != null) {
				_redispatchHistoryJSONArray = redispatchHistoryJSONArray;
			}
			else {
				_redispatchHistoryJSONArray = new JSONArray();
			}

			Status status = getStatus();

			if (status == Status.FAILED) {
				if (_isTransientFailure(_build) ||
					(_redispatchAttempts < _MAX_REDISPATCH_ATTEMPTS)) {

					_redispatchBuild(dataJSONObject);
				}
				else {
					print("No redispatch attempts remaining");
				}

				return;
			}

			if (((status == Status.NOT_STARTED) ||
				 (status == Status.IN_QUEUE) ||
				 (status == Status.IN_PROGRESS)) &&
				_isControllerBuildFinished()) {

				print(
					"Redispatching bundles after controller build completed " +
						"at " + getControllerBuildURL());

				_redispatchBuild(dataJSONObject);

				return;
			}

			if (isMissing()) {
				_missingCount++;

				if (_missingCount >= _MAX_MISSING_COUNT) {
					start();

					_missingCount = 0;

					return;
				}
			}

			return;
		}

		Status status = getStatus();

		if (status == Status.NOT_STARTED) {
			if (_transientReinvocationCount <
					_MAX_TRANSIENT_REINVOCATION_COUNT) {

				_transientReinvocationCount++;

				start();
			}
			else {
				print("No transient reinvocation attempts remaining");
			}

			return;
		}

		if (status == Status.IN_QUEUE) {
			JenkinsMaster producerJenkinsMaster = getProducerJenkinsMaster();

			long producerQueueId = getProducerQueueId();

			for (JenkinsMaster.QueueItem queueItem :
					producerJenkinsMaster.getQueueItems()) {

				if (queueItem.getId() == producerQueueId) {
					return;
				}
			}

			String producerBuildURL = JenkinsResultsParserUtil.getBuildURL(
				_JOB_NAME, producerJenkinsMaster, producerQueueId);

			if (JenkinsResultsParserUtil.isURL(producerBuildURL)) {
				setStatus(Status.IN_PROGRESS);

				setProducerBuildURL(producerBuildURL);
				setProducerJenkinsMaster(producerJenkinsMaster);
				setProducerQueueId(producerQueueId);

				_updateBuild(producerBuildURL);

				save();

				return;
			}

			print("WARNING: Unable to find queue item");

			_missingCount++;

			if (_missingCount >= _MAX_MISSING_COUNT) {
				_missingCount = 0;

				print("Reinvoking bundles after missing queue item");

				start();
			}

			return;
		}

		String producerBuildURL = getProducerBuildURL();

		if (!JenkinsResultsParserUtil.isURL(producerBuildURL)) {
			print("WARNING: Unable to find producer build url");

			return;
		}

		if (status == Status.IN_PROGRESS) {
			JSONObject apiJSONObject = JenkinsAPIUtil.getAPIJSONObject(
				producerBuildURL);

			String result = apiJSONObject.optString("result");

			if (JenkinsResultsParserUtil.isNullOrEmpty(result)) {
				return;
			}

			_updateBuild(producerBuildURL);

			if (Objects.equals(result, "SUCCESS")) {
				setStatus(Status.SUCCESS);
			}
			else {
				_failCount++;

				if (_failCount <= _MAX_FAIL_COUNT) {
					print(
						JenkinsResultsParserUtil.combine(
							"Retry ", String.valueOf(_failCount), " of ",
							String.valueOf(_MAX_FAIL_COUNT),
							" due to FAILURE in ", getProducerBuildURL()));

					start();

					return;
				}

				if (_isTransientFailure(_build)) {
					print(
						"Resetting bundles after transient failure in " +
							getProducerBuildURL());

					_failCount = 0;

					setStatus(Status.NOT_STARTED);
				}
				else {
					setStatus(Status.FAILED);
				}
			}

			save();
		}
	}

	private String _getAxisVariable() {
		return String.valueOf(getType());
	}

	private String _getBaseInvocationURL() {
		try {
			String serverType = "production";

			String topLevelBuildURL = getCurrentTopLevelBuildURL();

			if (topLevelBuildURL.contains("test-5")) {
				serverType = "staging";
			}

			return JenkinsResultsParserUtil.getBuildProperty(
				"github.webhook.base.invocation.url", serverType);
		}
		catch (IOException ioException) {
			return _BASE_INVOCATION_URL;
		}
	}

	private JenkinsCohort _getJenkinsCohort(JenkinsMaster jenkinsMaster) {
		if (jenkinsMaster != null) {
			JenkinsCohort jenkinsCohort = jenkinsMaster.getJenkinsCohort();

			if (jenkinsCohort != null) {
				return jenkinsCohort;
			}
		}

		String baseInvocationURL = _getBaseInvocationURL();

		Matcher matcher = _baseInvocationURLPattern.matcher(baseInvocationURL);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Unable to determine Jenkins cohort from " + baseInvocationURL);
		}

		return JenkinsCohort.getInstance(matcher.group("cohortName"));
	}

	private String _getProducerJobURL() {
		JenkinsMaster producerJenkinsMaster = getProducerJenkinsMaster();

		if (producerJenkinsMaster == null) {
			return null;
		}

		return producerJenkinsMaster.getRemoteURL() + "job/" + _JOB_NAME;
	}

	private Map<String, String> _getTopLevelJenkinsBuildParameters() {
		Map<String, String> jenkinsBuildParameters = new HashMap<>();

		String currentTopLevelBuildURL = getCurrentTopLevelBuildURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(currentTopLevelBuildURL)) {
			return jenkinsBuildParameters;
		}

		Map<String, String> buildParameters;

		try {
			buildParameters = JenkinsResultsParserUtil.getBuildParameters(
				currentTopLevelBuildURL);
		}
		catch (RuntimeException runtimeException) {
			print(
				"WARNING: Unable to get build parameters from " +
					currentTopLevelBuildURL);

			return jenkinsBuildParameters;
		}

		for (Map.Entry<String, String> entry : buildParameters.entrySet()) {
			String name = entry.getKey();

			if (!name.startsWith("JENKINS_GITHUB_")) {
				continue;
			}

			String value = entry.getValue();

			if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
				continue;
			}

			jenkinsBuildParameters.put(name, value);
		}

		return jenkinsBuildParameters;
	}

	private void _invokeBuild() {
		setControllerBuildURL(getCurrentTopLevelBuildURL());

		JenkinsMaster currentProducerJenkinsMaster = getProducerJenkinsMaster();

		JenkinsCohort jenkinsCohort = _getJenkinsCohort(
			currentProducerJenkinsMaster);

		JenkinsMaster producerJenkinsMaster =
			jenkinsCohort.getMostAvailableJenkinsMaster(
				currentProducerJenkinsMaster, 1, _JOB_NAME);

		setProducerJenkinsMaster(producerJenkinsMaster);

		Map<String, String> buildParameters = new HashMap<>();

		BuildDatabase buildDatabase = getBuildDatabase();

		Properties startProperties = getStartProperties();

		buildDatabase.putProperties(
			_JOB_VARIANT + "/start.properties", startProperties, true);

		buildDatabase.uploadBuildDatabaseFileToCloudBucket();

		for (String startPropertyName : startProperties.stringPropertyNames()) {
			String startPropertyValue = JenkinsResultsParserUtil.getProperty(
				startProperties, startPropertyName);

			if (JenkinsResultsParserUtil.isNullOrEmpty(startPropertyValue)) {
				continue;
			}

			buildParameters.put(startPropertyName, startPropertyValue);
		}

		buildParameters.putAll(_getTopLevelJenkinsBuildParameters());

		buildParameters.put("AXIS_VARIABLE", _getAxisVariable());
		buildParameters.put("BUILD_PRIORITY", _BUILD_PRIORITY);
		buildParameters.put("JOB_VARIANT", _JOB_VARIANT);
		buildParameters.put("PARENT_BUILD_URL", getCurrentTopLevelBuildURL());
		buildParameters.put("SLAVE_LABEL", "slave-bundle-builder");

		setProducerQueueId(
			JenkinsResultsParserUtil.invokeJenkinsBuild(
				producerJenkinsMaster, _JOB_NAME, buildParameters));

		setStatus(Status.IN_QUEUE);

		save();
	}

	private boolean _isControllerBuildFinished() {
		String controllerBuildURL = getControllerBuildURL();

		if (!JenkinsResultsParserUtil.isURL(controllerBuildURL)) {
			return false;
		}

		JSONObject apiJSONObject = JenkinsAPIUtil.getAPIJSONObject(
			controllerBuildURL, "result");

		return !JenkinsResultsParserUtil.isNullOrEmpty(
			apiJSONObject.optString("result"));
	}

	private boolean _isTransientFailure(Build build) {
		if (build == null) {
			return false;
		}

		for (ReinvokeRule reinvokeRule : ReinvokeRule.getReinvokeRules()) {
			if (reinvokeRule.matches(build)) {
				return true;
			}
		}

		for (SlaveOfflineRule slaveOfflineRule :
				SlaveOfflineRule.getSlaveOfflineRules()) {

			if (slaveOfflineRule.matches(build)) {
				return true;
			}
		}

		return false;
	}

	private void _redispatchBuild(JSONObject cachedDataJSONObject) {
		_redispatchHistoryJSONArray.put(
			new JSONObject(
			).put(
				"controller_build_url",
				cachedDataJSONObject.optString("controller_build_url")
			).put(
				"producer_build_url",
				cachedDataJSONObject.optString("producer_build_url")
			).put(
				"status", cachedDataJSONObject.optString("status")
			));

		while (_redispatchHistoryJSONArray.length() >
					_MAX_REDISPATCH_ATTEMPTS) {

			_redispatchHistoryJSONArray.remove(0);
		}

		_redispatchAttempts++;

		String currentTopLevelBuildURL = getCurrentTopLevelBuildURL();

		setControllerBuildURL(currentTopLevelBuildURL);

		setProducerBuildURL(null);
		setProducerJenkinsMaster(null);
		setProducerQueueId(0);
		setStatus(Status.IN_QUEUE);

		save();

		JenkinsResultsParserUtil.sleep(_REDISPATCH_VERIFY_TIME);

		JSONObject dataJSONObject = getDataJSONObject();

		if (dataJSONObject != null) {
			String controllerBuildURL = dataJSONObject.optString(
				"controller_build_url");

			if (!Objects.equals(currentTopLevelBuildURL, controllerBuildURL)) {
				print(
					"Following redispatched bundles at " + controllerBuildURL);

				setControllerBuildURL(controllerBuildURL);
				setProducerBuildURL(
					dataJSONObject.optString("producer_build_url"));

				String producerJenkinsMasterName = dataJSONObject.optString(
					"producer_jenkins_master");

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						producerJenkinsMasterName)) {

					setProducerJenkinsMaster(
						JenkinsMaster.getInstance(producerJenkinsMasterName));
				}
				else {
					setProducerJenkinsMaster(null);
				}

				setProducerQueueId(dataJSONObject.optLong("producer_queue_id"));
				setStatus(Status.valueOf(dataJSONObject.getString("status")));

				_redispatchAttempts = dataJSONObject.optInt(
					"redispatch_attempts", _redispatchAttempts);

				JSONArray redispatchHistoryJSONArray =
					dataJSONObject.optJSONArray("redispatch_history");

				if (redispatchHistoryJSONArray != null) {
					_redispatchHistoryJSONArray = redispatchHistoryJSONArray;
				}

				return;
			}
		}

		_invokeBuild();

		print(
			JenkinsResultsParserUtil.combine(
				"Redispatching bundles (", String.valueOf(_redispatchAttempts),
				" of ", String.valueOf(_MAX_REDISPATCH_ATTEMPTS), ") at ",
				_getProducerJobURL()));
	}

	private void _updateBuild(String producerBuildURL) {
		if (!JenkinsResultsParserUtil.isURL(producerBuildURL) ||
			(_topLevelBuild == null)) {

			return;
		}

		if (_build == null) {
			_build = BuildFactory.newBuild(producerBuildURL, _topLevelBuild);

			_topLevelBuild.addDownstreamBuild(_build);
		}
		else if (Objects.equals(producerBuildURL, _build.getBuildURL())) {
			return;
		}
		else {
			_build.reset();
		}

		_build.setBuildURL(producerBuildURL);

		_build.saveBuildURLInBuildDatabase();

		_topLevelBuild.update();

		BuildDatabase buildDatabase = getBuildDatabase();

		buildDatabase.uploadBuildDatabaseFileToCloudBucket();

		buildDatabase.rsyncBuildDatabaseFileToJenkinsMaster(
			JenkinsResultsParserUtil.combine(
				Environment.get("JENKINS_HOME"), "/userContent/jobs/",
				getStartProperty("TOP_LEVEL_JOB_NAME"), "/builds/",
				getStartProperty("TOP_LEVEL_BUILD_NUMBER")),
			JenkinsMaster.getInstance(
				getStartProperty("TOP_LEVEL_MASTER_HOSTNAME")));
	}

	private static final String _BASE_INVOCATION_URL =
		"http://test-1.liferay.com";

	private static final String _BUILD_PRIORITY = "2";

	private static final String _JOB_NAME = "app-server-bundle-builder";

	private static final String _JOB_VARIANT = "app-server-bundle-builder";

	private static final int _MAX_FAIL_COUNT = 2;

	private static final int _MAX_MISSING_COUNT = 2;

	private static final int _MAX_REDISPATCH_ATTEMPTS = 1;

	private static final int _MAX_TRANSIENT_REINVOCATION_COUNT = 2;

	private static final long _REDISPATCH_VERIFY_TIME = 1000 * 10;

	private static final Pattern _baseInvocationURLPattern = Pattern.compile(
		"https?://(?<cohortName>test-\\d+)(\\.liferay\\.com)?");

	private Build _build;
	private int _failCount;
	private int _missingCount;
	private int _redispatchAttempts;
	private JSONArray _redispatchHistoryJSONArray = new JSONArray();
	private final TopLevelBuild _topLevelBuild;
	private int _transientReinvocationCount;
	private Workspace _workspace;

}