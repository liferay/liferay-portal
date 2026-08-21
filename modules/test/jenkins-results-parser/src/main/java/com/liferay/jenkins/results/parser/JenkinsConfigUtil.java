/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * @author Michael Hashimoto
 */
public class JenkinsConfigUtil {

	public static void updateLiveJenkinsUser(
		JenkinsMaster jenkinsMaster, String jenkinsUserName) {

		Element userConfigElement = _getUserConfigElement(
			jenkinsMaster.getName(), jenkinsUserName);

		if (userConfigElement == null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find user config for ", jenkinsUserName, " on ",
					jenkinsMaster.getName()));
		}

		String emailAddress = userConfigElement.elementText("id");

		if (JenkinsResultsParserUtil.isNullOrEmpty(emailAddress)) {
			throw new RuntimeException(
				"Unable to find email address for " + jenkinsUserName);
		}

		File candidateUserConfigFile = _createCandidateUserConfigFile(
			userConfigElement);

		String liveUserConfigFilePath = _getLiveUserConfigFilePath(
			jenkinsMaster, emailAddress);

		File backupUserConfigFile = null;

		try {
			backupUserConfigFile = _createBackupUserConfigFile(
				jenkinsMaster, liveUserConfigFilePath);

			jenkinsMaster.copyFileToJenkinsMaster(
				candidateUserConfigFile, liveUserConfigFilePath);

			jenkinsMaster.reloadJenkinsUser(jenkinsUserName);

			_validateAPITokens(jenkinsMaster, jenkinsUserName);

			JenkinsResultsParserUtil.delete(backupUserConfigFile);
		}
		catch (Exception exception1) {
			if (backupUserConfigFile != null) {
				try {
					jenkinsMaster.copyFileToJenkinsMaster(
						backupUserConfigFile, liveUserConfigFilePath);

					jenkinsMaster.reloadJenkinsUser(jenkinsUserName);

					JenkinsResultsParserUtil.delete(backupUserConfigFile);
				}
				catch (Exception exception2) {
					exception1.addSuppressed(exception2);
				}
			}

			throw exception1;
		}
		finally {
			JenkinsResultsParserUtil.delete(candidateUserConfigFile);
		}
	}

	public static void updateLocalJenkinsUser(
		String jenkinsMasterName, String jenkinsUserName) {

		Element userConfigElement = _getUserConfigElement(
			jenkinsMasterName, jenkinsUserName);

		if (userConfigElement == null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find user config for ", jenkinsUserName, " on ",
					jenkinsMasterName));
		}

		String emailAddress = userConfigElement.elementText("id");

		if (JenkinsResultsParserUtil.isNullOrEmpty(emailAddress)) {
			throw new RuntimeException(
				"Unable to find email address for " + jenkinsUserName);
		}

		File localUserConfigFile = _getLocalUserConfigFile(emailAddress);

		String localUserConfigFilePath =
			JenkinsResultsParserUtil.getCanonicalPath(localUserConfigFile);

		String userConfigContent = null;

		try {
			userConfigContent = JenkinsResultsParserUtil.combine(
				"<?xml version=\"1.0\"?>\n\n",
				Dom4JUtil.format(userConfigElement));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to format the user config for " + jenkinsUserName,
				ioException);
		}

		if (userConfigContent.contains("op://")) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to resolve secrets in the user config for ",
					jenkinsUserName, " on ", jenkinsMasterName));
		}

		File backupUserConfigFile = _createLocalBackupUserConfigFile(
			localUserConfigFile);

		try {
			JenkinsResultsParserUtil.write(
				localUserConfigFile, userConfigContent);

			if (backupUserConfigFile != null) {
				JenkinsResultsParserUtil.delete(backupUserConfigFile);
			}
		}
		catch (Exception exception1) {
			if (backupUserConfigFile != null) {
				try {
					JenkinsResultsParserUtil.copy(
						backupUserConfigFile, localUserConfigFile);

					JenkinsResultsParserUtil.delete(backupUserConfigFile);
				}
				catch (Exception exception2) {
					exception1.addSuppressed(exception2);
				}
			}

			throw new RuntimeException(
				"Unable to write " + localUserConfigFilePath, exception1);
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Successfully updated ", localUserConfigFilePath, " for ",
				jenkinsUserName));
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

	private static File _createCandidateUserConfigFile(
		Element userConfigElement) {

		File candidateUserConfigFile = null;

		try {
			candidateUserConfigFile = File.createTempFile(
				"user-config-", ".xml");

			JenkinsResultsParserUtil.write(
				candidateUserConfigFile,
				JenkinsResultsParserUtil.combine(
					"<?xml version=\"1.0\"?>\n\n",
					Dom4JUtil.format(userConfigElement)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to write candidate user config file", ioException);
		}

		return candidateUserConfigFile;
	}

	private static File _createLocalBackupUserConfigFile(
		File localUserConfigFile) {

		if (!localUserConfigFile.exists()) {
			return null;
		}

		File backupUserConfigFile = null;

		try {
			backupUserConfigFile = File.createTempFile(
				"backup-user-config-", ".xml");

			JenkinsResultsParserUtil.copy(
				localUserConfigFile, backupUserConfigFile);

			return backupUserConfigFile;
		}
		catch (Exception exception) {
			if (backupUserConfigFile != null) {
				backupUserConfigFile.delete();
			}

			String localUserConfigFilePath =
				JenkinsResultsParserUtil.getCanonicalPath(localUserConfigFile);

			throw new RuntimeException(
				"Unable to back up " + localUserConfigFilePath, exception);
		}
	}

	private static File _getJenkinsUsersDir() {
		String jenkinsHome = System.getenv("JENKINS_HOME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsHome)) {
			return new File(_JENKINS_USERS_DIR_PATH);
		}

		return new File(jenkinsHome, "users");
	}

	private static String _getLiveUserConfigFilePath(
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

	private static File _getLocalUserConfigFile(String emailAddress) {
		File jenkinsUsersDir = _getJenkinsUsersDir();

		String jenkinsUsersDirPath = JenkinsResultsParserUtil.getCanonicalPath(
			jenkinsUsersDir);

		File[] userDirs = jenkinsUsersDir.listFiles(File::isDirectory);

		if (userDirs == null) {
			throw new RuntimeException("Unable to find " + jenkinsUsersDirPath);
		}

		String idElementText = JenkinsResultsParserUtil.combine(
			"<id>", emailAddress, "</id>");

		for (File userDir : userDirs) {
			File userConfigFile = new File(userDir, "config.xml");

			if (!userConfigFile.exists()) {
				continue;
			}

			String userConfigFilePath =
				JenkinsResultsParserUtil.getCanonicalPath(userConfigFile);

			String userConfigContent = null;

			try {
				userConfigContent = JenkinsResultsParserUtil.read(
					userConfigFile);
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to read " + userConfigFilePath, ioException);
			}

			if (userConfigContent.contains(idElementText)) {
				return userConfigFile;
			}
		}

		throw new RuntimeException(
			JenkinsResultsParserUtil.combine(
				"Unable to find user config for ", emailAddress, " in ",
				jenkinsUsersDirPath));
	}

	private static Element _getUserConfigElement(
		String jenkinsMasterName, String jenkinsUserName) {

		File userConfigFile = _getUserConfigFile(
			jenkinsMasterName, jenkinsUserName);

		if (userConfigFile == null) {
			return null;
		}

		String userConfigFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			userConfigFile);

		String userConfigContent = null;

		try {
			userConfigContent = SecretsUtil.replaceSecrets(
				JenkinsResultsParserUtil.read(userConfigFile));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read " + userConfigFilePath, ioException);
		}

		Document document = null;

		try {
			document = Dom4JUtil.parse(userConfigContent);
		}
		catch (DocumentException documentException) {
			throw new RuntimeException(
				"Unable to parse " + userConfigFilePath, documentException);
		}

		try {
			_setAPITokenElements(document, jenkinsMasterName, jenkinsUserName);
		}
		catch (RuntimeException runtimeException) {
			throw new RuntimeException(
				"Unable to set API tokens in " + userConfigFilePath,
				runtimeException);
		}

		return document.getRootElement();
	}

	private static File _getUserConfigFile(
		String jenkinsMasterName, String jenkinsUserName) {

		File jenkinsGitRepositoryDir = new File(
			JenkinsResultsParserUtil.getBaseGitRepositoryDir(),
			JenkinsResultsParserUtil.JENKINS_REPOSITORY_NAME);

		for (String mastersDirName : _MASTERS_DIR_NAMES) {
			File userConfigFile = new File(
				jenkinsGitRepositoryDir,
				JenkinsResultsParserUtil.combine(
					mastersDirName, "/", jenkinsMasterName, "/users/",
					jenkinsUserName, "/config.xml"));

			if (userConfigFile.exists()) {
				return userConfigFile;
			}
		}

		return null;
	}

	private static void _setAPITokenElements(
		Document document, String jenkinsMasterName, String jenkinsUserName) {

		JenkinsUser jenkinsUser = JenkinsUserFactory.getJenkinsUser(
			jenkinsMasterName, jenkinsUserName);

		List<JenkinsUser.APIToken> apiTokens = jenkinsUser.getAPITokens();

		if (apiTokens.isEmpty()) {
			return;
		}

		Node tokenListNode = Dom4JUtil.getNodeByXPath(
			document, _TOKEN_LIST_XPATH);

		if (!(tokenListNode instanceof Element)) {
			throw new RuntimeException("Unable to find " + _TOKEN_LIST_XPATH);
		}

		Element tokenListElement = (Element)tokenListNode;

		tokenListElement.clearContent();

		for (JenkinsUser.APIToken apiToken : apiTokens) {
			Element hashedTokenElement = Dom4JUtil.getNewElement(
				"jenkins.security.apitoken.ApiTokenStore_-HashedToken",
				tokenListElement);

			Dom4JUtil.getNewElement(
				"creationDate", hashedTokenElement,
				apiToken.getCreationDateString());
			Dom4JUtil.getNewElement(
				"name", hashedTokenElement, apiToken.getName());
			Dom4JUtil.getNewElement(
				"uuid", hashedTokenElement, apiToken.getUUID());

			Element valueElement = Dom4JUtil.getNewElement(
				"value", hashedTokenElement);

			Dom4JUtil.getNewElement("hash", valueElement, apiToken.getHash());
			Dom4JUtil.getNewElement(
				"version", valueElement, apiToken.getVersion());
		}
	}

	private static void _validateAPITokens(
		JenkinsMaster jenkinsMaster, String jenkinsUserName) {

		List<JenkinsUser.APIToken> apiTokens = jenkinsMaster.getAPITokens(
			jenkinsUserName);

		if ((apiTokens == null) || apiTokens.isEmpty()) {
			throw new RuntimeException(
				"Unable to find API tokens for " + jenkinsUserName);
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
						"Unable to validate API token \"", apiToken.getName(),
						"\" for ", jenkinsUserName, " against ", nodeNameURL),
					ioException);
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Successfully validated API token \"", apiToken.getName(),
					"\" for ", jenkinsUserName));
		}
	}

	private static final String _JENKINS_USERS_DIR_PATH =
		"/opt/java/jenkins/users";

	private static final String[] _MASTERS_DIR_NAMES = {
		"masters/generated", "masters/static"
	};

	private static final String _TOKEN_LIST_XPATH =
		"/user/properties/jenkins.security.ApiTokenProperty/tokenStore" +
			"/tokenList";

}