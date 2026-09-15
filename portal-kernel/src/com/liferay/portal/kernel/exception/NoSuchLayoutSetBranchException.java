/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.exception;

/**
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class NoSuchLayoutSetBranchException extends NoSuchModelException {

	public NoSuchLayoutSetBranchException() {
	}

	public NoSuchLayoutSetBranchException(String msg) {
		super(msg);
	}

	public NoSuchLayoutSetBranchException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public NoSuchLayoutSetBranchException(Throwable throwable) {
		super(throwable);
	}

}