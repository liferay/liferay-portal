/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.main;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.liferay.osb.faro.util.DateUtil;

import java.util.Date;

/**
 * @author Marcellus Tavares
 */
public class TokenDisplay {

	public TokenDisplay(
		Date createDate, Date expirationDate, Date lastAccessDate,
		String token) {

		_createDate = new Date(createDate.getTime());
		_expirationDate = new Date(expirationDate.getTime());
		_lastAccessDate = new Date(lastAccessDate.getTime());
		_token = token;
	}

	@JsonFormat(
		pattern = DateUtil.PATTERN_DATE_TIME, shape = JsonFormat.Shape.STRING,
		timezone = "UTC"
	)
	public Date getCreateDate() {
		return new Date(_createDate.getTime());
	}

	@JsonFormat(
		pattern = DateUtil.PATTERN_DATE_TIME, shape = JsonFormat.Shape.STRING,
		timezone = "UTC"
	)
	public Date getExpirationDate() {
		return new Date(_expirationDate.getTime());
	}

	@JsonFormat(
		pattern = DateUtil.PATTERN_DATE_TIME, shape = JsonFormat.Shape.STRING,
		timezone = "UTC"
	)
	public Date getLastAccessDate() {
		return new Date(_lastAccessDate.getTime());
	}

	public String getToken() {
		return _token;
	}

	private final Date _createDate;
	private final Date _expirationDate;
	private final Date _lastAccessDate;
	private final String _token;

}