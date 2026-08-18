/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class StyleBookEntryFrontendTokensValuesException
	extends PortalException {

	public static class MustBeValidJSON
		extends StyleBookEntryFrontendTokensValuesException {

		public MustBeValidJSON(Throwable throwable) {
			super("Unable to parse frontend tokens values", throwable);
		}

	}

	public static class MustNotContainInvalidCharacters
		extends StyleBookEntryFrontendTokensValuesException {

		public MustNotContainInvalidCharacters(String key) {
			super(
				StringBundler.concat(
					"Frontend token value \"", key,
					"\" contains invalid characters"));
		}

	}

	private StyleBookEntryFrontendTokensValuesException(String msg) {
		super(msg);
	}

	private StyleBookEntryFrontendTokensValuesException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

}