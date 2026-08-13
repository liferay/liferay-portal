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

		String jenkinsMasterUserConfigFilePath =
			_getJenkinsMasterUserConfigFilePath(jenkinsMaster, emailAddress);

		try {
			_backupJenkinsMasterUserConfigFile(
				jenkinsMaster, jenkinsMasterUserConfigFilePath);

			_copyUserConfigFileToJenkinsMaster(
				jenkinsMaster, userConfigFile, jenkinsMasterUserConfigFilePath);

			jenkinsMaster.reload();

			_validateAPITokens(jenkinsMaster, jenkinsUserID);
		}
		catch (Exception exception) {
			_restoreJenkinsMasterUserConfigFile(
				jenkinsMaster, jenkinsMasterUserConfigFilePath);

			throw exception;
		}
		finally {
			userConfigFile.delete();
		}
	}

	private static void _backupJenkinsMasterUserConfigFile(
		JenkinsMaster jenkinsMaster, String userConfigFilePath) {

		String backupUserConfigFilePath = userConfigFilePath + ".bak";

		jenkinsMaster.executeSSHCommand(
			JenkinsResultsParserUtil.combine(
				"cp ", userConfigFilePath, " ", backupUserConfigFilePath));
	}

	private static void _copyUserConfigFileToJenkinsMaster(
		JenkinsMaster jenkinsMaster, File userConfigFile,
		String userConfigFilePath) {

		jenkinsMaster.copyFile(userConfigFile, userConfigFilePath);
	}

	private static String _getJenkinsMasterUserConfigFilePath(
		JenkinsMaster jenkinsMaster, String emailAddress) {

		String output = jenkinsMaster.executeSSHCommand(
			JenkinsResultsParserUtil.combine(
				"grep -l '<id>", emailAddress, "</id>' ",
				_JENKINS_USERS_DIR_PATH, "/*/config.xml"));

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

	private static void _restoreJenkinsMasterUserConfigFile(
		JenkinsMaster jenkinsMaster, String userConfigFilePath) {

		String backupUserConfigFilePath = userConfigFilePath + ".bak";

		jenkinsMaster.executeSSHCommand(
			JenkinsResultsParserUtil.combine(
				"cp ", backupUserConfigFilePath, " ", userConfigFilePath));
	}

	private static void _validateAPITokens(
		JenkinsMaster jenkinsMaster, String jenkinsUserID) {

		List<JenkinsMaster.APIToken> apiTokens = jenkinsMaster.getAPITokens(
			jenkinsUserID);

		if ((apiTokens == null) || apiTokens.isEmpty()) {
			throw new RuntimeException(
				"Unable to find API tokens for " + jenkinsUserID);
		}

		String nodeNameURL = JenkinsResultsParserUtil.getLocalURL(
			jenkinsMaster.getURL() + "/api/json?tree=nodeName");

		for (JenkinsMaster.APIToken apiToken : apiTokens) {
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
		}
	}

	private static File _writeUserConfigFile(Element userConfigElement) {
		File userConfigFile = null;

		try {
			userConfigFile = File.createTempFile("user-config-", ".xml");

			JenkinsResultsParserUtil.write(
				userConfigFile,
				JenkinsResultsParserUtil.combine(
					_XML_DECLARATION, "\n\n",
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

	private static final String _XML_DECLARATION = "<?xml version=\"1.0\"?>";

}