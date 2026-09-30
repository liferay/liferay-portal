/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class MemberRequestReceiverUserIdException extends PortalException {

	public MemberRequestReceiverUserIdException() {
	}

	public MemberRequestReceiverUserIdException(String msg) {
		super(msg);
	}

	public MemberRequestReceiverUserIdException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public MemberRequestReceiverUserIdException(Throwable throwable) {
		super(throwable);
	}

}