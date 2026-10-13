/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Rubén Pulido
 */
public class DuplicateResourceFileExternalReferenceCodeException
	extends PortalException {

	public DuplicateResourceFileExternalReferenceCodeException(String msg) {
		super(msg);
	}

}