/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseJenkinsUser implements JenkinsUser {

	@Override
	public synchronized List<APIToken> getAPITokens() {
		if (_apiTokens != null) {
			return _apiTokens;
		}

		List<APIToken> apiTokens = new ArrayList<>(loadAPITokens());

		Collections.sort(apiTokens);

		_apiTokens = Collections.unmodifiableList(apiTokens);

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

	protected abstract List<APIToken> loadAPITokens();

	private List<APIToken> _apiTokens;
	private final String _jenkinsMasterHostname;
	private final String _jenkinsUserName;

}