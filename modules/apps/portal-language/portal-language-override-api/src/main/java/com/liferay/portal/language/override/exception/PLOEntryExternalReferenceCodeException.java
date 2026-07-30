/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Thiago Buarque
 */
public class PLOEntryExternalReferenceCodeException extends PortalException {

	public static class MustNotExceedMaximumLength
		extends PLOEntryExternalReferenceCodeException {

		public MustNotExceedMaximumLength(long maxLength) {
			super(
				StringBundler.concat(
					"External reference code must have fewer than ", maxLength,
					" characters"));

			this.maxLength = maxLength;
		}

		public final long maxLength;

	}

	private PLOEntryExternalReferenceCodeException(String msg) {
		super(msg);
	}

}