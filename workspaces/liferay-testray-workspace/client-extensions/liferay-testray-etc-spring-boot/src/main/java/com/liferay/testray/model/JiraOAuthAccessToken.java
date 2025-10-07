/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.model;

import java.time.OffsetDateTime;

/**
 * @author Nilton Vieira
 */
public class JiraOAuthAccessToken {

	public JiraOAuthAccessToken(String accessToken, String refreshToken) {
		_accessToken = accessToken;
		_refreshToken = refreshToken;

		_dateCreated = OffsetDateTime.now();
	}

	public String getAccessToken() {
		return _accessToken;
	}

	public OffsetDateTime getDateCreated() {
		return _dateCreated;
	}

	public String getRefreshToken() {
		return _refreshToken;
	}

	public boolean isValid() {
		return _dateCreated.plusMinutes(
			55
		).isAfter(
			OffsetDateTime.now()
		);
	}

	public void setAccessToken(String accessToken) {
		_accessToken = accessToken;
	}

	public void setDateCreated(OffsetDateTime dateCreated) {
		_dateCreated = dateCreated;
	}

	public void setRefreshToken(String refreshToken) {
		_refreshToken = refreshToken;
	}

	private String _accessToken;
	private OffsetDateTime _dateCreated;
	private String _refreshToken;

}