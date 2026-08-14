/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Miguel Arroyo
 */
public class AudiencesEntryAttributeException extends PortalException {

	public AudiencesEntryAttributeException() {
	}

	public AudiencesEntryAttributeException(String msg) {
		super(msg);
	}

	public AudiencesEntryAttributeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public AudiencesEntryAttributeException(Throwable throwable) {
		super(throwable);
	}

}