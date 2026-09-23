/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokenException extends PortalException {

	public static class MustHaveValidType
		extends StyleBookEntryFrontendTokenException {

		public MustHaveValidType(String type, Throwable throwable) {
			super(
				StringBundler.concat(
					"Frontend token type \"", type, "\" is not supported"),
				throwable);
		}

	}

	public static class MustNotBeNull
		extends StyleBookEntryFrontendTokenException {

		public MustNotBeNull(String fieldName) {
			super(
				StringBundler.concat(
					"Frontend token ", fieldName, " must not be null"));
		}

	}

	private StyleBookEntryFrontendTokenException(String msg) {
		super(msg);
	}

	private StyleBookEntryFrontendTokenException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

}