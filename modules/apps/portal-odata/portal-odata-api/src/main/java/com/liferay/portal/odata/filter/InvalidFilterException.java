/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.filter;

import jakarta.ws.rs.BadRequestException;

/**
 * Models errors that occur when handling {@link Filter} objects.
 *
 * @author David Arques
 * @review
 */
public class InvalidFilterException extends BadRequestException {

	/**
	 * Creates a new {@code InvalidFilterException} with a message.
	 *
	 * @param  msg the message
	 * @review
	 */
	public InvalidFilterException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new {@code InvalidFilterException} with a message and the
	 * throwable of the exception.
	 *
	 * @param  msg the message
	 * @param  throwable the throwable
	 * @review
	 */
	public InvalidFilterException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}