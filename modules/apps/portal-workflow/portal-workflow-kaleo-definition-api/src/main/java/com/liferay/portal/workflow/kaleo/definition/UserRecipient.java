/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

/**
 * @author Michael C. Han
 */
public class UserRecipient extends Recipient {

	public UserRecipient() {
		this(0, null, null);
	}

	public UserRecipient(long userId, String screenName, String emailAddress) {
		this(null, userId, screenName, emailAddress);
	}

	public UserRecipient(
		String externalReferenceCode, long userId, String screenName,
		String emailAddress) {

		super(RecipientType.USER);

		_externalReferenceCode = GetterUtil.getString(externalReferenceCode);
		_userId = userId;
		_screenName = GetterUtil.getString(screenName);
		_emailAddress = GetterUtil.getString(emailAddress);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserRecipient)) {
			return false;
		}

		UserRecipient userRecipient = (UserRecipient)object;

		if (Objects.equals(_emailAddress, userRecipient._emailAddress) &&
			Objects.equals(
				_externalReferenceCode, userRecipient._externalReferenceCode) &&
			Objects.equals(_screenName, userRecipient._screenName) &&
			(_userId == userRecipient._userId)) {

			return true;
		}

		return true;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public String getScreenName() {
		return _screenName;
	}

	public long getUserId() {
		return _userId;
	}

	@Override
	public int hashCode() {
		String s = StringBundler.concat(
			_emailAddress, _externalReferenceCode, _screenName, _userId);

		return s.hashCode();
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{emailAddress=", _emailAddress, ", externalReferenceCode=",
			_externalReferenceCode, ", screenName=", _screenName, ", userId=",
			_userId, "}");
	}

	private final String _emailAddress;
	private final String _externalReferenceCode;
	private final String _screenName;
	private long _userId;

}