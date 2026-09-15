/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
package com.liferay.audiences.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class AudiencesEntryGroupERCException extends PortalException {

	public AudiencesEntryGroupERCException() {
	}

	public AudiencesEntryGroupERCException(String msg) {
		super(msg);
	}

	public AudiencesEntryGroupERCException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public AudiencesEntryGroupERCException(Throwable throwable) {
		super(throwable);
	}

}