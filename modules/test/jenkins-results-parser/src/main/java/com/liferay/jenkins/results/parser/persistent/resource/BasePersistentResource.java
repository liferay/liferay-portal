/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.CloudBucketUtil;
import com.liferay.jenkins.results.parser.JenkinsAPIUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public abstract class BasePersistentResource implements PersistentResource {

	@Override
	public void download(String artifactName, File destinationDir) {
		Artifact artifact = _artifacts.get(artifactName);

		if (artifact == null) {
			throw new RuntimeException(artifactName + " does not exist");
		}

		if (!artifact.isAvailable()) {
			throw new RuntimeException(artifactName + " is not available");
		}

		if (!destinationDir.exists()) {
			destinationDir.mkdirs();
		}

		try {
			CloudBucketUtil.downloadS3File(
				new File(destinationDir, artifactName),
				artifact.getS3ObjectPath());

			touch();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public List<Artifact> getArtifacts() {
		return new ArrayList<>(_artifacts.values());
	}

	@Override
	public String getControllerBuildURL() {
		return _controllerBuildURL;
	}

	@Override
	public String getProducerBuildURL() {
		return _producerBuildURL;
	}

	@Override
	public JenkinsMaster getProducerJenkinsMaster() {
		return _producerJenkinsMaster;
	}

	@Override
	public long getProducerQueueId() {
		return _producerQueueId;
	}

	@Override
	public Status getStatus() {
		return _status;
	}

	@Override
	public void touch() {
		if (_touched) {
			return;
		}

		synchronized (this) {
			if (_touched) {
				return;
			}

			_touched = true;
		}

		if (!isBuildCachingEnabled()) {
			return;
		}

		try {
			if (CloudBucketUtil.isS3ObjectPathAvailable(
					_getDataS3ObjectPath())) {

				CloudBucketUtil.touchS3File(_getDataS3ObjectPath());
			}

			for (Artifact artifact : getArtifacts()) {
				if (artifact.isAvailable()) {
					CloudBucketUtil.touchS3File(artifact.getS3ObjectPath());
				}
			}
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Failed to touch " + getType() + " S3 resource: " +
					ioException.getMessage());
		}
	}

	@Override
	public void upload(File baseDir) {
		for (Artifact artifact : getArtifacts()) {
			File artifactFile = new File(baseDir, artifact.getName());

			if (!artifactFile.exists()) {
				continue;
			}

			try {
				CloudBucketUtil.uploadS3File(
					artifact.getS3ObjectPath(), artifactFile);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	@Override
	public void waitForUpdate(long waitTime) {
		update();

		if (waitTime <= 0L) {
			waitTime = _MAX_WAIT_TIME;
		}

		long stopTime = System.currentTimeMillis() + waitTime;

		while (System.currentTimeMillis() < stopTime) {
			Status status = getStatus();

			String statusMessage = getStatusMessage();

			if (status == Status.FAILED) {
				print(statusMessage);

				throw new RuntimeException(statusMessage);
			}
			else if (status == Status.SUCCESS) {
				print(statusMessage);

				touch();

				return;
			}

			print(statusMessage);

			JenkinsResultsParserUtil.sleep(30000);

			update();
		}

		throw new RuntimeException("Timeout waiting for update");
	}

	protected BasePersistentResource(BuildDatabase buildDatabase) {
		_buildDatabase = buildDatabase;

		for (String artifactName : getArtifactNames()) {
			_artifacts.put(artifactName, new Artifact(artifactName, this));
		}
	}

	protected abstract Set<String> getArtifactNames();

	protected BuildDatabase getBuildDatabase() {
		return _buildDatabase;
	}

	protected String getCurrentTopLevelBuildURL() {
		return getStartProperty("TOP_LEVEL_BUILD_URL");
	}

	protected JSONObject getDataJSONObject() {
		if (!isBuildCachingEnabled()) {
			return _dataJSONObject;
		}

		String dataS3ObjectPath = _getDataS3ObjectPath();

		if (!CloudBucketUtil.isS3ObjectPathAvailable(dataS3ObjectPath)) {
			return null;
		}

		try {
			return new JSONObject(
				CloudBucketUtil.readS3Object(dataS3ObjectPath));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	protected synchronized Properties getStartProperties() {
		if (_startProperties != null) {
			return _startProperties;
		}

		_startProperties = new Properties();

		if (_buildDatabase.hasProperties("start.properties")) {
			_startProperties.putAll(
				_buildDatabase.getProperties("start.properties"));
		}

		String jobVariant = System.getenv("JOB_VARIANT");

		if (_buildDatabase.hasProperties(jobVariant + "/start.properties")) {
			_startProperties.putAll(
				_buildDatabase.getProperties(jobVariant + "/start.properties"));
		}

		return _startProperties;
	}

	protected String getStartProperty(String propertyName) {
		return JenkinsResultsParserUtil.getProperty(
			getStartProperties(), propertyName);
	}

	protected abstract String getStatusMessage();

	protected boolean isArtifactsAvailable() {
		for (Artifact artifact : getArtifacts()) {
			if (!artifact.isAvailable()) {
				return false;
			}
		}

		return true;
	}

	protected boolean isBuildCachingEnabled() {
		if (_buildCachingEnabled != null) {
			return _buildCachingEnabled;
		}

		String currentTopLevelBuildURL = getCurrentTopLevelBuildURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(currentTopLevelBuildURL)) {
			_buildCachingEnabled = false;

			return _buildCachingEnabled;
		}

		Matcher matcher = _buildURLPattern.matcher(currentTopLevelBuildURL);

		if (!matcher.find()) {
			_buildCachingEnabled = false;

			return _buildCachingEnabled;
		}

		Map<String, String> buildParameters =
			JenkinsResultsParserUtil.getBuildParameters(
				currentTopLevelBuildURL);

		_buildCachingEnabled = JenkinsResultsParserUtil.isBuildCachingEnabled(
			matcher.group("jobName"), buildParameters.get("CI_TEST_SUITE"));

		return _buildCachingEnabled;
	}

	protected boolean isController() {
		return Objects.equals(
			getCurrentTopLevelBuildURL(), getControllerBuildURL());
	}

	protected boolean isMissing() {
		if (isController() || isArtifactsAvailable()) {
			return false;
		}

		String controllerBuildURL = getControllerBuildURL();

		if (!JenkinsResultsParserUtil.isURL(controllerBuildURL)) {
			return false;
		}

		JSONObject apiJSONObject = JenkinsAPIUtil.getAPIJSONObject(
			controllerBuildURL, "result");

		return !JenkinsResultsParserUtil.isNullOrEmpty(
			apiJSONObject.optString("result"));
	}

	protected void print(String message) {
		System.out.println("[" + getType() + "] " + message);
	}

	protected void save() {
		JSONObject dataJSONObject = new JSONObject();

		JSONArray artifactsJSONArray = new JSONArray();

		for (Artifact artifact : getArtifacts()) {
			artifactsJSONArray.put(artifact.getJSONObject());
		}

		dataJSONObject.put(
			"artifacts", artifactsJSONArray
		).put(
			"controller_build_url", getControllerBuildURL()
		).put(
			"producer_build_url", getProducerBuildURL()
		);

		JenkinsMaster producerJenkinsMaster = getProducerJenkinsMaster();

		if (producerJenkinsMaster != null) {
			dataJSONObject.put(
				"producer_jenkins_master", producerJenkinsMaster.getName());
		}

		dataJSONObject.put(
			"producer_queue_id", getProducerQueueId()
		).put(
			"status", String.valueOf(getStatus())
		);

		if (!isBuildCachingEnabled()) {
			_dataJSONObject = dataJSONObject;

			return;
		}

		try {
			CloudBucketUtil.uploadS3Object(
				String.valueOf(dataJSONObject), _getDataS3ObjectPath());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected void setControllerBuildURL(String controllerBuildURL) {
		_controllerBuildURL = controllerBuildURL;
	}

	protected void setProducerBuildURL(String producerBuildURL) {
		_producerBuildURL = producerBuildURL;
	}

	protected void setProducerJenkinsMaster(
		JenkinsMaster producerJenkinsMaster) {

		_producerJenkinsMaster = producerJenkinsMaster;
	}

	protected void setProducerQueueId(long producerQueueId) {
		_producerQueueId = producerQueueId;
	}

	protected void setStatus(Status status) {
		_status = status;
	}

	protected abstract void update();

	private String _getDataS3ObjectPath() {
		return JenkinsResultsParserUtil.combine(
			getBaseS3ObjectPath(), "/data.json.gz");
	}

	private static final long _MAX_WAIT_TIME = 1000 * 60 * 120;

	private static final Pattern _buildURLPattern = Pattern.compile(
		"https?://.+/job/(?<jobName>[^/]+)/(?<buildNumber>\\d+)");

	private final Map<String, Artifact> _artifacts = new HashMap<>();
	private Boolean _buildCachingEnabled;
	private final BuildDatabase _buildDatabase;
	private String _controllerBuildURL;
	private JSONObject _dataJSONObject;
	private String _producerBuildURL;
	private JenkinsMaster _producerJenkinsMaster;
	private long _producerQueueId;
	private Properties _startProperties;
	private Status _status;
	private volatile boolean _touched;

}