/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.exception;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;

/**
 * @author Brian Wing Shun Chan
 */
public class DuplicateDDMStructureExternalReferenceCodeException
	extends DuplicateExternalReferenceCodeException {

	public DuplicateDDMStructureExternalReferenceCodeException() {
	}

	public DuplicateDDMStructureExternalReferenceCodeException(String msg) {
		super(msg);
	}

	public DuplicateDDMStructureExternalReferenceCodeException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public DuplicateDDMStructureExternalReferenceCodeException(
		Throwable throwable) {

		super(throwable);
	}

}