/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marco Leo
 */
public class ObjectDefinitionNameException extends PortalException {

	public static class ForbiddenModifiableSystemObjectDefinitionName
		extends ObjectDefinitionNameException {

		public ForbiddenModifiableSystemObjectDefinitionName(String name) {
			super("Forbidden modifiable system object definition name " + name);
		}

	}

	public static class MustBeLessThan41Characters
		extends ObjectDefinitionNameException {

		public MustBeLessThan41Characters() {
			super("Name must be less than 41 characters");
		}

	}

	public static class MustBeginWithUpperCaseLetter
		extends ObjectDefinitionNameException {

		public MustBeginWithUpperCaseLetter() {
			super("The first character of a name must be an upper case letter");
		}

	}

	public static class MustNotBeDuplicate
		extends ObjectDefinitionNameException {

		public MustNotBeDuplicate(String name) {
			super("Duplicate name " + name);
		}

	}

	public static class MustNotBeNull extends ObjectDefinitionNameException {

		public MustNotBeNull() {
			super("Name is null");
		}

	}

	public static class MustNotStartWithCAndUnderscoreForSystemObject
		extends ObjectDefinitionNameException {

		public MustNotStartWithCAndUnderscoreForSystemObject() {
			super("System object definition names must not start with \"C_\"");
		}

	}

	public static class MustOnlyContainLettersAndDigits
		extends ObjectDefinitionNameException {

		public MustOnlyContainLettersAndDigits() {
			super("Name must only contain letters and digits");
		}

	}

	public static class MustStartWithCAndUnderscoreForCustomObject
		extends ObjectDefinitionNameException {

		public MustStartWithCAndUnderscoreForCustomObject() {
			super("Custom object definition names must start with \"C_\"");
		}

	}

	private ObjectDefinitionNameException(String msg) {
		super(msg);
	}

}