/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher;

import com.liferay.portal.NoSuchModelException;

/**
 * @author Calvin Keum
 */
public class NoSuchPatcherAccountException extends NoSuchModelException {

	public NoSuchPatcherAccountException() {
	}

	public NoSuchPatcherAccountException(String msg) {
		super(msg);
	}

	public NoSuchPatcherAccountException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchPatcherAccountException(Throwable cause) {
		super(cause);
	}

}