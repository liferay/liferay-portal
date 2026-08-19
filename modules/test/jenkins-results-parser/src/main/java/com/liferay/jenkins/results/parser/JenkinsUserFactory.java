/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class JenkinsUserFactory {

	public static JenkinsUser getJenkinsUser(
		JenkinsMaster jenkinsMaster, String jenkinsUserName) {

		if (jenkinsMaster == null) {
			throw new IllegalArgumentException("Jenkins master is null");
		}

		return getJenkinsUser(jenkinsMaster.getName(), jenkinsUserName);
	}

	public static synchronized JenkinsUser getJenkinsUser(
		String jenkinsMasterName, String jenkinsUserName) {

		String key = JenkinsResultsParserUtil.combine(
			jenkinsMasterName, "/", jenkinsUserName);

		JenkinsUser jenkinsUser = _jenkinsUsers.get(key);

		if (jenkinsUser != null) {
			return jenkinsUser;
		}

		jenkinsUser = new DefaultJenkinsUser(
			jenkinsMasterName, jenkinsUserName);

		_jenkinsUsers.put(key, jenkinsUser);

		return jenkinsUser;
	}

	private static final Map<String, JenkinsUser> _jenkinsUsers =
		new HashMap<>();

}