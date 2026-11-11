/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib.util;

import com.liferay.petra.string.StringPool;

/**
 * @author Eudaldo Alonso
 */
public enum AnimationType {

	EMPTY("empty-state"), NONE(StringPool.BLANK), SEARCH("search-state"),
	SUCCESS("success-state");

	public String getSymbol() {
		return _symbol;
	}

	private AnimationType(String symbol) {
		_symbol = symbol;
	}

	private final String _symbol;

}