/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

/**
 * @author Brian I. Kim
 * @author Danny Situ
 */
public class DSRequestRecipient {

	public DSRequestRecipient(Map<String, Serializable> values) {
		_emailAddress = MapUtil.getString(values, "emailAddress");
		_name = MapUtil.getString(values, "name");
		_sentDate = _toDate(values.get("sentDate"));
		_signingOrder = MapUtil.getInteger(values, "signingOrder");
		_status = MapUtil.getString(values, "requestRecipientStatus");
		_statusDate = _toDate(values.get("requestRecipientStatusDate"));
		_userId = MapUtil.getLong(values, "r_userToDSRequestRecipients_userId");
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getName() {
		return _name;
	}

	public Date getSentDate() {
		if (_sentDate == null) {
			return null;
		}

		return new Date(_sentDate.getTime());
	}

	public int getSigningOrder() {
		return _signingOrder;
	}

	public String getStatus() {
		return _status;
	}

	public Date getStatusDate() {
		if (_statusDate == null) {
			return null;
		}

		return new Date(_statusDate.getTime());
	}

	public long getUserId() {
		return _userId;
	}

	private Date _toDate(Serializable value) {
		if (value instanceof Date) {
			return (Date)value;
		}

		return null;
	}

	private final String _emailAddress;
	private final String _name;
	private final Date _sentDate;
	private final int _signingOrder;
	private final String _status;
	private final Date _statusDate;
	private final long _userId;

}