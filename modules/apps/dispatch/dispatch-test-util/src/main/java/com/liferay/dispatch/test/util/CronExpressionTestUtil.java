/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.test.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Calendar;

/**
 * @author Igor Beslic
 */
public class CronExpressionTestUtil {

	public static String getCronExpression() {
		return String.format("0 0 0 ? %d/2 * %d", _MONTH, _YEAR);
	}

	public static int getMonth() {
		return _MONTH;
	}

	public static int getYear() {
		return _YEAR;
	}

	private static final int _MONTH;

	private static final int _YEAR;

	static {
		_MONTH = RandomTestUtil.randomInt(1, 9);

		Calendar calendar = Calendar.getInstance();

		_YEAR = calendar.get(Calendar.YEAR) + 1;
	}

}