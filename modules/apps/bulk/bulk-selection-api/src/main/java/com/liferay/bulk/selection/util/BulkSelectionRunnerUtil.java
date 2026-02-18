/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.selection.util;

import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Alejandro Tardín
 */
public class BulkSelectionRunnerUtil {

	public static BulkSelectionRunner getBulkSelectionRunner() {
		return _bulkSelectionRunnerSnapshot.get();
	}

	private static final Snapshot<BulkSelectionRunner>
		_bulkSelectionRunnerSnapshot = new Snapshot<>(
			BulkSelectionRunnerUtil.class, BulkSelectionRunner.class);

}