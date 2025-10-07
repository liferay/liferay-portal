/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Eduardo Lundgren
 */
public class DuplicateCalendarBookingExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicateCalendarBookingExternalReferenceCodeException() {
	}

	public DuplicateCalendarBookingExternalReferenceCodeException(String msg) {
		super(msg);
	}

	public DuplicateCalendarBookingExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateCalendarBookingExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}