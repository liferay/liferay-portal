/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Crescenzo Rega
 */
public class CommerceReturnThreadLocal {

	public static boolean isMarkAsCompleted() {
		return _markAsCompleted.get();
	}

	public static boolean isMarkAsProcessed() {
		return _markAsProcessed.get();
	}

	public static boolean isSkipCommerceReturnItemContributor() {
		return _skipCommerceReturnItemContributor.get();
	}

	public static void setMarkAsCompleted(boolean markAsCompleted) {
		_markAsCompleted.set(markAsCompleted);
	}

	public static void setMarkAsProcessed(boolean markAsProcessed) {
		_markAsProcessed.set(markAsProcessed);
	}

	public static void setSkipCommerceReturnItemContributor(
		boolean skipCommerceReturnItemContributor) {

		_skipCommerceReturnItemContributor.set(
			skipCommerceReturnItemContributor);
	}

	private static final ThreadLocal<Boolean> _markAsCompleted =
		new CentralizedThreadLocal<>(
			CommerceReturnThreadLocal.class + "._markAsCompleted",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean> _markAsProcessed =
		new CentralizedThreadLocal<>(
			CommerceReturnThreadLocal.class + "._markAsProcessed",
			() -> Boolean.FALSE);
	private static final ThreadLocal<Boolean>
		_skipCommerceReturnItemContributor = new CentralizedThreadLocal<>(
			CommerceReturnThreadLocal.class +
				"._skipCommerceReturnItemContributor",
			() -> Boolean.FALSE);

}