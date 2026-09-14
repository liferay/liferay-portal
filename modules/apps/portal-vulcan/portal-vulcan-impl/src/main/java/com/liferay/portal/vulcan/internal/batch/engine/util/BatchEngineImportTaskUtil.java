/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.util;

import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.batch.engine.constants.UpdateStrategy;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Jhosseph Gonzalez
 */
public class BatchEngineImportTaskUtil {

	public static boolean isPartialUpdate(
		BatchEngineImportTask batchEngineImportTask) {

		BatchEngineTaskOperation batchEngineTaskOperation =
			BatchEngineTaskOperation.valueOf(
				batchEngineImportTask.getOperation());

		String createStrategy = batchEngineImportTask.getParameterValue(
			"createStrategy");
		String updateStrategy = batchEngineImportTask.getParameterValue(
			"updateStrategy");

		if ((batchEngineTaskOperation == BatchEngineTaskOperation.CREATE) &&
			StringUtil.equals(
				createStrategy, CreateStrategy.UPSERT.getDBOperation()) &&
			StringUtil.equals(
				updateStrategy,
				UpdateStrategy.PARTIAL_UPDATE.getDBOperation())) {

			return true;
		}

		if ((batchEngineTaskOperation == BatchEngineTaskOperation.UPDATE) &&
			StringUtil.equals(
				updateStrategy,
				UpdateStrategy.PARTIAL_UPDATE.getDBOperation())) {

			return true;
		}

		return false;
	}

}