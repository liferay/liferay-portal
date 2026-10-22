/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian I. Kim
 */
public class DSEnvelopeException extends PortalException {

	public static class MustBeVoidable extends DSEnvelopeException {

		public MustBeVoidable(String message) {
			super(message);
		}

	}

	private DSEnvelopeException(String message) {
		super(message);
	}

}