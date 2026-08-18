/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public class JenkinsConfigUtil {

	public static void updateJenkinsMasterUserConfig(
		JenkinsMaster jenkinsMaster, String jenkinsUserID,
		JenkinsWorkspaceGitRepository jenkinsWorkspaceGitRepository) {

		Element userConfigElement =
			jenkinsWorkspaceGitRepository.getUserConfigElement(
				jenkinsMaster, jenkinsUserID);

		if (userConfigElement == null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find user config for ", jenkinsUserID, " in ",
					jenkinsWorkspaceGitRepository.getName()));
		}

		String emailAddress = userConfigElement.elementText("id");

		if (JenkinsResultsParserUtil.isNullOrEmpty(emailAddress)) {
			throw new RuntimeException(
				"Unable to find email address for " + jenkinsUserID);
		}

		File userConfigFile = _writeUserConfigFile(userConfigElement);

		String userConfigFilePath = _getUserConfigFilePath(
			jenkinsMaster, emailAddress);

		File backupUserConfigFile = null;

		try {
			backupUserConfigFile = _createBackupUserConfigFile(
				jenkinsMaster, userConfigFilePath);

			jenkinsMaster.copyFileToJenkinsMaster(
				userConfigFile, userConfigFilePath);

			jenkinsMaster.reloadUser(jenkinsUserID);

			_validateAPITokens(jenkinsMaster, jenkinsUserID);

			JenkinsResultsParserUtil.delete(backupUserConfigFile);
		}
		catch (Exception exception1) {
			if (backupUserConfigFile != null) {
				try {
					jenkinsMaster.copyFileToJenkinsMaster(
						backupUserConfigFile, userConfigFilePath);

					jenkinsMaster.reloadUser(jenkinsUserID);

					JenkinsResultsParserUtil.delete(backupUserConfigFile);
				}
				catch (Exception exception2) {
					exception1.addSuppressed(exception2);
				}
			}

			throw exception1;
		}
		finally {
			userConfigFile.delete();
		}
	}

	private static File _createBackupUserConfigFile(
		JenkinsMaster jenkinsMaster, String userConfigFilePath) {

		File backupUserConfigFile = null;

		try {
			backupUserConfigFile = File.createTempFile(
				"backup-user-config-", ".xml");

			jenkinsMaster.copyFileFromJenkinsMaster(
				userConfigFilePath, backupUserConfigFile);

			return backupUserConfigFile;
		}
		catch (Exception exception) {
			if (backupUserConfigFile != null) {
				backupUserConfigFile.delete();
			}

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to back up ", jenkinsMaster.getName(), ":",
					userConfigFilePath),
				exception);
		}
	}

	private static String _getUserConfigFilePath(
		JenkinsMaster jenkinsMaster, String emailAddress) {

		String output = jenkinsMaster.executeBashCommand(
			JenkinsResultsParserUtil.combine(
				"grep --files-with-matches '<id>", emailAddress, "</id>' ",
				_JENKINS_USERS_DIR_PATH, "/*/config.xml || true"));

		for (String line : output.split("\n")) {
			line = line.trim();

			if (!line.isEmpty()) {
				return line;
			}
		}

		throw new RuntimeException(
			JenkinsResultsParserUtil.combine(
				"Unable to find user config for ", emailAddress, " in ",
				jenkinsMaster.getName(), ":", _JENKINS_USERS_DIR_PATH));
	}

	private static void _validateAPITokens(
		JenkinsMaster jenkinsMaster, String jenkinsUserID) {

		List<JenkinsUser.APIToken> apiTokens = jenkinsMaster.getAPITokens(
			jenkinsUserID);

		if ((apiTokens == null) || apiTokens.isEmpty()) {
			throw new RuntimeException(
				"Unable to find API tokens for " + jenkinsUserID);
		}

		String nodeNameURL = JenkinsResultsParserUtil.getLocalURL(
			jenkinsMaster.getURL() + "/api/json?tree=nodeName");

		for (JenkinsUser.APIToken apiToken : apiTokens) {
			try {
				JenkinsResultsParserUtil.toJSONObject(
					nodeNameURL, false, apiToken.getHTTPAuthorization());
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to validate API token ", apiToken.getName(),
						" for ", jenkinsUserID, " against ", nodeNameURL),
					ioException);
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Successfully validated API token ", apiToken.getName(),
					" for ", jenkinsUserID));
		}
	}

	private static File _writeUserConfigFile(Element userConfigElement) {
		File userConfigFile = null;

		try {
			userConfigFile = File.createTempFile("user-config-", ".xml");

			JenkinsResultsParserUtil.write(
				userConfigFile,
				JenkinsResultsParserUtil.combine(
					"<?xml version=\"1.0\"?>\n\n",
					Dom4JUtil.format(userConfigElement)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to write user config file", ioException);
		}

		return userConfigFile;
	}

	private static final String _JENKINS_USERS_DIR_PATH =
		"/opt/java/jenkins/users";

}