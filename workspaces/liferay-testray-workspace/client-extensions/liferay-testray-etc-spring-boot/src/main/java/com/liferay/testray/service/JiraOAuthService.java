/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.service;

import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.testray.model.JiraOAuthAccessToken;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author Nilton Vieira
 */
@Component
public class JiraOAuthService extends BaseService {

	public void generateToken(String code, String type, String redirectURI) {
		try {
			JSONObject jsonObject = new JSONObject(
				post(
					"",
					new JSONObject(
					).put(
						"client_id", _liferayTestrayJiraOAuthClientId
					).put(
						"client_secret", _liferayTestrayJiraOAuthClientSecret
					).put(
						type, code
					).put(
						"grant_type",
						StringUtil.equals(type, "refresh_token") ? type :
							"authorization_code"
					).put(
						"redirect_uri", redirectURI
					).toString(),
					URI.create("https://auth.atlassian.com/oauth/token")));

			_jiraOAuthAccessToken = new JiraOAuthAccessToken(
				jsonObject.getString("access_token"),
				jsonObject.getString("refresh_token"));
		}
		catch (WebClientResponseException webClientResponseException) {
			_log.error("Unable to get OAuth 2 access token");

			throw webClientResponseException;
		}
	}

	public String getAuthorization() {
		if (_jiraOAuthAccessToken == null) {
			_log.error("Unable to get OAuth 2 authorized client");

			return "";
		}

		if (!_jiraOAuthAccessToken.isValid()) {
			generateToken(
				_jiraOAuthAccessToken.getRefreshToken(), "refresh_token", "");
		}

		return "Bearer " + _jiraOAuthAccessToken.getAccessToken();
	}

	private static final Log _log = LogFactory.getLog(JiraOAuthService.class);

	private JiraOAuthAccessToken _jiraOAuthAccessToken;

	@Value("${liferay.testray.jira.oauth.client.id}")
	private String _liferayTestrayJiraOAuthClientId;

	@Value("${liferay.testray.jira.oauth.client.secret}")
	private String _liferayTestrayJiraOAuthClientSecret;

}