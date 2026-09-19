/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marco Leo
 */
public class ObjectFieldNameException extends PortalException {

	public static class MustBeLessThan41Characters
		extends ObjectFieldNameException {

		public MustBeLessThan41Characters() {
			super("Name must be less than 41 characters");
		}

	}

	public static class MustBeginWithLowerCaseLetter
		extends ObjectFieldNameException {

		public MustBeginWithLowerCaseLetter() {
			super("The first character of a name must be a lower case letter");
		}

	}

	public static class MustNotBeDuplicate extends ObjectFieldNameException {

		public MustNotBeDuplicate(String name) {
			super("Duplicate name " + name);
		}

	}

	public static class MustNotBeEqualToObjectRelationshipName
		extends ObjectFieldNameException {

		public MustNotBeEqualToObjectRelationshipName(
			String objectDefinitionName) {

			super(
				StringBundler.concat(
					"There is already an object relationship with this name ",
					"in the object definition \"", objectDefinitionName,
					".\" Object fields and object relationships cannot have ",
					"the same name."));
		}

	}

	public static class MustNotBeNull extends ObjectFieldNameException {

		public MustNotBeNull() {
			super("Name is null");
		}

	}

	public static class MustNotBeReserved extends ObjectFieldNameException {

		public MustNotBeReserved(String name) {
			super("Reserved name " + name);
		}

	}

	public static class MustOnlyContainLettersAndDigits
		extends ObjectFieldNameException {

		public MustOnlyContainLettersAndDigits() {
			super("Name must only contain letters and digits");
		}

	}

	private ObjectFieldNameException(String msg) {
		super(msg);
	}

}