/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.exception;

import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Adolfo Pérez
 */
public class AMRuntimeException extends SystemException {

	/**
	 * This exception is thrown when a value serialized as a <code>String</code>
	 * cannot be converted by an {@link com.liferay.adaptive.media.AMAttribute}.
	 */
	public static final class AMAttributeFormatException
		extends AMRuntimeException {

		public AMAttributeFormatException() {
		}

		public AMAttributeFormatException(String s) {
			super(s);
		}

		public AMAttributeFormatException(String s, Throwable throwable) {
			super(s, throwable);
		}

		public AMAttributeFormatException(Throwable throwable) {
			super(throwable);
		}

	}

	/**
	 * This exception wraps {@link java.io.IOException} instances. Since it is a
	 * system error, it is reasonable to wrap it inside a runtime exception.
	 */
	public static final class IOException extends AMRuntimeException {

		public IOException() {
		}

		public IOException(String s) {
			super(s);
		}

		public IOException(String s, Throwable throwable) {
			super(s, throwable);
		}

		public IOException(Throwable throwable) {
			super(throwable);
		}

	}

	/**
	 * This exception is thrown when there is a processor configuration error.
	 */
	public static final class InvalidConfiguration extends AMRuntimeException {

		public InvalidConfiguration() {
		}

		public InvalidConfiguration(String s) {
			super(s);
		}

		public InvalidConfiguration(String s, Throwable throwable) {
			super(s, throwable);
		}

		public InvalidConfiguration(Throwable throwable) {
			super(throwable);
		}

	}

	/**
	 * This exception wraps {@link java.io.UnsupportedEncodingException}
	 * instances. Since it is a system error, it is reasonable to wrap it inside
	 * a runtime exception.
	 */
	public static final class UnsupportedEncodingException
		extends AMRuntimeException {

		public UnsupportedEncodingException() {
		}

		public UnsupportedEncodingException(String s) {
			super(s);
		}

		public UnsupportedEncodingException(String s, Throwable throwable) {
			super(s, throwable);
		}

		public UnsupportedEncodingException(Throwable throwable) {
			super(throwable);
		}

	}

	private AMRuntimeException() {
	}

	private AMRuntimeException(String s) {
		super(s);
	}

	private AMRuntimeException(String s, Throwable throwable) {
		super(s, throwable);
	}

	private AMRuntimeException(Throwable throwable) {
		super(throwable);
	}

}