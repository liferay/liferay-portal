/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.net.HttpURLConnection;

/**
 * @author Pedro Malta
 */
public class JiraUtil {

	public static JSONObject getIssueJSONObject(
			PatcherConfiguration patcherConfiguration, String issueKey)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);

		String credentials =
			patcherConfiguration.jiraEmailAddress() + StringPool.COLON +
				patcherConfiguration.jiraAPIToken();

		options.addHeader(
			"Authorization", "Basic " + Base64.encode(credentials.getBytes()));

		options.setLocation(_buildIssueURL(patcherConfiguration, issueKey));
		options.setPost(false);

		String responseString = HttpUtil.URLtoString(options);

		Http.Response response = options.getResponse();

		int responseCode = response.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			_log.error(
				StringBundler.concat(
					"Response code ", responseCode, ": ", responseString));

			throw new PortalException(
				LanguageUtil.format(
					LocaleUtil.getMostRelevantLocale(),
					"unable-to-fetch-jira-issue-x", issueKey));
		}

		return JSONFactoryUtil.createJSONObject(responseString);
	}

	private static String _buildIssueURL(
		PatcherConfiguration patcherConfiguration, String issueKey) {

		return StringBundler.concat(
			patcherConfiguration.jiraAPIURL(), "/issue/", issueKey,
			"?fields=issuelinks");
	}

	private static final Log _log = LogFactoryUtil.getLog(JiraUtil.class);

}