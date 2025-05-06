/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class ReinvokeRule implements Comparable<ReinvokeRule> {

	public static List<ReinvokeRule> getReinvokeRules() {
		if (_reinvokeRules != null) {
			return new ArrayList<>(_reinvokeRules);
		}

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load reinvoke rules", ioException);
		}

		_reinvokeRules = new ArrayList<>();

		for (Object propertyNameObject : buildProperties.keySet()) {
			String propertyName = propertyNameObject.toString();

			if (propertyName.startsWith("reinvoke.rule[")) {
				String ruleName = propertyName.substring(
					"reinvoke.rule[".length(), propertyName.lastIndexOf("]"));

				_reinvokeRules.add(
					new ReinvokeRule(
						buildProperties.getProperty(propertyName), ruleName));
			}

			Collections.sort(_reinvokeRules);
		}

		return new ArrayList<>(_reinvokeRules);
	}

	@Override
	public int compareTo(ReinvokeRule reinvokeRule) {
		if (priority == reinvokeRule.getPriority()) {
			return name.compareTo(reinvokeRule.getName());
		}

		return Integer.compare(priority, reinvokeRule.getPriority());
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ReinvokeRule)) {
			return false;
		}

		ReinvokeRule reinvokeRule = (ReinvokeRule)object;

		if (Objects.equals(getName(), reinvokeRule.getName())) {
			return true;
		}

		return false;
	}

	public int getMaximumInvocationCount() {
		if (maximumInvocationCount != null) {
			return maximumInvocationCount;
		}

		try {
			Properties properties =
				JenkinsResultsParserUtil.getBuildProperties();

			String propertyName = "reinvoke.rule.max.invocation.count";

			if (properties.containsKey(propertyName)) {
				maximumInvocationCount = Integer.parseInt(
					properties.getProperty(propertyName));

				return maximumInvocationCount;
			}
		}
		catch (IOException ioException) {
			System.out.println(
				"Unable to load reinvoke.rule.max.invocation.count");
		}

		maximumInvocationCount = _MAXIMUM_INVOCATION_COUNT;

		return maximumInvocationCount;
	}

	public String getName() {
		return name;
	}

	public String getNotificationRecipients() {
		return notificationRecipients;
	}

	public int getPriority() {
		return priority;
	}

	public String getReinvokeBuildPriority() {
		return reinvokeBuildPriority;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public boolean matches(Build build) {
		if (build == null) {
			return false;
		}

		Matcher matcher = null;

		if (axisVariablePattern != null) {
			String axisVariable = null;

			if (build instanceof AxisBuild) {
				AxisBuild axisBuild = (AxisBuild)build;

				axisVariable = axisBuild.getAxisVariable();
			}
			else if (build instanceof DownstreamBuild) {
				DownstreamBuild downstreamBuild = (DownstreamBuild)build;

				axisVariable = downstreamBuild.getAxisVariable();
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(axisVariable)) {
				return false;
			}

			matcher = axisVariablePattern.matcher(axisVariable);

			if (!matcher.find()) {
				return false;
			}
		}

		if (jobVariantPattern != null) {
			String jobVariant = build.getJobVariant();

			if (jobVariant == null) {
				jobVariant = "";
			}

			matcher = jobVariantPattern.matcher(jobVariant);

			if (!matcher.find()) {
				return false;
			}
		}

		if (testSuiteNamePattern != null) {
			TopLevelBuild topLevelBuild = build.getTopLevelBuild();

			if (topLevelBuild != null) {
				String testSuiteName = topLevelBuild.getTestSuiteName();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
					matcher = testSuiteNamePattern.matcher(testSuiteName);

					if (!matcher.find()) {
						return false;
					}
				}
			}
		}

		if (topLevelBuildJobNamePattern != null) {
			TopLevelBuild topLevelBuild = build.getTopLevelBuild();

			if (topLevelBuild != null) {
				matcher = topLevelBuildJobNamePattern.matcher(
					topLevelBuild.getJobName());

				if (!matcher.find()) {
					return false;
				}
			}
		}

		if (consolePattern != null) {
			String consoleText = build.getConsoleText();

			for (String line : consoleText.split("\n")) {
				matcher = consolePattern.matcher(line);

				if (matcher.find()) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (axisVariablePattern != null) {
			sb.append("axisVariable=");
			sb.append(axisVariablePattern.pattern());
			sb.append("\n");
		}

		if (consolePattern != null) {
			sb.append("console=");
			sb.append(consolePattern.pattern());
			sb.append("\n");
		}

		if (jobVariantPattern != null) {
			sb.append("jobVariant=");
			sb.append(jobVariantPattern.pattern());
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

		if (testSuiteNamePattern != null) {
			sb.append("testSuiteName=");
			sb.append(testSuiteNamePattern.pattern());
			sb.append("\n");
		}

		if (topLevelBuildJobNamePattern != null) {
			sb.append("topLevelJobName=");
			sb.append(topLevelBuildJobNamePattern.pattern());
			sb.append("\n");
		}

		return sb.toString();
	}

	protected Pattern axisVariablePattern;
	protected Pattern consolePattern;
	protected Pattern jobVariantPattern;
	protected Integer maximumInvocationCount;
	protected String name;
	protected String notificationRecipients;
	protected Integer priority = 5;
	protected String reinvokeBuildPriority;
	protected Pattern testSuiteNamePattern;
	protected Pattern topLevelBuildJobNamePattern;

	private ReinvokeRule(String configurations, String ruleName) {
		name = ruleName;

		for (String configuration : configurations.split("\n")) {
			int x = configuration.indexOf("=");

			String name = configuration.substring(0, x);

			String value = configuration.substring(x + 1);

			value = value.trim();

			if (value.isEmpty()) {
				continue;
			}

			if (name.equals("maximumInvocationCount")) {
				maximumInvocationCount = Integer.valueOf(value);

				continue;
			}

			if (name.equals("notificationRecipients")) {
				notificationRecipients = value;

				continue;
			}

			if (name.equals("priority")) {
				if (JenkinsResultsParserUtil.isInteger(value)) {
					priority = Integer.parseInt(value);
				}

				continue;
			}

			if (name.equals("reinvokeBuildPriority")) {
				reinvokeBuildPriority = value;

				continue;
			}

			Pattern pattern = Pattern.compile(value);

			if (name.equals("axisVariable")) {
				axisVariablePattern = pattern;

				continue;
			}

			if (name.equals("console")) {
				consolePattern = pattern;

				continue;
			}

			if (name.equals("jobVariant")) {
				jobVariantPattern = pattern;

				continue;
			}

			if (name.equals("testSuiteName")) {
				testSuiteNamePattern = pattern;
			}

			if (name.equals("topLevelJobName")) {
				topLevelBuildJobNamePattern = pattern;
			}
		}
	}

	private static final int _MAXIMUM_INVOCATION_COUNT = 1;

	private static List<ReinvokeRule> _reinvokeRules;

}