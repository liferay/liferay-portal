/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Alessio Antonio Rendina
 */
public class DuplicateCSDiagramEntryExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicateCSDiagramEntryExternalReferenceCodeException() {
	}

	public DuplicateCSDiagramEntryExternalReferenceCodeException(String msg) {
		super(msg);
	}

	public DuplicateCSDiagramEntryExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateCSDiagramEntryExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}