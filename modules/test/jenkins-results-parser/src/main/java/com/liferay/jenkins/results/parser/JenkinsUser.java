/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface JenkinsUser {

	public List<APIToken> getAPITokens();

	public JenkinsResultsParserUtil.HTTPAuthorization getHTTPAuthorization();

	public String getJenkinsMasterName();

	public String getJenkinsUserID();

	public String getJenkinsUserName();

	public APIToken getPrimaryAPIToken();

	public static class APIToken implements Comparable<APIToken> {

		@Override
		public int compareTo(APIToken apiToken) {
			return _creationDate.compareTo(apiToken.getCreationDate());
		}

		public Date getCreationDate() {
			return _creationDate;
		}

		public String getCreationDateString() {
			return _creationDateString;
		}

		public String getHash() {
			return _hash;
		}

		public JenkinsResultsParserUtil.HTTPAuthorization
			getHTTPAuthorization() {

			return new JenkinsResultsParserUtil.BasicHTTPAuthorization(
				getToken(), getJenkinsUserID());
		}

		public String getJenkinsUserID() {
			return _jenkinsUserID;
		}

		public String getName() {
			return _name;
		}

		public String getToken() {
			return _token;
		}

		public String getUUID() {
			return _uuid;
		}

		public String getVersion() {
			return _version;
		}

		protected APIToken(JSONObject jsonObject, String jenkinsUserID) {
			_creationDateString = jsonObject.getString(
				"api.token.creation.date");
			_hash = jsonObject.getString("api.token.hash");
			_name = jsonObject.getString("api.token.name");
			_token = jsonObject.getString("api.token");
			_uuid = jsonObject.getString("api.token.uuid");
			_version = jsonObject.getString("api.token.version");

			_jenkinsUserID = jenkinsUserID;

			_creationDate = _getCreationDate(_creationDateString);
		}

		private Date _getCreationDate(String creationDateString) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				_CREATION_DATE_FORMAT);

			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			try {
				return simpleDateFormat.parse(creationDateString);
			}
			catch (ParseException parseException) {
				throw new RuntimeException(
					"Unable to parse creation date " + creationDateString,
					parseException);
			}
		}

		private static final String _CREATION_DATE_FORMAT =
			"yyyy-MM-dd HH:mm:ss.SSS z";

		private final Date _creationDate;
		private final String _creationDateString;
		private final String _hash;
		private final String _jenkinsUserID;
		private final String _name;
		private final String _token;
		private final String _uuid;
		private final String _version;

	}

}