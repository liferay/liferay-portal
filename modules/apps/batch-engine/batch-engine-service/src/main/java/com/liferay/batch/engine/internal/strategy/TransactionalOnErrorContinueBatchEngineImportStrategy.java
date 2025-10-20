/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;

import java.util.List;

/**
 * @author Daniel Raposo
 */
public class TransactionalOnErrorContinueBatchEngineImportStrategy
	extends BaseBatchEngineImportStrategy {

	public TransactionalOnErrorContinueBatchEngineImportStrategy(
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
		UnsafeFunction<T, T, Exception> unsafeFunction) {

		T persistedItem = null;

		try {
			persistedItem = TransactionInvokerUtil.invoke(
				_transactionConfig, () -> unsafeFunction.apply(item));
		}
		catch (Throwable throwable) {
			_log.error(throwable);

			addBatchEngineImportTaskError(
				batchEngineImportTask, batchEngineTaskItemDelegate, item,
				ItemIndexThreadLocal.get(),
				new Exception(throwable.getMessage(), throwable));
		}
		finally {
			ItemIndexThreadLocal.remove();
		}

		return persistedItem;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TransactionalOnErrorContinueBatchEngineImportStrategy.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

}