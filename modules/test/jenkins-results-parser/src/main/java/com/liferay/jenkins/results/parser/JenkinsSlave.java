/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JenkinsSlave implements JenkinsNode<JenkinsSlave> {

	public JenkinsSlave() {
		_jenkinsMaster = JenkinsMaster.getInstance(
			Environment.get("MASTER_HOSTNAME"));
		_name = Environment.get("NODE_NAME");

		update(
			JenkinsAPIUtil.getAPIJSONObject(
				getComputerURL(),
				"assignedLabels[name],displayName,idle,offline," +
					"offlineCauseReason"));
	}

	public JenkinsSlave(String hostname) {
		hostname = hostname.replaceAll("([^\\.]+).*", "$1");

		String jenkinsMasterName =
			JenkinsResultsParserUtil.getJenkinsMasterName(hostname);

		if (jenkinsMasterName == null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find Jenkins master name for Jenkins slave ",
					"hostname ", hostname));
		}

		_jenkinsMaster = JenkinsMaster.getInstance(jenkinsMasterName);

		if (hostname.equals(jenkinsMasterName)) {
			_name = "master";
		}
		else {
			_name = hostname;
		}

		JSONObject jenkinsSlaveJSONObject = JenkinsAPIUtil.getAPIJSONObject(
			getComputerURL(),
			"assignedLabels[name],displayName,idle,offline,offlineCauseReason");

		update(jenkinsSlaveJSONObject);
	}

	@Override
	public int compareTo(JenkinsSlave jenkinsSlave) {
		return _name.compareTo(jenkinsSlave.getName());
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof JenkinsSlave) {
			JenkinsSlave jenkinsSlave = (JenkinsSlave)object;

			if (compareTo(jenkinsSlave) == 0) {
				return true;
			}

			return false;
		}

		return super.equals(object);
	}

	@Override
	public List<String> getAssignedLabels() {
		return _assignedLabels;
	}

	public String getComputerURL() {
		String name = getName();

		if (name.equals("master")) {
			name = "(" + name + ")";
		}

		return JenkinsResultsParserUtil.combine(
			_jenkinsMaster.getURL(), "/computer/", name);
	}

	public Build getCurrentBuild() {
		JSONObject jsonObject = JenkinsAPIUtil.getAPIJSONObject(
			getComputerURL(), "executors[currentExecutable[url]]");

		JSONArray jsonArray = jsonObject.getJSONArray("executors");

		jsonObject = jsonArray.getJSONObject(0);

		JSONObject currentExecutableJSONObject = jsonObject.getJSONObject(
			"currentExecutable");

		String buildURL = currentExecutableJSONObject.optString("url");

		if (buildURL == null) {
			return null;
		}

		return BuildFactory.newBuild(buildURL, null);
	}

	@Override
	public JenkinsCohort getJenkinsCohort() {
		return _jenkinsMaster.getJenkinsCohort();
	}

	@Override
	public JenkinsMaster getJenkinsMaster() {
		return _jenkinsMaster;
	}

	@Override
	public String getName() {
		return _name;
	}

	public String getNamePrefix() {
		Matcher matcher = _namePattern.matcher(getName());

		if (!matcher.matches()) {
			return null;
		}

		return matcher.group("prefix");
	}

	public Integer getNumber() {
		Matcher matcher = _namePattern.matcher(getName());

		if (!matcher.matches()) {
			return null;
		}

		return Integer.parseInt(matcher.group("number"));
	}

	public String getOfflineCauseReason() {
		return _offlineCauseReason;
	}

	public Set<JenkinsSlave> getSiblings() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		if (jenkinsMaster.getSlavesPerHost() < 2) {
			return Collections.emptySet();
		}

		Integer slaveNumber = getNumber();

		if (slaveNumber == null) {
			return Collections.emptySet();
		}

		Set<JenkinsSlave> siblings = new HashSet<>();

		int siblingSlaveNumber = slaveNumber + 1;

		if ((slaveNumber % 2) == 0) {
			siblingSlaveNumber = slaveNumber - 1;
		}

		siblings.add(
			jenkinsMaster.getJenkinsSlave(
				getNamePrefix() + siblingSlaveNumber));

		return siblings;
	}

	@Override
	public int hashCode() {
		String hashCodeString = _jenkinsMaster.getName() + "_" + _name;

		return hashCodeString.hashCode();
	}

	public boolean isEC2FleetNodeComputer() {
		if (_jenkinsNodeClassName == null) {
			update();
		}

		return _jenkinsNodeClassName.equals(
			"com.amazon.jenkins.ec2fleet.EC2FleetNodeComputer");
	}

	@Override
	public boolean isIdle() {
		return _idle;
	}

	@Override
	public boolean isOffline() {
		return _offline;
	}

	public boolean isReachable() {
		return JenkinsResultsParserUtil.isServerPortReachable(getName(), 22);
	}

	public void takeSlavesOffline(String offlineReason) {
		_setSlaveStatus(offlineReason, true);
	}

	public void takeSlavesOnline(String offlineReason) {
		_setSlaveStatus(offlineReason, false);
	}

	@Override
	public String toString() {
		return getName();
	}

	public void update() {
		_jenkinsMaster.update();
	}

	protected static String getDisplayName(JSONObject jenkinsSlaveJSONObject) {
		String displayName = jenkinsSlaveJSONObject.getString("displayName");

		String className = jenkinsSlaveJSONObject.getString("_class");

		if (className.contains("EC2FleetNodeComputer")) {
			Matcher matcher = _instanceIdPattern.matcher(displayName);

			if (matcher.find()) {
				displayName = matcher.group("instanceId");
			}
		}

		return displayName;
	}

	protected JenkinsSlave(
		JenkinsMaster jenkinsMaster, JSONObject jenkinsSlaveJSONObject) {

		_jenkinsMaster = jenkinsMaster;

		_name = getDisplayName(jenkinsSlaveJSONObject);

		update(jenkinsSlaveJSONObject);
	}

	protected void update(JSONObject jenkinsSlaveJSONObject) {
		_assignedLabels.clear();

		JSONArray assignedLabelsJSONArray = jenkinsSlaveJSONObject.optJSONArray(
			"assignedLabels");

		if (assignedLabelsJSONArray != null) {
			for (int i = 0; i < assignedLabelsJSONArray.length(); i++) {
				JSONObject assignedLabelJSONObject =
					assignedLabelsJSONArray.getJSONObject(i);

				String assignedLabelName = assignedLabelJSONObject.optString(
					"name");

				if (JenkinsResultsParserUtil.isNullOrEmpty(assignedLabelName)) {
					continue;
				}

				_assignedLabels.add(assignedLabelName);
			}
		}

		_idle = jenkinsSlaveJSONObject.getBoolean("idle");
		_jenkinsNodeClassName = jenkinsSlaveJSONObject.getString("_class");
		_offline = jenkinsSlaveJSONObject.getBoolean("offline");
		_offlineCauseReason = jenkinsSlaveJSONObject.optString(
			"offlineCauseReason");
	}

	private void _setSlaveStatus(String offlineReason, boolean offlineStatus) {
		try {
			Class<?> clazz = JenkinsSlave.class;

			String script = JenkinsResultsParserUtil.readInputStream(
				clazz.getResourceAsStream(
					"dependencies/set-slave-status.groovy"));

			script = script.replace("${slaves}", _name);
			script = script.replace(
				"${offline.reason}",
				offlineReason.replaceAll("\n", "<br />\\\\n"));
			script = script.replace(
				"${offline.status}", String.valueOf(offlineStatus));

			System.out.println(
				JenkinsResultsParserUtil.executeJenkinsScript(
					_jenkinsMaster.getName(), script));
		}
		catch (IOException ioException) {
			System.out.println("Unable to set the status for slaves: " + _name);

			ioException.printStackTrace();
		}
	}

	private static final Pattern _instanceIdPattern = Pattern.compile(
		".* (?<instanceId>i-[0-9a-z]+) .*");
	private static final Pattern _namePattern = Pattern.compile(
		"(?<prefix>.*[^\\d]+)(?<number>\\d+)");

	private final List<String> _assignedLabels = new ArrayList<>();
	private boolean _idle;
	private final JenkinsMaster _jenkinsMaster;
	private String _jenkinsNodeClassName;
	private final String _name;
	private boolean _offline;
	private String _offlineCauseReason;

}