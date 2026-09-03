/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Gabriel Lima
 */
public class InvalidPropagationTargetGroupsException extends PortalException {

	public InvalidPropagationTargetGroupsException() {
	}

	public InvalidPropagationTargetGroupsException(String msg) {
		super(msg);
	}

	public InvalidPropagationTargetGroupsException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public InvalidPropagationTargetGroupsException(Throwable throwable) {
		super(throwable);
	}

}