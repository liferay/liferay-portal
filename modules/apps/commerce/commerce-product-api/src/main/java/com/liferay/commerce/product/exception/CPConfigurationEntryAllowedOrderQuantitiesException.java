/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marco Leo
 */
public class CPConfigurationEntryAllowedOrderQuantitiesException
	extends PortalException {

	public CPConfigurationEntryAllowedOrderQuantitiesException() {
	}

	public CPConfigurationEntryAllowedOrderQuantitiesException(String msg) {
		super(msg);
	}

	public CPConfigurationEntryAllowedOrderQuantitiesException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public CPConfigurationEntryAllowedOrderQuantitiesException(
		Throwable throwable) {

		super(throwable);
	}

}