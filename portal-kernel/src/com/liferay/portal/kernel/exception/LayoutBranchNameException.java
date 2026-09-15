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
public class LayoutBranchNameException extends PortalException {

	public static final int DUPLICATE = 1;

	public static final int TOO_LONG = 2;

	public static final int TOO_SHORT = 3;

	public LayoutBranchNameException(int type) {
		_type = type;
	}

	public int getType() {
		return _type;
	}

	private final int _type;

}