/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.aws.AWSFactory;
import com.liferay.jenkins.results.parser.aws.AWSFleetCloud;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public class JenkinsMaster implements JenkinsNode<JenkinsMaster> {

	public static final Integer SLAVE_RAM_DEFAULT = 12;

	public static final Integer SLAVES_PER_HOST_DEFAULT = 2;

	public static synchronized JenkinsMaster getInstance(String masterName) {
		return _jenkinsMasters.computeIfAbsent(masterName, JenkinsMaster::new);
	}

	public static Integer getSlaveRAMMinimumDefault() {
		try {
			String propertyValue = JenkinsResultsParserUtil.getBuildProperty(
				"slave.ram.minimum.default");

			if (propertyValue == null) {
				return SLAVE_RAM_DEFAULT;
			}

			return Integer.valueOf(propertyValue);
		}
		catch (Exception exception) {
			StringBuilder sb = new StringBuilder();

			sb.append("Unable to get property '");
			sb.append("slave.ram.minimum.default");
			sb.append("', defaulting to '");
			sb.append(SLAVE_RAM_DEFAULT);
			sb.append("'");

			System.out.println(sb.toString());

			exception.printStackTrace();

			return SLAVE_RAM_DEFAULT;
		}
	}

	public static Integer getSlavesPerHostDefault() {
		try {
			String propertyValue = JenkinsResultsParserUtil.getBuildProperty(
				"slaves.per.host.default");

			if (propertyValue == null) {
				return SLAVES_PER_HOST_DEFAULT;
			}

			return Integer.valueOf(propertyValue);
		}
		catch (Exception exception) {
			StringBuilder sb = new StringBuilder();

			sb.append("Unable to get property '");
			sb.append("slaves.per.host.default");
			sb.append("', defaulting to '");
			sb.append(SLAVES_PER_HOST_DEFAULT);
			sb.append("'");

			System.out.println(sb.toString());

			exception.printStackTrace();

			return SLAVES_PER_HOST_DEFAULT;
		}
	}

	public synchronized void addRecentBatch(
		int batchSize, String labelExpression) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(labelExpression)) {
			labelExpression = null;
		}
		else {
			for (AWSFleetCloud awsFleetCloud : getAWSFleetClouds()) {
				if (_matchesLabels(
						labelExpression, awsFleetCloud.getLabels())) {

					labelExpression = awsFleetCloud.getPrimaryLabel();

					break;
				}
			}
		}

		Map<Long, Integer> batchSizes = _labelBatchSizes.get(labelExpression);

		if (batchSizes == null) {
			batchSizes = new TreeMap<>();

			_labelBatchSizes.put(labelExpression, batchSizes);
		}

		batchSizes.put(
			JenkinsResultsParserUtil.getCurrentTimeMillis() + maxRecentBatchAge,
			batchSize);

		_labelBatchSizes.put(labelExpression, batchSizes);
	}

	@Override
	public int compareTo(JenkinsMaster jenkinsMaster) {
		Integer value = null;

		Integer availableSlavesCount = getAvailableSlavesCount(null);
		Integer otherAvailableSlavesCount =
			jenkinsMaster.getAvailableSlavesCount(null);

		if ((availableSlavesCount > 0) || (otherAvailableSlavesCount > 0)) {
			value = availableSlavesCount.compareTo(otherAvailableSlavesCount);
		}

		if ((value == null) || (value == 0)) {
			Float averageQueueLength = getAverageQueueLength(null);
			Float otherAverageQueueLength = jenkinsMaster.getAverageQueueLength(
				null);

			value = -1 * averageQueueLength.compareTo(otherAverageQueueLength);
		}

		if (value != 0) {
			return -value;
		}

		Random random = new Random();

		while (true) {
			int result = random.nextInt(3) - 1;

			if (result != 0) {
				return result;
			}
		}
	}

	public void copyFileFromJenkinsMaster(
		String sourceFilePath, File targetFile) {

		String targetFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			targetFile);

		if (!_isRunningOnJenkinsMaster()) {
			sourceFilePath = JenkinsResultsParserUtil.combine(
				_SSH_USER_NAME, "@", getName(), ":", sourceFilePath);
		}

		_executeSCPCommand(sourceFilePath, targetFilePath);
	}

	public void copyFileToJenkinsMaster(
		File sourceFile, String targetFilePath) {

		String sourceFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			sourceFile);

		if (!_isRunningOnJenkinsMaster()) {
			targetFilePath = JenkinsResultsParserUtil.combine(
				_SSH_USER_NAME, "@", getName(), ":", targetFilePath);
		}

		_executeSCPCommand(sourceFilePath, targetFilePath);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof JenkinsMaster)) {
			return false;
		}

		JenkinsMaster jenkinsMaster = (JenkinsMaster)object;

		return Objects.equals(jenkinsMaster.getName(), getName());
	}

	public String executeBashCommand(String command) {
		String sshCommand = JenkinsResultsParserUtil.combine(
			"ssh ", _SSH_OPTIONS, " ", _SSH_USER_NAME, "@", getName(), " \"",
			command, "\"");

		Process process = null;

		try {
			if (_isRunningOnJenkinsMaster()) {
				process = JenkinsResultsParserUtil.executeBashCommands(
					new File("."), true, false, _SSH_COMMAND_TIMEOUT, command);
			}
			else {
				process = JenkinsResultsParserUtil.executeBashCommands(
					new File("."), true, false, _SSH_COMMAND_TIMEOUT,
					sshCommand);
			}
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(
				"Unable to execute command " + sshCommand, exception);
		}

		if (process.exitValue() != 0) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to execute command ", command, " on ", getName()));
		}

		try {
			String output = JenkinsResultsParserUtil.readInputStream(
				process.getInputStream());

			return output.replace("Finished executing Bash commands.", "");
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read output of command " + sshCommand, ioException);
		}
	}

	public List<JenkinsUser.APIToken> getAPITokens(String jenkinsUserName) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsUserName)) {
			return null;
		}

		JenkinsUser jenkinsUser = JenkinsUserFactory.getJenkinsUser(
			this, jenkinsUserName);

		return jenkinsUser.getAPITokens();
	}

	@Override
	public List<String> getAssignedLabels() {
		return _assignedLabels;
	}

	public int getAvailableSlavesCount(String labelExpression) {
		int idleNodesCount = _getIdleNodesCount(labelExpression);
		int queueItemsCount = _getQueueItemsCount(labelExpression);
		int recentBatchSizesTotal = _getRecentBatchSizesTotal(labelExpression);

		return idleNodesCount - queueItemsCount - recentBatchSizesTotal;
	}

	public float getAverageQueueLength(String labelExpression) {
		int busyNodesCount = _getBusyNodesCount(labelExpression);
		int queueItemsCount = _getQueueItemsCount(labelExpression);
		int recentBatchSizesTotal = _getRecentBatchSizesTotal(labelExpression);
		int usableNodesCount = _getUsableNodesCount(labelExpression);

		float queueLength =
			(float)busyNodesCount + queueItemsCount + recentBatchSizesTotal;

		return queueLength / usableNodesCount;
	}

	public List<AWSFleetCloud> getAWSFleetClouds() {
		long currentTimestamp = JenkinsResultsParserUtil.getCurrentTimeMillis();

		long timeSinceLastUpdate =
			currentTimestamp - _awsFleetCloudLastUpdateTimestamp;

		if ((_awsFleetClouds != null) &&
			(timeSinceLastUpdate <= _AWS_FLEET_CLOUD_UPDATE_DURATION)) {

			return _awsFleetClouds;
		}

		_awsFleetClouds = AWSFactory.getAWSFleetClouds(this);

		_awsFleetCloudLastUpdateTimestamp = currentTimestamp;

		return _awsFleetClouds;
	}

	public List<JSONObject> getBuildJSONObjects(String jobName) {
		synchronized (_buildJSONObjectsMap) {
			List<JSONObject> buildsJSONObjects = _buildJSONObjectsMap.get(
				jobName);
			Long buildsUpdateTime = _buildsUpdateTimes.get(jobName);

			if ((buildsJSONObjects != null) && (buildsUpdateTime != null)) {
				long currentTime =
					JenkinsResultsParserUtil.getCurrentTimeMillis();

				long buildUpdateDuration = currentTime - buildsUpdateTime;

				if (buildUpdateDuration <= _MAXIMUM_BUILD_UPDATE_DURATION) {
					return buildsJSONObjects;
				}
			}

			buildsJSONObjects = new ArrayList<>();

			int page = 0;

			while (true) {
				JSONArray buildsJSONArray = _getBuildsJSONArray(jobName, page);

				if (buildsJSONArray.length() == 0) {
					break;
				}

				boolean findNextBuild = true;

				for (int i = 0; i < buildsJSONArray.length(); i++) {
					JSONObject buildsJSONObject = buildsJSONArray.getJSONObject(
						i);

					buildsJSONObjects.add(buildsJSONObject);

					long buildAge =
						JenkinsResultsParserUtil.getCurrentTimeMillis() -
							buildsJSONObject.getLong("timestamp");

					if (buildAge >= _MAXIMUM_BUILD_AGE) {
						findNextBuild = false;

						break;
					}
				}

				if (!findNextBuild) {
					break;
				}

				page++;
			}

			_buildJSONObjectsMap.put(jobName, buildsJSONObjects);
			_buildsUpdateTimes.put(
				jobName, JenkinsResultsParserUtil.getCurrentTimeMillis());

			return buildsJSONObjects;
		}
	}

	public List<String> getBuildURLs() {
		return new ArrayList<>(_buildURLs);
	}

	public List<DefaultBuild> getDefaultBuilds() {
		List<String> buildURLs = getBuildURLs();

		List<DefaultBuild> oldDefaultBuilds = new ArrayList<>();

		for (DefaultBuild defaultBuild : _defaultBuilds) {
			if (!buildURLs.remove(defaultBuild.getBuildURL())) {
				oldDefaultBuilds.add(defaultBuild);
			}
		}

		_defaultBuilds.removeAll(oldDefaultBuilds);

		for (String buildURL : buildURLs) {
			_defaultBuilds.add(BuildFactory.newDefaultBuild(buildURL));
		}

		return _defaultBuilds;
	}

	public Map<String, String> getGlobalEnvironmentVariables() {
		if (_globalEnvironmentVariables != null) {
			return _globalEnvironmentVariables;
		}

		if (!isAvailable()) {
			return new HashMap<>();
		}

		StringBuilder sb = new StringBuilder();

		sb.append("import jenkins.model.Jenkins;\n");

		sb.append("def globalNodeProperties = ");
		sb.append("Jenkins.instance.getGlobalNodeProperties();\n");

		sb.append("def envVars = globalNodeProperties[0].getEnvVars();\n");

		sb.append("def sb = new StringBuilder();\n");

		sb.append("sb.append(\"{\");\n");

		sb.append("if (!envVars.isEmpty()) {\n");

		sb.append("for (def envVar : envVars.entrySet()) {\n");
		sb.append("sb.append('\"');");
		sb.append("sb.append(envVar.key);");
		sb.append("sb.append('\":\"');");
		sb.append("sb.append(envVar.value.replaceAll('\"', '\\\\\\\\\"'));");
		sb.append("sb.append('\",');");
		sb.append("}\n");

		sb.append("sb.setLength(sb.length() - 1);");
		sb.append("}\n");

		sb.append("sb.append('}');");

		sb.append("println sb;");

		_globalEnvironmentVariables = new HashMap<>();

		try {
			String results = JenkinsResultsParserUtil.executeJenkinsScript(
				getName(), sb.toString());

			Matcher globalEnvironmentVariablesMatcher =
				_globalEnvironmentVariablesPattern.matcher(results);

			if (!globalEnvironmentVariablesMatcher.find()) {
				return _globalEnvironmentVariables;
			}

			JSONObject jsonObject = new JSONObject(
				globalEnvironmentVariablesMatcher.group("json"));

			for (String key : jsonObject.keySet()) {
				_globalEnvironmentVariables.put(key, jsonObject.getString(key));
			}

			return _globalEnvironmentVariables;
		}
		catch (Exception exception) {
			return _globalEnvironmentVariables;
		}
	}

	public int getIdleJenkinsSlavesCount() {
		int idleSlavesCount = 0;

		if (JenkinsResultsParserUtil.isCloudCINode()) {
			for (AWSFleetCloud awsFleetCloud : getAWSFleetClouds()) {
				idleSlavesCount += awsFleetCloud.getMaxSize();
			}

			for (JenkinsSlave jenkinsSlave : _jenkinsSlavesMap.values()) {
				if (jenkinsSlave.isOffline()) {
					continue;
				}

				if (!jenkinsSlave.isIdle()) {
					idleSlavesCount--;
				}
			}

			return idleSlavesCount;
		}

		for (JenkinsSlave jenkinsSlave : _jenkinsSlavesMap.values()) {
			if (jenkinsSlave.isOffline()) {
				continue;
			}

			if (jenkinsSlave.isIdle()) {
				idleSlavesCount++;
			}
		}

		return idleSlavesCount;
	}

	public JSONObject getInProgressBuildJSONObject(
		String jobName, Map<String, String> buildParameters) {

		try {
			JSONObject jobJSONObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.combine(
					getURL(), "/job/", jobName, "/api/json?",
					"tree=builds[actions[parameters[name,value]],queueId,",
					"result,url]"),
				false, 5000);

			JSONArray buildsJSONArray = jobJSONObject.optJSONArray("builds");

			for (int i = 0; i < buildsJSONArray.length(); i++) {
				JSONObject buildJSONObject = buildsJSONArray.optJSONObject(i);

				if ((buildJSONObject == JSONObject.NULL) ||
					!JenkinsResultsParserUtil.isNullOrEmpty(
						buildJSONObject.optString("result"))) {

					continue;
				}

				Map<String, String> parameters = _getParameters(
					buildJSONObject);

				boolean matchingBuildParameters = true;

				for (Map.Entry<String, String> buildParameter :
						buildParameters.entrySet()) {

					String parameterValue = parameters.get(
						buildParameter.getKey());

					if (!Objects.equals(
							buildParameter.getValue(), parameterValue)) {

						matchingBuildParameters = false;

						break;
					}
				}

				if (matchingBuildParameters) {
					return buildJSONObject;
				}
			}
		}
		catch (Exception exception) {
			return null;
		}

		return null;
	}

	@Override
	public JenkinsCohort getJenkinsCohort() {
		if (_jenkinsCohort != null) {
			return _jenkinsCohort;
		}

		Matcher matcher = _masterNamePattern.matcher(getName());

		if (!matcher.find()) {
			return null;
		}

		String cohortName = matcher.group("cohortName");

		_jenkinsCohort = JenkinsCohort.getInstance(cohortName);

		return _jenkinsCohort;
	}

	@Override
	public JenkinsMaster getJenkinsMaster() {
		return this;
	}

	public JenkinsSlave getJenkinsSlave(String jenkinsSlaveName) {
		if (_jenkinsSlavesMap.isEmpty() ||
			JenkinsResultsParserUtil.isCloudCINode()) {

			update();
		}

		return _jenkinsSlavesMap.get(jenkinsSlaveName);
	}

	public List<String> getJenkinsSlaveNames() {
		List<JenkinsSlave> jenkinsSlaves = getJenkinsSlaves();

		List<String> jenkinsSlaveNames = new ArrayList<>(jenkinsSlaves.size());

		for (JenkinsSlave jenkinsSlave : jenkinsSlaves) {
			jenkinsSlaveNames.add(jenkinsSlave.getName());
		}

		return jenkinsSlaveNames;
	}

	public List<JenkinsSlave> getJenkinsSlaves() {
		if (_jenkinsSlavesMap.isEmpty() ||
			JenkinsResultsParserUtil.isCloudCINode()) {

			update();
		}

		return new ArrayList<>(_jenkinsSlavesMap.values());
	}

	public int getMaxRunningBuildsCount() {
		return Math.max(_busyExecutorsCount, _runningBuilds.size());
	}

	@Override
	public String getName() {
		return _masterName;
	}

	public String getNetworkName() {
		Map<String, String> globalEnvironmentVariables =
			getGlobalEnvironmentVariables();

		String networkName = globalEnvironmentVariables.get(
			"MASTER_NETWORK_NAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(networkName)) {
			return null;
		}

		return networkName;
	}

	public int getOfflineJenkinsSlavesCount() {
		int offlineJenkinsSlavesCount = 0;

		for (JenkinsSlave jenkinsSlave : _jenkinsSlavesMap.values()) {
			if (jenkinsSlave.isOffline()) {
				offlineJenkinsSlavesCount++;
			}
		}

		return offlineJenkinsSlavesCount;
	}

	public List<JenkinsSlave> getOnlineJenkinsSlaves() {
		List<JenkinsSlave> onlineJenkinsSlaves = new ArrayList<>();

		for (JenkinsSlave jenkinsSlave : _jenkinsSlavesMap.values()) {
			if (!jenkinsSlave.isOffline()) {
				onlineJenkinsSlaves.add(jenkinsSlave);
			}
		}

		return onlineJenkinsSlaves;
	}

	public int getOnlineJenkinsSlavesCount() {
		int onlineJenkinsSlavesCount = 0;

		for (JenkinsSlave jenkinsSlave : _jenkinsSlavesMap.values()) {
			if (!jenkinsSlave.isOffline()) {
				onlineJenkinsSlavesCount++;
			}
		}

		return onlineJenkinsSlavesCount;
	}

	public JSONObject getQueuedBuildJSONObject(
		String jobName, Map<String, String> buildParameters) {

		try {
			JSONObject queueJSONObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.combine(
					getURL(), "/queue/api/json?",
					"tree=items[actions[parameters[name,value]],id,task[url]]"),
				false, 5000);

			JSONArray itemsJSONArray = queueJSONObject.optJSONArray("items");

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				JSONObject itemJSONObject = itemsJSONArray.optJSONObject(i);

				if (itemJSONObject == JSONObject.NULL) {
					continue;
				}

				JSONObject taskJSONObject = itemJSONObject.optJSONObject(
					"task");

				String taskURL = taskJSONObject.optString("url", "");

				if (!taskURL.contains("/" + jobName + "/")) {
					continue;
				}

				boolean matchingBuildParameters = true;

				Map<String, String> parameters = _getParameters(itemJSONObject);

				for (Map.Entry<String, String> buildParameter :
						buildParameters.entrySet()) {

					String parameterValue = parameters.get(
						buildParameter.getKey());

					if (!Objects.equals(
							buildParameter.getValue(), parameterValue)) {

						matchingBuildParameters = false;

						break;
					}
				}

				if (matchingBuildParameters) {
					return itemJSONObject;
				}
			}
		}
		catch (Exception exception) {
			return null;
		}

		return null;
	}

	public Map<String, JSONObject> getQueuedBuildURLs() {
		Map<String, JSONObject> queuedBuildURLs = new HashMap<>();

		List<QueueItem> queueItems = getQueueItems();

		if (queueItems.isEmpty()) {
			return queuedBuildURLs;
		}

		for (QueueItem queueItem : queueItems) {
			if (!queueItem.isValidQueueItem()) {
				continue;
			}

			String queueItemURL = queueItem.getURL();

			if (queueItemURL == null) {
				continue;
			}

			queuedBuildURLs.put(queueItemURL, queueItem.getJSONObject());
		}

		return queuedBuildURLs;
	}

	public QueueItem getQueueItem(long queueId) {
		String queueItemAPIURL = JenkinsResultsParserUtil.combine(
			getURL(), "/queue/item/", String.valueOf(queueId),
			"/api/json?tree=actions[parameters[name,value]],",
			"id,inQueueSince,task[name,url],url,why");

		try {
			String response = JenkinsResultsParserUtil.toString(
				queueItemAPIURL, false, 0, 0, 5000);

			if (JenkinsResultsParserUtil.isNullOrEmpty(response)) {
				return null;
			}

			JSONObject queueItemJSONObject =
				JenkinsResultsParserUtil.createJSONObject(response);

			if (!queueItemJSONObject.has("id")) {
				return null;
			}

			return new QueueItem(this, queueItemJSONObject);
		}
		catch (IOException ioException) {
			return null;
		}
	}

	public List<JSONObject> getQueueItemJSONObjects() {
		List<JSONObject> queueItemJSONObjects = new ArrayList<>();

		List<QueueItem> queueItems = getQueueItems();

		if (queueItems.isEmpty()) {
			return queueItemJSONObjects;
		}

		for (QueueItem queueItem : queueItems) {
			queueItemJSONObjects.add(queueItem.getJSONObject());
		}

		return queueItemJSONObjects;
	}

	public synchronized List<QueueItem> getQueueItems() {
		if (_queueUpdateTime != null) {
			long currentTime = JenkinsResultsParserUtil.getCurrentTimeMillis();

			long queueUpdateDuration = currentTime - _queueUpdateTime;

			if (queueUpdateDuration <= _MAXIMUM_QUEUE_UPDATE_DURATION) {
				return _queueItems;
			}
		}

		_queueItems.clear();

		try {
			JSONObject queueAPIJSONObject =
				JenkinsResultsParserUtil.toJSONObject(
					JenkinsResultsParserUtil.combine(
						getURL(), "/queue/api/json?tree=items[actions[",
						"parameters[name,value]],id,inQueueSince,",
						"task[name,url],url,why]"),
					false, 5000);

			if (!queueAPIJSONObject.has("items")) {
				_queueUpdateTime =
					JenkinsResultsParserUtil.getCurrentTimeMillis();

				return _queueItems;
			}

			JSONArray itemsJSONArray = queueAPIJSONObject.getJSONArray("items");

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				_queueItems.add(
					new QueueItem(this, itemsJSONArray.getJSONObject(i)));
			}

			_queueUpdateTime = JenkinsResultsParserUtil.getCurrentTimeMillis();

			return _queueItems;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public JenkinsSlave getRandomJenkinsSlave() {
		List<JenkinsSlave> jenkinsSlaves = new ArrayList<>(getJenkinsSlaves());

		while (!jenkinsSlaves.isEmpty()) {
			JenkinsSlave jenkinsSlave =
				JenkinsResultsParserUtil.getRandomListItem(jenkinsSlaves);

			if (!jenkinsSlave.isOffline() && jenkinsSlave.isReachable()) {
				return jenkinsSlave;
			}

			jenkinsSlaves.remove(jenkinsSlave);
		}

		return null;
	}

	public String getRemoteURL() {
		return _masterRemoteURL;
	}

	public List<RunningBuild> getRunningBuilds() {
		return new ArrayList<>(_runningBuilds);
	}

	public Integer getSlaveRAM() {
		return _slaveRAM;
	}

	public Integer getSlavesPerHost() {
		return _slavesPerHost;
	}

	public int getStartedBuildsCountAfter(Date date, boolean topLevelBuilds) {
		if (_buildsCountJSONObject == null) {
			try {
				_buildsCountJSONObject = JenkinsResultsParserUtil.toJSONObject(
					getURL() + "api/json?tree=jobs[name,allBuilds[timestamp]]");
			}
			catch (IOException ioException) {
				return 0;
			}
		}

		JSONArray jobsJSONArray = _buildsCountJSONObject.optJSONArray("jobs");

		if (jobsJSONArray == null) {
			return 0;
		}

		int buildsCount = 0;

		for (int i = 0; i < jobsJSONArray.length(); i++) {
			JSONObject jobJSONObject = jobsJSONArray.optJSONObject(i);

			if (jobJSONObject == null) {
				continue;
			}

			String jobName = jobJSONObject.getString("name");

			if (topLevelBuilds) {
				if (!_isTopLevelJobName(jobName)) {
					continue;
				}
			}
			else {
				if (_isTopLevelJobName(jobName)) {
					continue;
				}
			}

			JSONArray buildsJSONArray = jobJSONObject.optJSONArray("allBuilds");

			if (buildsJSONArray == null) {
				continue;
			}

			for (int j = 0; j < buildsJSONArray.length(); j++) {
				JSONObject buildJSONObject = buildsJSONArray.optJSONObject(j);

				if (buildJSONObject == null) {
					continue;
				}

				Date buildDate = new Date(buildJSONObject.getLong("timestamp"));

				if (buildDate.after(date)) {
					buildsCount++;
				}
			}
		}

		return buildsCount;
	}

	public String getURL() {
		return _masterURL;
	}

	@Override
	public int hashCode() {
		String name = getName();

		return name.hashCode();
	}

	public synchronized boolean isAvailable() {
		if (isBlacklisted()) {
			return false;
		}

		return _isReachable();
	}

	public boolean isBlacklisted() {
		if (_jenkinsMastersBlacklist.contains(getName())) {
			_blacklisted = true;
		}

		return _blacklisted;
	}

	public boolean isBuildInProgress(
		String jobName, Map<String, String> buildParameters) {

		if (getInProgressBuildJSONObject(jobName, buildParameters) != null) {
			return true;
		}

		return false;
	}

	public boolean isBuildQueued(
		String jobName, Map<String, String> buildParameters) {

		if (getQueuedBuildJSONObject(jobName, buildParameters) != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isIdle() {
		return _idle;
	}

	@Override
	public boolean isOffline() {
		return _offline;
	}

	public boolean matchesLabelExpression(String labelExpression) {
		if (_matchesLabels(labelExpression, getAssignedLabels())) {
			return true;
		}

		for (JenkinsSlave jenkinsSlave : getJenkinsSlaves()) {
			if (jenkinsSlave.isEC2FleetNodeComputer() ||
				jenkinsSlave.isOffline()) {

				continue;
			}

			if (_matchesLabels(
					labelExpression, jenkinsSlave.getAssignedLabels())) {

				return true;
			}
		}

		for (AWSFleetCloud awsFleetCloud : getAWSFleetClouds()) {
			if (_matchesLabels(labelExpression, awsFleetCloud.getLabels())) {
				return true;
			}
		}

		return false;
	}

	public void reloadJenkinsUser(String jenkinsUserName) {
		JenkinsUser jenkinsUser = JenkinsUserFactory.getJenkinsUser(
			this, jenkinsUserName);

		String jenkinsUserID = jenkinsUser.getJenkinsUserID();

		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsUserID)) {
			throw new RuntimeException(
				"Unable to find Jenkins user ID for " + jenkinsUserName);
		}

		JenkinsResultsParserUtil.executeJenkinsScript(
			getName(),
			JenkinsResultsParserUtil.combine(
				"hudson.model.User user = hudson.model.User.getById('",
				jenkinsUserID, "', false)\n", "if (user == null) {\n",
				"throw new RuntimeException('Unable to find user ",
				jenkinsUserID, "')\n", "}\n", "user.load()"));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Successfully reloaded ", jenkinsUserName, " for ", getURL()));
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.combine(
			"{availableSlavesCount=",
			String.valueOf(getAvailableSlavesCount(null)),
			", averageQueueLength=",
			String.valueOf(getAverageQueueLength(null)), ", masterURL=",
			_masterURL, ", recentBatchSizesTotal=",
			String.valueOf(_getRecentBatchSizesTotal(null)), "}");
	}

	public synchronized void update() {
		update(true);
	}

	public synchronized void update(boolean minimal) {
		if (_isUpdated(minimal)) {
			return;
		}

		_labelExpressionLabels.clear();

		if (!_isReachable()) {
			_assignedLabels.clear();
			_buildURLs.clear();
			_busyExecutorsCount = 0;
			_jenkinsSlavesMap.clear();
			_runningBuilds.clear();

			return;
		}

		_assignedLabels.clear();

		JSONObject computerAPIJSONObject = null;

		try {
			String treeQuery = JenkinsResultsParserUtil.combine(
				"/computer/api/json?tree=computer[assignedLabels[name],",
				"displayName,executors[currentExecutable[url]],idle,offline,",
				"offlineCauseReason]");

			if (!minimal) {
				treeQuery = JenkinsResultsParserUtil.combine(
					"/computer/api/json?tree=busyExecutors,computer",
					"[assignedLabels[name],displayName,executors",
					"[currentExecutable[building,estimatedDuration,",
					"fullDisplayName,timestamp,url],likelyStuck],idle,",
					"offline,offlineCause[timestamp],offlineCauseReason,",
					"oneOffExecutors[currentExecutable[building,",
					"estimatedDuration,fullDisplayName,timestamp,url],",
					"likelyStuck],temporarilyOffline]");
			}

			computerAPIJSONObject = JenkinsResultsParserUtil.toJSONObject(
				getURL() + treeQuery, false, 5000);
		}
		catch (Exception exception) {
			_assignedLabels.clear();
			_buildURLs.clear();
			_busyExecutorsCount = 0;
			_jenkinsSlavesMap.clear();
			_labelExpressionLabels.clear();
			_runningBuilds.clear();

			System.out.println("Unable to read " + _masterURL);

			return;
		}

		List<String> buildURLs = new ArrayList<>();
		List<RunningBuild> runningBuilds = new ArrayList<>();

		JSONArray computerJSONArray = computerAPIJSONObject.getJSONArray(
			"computer");

		for (int i = 0; i < computerJSONArray.length(); i++) {
			JSONObject computerJSONObject = computerJSONArray.getJSONObject(i);

			String jenkinsSlaveName = JenkinsSlave.getDisplayName(
				computerJSONObject);

			if (!minimal) {
				_addRunningBuilds(
					computerJSONObject, jenkinsSlaveName, runningBuilds);
			}

			if (jenkinsSlaveName.equals("Built-In Node") ||
				jenkinsSlaveName.equals("master")) {

				JSONArray assignedLabelsJSONArray =
					computerJSONObject.optJSONArray("assignedLabels");

				if (assignedLabelsJSONArray == null) {
					continue;
				}

				for (int j = 0; j < assignedLabelsJSONArray.length(); j++) {
					JSONObject assignedLabelJSONObject =
						assignedLabelsJSONArray.getJSONObject(j);

					String assignedLabelName =
						assignedLabelJSONObject.optString("name");

					if (JenkinsResultsParserUtil.isNullOrEmpty(
							assignedLabelName)) {

						continue;
					}

					_assignedLabels.add(assignedLabelName);
				}

				_idle = computerJSONObject.optBoolean("idle", true);
				_offline = computerJSONObject.optBoolean("offline", true);

				continue;
			}

			JenkinsSlave jenkinsSlave = _jenkinsSlavesMap.get(jenkinsSlaveName);

			if (jenkinsSlave != null) {
				jenkinsSlave.update(computerJSONObject);
			}
			else {
				jenkinsSlave = new JenkinsSlave(this, computerJSONObject);

				_jenkinsSlavesMap.put(jenkinsSlave.getName(), jenkinsSlave);
			}

			String computerClassName = computerJSONObject.getString("_class");

			if (computerClassName.contains("EC2FleetNodeComputer") ||
				computerClassName.contains("hudson.slaves.SlaveComputer")) {

				JSONArray executorsJSONArray = computerJSONObject.getJSONArray(
					"executors");

				for (int j = 0; j < executorsJSONArray.length(); j++) {
					JSONObject executorJSONObject =
						executorsJSONArray.getJSONObject(j);

					if (executorJSONObject.has("currentExecutable") &&
						(executorJSONObject.get("currentExecutable") !=
							JSONObject.NULL)) {

						JSONObject currentExecutableJSONObject =
							executorJSONObject.getJSONObject(
								"currentExecutable");

						if (currentExecutableJSONObject.has("url")) {
							buildURLs.add(
								currentExecutableJSONObject.getString("url"));
						}
					}
				}
			}
		}

		_buildURLs.clear();

		_buildURLs.addAll(buildURLs);

		if (minimal) {
			_busyExecutorsCount = 0;

			_runningBuilds.clear();

			return;
		}

		_busyExecutorsCount = computerAPIJSONObject.optInt("busyExecutors", 0);

		_runningBuilds.clear();

		_runningBuilds.addAll(runningBuilds);
	}

	public static class QueueItem implements Comparable<QueueItem> {

		@Override
		public int compareTo(QueueItem queueItem) {
			return Long.compare(getInQueueSince(), queueItem.getInQueueSince());
		}

		public AWSFleetCloud getAWSFleetCloud() {
			if (_awsFleetCloud != null) {
				return _awsFleetCloud;
			}

			for (AWSFleetCloud awsFleetCloud :
					_jenkinsMaster.getAWSFleetClouds()) {

				if (_jenkinsMaster._matchesLabels(
						getLabelExpression(), awsFleetCloud.getLabels())) {

					_awsFleetCloud = awsFleetCloud;

					return _awsFleetCloud;
				}
			}

			return null;
		}

		public long getId() {
			return _jsonObject.getLong("id");
		}

		public long getInQueueSince() {
			return _jsonObject.getLong("inQueueSince");
		}

		public JenkinsMaster getJenkinsMaster() {
			return _jenkinsMaster;
		}

		public JSONObject getJSONObject() {
			return _jsonObject;
		}

		public String getLabelExpression() {
			Map<String, String> parameters = getParameters();

			String label = parameters.get("SLAVE_LABEL");

			if (JenkinsResultsParserUtil.isNullOrEmpty(label)) {
				label = parameters.get("NODE_NAME");
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(label)) {
				String taskName = getTaskName();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(taskName) &&
					taskName.startsWith("label=")) {

					label = taskName.substring("label=".length());
				}
			}

			return label;
		}

		public Map<String, String> getParameters() {
			return _getParameters(_jsonObject);
		}

		public String getPrimaryLabel() {
			AWSFleetCloud awsFleetCloud = getAWSFleetCloud();

			if (awsFleetCloud == null) {
				return null;
			}

			return awsFleetCloud.getPrimaryLabel();
		}

		public String getTaskName() {
			JSONObject taskJSONObject = _jsonObject.optJSONObject("task");

			if (taskJSONObject == null) {
				return null;
			}

			return taskJSONObject.optString("name");
		}

		public String getTaskURL() {
			JSONObject taskJSONObject = _jsonObject.optJSONObject("task");

			if (taskJSONObject == null) {
				return null;
			}

			return taskJSONObject.optString("url");
		}

		public String getURL() {
			if (!_jsonObject.has("url")) {
				return null;
			}

			return _jenkinsMaster.getURL() + _jsonObject.getString("url");
		}

		public String getWhy() {
			return _jsonObject.optString("why");
		}

		public boolean isValidQueueItem() {
			String taskName = getTaskName();

			if (taskName.equals("verification-node")) {
				return false;
			}

			String why = getWhy();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(why)) {
				if (taskName.startsWith("label=")) {
					String offlineSlaveWhy = JenkinsResultsParserUtil.combine(
						"‘", taskName.substring("label=".length()),
						"’ is offline");

					if (why.contains(offlineSlaveWhy)) {
						return false;
					}
				}

				if (why.startsWith("There are no nodes") ||
					why.contains("already in progress")) {

					return false;
				}
			}

			return true;
		}

		protected QueueItem(
			JenkinsMaster jenkinsMaster, JSONObject jsonObject) {

			_jenkinsMaster = jenkinsMaster;
			_jsonObject = jsonObject;
		}

		private AWSFleetCloud _awsFleetCloud;
		private final JenkinsMaster _jenkinsMaster;
		private final JSONObject _jsonObject;

	}

	public static class RunningBuild {

		public long getDuration(long currentTimeMillis) {
			long startTime = _getStartTime();

			if (startTime <= 0) {
				return 0;
			}

			return currentTimeMillis - startTime;
		}

		public long getEstimatedDuration() {
			return _currentExecutableJSONObject.optLong(
				"estimatedDuration", -1);
		}

		public String getFullDisplayName() {
			return _currentExecutableJSONObject.optString("fullDisplayName");
		}

		public JenkinsMaster getJenkinsMaster() {
			return _jenkinsMaster;
		}

		public String getJenkinsSlaveName() {
			return _jenkinsSlaveName;
		}

		public long getJenkinsSlaveOfflineDuration(long currentTimeMillis) {
			long jenkinsSlaveOfflineTime = _getJenkinsSlaveOfflineTime();

			if (!_isJenkinsSlaveOffline() || (jenkinsSlaveOfflineTime <= 0)) {
				return 0;
			}

			return currentTimeMillis - jenkinsSlaveOfflineTime;
		}

		public String getURL() {
			return _currentExecutableJSONObject.optString("url");
		}

		public boolean isBuilding() {
			return _currentExecutableJSONObject.optBoolean("building", false);
		}

		public boolean isJenkinsSlaveBeingRemoved() {
			String offlineCauseReason = _getOfflineCauseReason();

			if (!_isJenkinsSlaveOffline() ||
				JenkinsResultsParserUtil.isNullOrEmpty(offlineCauseReason)) {

				return false;
			}

			return offlineCauseReason.contains("is being removed");
		}

		public boolean isJenkinsSlaveOfflineUnexpectedly() {
			if (_isJenkinsSlaveOffline() &&
				!_isJenkinsSlaveTemporarilyOffline()) {

				return true;
			}

			return false;
		}

		public boolean isLikelyStuck() {
			return _likelyStuck;
		}

		@Override
		public String toString() {
			return JenkinsResultsParserUtil.combine(
				_jenkinsMaster.getName(), " ", getFullDisplayName(), " (",
				getURL(), ")");
		}

		protected RunningBuild(
			JSONObject computerJSONObject,
			JSONObject currentExecutableJSONObject, JenkinsMaster jenkinsMaster,
			String jenkinsSlaveName, boolean likelyStuck) {

			_computerJSONObject = computerJSONObject;
			_currentExecutableJSONObject = currentExecutableJSONObject;
			_jenkinsMaster = jenkinsMaster;
			_jenkinsSlaveName = jenkinsSlaveName;
			_likelyStuck = likelyStuck;
		}

		private long _getJenkinsSlaveOfflineTime() {
			JSONObject offlineCauseJSONObject =
				_computerJSONObject.optJSONObject("offlineCause");

			if (offlineCauseJSONObject == null) {
				return -1;
			}

			return offlineCauseJSONObject.optLong("timestamp", -1);
		}

		private String _getOfflineCauseReason() {
			return _computerJSONObject.optString("offlineCauseReason");
		}

		private long _getStartTime() {
			return _currentExecutableJSONObject.optLong("timestamp", -1);
		}

		private boolean _isJenkinsSlaveOffline() {
			return _computerJSONObject.optBoolean("offline", false);
		}

		private boolean _isJenkinsSlaveTemporarilyOffline() {
			return _computerJSONObject.optBoolean("temporarilyOffline", true);
		}

		private final JSONObject _computerJSONObject;
		private final JSONObject _currentExecutableJSONObject;
		private final JenkinsMaster _jenkinsMaster;
		private final String _jenkinsSlaveName;
		private final boolean _likelyStuck;

	}

	protected static long maxRecentBatchAge = 120 * 1000;

	private static Map<String, String> _getParameters(JSONObject jsonObject) {
		Map<String, String> parameters = new HashMap<>();

		if (jsonObject == null) {
			return parameters;
		}

		JSONArray actionsJSONArray = jsonObject.optJSONArray("actions");

		if (actionsJSONArray == null) {
			return parameters;
		}

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			JSONObject actionJSONObject = actionsJSONArray.optJSONObject(i);

			if ((actionJSONObject == JSONObject.NULL) ||
				!Objects.equals(
					actionJSONObject.optString("_class"),
					"hudson.model.ParametersAction")) {

				continue;
			}

			JSONArray parametersJSONArray = actionJSONObject.optJSONArray(
				"parameters");

			if (parametersJSONArray == JSONObject.NULL) {
				continue;
			}

			for (int k = 0; k < parametersJSONArray.length(); k++) {
				JSONObject parameterJSONObject =
					parametersJSONArray.optJSONObject(k);

				if (parameterJSONObject == JSONObject.NULL) {
					continue;
				}

				parameters.put(
					parameterJSONObject.getString("name"),
					parameterJSONObject.getString("value"));
			}

			break;
		}

		return parameters;
	}

	private JenkinsMaster(String masterName) {
		if (masterName.contains(".")) {
			_masterName = masterName.substring(0, masterName.indexOf("."));
		}
		else {
			_masterName = masterName;
		}

		try {
			Properties properties =
				JenkinsResultsParserUtil.getBuildProperties();

			_masterURL = properties.getProperty(
				JenkinsResultsParserUtil.combine(
					"jenkins.local.url[", _masterName, "]"));

			_masterRemoteURL = properties.getProperty(
				JenkinsResultsParserUtil.combine(
					"jenkins.remote.url[", _masterName, "]"));

			if (JenkinsResultsParserUtil.isNullOrEmpty(_masterRemoteURL) ||
				JenkinsResultsParserUtil.isNullOrEmpty(_masterURL)) {

				throw new IllegalArgumentException(masterName + " is unknown");
			}

			Integer slaveRAM = getSlaveRAMMinimumDefault();

			String slaveRAMString = JenkinsResultsParserUtil.getProperty(
				properties,
				JenkinsResultsParserUtil.combine(
					"master.property(", _masterName, "/slave.ram)"));

			if ((slaveRAMString != null) && slaveRAMString.matches("\\d+")) {
				slaveRAM = Integer.valueOf(slaveRAMString);
			}

			_slaveRAM = slaveRAM;

			Integer slavesPerHost = getSlavesPerHostDefault();

			String slavesPerHostString = JenkinsResultsParserUtil.getProperty(
				properties,
				JenkinsResultsParserUtil.combine(
					"master.property(", _masterName, "/slaves.per.host)"));

			if ((slavesPerHostString != null) &&
				slavesPerHostString.matches("\\d+")) {

				slavesPerHost = Integer.valueOf(slavesPerHostString);
			}

			_slavesPerHost = slavesPerHost;
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to determine URL for master " + _masterName, exception);
		}
	}

	private void _addRunningBuilds(
		JSONObject computerJSONObject, String jenkinsSlaveName,
		List<RunningBuild> runningBuilds) {

		for (String key : new String[] {"executors", "oneOffExecutors"}) {
			JSONArray executorsJSONArray = computerJSONObject.optJSONArray(key);

			if (executorsJSONArray == null) {
				continue;
			}

			for (int i = 0; i < executorsJSONArray.length(); i++) {
				JSONObject executorJSONObject =
					executorsJSONArray.getJSONObject(i);

				JSONObject currentExecutableJSONObject =
					executorJSONObject.optJSONObject("currentExecutable");

				if ((currentExecutableJSONObject == null) ||
					!currentExecutableJSONObject.has("url")) {

					continue;
				}

				runningBuilds.add(
					new RunningBuild(
						computerJSONObject, currentExecutableJSONObject, this,
						jenkinsSlaveName,
						executorJSONObject.optBoolean("likelyStuck", false)));
			}
		}
	}

	private void _executeSCPCommand(
		String sourceFilePath, String targetFilePath) {

		String scpCommand = JenkinsResultsParserUtil.combine(
			"scp ", _SSH_OPTIONS, " ", sourceFilePath, " ", targetFilePath);

		Process process = null;

		try {
			process = JenkinsResultsParserUtil.executeBashCommands(
				true, new File("."), _SSH_COMMAND_TIMEOUT, scpCommand);
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(
				"Unable to execute command " + scpCommand, exception);
		}

		if (process.exitValue() != 0) {
			throw new RuntimeException(
				"Unable to execute command " + scpCommand);
		}
	}

	private JSONArray _getBuildsJSONArray(
		final String jobName, final int page) {

		Retryable<JSONArray> retryable = new Retryable<JSONArray>(
			true, 2, 10, true) {

			@Override
			public JSONArray execute() {
				String url = JenkinsResultsParserUtil.getLocalURL(
					JenkinsResultsParserUtil.combine(
						String.valueOf(getURL()), "/job/", jobName,
						"/api/json?tree=allBuilds[actions[parameters",
						"[name,value]],queueId,timestamp,url]{",
						String.valueOf(page * 100), ",",
						String.valueOf((page + 1) * 100), "}"));

				try {
					JSONObject jsonObject =
						JenkinsResultsParserUtil.toJSONObject(url, false, 5000);

					return jsonObject.getJSONArray("allBuilds");
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

		};

		return retryable.executeWithRetries();
	}

	private int _getBusyNodesCount(String labelExpression) {
		int busyNodesCount = 0;

		List<JenkinsNode> jenkinsNodes = new ArrayList<>();

		jenkinsNodes.addAll(getJenkinsSlaves());

		jenkinsNodes.add(this);

		for (JenkinsNode jenkinsNode : jenkinsNodes) {
			if (_matchesLabels(
					labelExpression, jenkinsNode.getAssignedLabels()) &&
				!jenkinsNode.isIdle() && !jenkinsNode.isOffline()) {

				busyNodesCount++;
			}
		}

		return busyNodesCount;
	}

	private int _getIdleNodesCount(String labelExpression) {
		int idleNodesCount = 0;

		List<JenkinsNode> jenkinsNodes = new ArrayList<>();

		jenkinsNodes.addAll(getJenkinsSlaves());

		jenkinsNodes.add(this);

		for (JenkinsNode jenkinsNode : jenkinsNodes) {
			if (jenkinsNode instanceof JenkinsSlave) {
				JenkinsSlave jenkinsSlave = (JenkinsSlave)jenkinsNode;

				if (jenkinsSlave.isEC2FleetNodeComputer()) {
					continue;
				}
			}

			if (_matchesLabels(
					labelExpression, jenkinsNode.getAssignedLabels()) &&
				jenkinsNode.isIdle() && !jenkinsNode.isOffline()) {

				idleNodesCount++;
			}
		}

		List<AWSFleetCloud> awsFleetClouds = getAWSFleetClouds();

		if (awsFleetClouds.isEmpty()) {
			return idleNodesCount;
		}

		for (AWSFleetCloud awsFleetCloud : awsFleetClouds) {
			if (!_matchesLabels(labelExpression, awsFleetCloud.getLabels())) {
				continue;
			}

			int idleAWSFleetCloudSlavesCount = awsFleetCloud.getMaxSize();

			for (JenkinsSlave jenkinsSlave : awsFleetCloud.getJenkinsSlaves()) {
				if (!jenkinsSlave.isIdle() || jenkinsSlave.isOffline()) {
					idleAWSFleetCloudSlavesCount--;
				}
			}

			idleNodesCount += idleAWSFleetCloudSlavesCount;
		}

		return idleNodesCount;
	}

	private List<String> _getLabels(String labelExpression) {
		List<String> labelExpressionLabels = _labelExpressionLabels.get(
			labelExpression);

		if (labelExpressionLabels != null) {
			return labelExpressionLabels;
		}

		Set<String> labels = new HashSet<>();

		if (_matchesLabels(labelExpression, getAssignedLabels())) {
			labels.addAll(getAssignedLabels());
		}

		for (JenkinsSlave jenkinsSlave : getJenkinsSlaves()) {
			if (jenkinsSlave.isEC2FleetNodeComputer()) {
				continue;
			}

			if (_matchesLabels(
					labelExpression, jenkinsSlave.getAssignedLabels())) {

				labels.addAll(jenkinsSlave.getAssignedLabels());
			}
		}

		for (AWSFleetCloud awsFleetCloud : getAWSFleetClouds()) {
			if (_matchesLabels(labelExpression, awsFleetCloud.getLabels())) {
				labels.addAll(awsFleetCloud.getLabels());
			}
		}

		_labelExpressionLabels.put(labelExpression, new ArrayList<>(labels));

		return _labelExpressionLabels.get(labelExpression);
	}

	private int _getQueueItemsCount(String labelExpression) {
		int queueItemsCount = 0;

		List<String> labels = _getLabels(labelExpression);

		for (QueueItem queueItem : getQueueItems()) {
			if (!queueItem.isValidQueueItem()) {
				continue;
			}

			if (_matchesLabels(queueItem.getLabelExpression(), labels)) {
				queueItemsCount++;
			}
		}

		return queueItemsCount;
	}

	private synchronized int _getRecentBatchSizesTotal(String labelExpression) {
		int recentBatchSizesTotal = 0;

		long currentTimestamp = JenkinsResultsParserUtil.getCurrentTimeMillis();

		if (JenkinsResultsParserUtil.isNullOrEmpty(labelExpression)) {
			labelExpression = null;
		}

		List<String> labels = _getLabels(labelExpression);

		for (Map.Entry<String, Map<Long, Integer>> labelBatchSizesEntry :
				_labelBatchSizes.entrySet()) {

			String label = labelBatchSizesEntry.getKey();

			Map<Long, Integer> batchSizes = labelBatchSizesEntry.getValue();

			if (batchSizes == null) {
				batchSizes = new HashMap<>();
			}

			List<Long> expiredTimestamps = new ArrayList<>(batchSizes.size());

			for (Map.Entry<Long, Integer> entry : batchSizes.entrySet()) {
				Long expirationTimestamp = entry.getKey();

				if (expirationTimestamp < currentTimestamp) {
					expiredTimestamps.add(expirationTimestamp);

					continue;
				}

				if ((labelExpression == null) ||
					_matchesLabels(label, labels)) {

					recentBatchSizesTotal += entry.getValue();
				}
			}

			for (Long expiredTimestamp : expiredTimestamps) {
				batchSizes.remove(expiredTimestamp);
			}

			_labelBatchSizes.put(label, batchSizes);
		}

		return recentBatchSizesTotal;
	}

	private int _getUsableNodesCount(String labelExpression) {
		int usableNodesCount = 0;

		if (_matchesLabels(labelExpression, getAssignedLabels())) {
			usableNodesCount++;
		}

		for (JenkinsSlave jenkinsSlave : getJenkinsSlaves()) {
			if (!jenkinsSlave.isEC2FleetNodeComputer() &&
				!jenkinsSlave.isOffline() &&
				_matchesLabels(
					labelExpression, jenkinsSlave.getAssignedLabels())) {

				usableNodesCount++;
			}
		}

		for (AWSFleetCloud awsFleetCloud : getAWSFleetClouds()) {
			if (_matchesLabels(labelExpression, awsFleetCloud.getLabels())) {
				usableNodesCount += awsFleetCloud.getMaxSize();
			}
		}

		return usableNodesCount;
	}

	private synchronized boolean _isReachable() {
		if ((_availableTimestamp == -1) ||
			((System.currentTimeMillis() - _availableTimestamp) >
				_AVAILABLE_TIMEOUT)) {

			try {
				JenkinsResultsParserUtil.toJSONObject(
					getURL() + "/api/json?tree=mode", false, 1, 1, 1000);

				_available = true;
			}
			catch (Exception exception) {
				System.out.println(getName() + " is unreachable.");

				_available = false;
			}
			finally {
				_availableTimestamp = System.currentTimeMillis();
			}
		}

		return _available;
	}

	private boolean _isRunningOnJenkinsMaster() {
		return Objects.equals(System.getenv("HOSTNAME"), getName());
	}

	private boolean _isTopLevelJobName(String jobName) {
		if (_topLevelJobNames != null) {
			return _topLevelJobNames.contains(jobName);
		}

		_topLevelJobNames = new ArrayList<>();

		try {
			JSONObject topLevelBuildsJSONObject =
				JenkinsResultsParserUtil.toJSONObject(
					getURL() + "/view/Top%20Level/api/json?tree=jobs[name]");

			JSONArray jobsJSONArray = topLevelBuildsJSONObject.optJSONArray(
				"jobs");

			if (jobsJSONArray == null) {
				return false;
			}

			for (int i = 0; i < jobsJSONArray.length(); i++) {
				JSONObject jobJSONObject = jobsJSONArray.optJSONObject(i);

				if (jobJSONObject == null) {
					continue;
				}

				_topLevelJobNames.add(jobJSONObject.getString("name"));
			}
		}
		catch (IOException ioException) {
		}

		return _topLevelJobNames.contains(jobName);
	}

	private synchronized boolean _isUpdated(boolean minimal) {
		if ((_updateTimestamp == -1) ||
			((System.currentTimeMillis() - _updateTimestamp) >
				_MAXIMUM_UPDATE_DURATION) ||
			(_updateMinimal && !minimal)) {

			_updateMinimal = minimal;
			_updateTimestamp = System.currentTimeMillis();

			return false;
		}

		return true;
	}

	private boolean _matchesLabels(
		String labelExpression, List<String> labels) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(labelExpression)) {
			return true;
		}

		if ((labels == null) || labels.isEmpty()) {
			return false;
		}

		for (String label : labels) {
			if (Objects.equals(label, labelExpression)) {
				return true;
			}
		}

		if (!labelExpression.startsWith("!")) {
			return false;
		}

		String negativeLabelExpression = labelExpression.substring(1);

		boolean matchesNegativeLabelExpression = false;

		for (String label : labels) {
			if (Objects.equals(label, negativeLabelExpression)) {
				matchesNegativeLabelExpression = true;
			}
		}

		return !matchesNegativeLabelExpression;
	}

	private static final long _AVAILABLE_TIMEOUT = 1000 * 60 * 5;

	private static final long _AWS_FLEET_CLOUD_UPDATE_DURATION =
		15 * 1000 * 1000;

	private static final long _MAXIMUM_BUILD_AGE = 24 * 60 * 60 * 1000;

	private static final long _MAXIMUM_BUILD_UPDATE_DURATION = 30 * 1000;

	private static final long _MAXIMUM_QUEUE_UPDATE_DURATION = 15 * 1000;

	private static final long _MAXIMUM_UPDATE_DURATION = 1000 * 15;

	private static final long _SSH_COMMAND_TIMEOUT = 1000 * 60 * 5;

	private static final String _SSH_OPTIONS =
		"-o ConnectTimeout=60 -o NumberOfPasswordPrompts=0";

	private static final String _SSH_USER_NAME = "root";

	private static final Pattern _globalEnvironmentVariablesPattern =
		Pattern.compile("[^\\{]+(?<json>\\{.*\\})\\s+");
	private static final Map<String, JenkinsMaster> _jenkinsMasters =
		Collections.synchronizedMap(new HashMap<String, JenkinsMaster>());
	private static final List<String> _jenkinsMastersBlacklist =
		new ArrayList<>();
	private static final Pattern _masterNamePattern = Pattern.compile(
		"(?<cohortName>test-\\d+)-\\d+");

	static {
		try {
			String jenkinsMastersBlacklist =
				JenkinsResultsParserUtil.getBuildProperty(
					"jenkins.load.balancer.blacklist");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					jenkinsMastersBlacklist)) {

				Collections.addAll(
					_jenkinsMastersBlacklist,
					jenkinsMastersBlacklist.split(","));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private final List<String> _assignedLabels = new ArrayList<>();
	private boolean _available;
	private long _availableTimestamp = -1;
	private long _awsFleetCloudLastUpdateTimestamp;
	private List<AWSFleetCloud> _awsFleetClouds;
	private boolean _blacklisted;
	private final Map<String, List<JSONObject>> _buildJSONObjectsMap =
		new HashMap<>();
	private JSONObject _buildsCountJSONObject;
	private final Map<String, Long> _buildsUpdateTimes = new HashMap<>();
	private final List<String> _buildURLs = new CopyOnWriteArrayList<>();
	private int _busyExecutorsCount;
	private final List<DefaultBuild> _defaultBuilds = new ArrayList<>();
	private Map<String, String> _globalEnvironmentVariables;
	private boolean _idle;
	private JenkinsCohort _jenkinsCohort;
	private final Map<String, JenkinsSlave> _jenkinsSlavesMap =
		Collections.synchronizedMap(new HashMap<String, JenkinsSlave>());
	private final Map<String, Map<Long, Integer>> _labelBatchSizes =
		new HashMap<>();
	private final Map<String, List<String>> _labelExpressionLabels =
		new HashMap<>();
	private final String _masterName;
	private final String _masterRemoteURL;
	private final String _masterURL;
	private boolean _offline;
	private final List<QueueItem> _queueItems = new ArrayList<>();
	private Long _queueUpdateTime;
	private final List<RunningBuild> _runningBuilds =
		new CopyOnWriteArrayList<>();
	private final Integer _slaveRAM;
	private final Integer _slavesPerHost;
	private List<String> _topLevelJobNames;
	private boolean _updateMinimal = true;
	private long _updateTimestamp = -1;

}