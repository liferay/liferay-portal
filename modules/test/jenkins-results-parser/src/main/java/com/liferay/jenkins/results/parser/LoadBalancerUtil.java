/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class LoadBalancerUtil {

	public static List<JenkinsMaster> getAvailableJenkinsMasters(
		String blacklistString, String masterPrefix, Properties properties) {

		return getAvailableJenkinsMasters(
			blacklistString, masterPrefix, properties, true);
	}

	public static List<JenkinsMaster> getAvailableJenkinsMasters(
		String blacklistString, String masterPrefix, Properties properties,
		boolean verbose) {

		List<JenkinsMaster> allJenkinsMasters =
			_jenkinsMastersMap.computeIfAbsent(
				masterPrefix,
				key -> JenkinsResultsParserUtil.getJenkinsMasters(
					properties, JenkinsMaster.getSlaveRAMMinimumDefault(),
					JenkinsMaster.getSlavesPerHostDefault(), masterPrefix));

		List<String> blacklist = _getBlacklist(
			properties, blacklistString, verbose);

		List<JenkinsMaster> availableJenkinsMasters = new ArrayList<>(
			allJenkinsMasters.size());

		for (JenkinsMaster jenkinsMaster : allJenkinsMasters) {
			if (blacklist.contains(jenkinsMaster.getName())) {
				continue;
			}

			availableJenkinsMasters.add(jenkinsMaster);
		}

		return availableJenkinsMasters;
	}

	public static String getMostAvailableMasterURL(
			boolean verbose, String... overridePropertiesArray)
		throws Exception {

		return getMostAvailableMasterURL(
			null, overridePropertiesArray, verbose);
	}

	public static String getMostAvailableMasterURL(Properties properties) {
		return getMostAvailableMasterURL(properties, true);
	}

	public static String getMostAvailableMasterURL(
		Properties properties, boolean verbose) {

		String baseInvocationURL = JenkinsResultsParserUtil.getProperty(
			properties, "base.invocation.url");

		String masterPrefix = getMasterPrefix(baseInvocationURL);

		if (masterPrefix.equals(baseInvocationURL)) {
			return baseInvocationURL;
		}

		String blacklistString = JenkinsResultsParserUtil.getProperty(
			properties, "blacklist");

		List<JenkinsMaster> availableJenkinsMasters =
			getAvailableJenkinsMasters(
				blacklistString, masterPrefix, properties, verbose);

		if (availableJenkinsMasters.isEmpty()) {
			return null;
		}

		AtomicInteger counter = _roundRobinCounters.computeIfAbsent(
			masterPrefix,
			key -> new AtomicInteger(
				JenkinsResultsParserUtil.getRandomValue(
					0, availableJenkinsMasters.size() - 1)));

		int index = Math.floorMod(
			counter.getAndIncrement(), availableJenkinsMasters.size());

		JenkinsMaster jenkinsMaster = availableJenkinsMasters.get(index);

		if (verbose) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Selected master ", jenkinsMaster.getName(),
					" via round robin (",
					String.valueOf(availableJenkinsMasters.size()),
					" eligible masters under prefix ", masterPrefix, ")"));
		}

		return "http://" + jenkinsMaster.getName();
	}

	public static String getMostAvailableMasterURL(
			String... overridePropertiesArray)
		throws Exception {

		return getMostAvailableMasterURL(null, overridePropertiesArray, true);
	}

	public static String getMostAvailableMasterURL(
			String propertiesURL, String[] overridePropertiesArray)
		throws Exception {

		return getMostAvailableMasterURL(
			propertiesURL, overridePropertiesArray, true);
	}

	public static String getMostAvailableMasterURL(
			String propertiesURL, String[] overridePropertiesArray,
			boolean verbose)
		throws Exception {

		Properties properties;

		if (propertiesURL == null) {
			properties = JenkinsResultsParserUtil.getBuildProperties(false);
		}
		else {
			properties = new Properties();

			String propertiesString = JenkinsResultsParserUtil.toString(
				JenkinsResultsParserUtil.getLocalURL(propertiesURL), false,
				true);

			properties.load(new StringReader(propertiesString));
		}

		if ((overridePropertiesArray != null) &&
			(overridePropertiesArray.length > 0) &&
			((overridePropertiesArray.length % 2) == 0)) {

			for (int i = 0; i < overridePropertiesArray.length; i += 2) {
				String overridePropertyValue = overridePropertiesArray[i + 1];

				if (overridePropertyValue == null) {
					continue;
				}

				String overridePropertyName = overridePropertiesArray[i];

				properties.setProperty(
					overridePropertyName, overridePropertyValue);
			}
		}

		return getMostAvailableMasterURL(properties, verbose);
	}

	protected static String getMasterPrefix(String baseInvocationURL) {
		Matcher matcher = _urlPattern.matcher(baseInvocationURL);

		if (!matcher.find()) {
			return baseInvocationURL;
		}

		return matcher.group("masterPrefix");
	}

	private static List<String> _getBlacklist(
		Properties properties, String requestBlacklistString, boolean verbose) {

		List<String> blacklist = new ArrayList<>();

		String propertyBlacklistString = JenkinsResultsParserUtil.getProperty(
			properties, "jenkins.load.balancer.blacklist");

		if (propertyBlacklistString != null) {
			for (String blacklistItem : propertyBlacklistString.split(",")) {
				blacklistItem = blacklistItem.trim();

				if (!blacklistItem.isEmpty()) {
					blacklist.add(blacklistItem);
				}
			}
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(requestBlacklistString)) {
			requestBlacklistString = requestBlacklistString.toLowerCase();

			String[] requestBlacklistItems = requestBlacklistString.split(
				"\\s*,\\s*");

			for (String blacklistItem : requestBlacklistItems) {
				if (!blacklist.contains(blacklistItem)) {
					blacklist.add(blacklistItem);
				}
			}
		}

		if (verbose) {
			System.out.println("Blacklist: " + blacklist);
		}

		return blacklist;
	}

	private static final Map<String, List<JenkinsMaster>> _jenkinsMastersMap =
		new ConcurrentHashMap<>();
	private static final Map<String, AtomicInteger> _roundRobinCounters =
		new ConcurrentHashMap<>();
	private static final Pattern _urlPattern = Pattern.compile(
		"http://(?<masterPrefix>.+-\\d?).liferay.com");

}