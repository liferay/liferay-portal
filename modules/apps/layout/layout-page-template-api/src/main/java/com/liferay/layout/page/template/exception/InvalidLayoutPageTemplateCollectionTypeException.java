/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Javier Moral
 */
public class InvalidLayoutPageTemplateCollectionTypeException
	extends PortalException {

	public InvalidLayoutPageTemplateCollectionTypeException() {
	}

	public InvalidLayoutPageTemplateCollectionTypeException(String msg) {
		super(msg);
	}

	public InvalidLayoutPageTemplateCollectionTypeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public InvalidLayoutPageTemplateCollectionTypeException(
		Throwable throwable) {

		super(throwable);
	}

}