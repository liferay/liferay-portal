/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.exception;

/**
 * @author Dante Wang
 */
public class IllegalContextPathException
	extends HttpWhiteboardFailureException {

	public IllegalContextPathException(String message, int failureReason) {
		super(message, failureReason);
	}

}