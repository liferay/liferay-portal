/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseJenkinsUser implements JenkinsUser {

	@Override
	public synchronized List<APIToken> getAPITokens() {
		if (_apiTokens != null) {
			return _apiTokens;
		}

		List<APIToken> apiTokens = new ArrayList<>();

		String jenkinsUserID = getJenkinsUserID();

		Set<String> itemFieldLabels = new HashSet<>();

		for (SecretsUtil.ItemField itemField : _getItem().getItemFields()) {
			String label = itemField.getLabel();

			if ((label == null) ||
				!label.startsWith(_API_TOKEN_JSON_LABEL_PREFIX) ||
				!itemFieldLabels.add(label)) {

				continue;
			}

			apiTokens.add(
				new APIToken(
					new JSONObject(itemField.getValue()), jenkinsUserID));
		}

		Collections.sort(apiTokens);

		_apiTokens = apiTokens;

		return _apiTokens;
	}

	@Override
	public JenkinsResultsParserUtil.HTTPAuthorization getHTTPAuthorization() {
		APIToken primaryAPIToken = getPrimaryAPIToken();

		if (primaryAPIToken == null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find API tokens for ", getJenkinsUserName(),
					" on ", getJenkinsMasterHostname()));
		}

		return primaryAPIToken.getHTTPAuthorization();
	}

	@Override
	public String getJenkinsMasterHostname() {
		return _jenkinsMasterHostname;
	}

	@Override
	public synchronized String getJenkinsUserID() {
		if (_jenkinsUserID != null) {
			return _jenkinsUserID;
		}

		SecretsUtil.ItemField itemField = _getItem().getItemField("user.id");

		if (itemField == null) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find item field ", _getItemReference(),
					"/user.id"));
		}

		_jenkinsUserID = itemField.getValue();

		return _jenkinsUserID;
	}

	@Override
	public String getJenkinsUserName() {
		return _jenkinsUserName;
	}

	@Override
	public APIToken getPrimaryAPIToken() {
		List<APIToken> apiTokens = getAPITokens();

		if (apiTokens.isEmpty()) {
			return null;
		}

		return apiTokens.get(apiTokens.size() - 1);
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.combine(
			_jenkinsUserName, "@", _jenkinsMasterHostname);
	}

	protected BaseJenkinsUser(
		String jenkinsMasterHostname, String jenkinsUserName) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsMasterHostname)) {
			throw new IllegalArgumentException(
				"Jenkins master hostname is null");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsUserName)) {
			throw new IllegalArgumentException("Jenkins user name is null");
		}

		_jenkinsMasterHostname = jenkinsMasterHostname;
		_jenkinsUserName = jenkinsUserName;
	}

	private synchronized SecretsUtil.Item _getItem() {
		if (_item != null) {
			return _item;
		}

		String itemReference = _getItemReference();

		_item = SecretsUtil.getItem(itemReference);

		if (_item == null) {
			throw new RuntimeException("Unable to find item " + itemReference);
		}

		return _item;
	}

	private synchronized String _getItemReference() {
		if (_itemReference != null) {
			return _itemReference;
		}

		String propertyName = JenkinsResultsParserUtil.combine(
			"jenkins.user.op.item[", getJenkinsUserName(), "]");

		String itemReference = null;

		try {
			itemReference = JenkinsResultsParserUtil.getBuildProperty(
				propertyName);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get property " + propertyName, ioException);
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(itemReference)) {
			throw new RuntimeException(
				"Unable to find property " + propertyName);
		}

		_itemReference = itemReference;

		return _itemReference;
	}

	private static final String _API_TOKEN_JSON_LABEL_PREFIX =
		"api.token.json.";

	private List<APIToken> _apiTokens;
	private SecretsUtil.Item _item;
	private String _itemReference;
	private final String _jenkinsMasterHostname;
	private String _jenkinsUserID;
	private final String _jenkinsUserName;

}