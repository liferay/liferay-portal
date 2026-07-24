/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Drew Brokke
 */
public class DuplicatePLOEntryExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicatePLOEntryExternalReferenceCodeException() {
	}

	public DuplicatePLOEntryExternalReferenceCodeException(String msg) {
		super(msg);
	}

	public DuplicatePLOEntryExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicatePLOEntryExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}