/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.notification;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import java.io.UnsupportedEncodingException;

import java.util.Objects;

/**
 * @author Michael C. Han
 */
public class NotificationRecipient {

	public NotificationRecipient(
		String emailAddress,
		NotificationReceptionType notificationReceptionType) {

		_emailAddress = emailAddress;
		_notificationReceptionType = notificationReceptionType;

		_companyId = 0;
		_fullName = null;
		_screenName = null;
		_userId = 0;
	}

	public NotificationRecipient(
		User user, NotificationReceptionType notificationReceptionType) {

		_notificationReceptionType = notificationReceptionType;

		_companyId = user.getCompanyId();
		_emailAddress = user.getEmailAddress();
		_fullName = user.getFullName();
		_screenName = user.getScreenName();
		_userId = user.getUserId();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NotificationRecipient)) {
			return false;
		}

		NotificationRecipient notificationRecipient =
			(NotificationRecipient)object;

		if ((_companyId == notificationRecipient._companyId) &&
			Objects.equals(
				_emailAddress, notificationRecipient._emailAddress) &&
			(_userId == notificationRecipient._userId)) {

			return true;
		}

		return false;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getFullName() {
		return _fullName;
	}

	public InternetAddress getInternetAddress()
		throws AddressException, UnsupportedEncodingException {

		if (Validator.isNull(_fullName)) {
			return new InternetAddress(_emailAddress);
		}

		return new InternetAddress(_emailAddress, _fullName);
	}

	public NotificationReceptionType getNotificationReceptionType() {
		return _notificationReceptionType;
	}

	public String getScreenName() {
		return _screenName;
	}

	public long getUserId() {
		return _userId;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _companyId);

		if (Validator.isNotNull(_emailAddress)) {
			hash = HashUtil.hash(hash, _emailAddress);
		}

		return HashUtil.hash(hash, _userId);
	}

	private final long _companyId;
	private final String _emailAddress;
	private final String _fullName;
	private final NotificationReceptionType _notificationReceptionType;
	private final String _screenName;
	private final long _userId;

}