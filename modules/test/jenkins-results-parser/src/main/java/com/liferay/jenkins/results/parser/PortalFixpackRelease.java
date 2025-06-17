/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalFixpackRelease {

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		PortalRelease portalRelease = getPortalRelease();

		jsonObject.put(
			"portal_fixpack_url", String.valueOf(getPortalFixpackURL())
		).put(
			"portal_fixpack_version", getPortalFixpackVersion()
		).put(
			"portal_release_version", portalRelease.getPortalVersion()
		);

		return jsonObject;
	}

	public URL getPortalFixpackURL() {
		return _portalFixpackURL;
	}

	public String getPortalFixpackVersion() {
		return _portalFixpackVersion;
	}

	public PortalRelease getPortalRelease() {
		return _portalRelease;
	}

	protected static URL getPortalFixpackURL(
		String portalFixpackVersion, PortalRelease portalRelease) {

		try {
			String portalVersion = portalRelease.getPortalVersion();
			String portalFixpackType = "dxp";

			if (portalVersion.contains("7.0")) {
				portalFixpackType = "de";
			}

			String portalBaseVersion = portalVersion.replaceAll(
				"(\\d)\\.(\\d)\\.(\\d\\d).*", "$1.$2.$3");

			String portalBaseBuildVersion = portalBaseVersion.replaceAll(
				"\\.", "");

			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://files.liferay.com/private/ee/fix-packs/",
					portalBaseVersion, "/", portalFixpackType,
					"/liferay-fix-pack-", portalFixpackType, "-",
					portalFixpackVersion, "-", portalBaseBuildVersion, ".zip"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected PortalFixpackRelease(JSONObject jsonObject) {
		try {
			_portalFixpackURL = new URL(
				jsonObject.getString("portal_fixpack_url"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}

		_portalFixpackVersion = jsonObject.getString("portal_fixpack_version");
		_portalRelease = PortalReleaseFactory.newPortalRelease(
			jsonObject.getString("portal_release_version"));
	}

	protected PortalFixpackRelease(URL portalFixpackURL) {
		Matcher fixpackURLMatcher = _fixpackURLPattern.matcher(
			portalFixpackURL.toString());

		if (!fixpackURLMatcher.find()) {
			throw new RuntimeException("Invalid URL " + portalFixpackURL);
		}

		String fixpackFileName = fixpackURLMatcher.group("fixpackFileName");

		Matcher fixpackFileNameMatcher = _fixpackFileNamePattern.matcher(
			fixpackFileName);

		if (!fixpackFileNameMatcher.find()) {
			throw new RuntimeException(
				"Invalid fixpack file name " + fixpackFileName);
		}

		_portalFixpackVersion = fixpackFileNameMatcher.group(
			"portalFixpackVersion");

		_portalRelease = PortalReleaseFactory.newPortalRelease(
			_getPortalVersion(
				fixpackFileNameMatcher.group("portalBuildVersion"),
				_portalFixpackVersion));

		String portalFixpackURLString = portalFixpackURL.toString();

		portalFixpackURLString = portalFixpackURLString.replace(
			"http://mirrors/", "https://");
		portalFixpackURLString = portalFixpackURLString.replace(
			"http://mirrors.lax.liferay.com/", "https://");

		try {
			_portalFixpackURL = new URL(portalFixpackURLString);
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private String _getPortalVersion(
		String portalBuildVersion, String portalFixpackVersion) {

		if (portalBuildVersion.startsWith("73")) {
			if (portalFixpackVersion.equals("1") ||
				portalFixpackVersion.equals("2")) {

				return "7.3.10.1";
			}
			else if (portalFixpackVersion.equals("3")) {
				return "7.3.10.3";
			}

			return "7.3.10.u" + portalFixpackVersion;
		}

		String basePortalVersionRegex = "(\\d)(\\d)(\\d\\d)";

		StringBuilder sb = new StringBuilder();

		sb.append(portalBuildVersion.replaceAll(basePortalVersionRegex, "$1"));
		sb.append(".");
		sb.append(portalBuildVersion.replaceAll(basePortalVersionRegex, "$2"));
		sb.append(".");
		sb.append(portalBuildVersion.replaceAll(basePortalVersionRegex, "$3"));

		String basePortalVersion = sb.toString();

		String portalVersion = basePortalVersion;

		try {
			Properties buildProperties =
				JenkinsResultsParserUtil.getBuildProperties();

			int latestPortalFixVersion = -1;

			for (String propertyName : buildProperties.stringPropertyNames()) {
				String propertyNameRegex =
					_FIXPACK_VERSION_PROPERTY_NAME +
						"\\[\\d\\.\\d\\.\\d{2}\\.(\\d+)\\]";

				if (!propertyName.matches(propertyNameRegex)) {
					continue;
				}

				int portalFixVersion = Integer.valueOf(
					propertyName.replaceAll(propertyNameRegex, "$1"));

				if (portalFixVersion > latestPortalFixVersion) {
					latestPortalFixVersion = portalFixVersion;
				}
			}

			if (latestPortalFixVersion == -1) {
				return portalVersion;
			}

			for (int i = 1; i <= latestPortalFixVersion; i++) {
				String portalVersionCandidate = basePortalVersion + "." + i;

				String portalReleaseFixpackVersion =
					JenkinsResultsParserUtil.getProperty(
						buildProperties, _FIXPACK_VERSION_PROPERTY_NAME,
						portalVersionCandidate);

				if ((portalReleaseFixpackVersion == null) ||
					portalReleaseFixpackVersion.isEmpty()) {

					continue;
				}

				if (Integer.valueOf(portalFixpackVersion) >= Integer.valueOf(
						portalReleaseFixpackVersion)) {

					portalVersion = portalVersionCandidate;
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return portalVersion;
	}

	private static final String _FIXPACK_VERSION_PROPERTY_NAME =
		"portal.release.fixpack.version";

	private static final Pattern _fixpackFileNamePattern = Pattern.compile(
		"liferay-fix-pack-(de|dxp|portal)-(?<portalFixpackVersion>\\d+)-" +
			"(?<portalBuildVersion>\\d+)(-build\\d*)?(-src)?.zip");
	private static final Pattern _fixpackURLPattern = Pattern.compile(
		"https?://.+/(?<fixpackFileName>[^/]+.zip)");

	private final URL _portalFixpackURL;
	private final String _portalFixpackVersion;
	private final PortalRelease _portalRelease;

}