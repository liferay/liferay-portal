/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.exception;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutTypeException extends PortalException {

	public static final int EMPTY = 5;

	public static final int FIRST_LAYOUT = 2;

	public static final int FIRST_LAYOUT_PERMISSION = 3;

	public static final int NOT_INSTANCEABLE = 4;

	public static final int NOT_PARENTABLE = 1;

	public LayoutTypeException(int type) {
		_type = type;
	}

	public String getLayoutType() {
		return _layoutType;
	}

	public int getType() {
		return _type;
	}

	public void setLayoutType(String layoutType) {
		_layoutType = layoutType;
	}

	private String _layoutType;
	private final int _type;

}