/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionInventoryAllowedOrderQuantitiesException
	extends PortalException {

	public CPDefinitionInventoryAllowedOrderQuantitiesException() {
	}

	public CPDefinitionInventoryAllowedOrderQuantitiesException(String msg) {
		super(msg);
	}

	public CPDefinitionInventoryAllowedOrderQuantitiesException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public CPDefinitionInventoryAllowedOrderQuantitiesException(
		Throwable throwable) {

		super(throwable);
	}

}