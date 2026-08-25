/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Marco Leo
 */
public class DuplicateCPDefinitionOptionRelExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicateCPDefinitionOptionRelExternalReferenceCodeException() {
	}

	public DuplicateCPDefinitionOptionRelExternalReferenceCodeException(
		String msg) {

		super(msg);
	}

	public DuplicateCPDefinitionOptionRelExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateCPDefinitionOptionRelExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}