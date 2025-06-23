/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.strategy;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.action.ImportTaskPreAction;
import com.liferay.batch.engine.exception.handler.BatchEngineImportTaskExceptionHandler;
import com.liferay.batch.engine.internal.util.ItemIndexThreadLocal;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.petra.function.UnsafeFunction;

import java.util.List;

/**
 * @author Matija Petanjek
 */
public class OnErrorFailBatchEngineImportStrategy
	extends BaseBatchEngineImportStrategy {

	public OnErrorFailBatchEngineImportStrategy(
		BatchEngineImportTask batchEngineImportTask,
		List<BatchEngineImportTaskExceptionHandler>
			batchEngineImportTaskExceptionHandlers,
		List<ImportTaskPostAction> importTaskPostActions,
		List<ImportTaskPreAction> importTaskPreActions) {

		super(
			batchEngineImportTask, batchEngineImportTaskExceptionHandlers,
			importTaskPostActions, importTaskPreActions);
	}

	@Override
	public <T> T importItem(
			BatchEngineTaskItemDelegate<T> batchEngineTaskItemDelegate, T item,
			UnsafeFunction<T, T, Exception> unsafeFunction)
		throws Exception {

		try {
			return unsafeFunction.apply(item);
		}
		catch (Exception exception) {
			addBatchEngineImportTaskError(
				batchEngineImportTask, batchEngineTaskItemDelegate, item,
				ItemIndexThreadLocal.get(), exception);

			throw exception;
		}
		finally {
			ItemIndexThreadLocal.remove();
		}
	}

}