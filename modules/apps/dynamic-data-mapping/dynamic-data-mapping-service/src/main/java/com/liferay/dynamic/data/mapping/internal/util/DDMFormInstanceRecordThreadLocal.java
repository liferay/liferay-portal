/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Akhash Ramprakash
 */
public class DDMFormInstanceRecordThreadLocal {

	public static boolean isDeleteInProcess() {
		return _deleteInProcess.get();
	}

	public static SafeCloseable setDeleteInProcessWithSafeCloseable(
		boolean deleteInProcess) {

		return _deleteInProcess.setWithSafeCloseable(deleteInProcess);
	}

	private static final CentralizedThreadLocal<Boolean> _deleteInProcess =
		new CentralizedThreadLocal<>(
			DDMFormInstanceRecordThreadLocal.class + "._deleteInProcess",
			() -> Boolean.FALSE);

}