/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JenkinsWorkspaceGitRepository extends BaseWorkspaceGitRepository {

	public Properties getCommandsBuildProperties() {
		if (_commandsBuildProperties != null) {
			return _commandsBuildProperties;
		}

		File buildPropertiesFile = new File(
			getDirectory(), "commands/build.properties");

		String buildPropertiesFilePath =
			JenkinsResultsParserUtil.getCanonicalPath(buildPropertiesFile);

		Properties commandsBuildProperties = null;

		try {
			commandsBuildProperties = new EnvironmentBuildProperties(
				buildPropertiesFile);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load " + buildPropertiesFilePath, ioException);
		}

		if (commandsBuildProperties.isEmpty()) {
			throw new RuntimeException(
				"Unable to find build properties for " +
					buildPropertiesFilePath);
		}

		_commandsBuildProperties = commandsBuildProperties;

		return _commandsBuildProperties;
	}

	public Element getUserConfigElement(
		JenkinsMaster jenkinsMaster, String jenkinsUserID) {

		String userConfigFilePath = _getUserConfigFilePath(
			jenkinsMaster.getName(), jenkinsUserID);

		if (userConfigFilePath == null) {
			return null;
		}

		String userConfigContent = SecretsUtil.replaceSecrets(
			getFileContent(userConfigFilePath));

		Document document = null;

		try {
			document = Dom4JUtil.parse(userConfigContent);
		}
		catch (DocumentException documentException) {
			throw new RuntimeException(
				"Unable to parse " + userConfigFilePath, documentException);
		}

		try {
			_setAPITokenElements(document, jenkinsMaster, jenkinsUserID);
		}
		catch (RuntimeException runtimeException) {
			throw new RuntimeException(
				"Unable to set API tokens in " + userConfigFilePath,
				runtimeException);
		}

		return document.getRootElement();
	}

	protected JenkinsWorkspaceGitRepository(JSONObject jsonObject) {
		super(jsonObject);
	}

	protected JenkinsWorkspaceGitRepository(
		PullRequest pullRequest, String upstreamBranchName) {

		super(pullRequest, upstreamBranchName);
	}

	protected JenkinsWorkspaceGitRepository(
		RemoteGitRef remoteGitRef, String upstreamBranchName) {

		super(remoteGitRef, upstreamBranchName);
	}

	private String _getUserConfigFilePath(
		String masterName, String jenkinsUserID) {

		for (String mastersDirName : _MASTERS_DIR_NAMES) {
			String userConfigFilePath = JenkinsResultsParserUtil.combine(
				mastersDirName, "/", masterName, "/users/", jenkinsUserID,
				"/config.xml");

			File userConfigFile = new File(getDirectory(), userConfigFilePath);

			if (userConfigFile.exists()) {
				return userConfigFilePath;
			}
		}

		return null;
	}

	private void _setAPITokenElements(
		Document document, JenkinsMaster jenkinsMaster, String jenkinsUserID) {

		List<JenkinsMaster.APIToken> apiTokens = jenkinsMaster.getAPITokens(
			jenkinsUserID);

		if ((apiTokens == null) || apiTokens.isEmpty()) {
			return;
		}

		Node tokenListNode = Dom4JUtil.getNodeByXPath(
			document, _TOKEN_LIST_XPATH);

		if (!(tokenListNode instanceof Element)) {
			throw new RuntimeException("Unable to find " + _TOKEN_LIST_XPATH);
		}

		Element tokenListElement = (Element)tokenListNode;

		tokenListElement.clearContent();

		for (JenkinsMaster.APIToken apiToken : apiTokens) {
			Element hashedTokenElement = Dom4JUtil.getNewElement(
				"jenkins.security.apitoken.ApiTokenStore_-HashedToken",
				tokenListElement);

			Dom4JUtil.getNewElement(
				"creationDate", hashedTokenElement, apiToken.getCreationDate());
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

	private static final String[] _MASTERS_DIR_NAMES = {
		"masters/generated", "masters/static"
	};

	private static final String _TOKEN_LIST_XPATH =
		"/user/properties/jenkins.security.ApiTokenProperty/tokenStore" +
			"/tokenList";

	private Properties _commandsBuildProperties;

}