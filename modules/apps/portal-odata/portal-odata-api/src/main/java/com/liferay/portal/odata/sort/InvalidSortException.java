/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.sort;

import jakarta.ws.rs.BadRequestException;

/**
 * Models {@link Sort} errors.
 *
 * @author Cristina González
 * @review
 */
public class InvalidSortException extends BadRequestException {

	/**
	 * Creates a new {@code InvalidSortException} with the provided message.
	 *
	 * @param  msg the message
	 * @review
	 */
	public InvalidSortException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new {@code InvalidSortException} with the provided message and
	 * throwable.
	 *
	 * @param  msg the message
	 * @param  throwable the throwable
	 * @review
	 */
	public InvalidSortException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}