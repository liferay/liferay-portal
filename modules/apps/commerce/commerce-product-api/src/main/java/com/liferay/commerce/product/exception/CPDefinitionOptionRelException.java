/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marco Leo
 */
public class CPDefinitionOptionRelException extends PortalException {

	public CPDefinitionOptionRelException() {
	}

	public CPDefinitionOptionRelException(String msg) {
		super(msg);
	}

	public CPDefinitionOptionRelException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public CPDefinitionOptionRelException(Throwable throwable) {
		super(throwable);
	}

}