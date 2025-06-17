/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Michael Hashimoto
 */
public class PortalReleaseFactory {

	public static PortalFixpackRelease newPortalFixpackRelease(
		String portalFixpackVersion, PortalRelease portalRelease) {

		return newPortalFixpackRelease(
			PortalFixpackRelease.getPortalFixpackURL(
				portalFixpackVersion, portalRelease));
	}

	public static PortalFixpackRelease newPortalFixpackRelease(
		URL portalFixpackURL) {

		String key = String.valueOf(portalFixpackURL);

		PortalFixpackRelease portalFixpackRelease = _portalFixpackReleases.get(
			key);

		if (portalFixpackRelease != null) {
			return portalFixpackRelease;
		}

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		if (buildDatabase.hasPortalFixpackRelease(key)) {
			portalFixpackRelease = buildDatabase.getPortalFixpackRelease(key);

			_portalFixpackReleases.put(key, portalFixpackRelease);

			return portalFixpackRelease;
		}

		portalFixpackRelease = new PortalFixpackRelease(portalFixpackURL);

		_portalFixpackReleases.put(key, portalFixpackRelease);

		buildDatabase.putPortalFixpackRelease(key, portalFixpackRelease);

		return portalFixpackRelease;
	}

	public static PortalHotfixRelease newPortalHotfixRelease(
		URL portalHotfixReleaseURL) {

		return newPortalHotfixRelease(portalHotfixReleaseURL, null, null);
	}

	public static PortalHotfixRelease newPortalHotfixRelease(
		URL portalHotfixReleaseURL, PortalFixpackRelease portalFixpackRelease,
		PortalRelease portalRelease) {

		String key = String.valueOf(portalHotfixReleaseURL);

		PortalHotfixRelease portalHotfixRelease = _portalHotfixReleases.get(
			key);

		if (portalHotfixRelease != null) {
			return portalHotfixRelease;
		}

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		if (buildDatabase.hasPortalFixpackRelease(key)) {
			portalHotfixRelease = buildDatabase.getPortalHotfixRelease(key);

			_portalHotfixReleases.put(key, portalHotfixRelease);

			return portalHotfixRelease;
		}

		portalHotfixRelease = new PortalHotfixRelease(
			portalHotfixReleaseURL, portalFixpackRelease, portalRelease);

		_portalHotfixReleases.put(key, portalHotfixRelease);

		buildDatabase.putPortalHotfixRelease(key, portalHotfixRelease);

		return portalHotfixRelease;
	}

	public static PortalRelease newPortalRelease(String portalVersion) {
		return _newPortalRelease(
			PortalRelease.getPortalVersion(portalVersion),
			"Invalid Portal Version: " + portalVersion,
			() -> new PortalRelease(portalVersion));
	}

	public static PortalRelease newPortalRelease(URL bundleURL) {
		return _newPortalRelease(
			PortalRelease.getPortalVersion(bundleURL),
			"Invalid Bundle URL: " + bundleURL,
			() -> new PortalRelease(bundleURL));
	}

	private static PortalRelease _newPortalRelease(
		String portalVersion, String errorMessage,
		Supplier<PortalRelease> portalReleaseSupplier) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalVersion)) {
			throw new RuntimeException(errorMessage);
		}

		PortalRelease portalRelease = _portalReleases.get(portalVersion);

		if (portalRelease != null) {
			return portalRelease;
		}

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		if (buildDatabase.hasPortalRelease(portalVersion)) {
			portalRelease = buildDatabase.getPortalRelease(portalVersion);

			_portalReleases.put(portalVersion, portalRelease);

			return portalRelease;
		}

		portalRelease = portalReleaseSupplier.get();

		_portalReleases.put(portalVersion, portalRelease);

		buildDatabase.putPortalRelease(portalVersion, portalRelease);

		return portalRelease;
	}

	private static final Map<String, PortalFixpackRelease>
		_portalFixpackReleases = new HashMap<>();
	private static final Map<String, PortalHotfixRelease>
		_portalHotfixReleases = new HashMap<>();
	private static final Map<String, PortalRelease> _portalReleases =
		new HashMap<>();

}