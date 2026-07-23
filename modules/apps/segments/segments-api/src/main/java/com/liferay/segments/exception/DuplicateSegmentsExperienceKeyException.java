/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
package com.liferay.segments.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Eduardo Garcia
 */
public class DuplicateSegmentsExperienceKeyException extends PortalException {

	public DuplicateSegmentsExperienceKeyException() {
	}

	public DuplicateSegmentsExperienceKeyException(String msg) {
		super(msg);
	}

	public DuplicateSegmentsExperienceKeyException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public DuplicateSegmentsExperienceKeyException(Throwable throwable) {
		super(throwable);
	}

}