/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Brian Wing Shun Chan
 */
public class NoSuchPatcherFixComponentException extends NoSuchModelException {

	public NoSuchPatcherFixComponentException() {
	}

	public NoSuchPatcherFixComponentException(String msg) {
		super(msg);
	}

	public NoSuchPatcherFixComponentException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchPatcherFixComponentException(Throwable throwable) {
		super(throwable);
	}

}