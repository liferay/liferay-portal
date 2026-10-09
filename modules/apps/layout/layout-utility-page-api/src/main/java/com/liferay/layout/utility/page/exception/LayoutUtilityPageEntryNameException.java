/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutUtilityPageEntryNameException extends PortalException {

	public static class MustNotBeDuplicate
		extends LayoutUtilityPageEntryNameException {

		public MustNotBeDuplicate(long groupId, String name) {
			super(
				String.format(
					StringBundler.concat(
						"Duplicate utility page for group ", groupId,
						" with name ", name)));

			this.name = name;
		}

		public final String name;

	}

	public static class MustNotBeNull
		extends LayoutUtilityPageEntryNameException {

		public MustNotBeNull(long groupId) {
			super("Name must not be null for group " + groupId);
		}

	}

	public static class MustNotContainInvalidCharacters
		extends LayoutUtilityPageEntryNameException {

		public MustNotContainInvalidCharacters(char character, String name) {
			super(
				StringBundler.concat(
					"Name ", name, " contains invalid character ", character));

			this.character = character;
			this.name = name;
		}

		public final char character;
		public final String name;

	}

	public static class MustNotExceedMaximumSize
		extends LayoutUtilityPageEntryNameException {

		public MustNotExceedMaximumSize(int maxLength, String name) {
			super(
				StringBundler.concat(
					"Length of name ", name, " exceeds maximum length ",
					maxLength));

			this.maxLength = maxLength;
			this.name = name;
		}

		public final int maxLength;
		public final String name;

	}

	private LayoutUtilityPageEntryNameException(String msg) {
		super(msg);
	}

}