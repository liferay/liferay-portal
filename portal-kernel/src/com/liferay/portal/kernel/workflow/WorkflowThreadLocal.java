/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Jorge Ferrer
 */
public class WorkflowThreadLocal {

	public static boolean isEnabled() {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return false;
		}

		return _enabled.get();
	}

	public static boolean isWaitForCompletion() {
		return _waitForCompletion.get();
	}

	public static void setEnabled(boolean enabled) {
		_enabled.set(enabled);
	}

	public static SafeCloseable setWaitForCompletionWithSafeCloseable(
		boolean waitForCompletion) {

		return _waitForCompletion.setWithSafeCloseable(waitForCompletion);
	}

	private static final ThreadLocal<Boolean> _enabled =
		new CentralizedThreadLocal<>(
			WorkflowThreadLocal.class + "._enabled", () -> Boolean.TRUE);
	private static final CentralizedThreadLocal<Boolean> _waitForCompletion =
		new CentralizedThreadLocal<>(
			WorkflowThreadLocal.class + "._waitForCompletion",
			() -> Boolean.FALSE);

}