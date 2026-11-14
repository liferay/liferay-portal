/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Shuyang Zhou
 */
public class CTCollectionDeletionThreadLocal {

	public static long getCTCollectionId() {
		return _ctCollectionId.get();
	}

	public static SafeCloseable setCTCollectionIdWithSafeCloseable(
		long ctCollectionId) {

		return _ctCollectionId.setWithSafeCloseable(ctCollectionId);
	}

	private CTCollectionDeletionThreadLocal() {
	}

	private static final CentralizedThreadLocal<Long> _ctCollectionId =
		new CentralizedThreadLocal<>(
			CTCollectionDeletionThreadLocal.class + "._ctCollectionId",
			() -> -1L);

}