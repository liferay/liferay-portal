/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public class MarketplaceAppPluginsTopLevelBuild
	extends PluginsTopLevelBuild implements PortalReleaseBuild {

	public MarketplaceAppPluginsTopLevelBuild(
		String url, TopLevelBuild topLevelBuild) {

		super(url, topLevelBuild);
	}

	@Override
	public String getBaseGitRepositoryName() {
		String branchName = getBranchName();

		if (branchName.equals("master")) {
			return "liferay-portal";
		}

		return "liferay-portal-ee";
	}

	@Override
	public String getBranchName() {
		Matcher matcher = _pattern.matcher(
			getParameterValue("TEST_PORTAL_BUILD_NUMBER"));

		if (!matcher.find()) {
			throw new RuntimeException("Please set 'TEST_PORTAL_BUILD_NUMBER'");
		}

		return JenkinsResultsParserUtil.combine(
			matcher.group("major"), ".", matcher.group("minor"), ".x");
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		Matcher matcher = _pattern.matcher(
			getParameterValue("TEST_PORTAL_BUILD_NUMBER"));

		if (!matcher.find()) {
			throw new RuntimeException("Please set 'TEST_PORTAL_BUILD_NUMBER'");
		}

		String fix = matcher.group("fix");

		if (fix.startsWith("1")) {
			return Job.BuildProfile.DXP;
		}

		return Job.BuildProfile.PORTAL;
	}

	@Override
	public String getPluginName() {
		return getParameterValue("TEST_PACKAGE_FILE_NAME");
	}

	@Override
	public PortalRelease getPortalRelease() {
		if (_portalRelease != null) {
			return _portalRelease;
		}

		String portalBuildNumber = getParameterValue(
			"TEST_PORTAL_BUILD_NUMBER");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBuildNumber)) {
			return null;
		}

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String portalVersion = JenkinsResultsParserUtil.getProperty(
			buildProperties, "portal.version", portalBuildNumber);

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalVersion)) {
			return null;
		}

		String latestPortalVersion = JenkinsResultsParserUtil.getProperty(
			buildProperties, "portal.version.latest", portalVersion);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(latestPortalVersion)) {
			_portalRelease = PortalReleaseFactory.newPortalRelease(
				latestPortalVersion);

			return _portalRelease;
		}

		_portalRelease = PortalReleaseFactory.newPortalRelease(portalVersion);

		return _portalRelease;
	}

	private static final Pattern _pattern = Pattern.compile(
		"(?<major>\\d)(?<minor>\\d)(?<fix>\\d+)");

	private PortalRelease _portalRelease;

}