/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.job.property.JobProperty;

import java.io.File;

import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Yi-Chen Tsai
 */
public abstract class PortalAcceptanceTestSuiteJob
	extends PortalGitRepositoryJob implements TestSuiteJob {

	@Override
	public DistType getDistType() {
		JobProperty jobProperty = getJobProperty("dist.type");

		String distType = jobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(distType)) {
			for (DistType distTypeValue : DistType.values()) {
				if (distType.equals(distTypeValue.toString())) {
					recordJobProperty(jobProperty);

					return distTypeValue;
				}
			}
		}

		return DistType.CI;
	}

	@Override
	public Set<String> getDistTypes() {
		Set<String> distTypes = super.getDistTypes();

		if (!_testSuiteName.equals("relevant")) {
			return distTypes;
		}

		JobProperty jobProperty = getJobProperty(
			"test.batch.dist.app.servers[stable]");

		distTypes.addAll(getSetFromString(jobProperty.getValue()));

		return distTypes;
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("test_suite_name", _testSuiteName);

		return jsonObject;
	}

	@Override
	public String getTestSuiteName() {
		return _testSuiteName;
	}

	public String getWorkspacePortalVersion() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		Properties workspaceGradleProperties =
			JenkinsResultsParserUtil.getProperties(
				new File(
					portalGitWorkingDirectory.getWorkingDirectory(),
					"workspaces/liferay-sample-workspace/gradle.properties"));

		String liferayWorkspaceProduct = JenkinsResultsParserUtil.getProperty(
			workspaceGradleProperties, "liferay.workspace.product");

		Matcher matcher = _liferayWorkspaceProductPattern.matcher(
			liferayWorkspaceProduct);

		if (!matcher.find()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		String basePortalVersion = JenkinsResultsParserUtil.combine(
			matcher.group("majorVersion"), ".", matcher.group("minorVersion"));

		sb.append(basePortalVersion);

		sb.append(".");

		String releaseProfile = matcher.group("releaseProfile");
		String patchType = matcher.group("patchType");
		int patchVersion = Integer.valueOf(matcher.group("patchVersion"));

		if (basePortalVersion.equals("7.0") ||
			basePortalVersion.equals("7.1") ||
			basePortalVersion.equals("7.2") ||
			basePortalVersion.equals("7.3")) {

			if (releaseProfile.equals("portal")) {
				if (patchType.equals("ga")) {
					sb.append(patchVersion - 1);
				}
				else {
					sb.append("0");
				}
			}
			else if (releaseProfile.equals("dxp")) {
				sb.append("10");

				if (patchType.equals("sp")) {
					sb.append(".");
					sb.append(patchVersion);
				}
				else if (patchType.equals("u")) {
					sb.append(".u");
					sb.append(patchVersion);
				}
			}
		}

		if (basePortalVersion.equals("7.4")) {
			if (releaseProfile.equals("portal")) {
				if (patchType.equals("ga")) {
					if (patchVersion <= 2) {
						sb.append(patchVersion - 1);
					}
					else {
						sb.append("3.");
						sb.append(patchVersion);
					}
				}
				else {
					sb.append("0");
				}
			}
			else if (releaseProfile.equals("dxp")) {
				if (patchType.equals("ep")) {
					sb.append(patchVersion + 9);
				}
				else if (patchType.equals("ga")) {
					sb.append("13");
				}
				else if (patchType.equals("u")) {
					sb.append("13.u");
					sb.append(patchVersion);
				}
			}
		}

		return sb.toString();
	}

	protected PortalAcceptanceTestSuiteJob(
		BuildProfile buildProfile, String jobName,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName, String upstreamBranchName) {

		super(
			buildProfile, jobName, portalGitWorkingDirectory,
			upstreamBranchName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
			testSuiteName = "default";
		}

		_testSuiteName = testSuiteName;
	}

	protected PortalAcceptanceTestSuiteJob(JSONObject jsonObject) {
		super(jsonObject);

		_testSuiteName = jsonObject.getString("test_suite_name");
	}

	@Override
	protected Set<String> getRawBatchNames() {
		Set<String> rawBatchNames = super.getRawBatchNames();

		if (!isTestRelevantChanges()) {
			return rawBatchNames;
		}

		JobProperty jobProperty = getJobProperty("test.batch.names[stable]");

		recordJobProperty(jobProperty);

		rawBatchNames.addAll(getSetFromString(jobProperty.getValue()));

		return rawBatchNames;
	}

	private static final Pattern _liferayWorkspaceProductPattern =
		Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"(?<releaseProfile>dxp|portal)-(?<majorVersion>\\d+)\\.",
				"(?<minorVersion>\\d)+(-(?<patchType>ep|ga|sp|u)",
				"(?<patchVersion>\\d+))?"));

	private final String _testSuiteName;

}