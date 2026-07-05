/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.transaction;

import java.util.concurrent.Callable;

/**
 * @author     Shuyang Zhou
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             TransactionCallbackUtil}
 */
@Deprecated
public class TransactionCommitCallbackUtil {

	public static final TransactionLifecycleListener
		TRANSACTION_LIFECYCLE_LISTENER =
			TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER;

	public static void registerCallback(Callable<?> callable) {
		TransactionCallbackUtil.registerCommitCallback(callable);
	}

}