/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.exception;

import com.liferay.portal.kernel.util.PropsKeys;

/**
 * @author Scott Lee
 */
public class PwdEncryptorException extends PortalException {

	public static class InvalidAlgorithm extends PwdEncryptorException {

		public InvalidAlgorithm(String msg) {
			super(msg);
		}

		public InvalidAlgorithm(String msg, Throwable throwable) {
			super(msg, throwable);
		}

	}

	public static class InvalidEncryptedPwd extends PwdEncryptorException {

		public InvalidEncryptedPwd(String msg, Throwable throwable) {
			super(msg, throwable);
		}

	}

	public static class MustSetLegacyAlgorithmProperty
		extends PwdEncryptorException {

		public MustSetLegacyAlgorithmProperty() {
			super(
				"The property \"" +
					PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY +
						"\" must be set");
		}

	}

	public static class PwdMustNotBeNull extends PwdEncryptorException {

		public PwdMustNotBeNull(String msg) {
			super(msg);
		}

	}

	public static class UnavailableAlgorithm extends PwdEncryptorException {

		public UnavailableAlgorithm(String msg, Throwable throwable) {
			super(msg, throwable);
		}

	}

	public static class UnsupportedEncoding extends PwdEncryptorException {

		public UnsupportedEncoding(String msg, Throwable throwable) {
			super(msg, throwable);
		}

	}

	private PwdEncryptorException(String msg) {
		super(msg);
	}

	private PwdEncryptorException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}