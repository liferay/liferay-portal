/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.HelpCenterConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.HttpURLConnection;

import java.util.Map;

/**
 * @author Zsolt Balogh
 */
public class HelpCenterUtil {

	public static void addAttachmentComment(
			String fileName, PatcherBuild patcherBuild)
		throws Exception {

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, patcherBuild.getCompanyId());

		String downloadURL = PatcherBuildUtil.getDownloadURL(patcherBuild);

		String credentials =
			patcherConfiguration.jiraServiceManagementUserEmailAddress() +
				StringPool.COLON +
					patcherConfiguration.jiraServiceManagementUserToken();

		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);
		options.addHeader(HttpHeaders.USER_AGENT, _PATCHER_USER_AGENT);
		options.addHeader(
			"Authorization", "Basic " + Base64.encode(credentials.getBytes()));
		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(
			_buildCommentJSONObject(
				downloadURL, fileName
			).toString(),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(
			StringBundler.concat(
				patcherConfiguration.jiraAPIURL(), "/issue/",
				patcherBuild.getSupportTicket(), "/comment"));
		options.setPost(true);

		String responseString = _sendRequest(options);

		if (Validator.isNull(responseString)) {
			throw new PortalException("failed-to-add-jira-comment");
		}
	}

	public static long fetchAccountEntryId(
			String accountEntryCode, long companyId)
		throws Exception {

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, companyId);

		Http.Options options = _initOptions(
			true,
			HashMapBuilder.put(
				HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON
			).build(),
			patcherConfiguration);

		options.setLocation(
			StringBundler.concat(
				patcherConfiguration.supportLiferayURL(),
				StringPool.FORWARD_SLASH,
				patcherConfiguration.supportLiferayAccountSearchAPIEndpoint(),
				StringPool.APOSTROPHE + accountEntryCode +
					StringPool.APOSTROPHE));
		options.setPost(false);

		String responseString = _sendRequest(options);

		if (Validator.isNull(responseString)) {
			return 0;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			responseString);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		if (itemsJSONArray.length() == 0) {
			return 0;
		}

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		return itemJSONObject.getLong("id");
	}

	protected static String getAuthenticationToken(
			PatcherConfiguration patcherConfiguration)
		throws Exception {

		if (System.currentTimeMillis() < _tokenExpirationTime) {
			return _accessToken;
		}

		Http.Options options = _initOptions(
			false,
			HashMapBuilder.put(
				"Content-Type", ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED
			).build(),
			patcherConfiguration);

		options.addPart(
			"client_id", patcherConfiguration.supportLiferayAPIClientId());
		options.addPart(
			"client_secret",
			patcherConfiguration.supportLiferayAPIClientSecret());
		options.addPart("grant_type", "client_credentials");
		options.setLocation(
			patcherConfiguration.supportLiferayURL() + "/o/oauth2/token");
		options.setPost(true);

		String responseString = _sendRequest(options);

		if (Validator.isNull(responseString)) {
			throw new PortalException(
				"failed-to-connect-to-the-authentication-service");
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			responseString);

		String accessToken = jsonObject.getString(
			"access_token", StringPool.BLANK);

		long expiresIn = jsonObject.getLong("expires_in", 0);

		_accessToken = accessToken;
		_tokenExpirationTime =
			System.currentTimeMillis() + ((expiresIn - 60) * 1000);

		return accessToken;
	}

	private static JSONObject _buildCommentJSONObject(
		String downloadURL, String fileName) {

		return JSONUtil.put(
			"body",
			JSONUtil.put(
				"content",
				JSONUtil.putAll(
					JSONUtil.put(
						"content",
						JSONUtil.putAll(
							JSONUtil.put(
								"text",
								HelpCenterConstants.HELP_CENTER_DOWNLOAD_COMMENT
							).put(
								"type", "text"
							),
							JSONUtil.put(
								"marks",
								JSONUtil.putAll(
									JSONUtil.put(
										"attrs",
										JSONUtil.put("href", downloadURL)
									).put(
										"type", "link"
									))
							).put(
								"text", fileName
							).put(
								"type", "text"
							))
					).put(
						"type", "paragraph"
					))
			).put(
				"type", "doc"
			).put(
				"version", 1
			));
	}

	private static Http.Options _initOptions(
			boolean authenticate, Map<String, String> headers,
			PatcherConfiguration patcherConfiguration)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.USER_AGENT, _PATCHER_USER_AGENT);

		if (authenticate) {
			options.addHeader(
				"Authorization",
				"Bearer " + getAuthenticationToken(patcherConfiguration));
		}

		for (Map.Entry<String, String> header : headers.entrySet()) {
			options.addHeader(header.getKey(), header.getValue());
		}

		return options;
	}

	private static String _sendRequest(Http.Options options) throws Exception {
		String responseString = HttpUtil.URLtoString(options);

		Http.Response response = options.getResponse();

		int responseCode = response.getResponseCode();

		if ((responseCode != HttpURLConnection.HTTP_CREATED) &&
			(responseCode != HttpURLConnection.HTTP_OK)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Response code ", responseCode, ": ", responseString));
			}

			return null;
		}

		return responseString;
	}

	private static final String _PATCHER_USER_AGENT = "OSB Patcher Portal/7.4";

	private static final Log _log = LogFactoryUtil.getLog(HelpCenterUtil.class);

	private static String _accessToken;
	private static long _tokenExpirationTime;

}