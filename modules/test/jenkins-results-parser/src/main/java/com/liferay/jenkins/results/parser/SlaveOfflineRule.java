/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class SlaveOfflineRule {

	public static List<SlaveOfflineRule> getSlaveOfflineRules() {
		if (_slaveOfflineRules != null) {
			return _slaveOfflineRules;
		}

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load slave offline rules", ioException);
		}

		_slaveOfflineRules = new ArrayList<>();

		for (Object propertyNameObject : buildProperties.keySet()) {
			String propertyName = propertyNameObject.toString();

			if (propertyName.startsWith("slave.offline.rule[")) {
				String ruleName = propertyName.substring(
					"slave.offline.rule[".length(),
					propertyName.lastIndexOf("]"));

				_slaveOfflineRules.add(
					new SlaveOfflineRule(
						buildProperties.getProperty(propertyName), ruleName));
			}
		}

		return _slaveOfflineRules;
	}

	public String getName() {
		return name;
	}

	public String getNotificationRecipients() {
		return notificationRecipients;
	}

	public boolean isOfflineSibling() {
		return Boolean.parseBoolean(offlineSibling);
	}

	public boolean matches(Build build) {
		if (consolePattern != null) {
			String consoleText = build.getConsoleText();

			if (JenkinsResultsParserUtil.isNullOrEmpty(consoleText)) {
				return false;
			}

			for (String line : consoleText.split("\n")) {
				Matcher matcher = consolePattern.matcher(line);

				if (matcher.find()) {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Build ", build.getBuildURL(), " matched with ",
							"slave offline rule ", getName(),
							".\nMatching console log line:\n", line));

					return true;
				}
			}

			return false;
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Slave offline rule ", getName(),
				" has a null console pattern"));

		return false;
	}

	public boolean shutdown() {
		return shutdown;
	}

	public void takeSlaveOffline(Build build) {
		String pinnedMessage = "";

		if (!shutdown) {
			pinnedMessage = "PINNED\n";
		}

		JenkinsSlave jenkinsSlave = build.getJenkinsSlave();

		JenkinsMaster jenkinsMaster = jenkinsSlave.getJenkinsMaster();

		String slaveOfflineRuleString = toString();

		slaveOfflineRuleString = slaveOfflineRuleString.replace("\\", "\\\\");

		String message = JenkinsResultsParserUtil.combine(
			pinnedMessage, getName(), " failure detected at ",
			build.getBuildURL(), ". \n\n", slaveOfflineRuleString,
			"\n\n\nOffline Slave URL: ", jenkinsSlave.getComputerURL(), "\n");

		if (isOfflineSibling() && (jenkinsMaster.getSlavesPerHost() == 2)) {
			Set<JenkinsSlave> siblingJenkinsSlaves = jenkinsSlave.getSiblings();

			for (JenkinsSlave siblingJenkinsSlave : siblingJenkinsSlaves) {
				message = JenkinsResultsParserUtil.combine(
					message, "Offline Slave URL: ",
					siblingJenkinsSlave.getComputerURL(), "\n");

				String siblingMessage = JenkinsResultsParserUtil.combine(
					pinnedMessage, "Offline Sibling: ", jenkinsSlave.getName(),
					" Reason: ", getName());

				siblingJenkinsSlave.takeSlavesOffline(siblingMessage);
			}
		}

		System.out.println(message);

		TopLevelBuild topLevelBuild = build.getTopLevelBuild();

		if (topLevelBuild != null) {
			message = JenkinsResultsParserUtil.combine(
				message, "Top Level Build URL: ", topLevelBuild.getBuildURL());
		}

		jenkinsSlave.takeSlavesOffline(message);

		if ((notificationRecipients != null) &&
			!notificationRecipients.isEmpty()) {

			NotificationUtil.sendEmail(
				message, "jenkins", "Slave offline", notificationRecipients);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (consolePattern != null) {
			sb.append("console=");
			sb.append(consolePattern.pattern());
			sb.append("\n");
		}

		sb.append("name=");
		sb.append(name);
		sb.append("\n");

		if (notificationRecipients != null) {
			sb.append("notificationRecipients=");
			sb.append(notificationRecipients);
			sb.append("\n");
		}

		if (offlineSibling != null) {
			sb.append("offlineSibling=");
			sb.append(offlineSibling);
			sb.append("\n");
		}

		return sb.toString();
	}

	protected Map<String, String> parseConfigurations(String configurations) {
		String[] configurationsArray = configurations.split("\\s*\n\\s*");

		Map<String, String> configurationsMap = new HashMap<>(
			configurationsArray.length);

		for (String configuration : configurationsArray) {
			Matcher matcher = _configurationsPattern.matcher(configuration);

			if (!matcher.matches()) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to parse configuration in slave offline ",
						"rule \"", getName(), "\"\n", configuration));
			}

			String value = matcher.group(2);

			if ((value == null) || value.isEmpty()) {
				continue;
			}

			configurationsMap.put(matcher.group(1), value);
		}

		return configurationsMap;
	}

	protected void validateRequiredConfigurationParameter(
		Map<String, String> configurationsMap, String parameterName) {

		if (!configurationsMap.containsKey(parameterName)) {
			throw new IllegalStateException(
				JenkinsResultsParserUtil.combine(
					"Unable to detect required configuration \"", parameterName,
					" in slave offline rule \"", getName(), "\""));
		}
	}

	protected Pattern consolePattern;
	protected String name;
	protected String notificationRecipients;
	protected String offlineSibling;
	protected boolean shutdown;

	private SlaveOfflineRule(String configurations, String ruleName) {
		name = ruleName;

		Map<String, String> configurationsMap = parseConfigurations(
			configurations);

		validateRequiredConfigurationParameter(configurationsMap, "console");

		consolePattern = Pattern.compile(configurationsMap.get("console"));

		validateRequiredConfigurationParameter(
			configurationsMap, "notificationRecipients");

		notificationRecipients = configurationsMap.get(
			"notificationRecipients");

		offlineSibling = configurationsMap.get("offlineSiblings");

		if (configurationsMap.containsKey("shutdown")) {
			shutdown = Boolean.parseBoolean(configurationsMap.get("shutdown"));
		}
		else {
			shutdown = false;
		}
	}

	private static final Pattern _configurationsPattern = Pattern.compile(
		"([^=]+)=(.*)");
	private static List<SlaveOfflineRule> _slaveOfflineRules;

}