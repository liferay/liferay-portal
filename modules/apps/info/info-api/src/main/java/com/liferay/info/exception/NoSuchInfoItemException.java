/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Jorge Ferrer
 */
public class NoSuchInfoItemException extends PortalException {

	public NoSuchInfoItemException(String msg) {
		super(msg);
	}

	public NoSuchInfoItemException(String msg, Throwable throwable) {
		super(throwable);
	}

	public NoSuchInfoItemException(Throwable throwable) {
		super(throwable);
	}

}