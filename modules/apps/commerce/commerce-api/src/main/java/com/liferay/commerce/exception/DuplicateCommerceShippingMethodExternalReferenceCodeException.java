/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Alessio Antonio Rendina
 */
public class DuplicateCommerceShippingMethodExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicateCommerceShippingMethodExternalReferenceCodeException() {
	}

	public DuplicateCommerceShippingMethodExternalReferenceCodeException(
		String msg) {

		super(msg);
	}

	public DuplicateCommerceShippingMethodExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateCommerceShippingMethodExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}