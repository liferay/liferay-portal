/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.request;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Brian Kim
 */
public class DSRequestDetail {

	public DSRequestDetail(
		Date createDate, String emailSubject, Date expirationDate,
		String providerRequestId,
		List<DSRequestRecipientDetail> recipientDetails,
		String requesterEmailAddress, String requesterName,
		long requesterUserId, String requestStatus, Date statusDate) {

		_createDate = createDate;
		_emailSubject = emailSubject;
		_expirationDate = expirationDate;
		_providerRequestId = providerRequestId;
		_recipientDetails = recipientDetails;
		_requesterEmailAddress = requesterEmailAddress;
		_requesterName = requesterName;
		_requesterUserId = requesterUserId;
		_requestStatus = requestStatus;
		_statusDate = statusDate;
	}

	public Date getCreateDate() {
		if (_createDate == null) {
			return null;
		}

		return new Date(_createDate.getTime());
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

	public String getProviderRequestId() {
		return _providerRequestId;
	}

	public List<DSRequestRecipientDetail> getRecipientDetails() {
		if (_recipientDetails == null) {
			return Collections.emptyList();
		}

		return _recipientDetails;
	}

	public String getRequesterEmailAddress() {
		return _requesterEmailAddress;
	}

	public String getRequesterName() {
		return _requesterName;
	}

	public long getRequesterUserId() {
		return _requesterUserId;
	}

	public String getRequestStatus() {
		return _requestStatus;
	}

	public Date getStatusDate() {
		if (_statusDate == null) {
			return null;
		}

		return new Date(_statusDate.getTime());
	}

	private final Date _createDate;
	private final String _emailSubject;
	private final Date _expirationDate;
	private final String _providerRequestId;
	private final List<DSRequestRecipientDetail> _recipientDetails;
	private final String _requesterEmailAddress;
	private final String _requesterName;
	private final long _requesterUserId;
	private final String _requestStatus;
	private final Date _statusDate;

}