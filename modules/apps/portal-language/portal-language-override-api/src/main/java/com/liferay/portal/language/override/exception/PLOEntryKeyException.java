/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Drew Brokke
 * @author Thiago Buarque
 */
public class PLOEntryKeyException extends PortalException {

	public static class MustBeShorter extends PLOEntryKeyException {

		public MustBeShorter(long maxLength) {
			super(
				String.format(
					"Key must not have more than %s characters", maxLength));

			this.maxLength = maxLength;
		}

		public final long maxLength;

	}

	public static class MustNotBeDuplicate extends PLOEntryKeyException {

		public MustNotBeDuplicate(String key, String languageId) {
			super(
				String.format(
					"Key \"%s\" is already overridden for language \"%s\"", key,
					languageId));

			this.key = key;
			this.languageId = languageId;
		}

		public final String key;
		public final String languageId;

	}

	public static class MustNotBeNull extends PLOEntryKeyException {

		public MustNotBeNull() {
			super("Key must not be null");
		}

	}

	private PLOEntryKeyException(String msg) {
		super(msg);
	}

}