/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.exception;

import com.liferay.petra.string.StringBundler;

/**
 * @author Brian Wing Shun Chan
 */
public class UserCommentsException extends PortalException {

	public static class MustNotExceedMaximumLength
		extends UserCommentsException {

		public MustNotExceedMaximumLength(
			String comments, int commentsMaxLength) {

			super(
				StringBundler.concat(
					"Comments ", comments, " must have fewer than ",
					commentsMaxLength, " characters"));
		}

	}

	private UserCommentsException() {
	}

	private UserCommentsException(String msg) {
		super(msg);
	}

	private UserCommentsException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	private UserCommentsException(Throwable throwable) {
		super(throwable);
	}

}