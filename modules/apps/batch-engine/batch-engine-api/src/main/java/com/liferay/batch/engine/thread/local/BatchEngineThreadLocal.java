/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.thread.local;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Alejandro Tardín
 */
public class BatchEngineThreadLocal {

	public static boolean isBatchImportInProcess() {
		return _batchImportInProcess.get();
	}

	public static SafeCloseable setBatchImportInProcessWithSafeCloseable(
		boolean batchImportInProcess) {

		return _batchImportInProcess.setWithSafeCloseable(batchImportInProcess);
	}

	private static final CentralizedThreadLocal<Boolean> _batchImportInProcess =
		new CentralizedThreadLocal<>(
			BatchEngineThreadLocal.class + "._batchImportInProcess",
			() -> Boolean.FALSE);

}