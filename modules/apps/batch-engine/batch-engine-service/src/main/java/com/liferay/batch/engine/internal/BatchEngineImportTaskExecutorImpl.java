/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal;

import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.ItemClassRegistry;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.action.ImportTaskPreAction;
import com.liferay.batch.engine.action.ItemReaderPostAction;
import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.exception.handler.BatchEngineImportTaskExceptionHandler;
import com.liferay.batch.engine.internal.item.BatchEngineTaskItemDelegateExecutor;
import com.liferay.batch.engine.internal.item.BatchEngineTaskItemDelegateExecutorFactory;
import com.liferay.batch.engine.internal.reader.BatchEngineImportTaskItemReader;
import com.liferay.batch.engine.internal.reader.BatchEngineImportTaskItemReaderBuilder;
import com.liferay.batch.engine.internal.reader.BatchEngineImportTaskItemReaderUtil;
import com.liferay.batch.engine.internal.strategy.OnErrorContinueBatchEngineImportStrategy;
import com.liferay.batch.engine.internal.strategy.OnErrorFailBatchEngineImportStrategy;
import com.liferay.batch.engine.internal.task.progress.BatchEngineTaskProgress;
import com.liferay.batch.engine.internal.task.progress.BatchEngineTaskProgressFactory;
import com.liferay.batch.engine.internal.util.ErrorMessageUtil;
import com.liferay.batch.engine.internal.util.ItemIndexThreadLocal;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskErrorLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.batch.engine.strategy.BatchEngineImportStrategy;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(service = BatchEngineImportTaskExecutor.class)
public class BatchEngineImportTaskExecutorImpl
	implements BatchEngineImportTaskExecutor {

	@Override
	public void execute(BatchEngineImportTask batchEngineImportTask) {
		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate =
			_batchEngineTaskItemDelegateRegistry.getBatchEngineTaskItemDelegate(
				batchEngineImportTask.getCompanyId(),
				batchEngineImportTask.getClassName(),
				batchEngineImportTask.getTaskItemDelegateName());

		execute(batchEngineImportTask, batchEngineTaskItemDelegate, true);
	}

	@Override
	public void execute(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
		boolean checkPermissions) {

		long startTime = 0;

		if (_log.isInfoEnabled()) {
			_log.info(
				"Started batch engine import task " +
					batchEngineImportTask.getBatchEngineImportTaskId());

			startTime = System.currentTimeMillis();
		}

		SafeCloseable safeCloseable1 =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				batchEngineImportTask.getCompanyId(),
				CTCollectionThreadLocal.getCTCollectionId());

		File file;

		try (InputStream inputStream =
				_batchEngineImportTaskLocalService.openContentInputStream(
					batchEngineImportTask.getBatchEngineImportTaskId())) {

			file = FileUtil.createTempFile(inputStream);
		}
		catch (Throwable throwable) {
			_log.error(
				"Unable to save batch engine import task content as temp file" +
					batchEngineImportTask,
				throwable);

			_updateBatchEngineImportTask(
				BatchEngineTaskExecuteStatus.FAILED, batchEngineImportTask,
				throwable);

			return;
		}

		try (SafeCloseable safeCloseable2 = SearchContext.openBatchMode()) {
			batchEngineImportTask.setExecuteStatus(
				BatchEngineTaskExecuteStatus.STARTED.toString());
			batchEngineImportTask.setStartTime(new Date());

			BatchEngineTaskProgress batchEngineTaskProgress =
				_batchEngineTaskProgressFactory.create(
					BatchEngineTaskContentType.valueOf(
						batchEngineImportTask.getContentType()));

			try (InputStream inputStream = new FileInputStream(file)) {
				batchEngineImportTask.setTotalItemsCount(
					batchEngineTaskProgress.getTotalItemsCount(inputStream));
			}

			_batchEngineImportTaskLocalService.updateBatchEngineImportTask(
				batchEngineImportTask);

			BatchEngineTaskExecutorUtil.execute(
				checkPermissions,
				() -> _importItems(
					batchEngineImportTask, batchEngineTaskItemDelegate, file),
				_userLocalService.getUser(batchEngineImportTask.getUserId()));

			_updateBatchEngineImportTask(
				BatchEngineTaskExecuteStatus.COMPLETED, batchEngineImportTask,
				null);
		}
		catch (Throwable throwable) {
			_log.error(
				"Unable to update batch engine import task " +
					batchEngineImportTask,
				throwable);

			_updateBatchEngineImportTask(
				BatchEngineTaskExecuteStatus.FAILED, batchEngineImportTask,
				throwable);
		}
		finally {
			file.delete();

			// LPS-167011 Because of call to _updateBatchEngineImportTask when
			// catching a Throwable

			safeCloseable1.close();
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Finished batch engine import task ",
					batchEngineImportTask.getBatchEngineImportTaskId(), " in ",
					System.currentTimeMillis() - startTime, "ms"));
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_batchEngineTaskItemDelegateExecutorFactory =
			new BatchEngineTaskItemDelegateExecutorFactory(
				_batchEngineTaskItemDelegateRegistry, null, null, null);
		_batchEngineImportTaskExceptionHandlers =
			ServiceTrackerListFactory.open(
				bundleContext, BatchEngineImportTaskExceptionHandler.class);
		_importTaskPostActions = ServiceTrackerListFactory.open(
			bundleContext, ImportTaskPostAction.class);
		_importTaskPreActions = ServiceTrackerListFactory.open(
			bundleContext, ImportTaskPreAction.class);
		_itemReaderPostActions = ServiceTrackerListFactory.open(
			bundleContext, ItemReaderPostAction.class);
	}

	@Deactivate
	protected void deactivate() {
		_batchEngineImportTaskExceptionHandlers.close();
		_importTaskPostActions.close();
		_importTaskPreActions.close();
		_itemReaderPostActions.close();
	}

	private void _commitItems(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegateExecutor
				batchEngineTaskItemDelegateExecutor,
			List<Object> items, int processedItemsCount)
		throws Throwable {

		batchEngineTaskItemDelegateExecutor.saveItems(
			_createBatchEngineImportStrategy(batchEngineImportTask),
			BatchEngineTaskOperation.valueOf(
				batchEngineImportTask.getOperation()),
			items);

		batchEngineImportTask.setProcessedItemsCount(processedItemsCount);

		_batchEngineImportTaskLocalService.updateBatchEngineImportTask(
			batchEngineImportTask);
	}

	private BatchEngineImportStrategy _createBatchEngineImportStrategy(
		BatchEngineImportTask batchEngineImportTask) {

		if (batchEngineImportTask.getImportStrategy() ==
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE) {

			return new OnErrorContinueBatchEngineImportStrategy(
				batchEngineImportTask,
				_batchEngineImportTaskExceptionHandlers.toList(),
				_importTaskPostActions.toList(),
				_importTaskPreActions.toList());
		}

		return new OnErrorFailBatchEngineImportStrategy(
			batchEngineImportTask,
			_batchEngineImportTaskExceptionHandlers.toList(),
			_importTaskPostActions.toList(), _importTaskPreActions.toList());
	}

	private BatchEngineImportTaskItemReader _getBatchEngineImportTaskItemReader(
			BatchEngineImportTask batchEngineImportTask,
			InputStream inputStream, Map<String, Serializable> parameters)
		throws Exception {

		BatchEngineImportTaskItemReaderBuilder
			batchEngineImportTaskItemReaderBuilder =
				new BatchEngineImportTaskItemReaderBuilder();

		Map<String, Serializable> fieldNameMapping =
			batchEngineImportTask.getFieldNameMapping();

		if (fieldNameMapping == null) {
			fieldNameMapping = Collections.emptyMap();
		}

		return batchEngineImportTaskItemReaderBuilder.
			batchEngineTaskContentType(
				BatchEngineTaskContentType.valueOf(
					batchEngineImportTask.getContentType())
			).csvFileColumnDelimiter(
				_getCSVFileColumnDelimiter(batchEngineImportTask.getCompanyId())
			).fieldNames(
				ListUtil.fromCollection(fieldNameMapping.keySet())
			).inputStream(
				inputStream
			).parameters(
				parameters
			).build();
	}

	private String _getCSVFileColumnDelimiter(long companyId) throws Exception {
		BatchEngineTaskCompanyConfiguration
			batchEngineTaskCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					BatchEngineTaskCompanyConfiguration.class, companyId);

		return batchEngineTaskCompanyConfiguration.csvFileColumnDelimiter();
	}

	private Map<String, Serializable> _getParameters(
		BatchEngineImportTask batchEngineImportTask) {

		Map<String, Serializable> parameters =
			batchEngineImportTask.getParameters();

		if (parameters == null) {
			parameters = new HashMap<>();
		}

		return parameters;
	}

	private void _handleException(
			BatchEngineImportTask batchEngineImportTask, Exception exception,
			int processedItemsCount)
		throws Exception {

		_batchEngineImportTaskErrorLocalService.addBatchEngineImportTaskError(
			batchEngineImportTask.getCompanyId(),
			batchEngineImportTask.getUserId(),
			batchEngineImportTask.getBatchEngineImportTaskId(), null,
			processedItemsCount,
			ErrorMessageUtil.getErrorMessage(
				exception, batchEngineImportTask.getUserId()));

		if (batchEngineImportTask.getImportStrategy() ==
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE) {

			_log.error(exception);
		}
		else if (batchEngineImportTask.getImportStrategy() ==
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_FAIL) {

			throw exception;
		}
	}

	private Void _importItems(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			File file)
		throws Throwable {

		Map<String, Serializable> parameters = _getParameters(
			batchEngineImportTask);

		try (InputStream inputStream = new FileInputStream(file);
			BatchEngineImportTaskItemReader batchEngineImportTaskItemReader =
				_getBatchEngineImportTaskItemReader(
					batchEngineImportTask, inputStream, parameters);
			SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			BatchEngineTaskItemDelegateExecutor
				batchEngineTaskItemDelegateExecutor =
					_batchEngineTaskItemDelegateExecutorFactory.create(
						batchEngineTaskItemDelegate,
						_companyLocalService.getCompany(
							batchEngineImportTask.getCompanyId()),
						parameters,
						_userLocalService.getUser(
							batchEngineImportTask.getUserId()));

			List<Object> items = new ArrayList<>();

			Class<?> itemClass = _itemClassRegistry.getItemClass(
				batchEngineTaskItemDelegate);

			int processedItemsCount = 0;

			while (true) {
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}

				try {
					Object item = _readItem(
						batchEngineImportTask, batchEngineImportTaskItemReader,
						batchEngineImportTask.getFieldNameMapping(), itemClass);

					if (item == null) {
						break;
					}

					items.add(item);

					processedItemsCount++;

					ItemIndexThreadLocal.add(processedItemsCount);
				}
				catch (Exception exception) {
					processedItemsCount++;

					_handleException(
						batchEngineImportTask, exception, processedItemsCount);
				}

				if (items.size() == batchEngineImportTask.getBatchSize()) {
					_commitItems(
						batchEngineImportTask,
						batchEngineTaskItemDelegateExecutor, items,
						processedItemsCount);

					items.clear();

					ItemIndexThreadLocal.clear();
				}
			}

			if (!items.isEmpty()) {
				_commitItems(
					batchEngineImportTask, batchEngineTaskItemDelegateExecutor,
					items, processedItemsCount);
			}
		}

		return null;
	}

	private Object _readItem(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineImportTaskItemReader batchEngineImportTaskItemReader,
			Map<String, Serializable> fieldNameMapping, Class<?> itemClass)
		throws Exception {

		Map<String, Object> fieldNameValueMap =
			batchEngineImportTaskItemReader.read();

		if (fieldNameValueMap == null) {
			return null;
		}

		return BatchEngineImportTaskItemReaderUtil.convertValue(
			batchEngineImportTask, itemClass,
			BatchEngineImportTaskItemReaderUtil.mapFieldNames(
				fieldNameMapping, fieldNameValueMap),
			_itemReaderPostActions.toList());
	}

	private void _updateBatchEngineImportTask(
		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus,
		BatchEngineImportTask batchEngineImportTask, Throwable throwable) {

		batchEngineImportTask.setEndTime(new Date());
		batchEngineImportTask.setErrorMessage(
			ErrorMessageUtil.getErrorMessage(
				throwable, batchEngineImportTask.getUserId()));
		batchEngineImportTask.setExecuteStatus(
			batchEngineTaskExecuteStatus.toString());

		batchEngineImportTask =
			_batchEngineImportTaskLocalService.updateBatchEngineImportTask(
				batchEngineImportTask);

		BatchEngineTaskCallbackUtil.sendCallback(
			batchEngineImportTask.getCallbackURL(),
			batchEngineImportTask.getExecuteStatus(),
			batchEngineImportTask.getBatchEngineImportTaskId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineImportTaskExecutorImpl.class);

	@Reference
	private BatchEngineImportTaskErrorLocalService
		_batchEngineImportTaskErrorLocalService;

	private ServiceTrackerList<BatchEngineImportTaskExceptionHandler>
		_batchEngineImportTaskExceptionHandlers;

	@Reference
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	private BatchEngineTaskItemDelegateExecutorFactory
		_batchEngineTaskItemDelegateExecutorFactory;

	@Reference
	private BatchEngineTaskItemDelegateRegistry
		_batchEngineTaskItemDelegateRegistry;

	private final BatchEngineTaskProgressFactory
		_batchEngineTaskProgressFactory = new BatchEngineTaskProgressFactory();

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private ServiceTrackerList<ImportTaskPostAction> _importTaskPostActions;
	private ServiceTrackerList<ImportTaskPreAction> _importTaskPreActions;

	@Reference
	private ItemClassRegistry _itemClassRegistry;

	private ServiceTrackerList<ItemReaderPostAction> _itemReaderPostActions;

	@Reference
	private UserLocalService _userLocalService;

}