/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.exception;

import com.liferay.portal.kernel.exception.NoSuchModelException;

/**
 * @author Brian Wing Shun Chan
 */
public class NoSuchPatcherBuildException extends NoSuchModelException {

	public NoSuchPatcherBuildException() {
	}

	public NoSuchPatcherBuildException(String msg) {
		super(msg);
	}

	public NoSuchPatcherBuildException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchPatcherBuildException(Throwable throwable) {
		super(throwable);
	}

}