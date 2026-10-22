/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian I. Kim
 */
public class DSRequestException extends PortalException {

	public static class MustBeEnabled extends DSRequestException {

		public MustBeEnabled(long groupId) {
			super(
				"Digital signature embedded view is not enabled for group " +
					groupId);
		}

	}

	public static class MustHaveExistingRecipient extends DSRequestException {

		public MustHaveExistingRecipient(String emailAddress) {
			super(
				"No user exists with recipient email address " + emailAddress);
		}

	}

	public static class MustHaveFileEntry extends DSRequestException {

		public MustHaveFileEntry() {
			super(
				"A digital signature request must have at least one file " +
					"entry");
		}

	}

	public static class MustHaveReplaceableFileEntry
		extends DSRequestException {

		public MustHaveReplaceableFileEntry(long fileEntryId, String status) {
			super(
				StringBundler.concat(
					"File entry ", fileEntryId,
					" already has a digital signature request with status \"",
					status, "\""));
		}

	}

	public static class MustHaveValidRecipient extends DSRequestException {

		public MustHaveValidRecipient(long userId) {
			super(
				"User " + userId +
					" cannot be a digital signature request recipient");
		}

	}

	public static class MustNotBeStagingGroup extends DSRequestException {

		public MustNotBeStagingGroup(long groupId) {
			super(
				"Digital signature requests are not supported in staging " +
					"group " + groupId);
		}

	}

	public static class MustNotDuplicateRecipient extends DSRequestException {

		public MustNotDuplicateRecipient(long userId) {
			super(
				"User " + userId +
					" is already a digital signature request recipient");
		}

	}

	public static class MustNotHaveNegativeExpireAfter
		extends DSRequestException {

		public MustNotHaveNegativeExpireAfter() {
			super("Expire after must not be negative");
		}

	}

	private DSRequestException(String message) {
		super(message);
	}

}