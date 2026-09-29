/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
package com.liferay.invitation.invite.members.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class MemberRequestInvalidURLException extends PortalException {

	public MemberRequestInvalidURLException() {
	}

	public MemberRequestInvalidURLException(String msg) {
		super(msg);
	}

	public MemberRequestInvalidURLException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public MemberRequestInvalidURLException(Throwable throwable) {
		super(throwable);
	}

}