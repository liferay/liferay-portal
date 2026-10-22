/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brian I. Kim
 * @author Danny Situ
 */
public class DSRequest {

	public DSRequest(
		long companyId, Date createDate, long dsRequestId,
		List<DSRequestRecipient> dsRequestRecipients,
		String externalReferenceCode, List<Long> fileEntryIds, long userId,
		String userName, Map<String, Serializable> values) {

		_companyId = companyId;
		_createDate = createDate;
		_dsRequestId = dsRequestId;
		_dsRequestRecipients = dsRequestRecipients;
		_externalReferenceCode = externalReferenceCode;
		_fileEntryIds = fileEntryIds;
		_userId = userId;
		_userName = userName;

		_emailMessage = MapUtil.getString(values, "emailMessage");
		_emailSubject = MapUtil.getString(values, "emailSubject");
		_expirationDate = _toDate(values.get("requestExpirationDate"));
		_siteGroupId = MapUtil.getLong(values, "siteGroupId");
		_status = MapUtil.getString(values, "requestStatus");
		_statusDate = _toDate(values.get("requestStatusDate"));
	}

	public DSRequestRecipient fetchSentDSRequestRecipient(long userId) {
		for (DSRequestRecipient dsRequestRecipient : getDSRequestRecipients()) {
			if ((dsRequestRecipient.getUserId() == userId) &&
				Objects.equals(
					DSRequestConstants.STATUS_SENT,
					dsRequestRecipient.getStatus())) {

				return dsRequestRecipient;
			}
		}

		return null;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public Date getCreateDate() {
		if (_createDate == null) {
			return null;
		}

		return new Date(_createDate.getTime());
	}

	public long getDSRequestId() {
		return _dsRequestId;
	}

	public List<DSRequestRecipient> getDSRequestRecipients() {
		if (_dsRequestRecipients == null) {
			return Collections.emptyList();
		}

		return _dsRequestRecipients;
	}

	public String getEmailMessage() {
		return _emailMessage;
	}

	public String getEmailSubject() {
		return _emailSubject;
	}

	public Date getExpirationDate() {
		if (_expirationDate == null) {
			return null;
		}

		return new Date(_expirationDate.getTime());
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public List<Long> getFileEntryIds() {
		if (_fileEntryIds == null) {
			return Collections.emptyList();
		}

		return _fileEntryIds;
	}

	public long getSiteGroupId() {
		return _siteGroupId;
	}

	public String getStatus() {
		if ((_expirationDate != null) && _expirationDate.before(new Date()) &&
			isSyncable()) {

			return DSRequestConstants.STATUS_EXPIRED;
		}

		return _status;
	}

	public Date getStatusDate() {
		Date statusDate = _statusDate;

		if (!Objects.equals(_status, getStatus())) {
			statusDate = _expirationDate;
		}

		if (statusDate == null) {
			return null;
		}

		return new Date(statusDate.getTime());
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserName() {
		return _userName;
	}

	public boolean isReplaceable() {
		String status = getStatus();

		if (ArrayUtil.contains(
				DSRequestConstants.STATUSES_REQUEST_TERMINAL, status) &&
			!Objects.equals(status, DSRequestConstants.STATUS_COMPLETED)) {

			return true;
		}

		return false;
	}

	public boolean isSignatureRequired(long userId) {
		if (isTerminal() || (userId <= 0) ||
			(fetchSentDSRequestRecipient(userId) == null)) {

			return false;
		}

		return true;
	}

	public boolean isSyncable() {
		return !ArrayUtil.contains(
			DSRequestConstants.STATUSES_REQUEST_TERMINAL, _status);
	}

	public boolean isTerminal() {
		return ArrayUtil.contains(
			DSRequestConstants.STATUSES_REQUEST_TERMINAL, getStatus());
	}

	private Date _toDate(Serializable value) {
		if (value instanceof Date) {
			return (Date)value;
		}

		return null;
	}

	private final long _companyId;
	private final Date _createDate;
	private final long _dsRequestId;
	private final List<DSRequestRecipient> _dsRequestRecipients;
	private final String _emailMessage;
	private final String _emailSubject;
	private final Date _expirationDate;
	private final String _externalReferenceCode;
	private final List<Long> _fileEntryIds;
	private final long _siteGroupId;
	private final String _status;
	private final Date _statusDate;
	private final long _userId;
	private final String _userName;

}