/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry;
import com.liferay.batch.engine.ItemClassRegistry;
import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.batch.engine.csv.ColumnDescriptorProvider;
import com.liferay.batch.engine.internal.writer.BatchEngineExportTaskItemWriter;
import com.liferay.batch.engine.internal.writer.BatchEngineExportTaskItemWriterBuilder;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.util.NestedFieldsContextUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(service = BatchEngineExportTaskExecutor.class)
public class BatchEngineExportTaskExecutorImpl
	implements BatchEngineExportTaskExecutor {

	@Override
	public void execute(BatchEngineExportTask batchEngineExportTask) {
		execute(
			batchEngineExportTask,
			new Settings() {

				@Override
				public boolean isCompressContent() {
					return true;
				}

				@Override
				public boolean isPersist() {
					return true;
				}

			});
	}

	@Override
	public Result execute(
		BatchEngineExportTask batchEngineExportTask, Settings settings) {

		if (!settings.isCompressContent() && settings.isPersist()) {
			throw new IllegalArgumentException(
				"Uncompressed content cannot be stored in the database");
		}

		if (settings.getMaxItems() < 0) {
			throw new IllegalArgumentException(
				"The maximum number of items must be a positive number");
		}

		SafeCloseable safeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				batchEngineExportTask.getCompanyId(),
				CTCollectionThreadLocal.getCTCollectionId());

		try {
			batchEngineExportTask.setExecuteStatus(
				BatchEngineTaskExecuteStatus.STARTED.toString());
			batchEngineExportTask.setStartTime(new Date());

			if (settings.isPersist()) {
				_batchEngineExportTaskLocalService.updateBatchEngineExportTask(
					batchEngineExportTask);
			}

			InputStream inputStream = BatchEngineTaskExecutorUtil.execute(
				true, () -> _exportItems(batchEngineExportTask, settings),
				_userLocalService.getUser(batchEngineExportTask.getUserId()));

			_updateBatchEngineExportTask(
				BatchEngineTaskExecuteStatus.COMPLETED, batchEngineExportTask,
				null, settings.isPersist());

			return new Result() {

				@Override
				public BatchEngineExportTask getBatchEngineExportTask() {
					return batchEngineExportTask;
				}

				@Override
				public InputStream getInputStream() {
					return inputStream;
				}

			};
		}
		catch (Throwable throwable) {
			_log.error(
				"Unable to update batch engine export task " +
					batchEngineExportTask,
				throwable);

			if (settings.isPersist()) {
				try {
					BatchEngineExportTask currentBatchEngineExportTask =
						_batchEngineExportTaskLocalService.
							getBatchEngineExportTask(
								batchEngineExportTask.getPrimaryKey());

					_updateBatchEngineExportTask(
						BatchEngineTaskExecuteStatus.FAILED,
						currentBatchEngineExportTask, throwable.getMessage(),
						settings.isPersist());
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to update batch engine export task",
						portalException);
				}
			}
		}
		finally {

			// LPS-167011 Because of call to _updateBatchEngineImportTask when
			// catching a Throwable

			safeCloseable.close();
		}

		return null;
	}

	private InputStream _exportItems(
			BatchEngineExportTask batchEngineExportTask, Settings settings)
		throws Exception {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		Map<String, Serializable> parameters = _getParameters(
			batchEngineExportTask);

		NestedFieldsContext oldNestedFieldsContext = null;

		try (BatchEngineExportTaskItemWriter batchEngineExportTaskItemWriter =
				_getBatchEngineExportTaskItemWriter(
					batchEngineExportTask, parameters, settings,
					unsyncByteArrayOutputStream)) {

			oldNestedFieldsContext =
				NestedFieldsContextThreadLocal.getNestedFieldsContext();

			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				new NestedFieldsContext(
					NestedFieldsContextUtil.limitDepth(
						GetterUtil.getInteger(
							parameters.get("batchNestedFieldsDepth"))),
					NestedFieldsContextUtil.toList(
						MapUtil.getString(parameters, "batchNestedFields"))));

			int maxItems = settings.getMaxItems();

			int exportBatchSize = Math.min(
				maxItems,
				_getExportBatchSize(batchEngineExportTask.getCompanyId()));

			batchEngineExportTask.setProcessedItemsCount(0);

			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate =
				_batchEngineTaskItemDelegateRegistry.
					getBatchEngineTaskItemDelegate(
						batchEngineExportTask.getCompanyId(),
						batchEngineExportTask.getClassName(),
						batchEngineExportTask.getTaskItemDelegateName());

			if (batchEngineTaskItemDelegate == null) {
				throw new IllegalStateException(
					"No batch engine delegate available for class name " +
						batchEngineExportTask.getClassName());
			}

			User user = _userLocalService.getUser(
				batchEngineExportTask.getUserId());

			BatchEngineTaskExecutorUtil.setContextFields(
				batchEngineExportTask.getCompanyId(),
				batchEngineTaskItemDelegate, parameters, user);

			Filter filter = _getFilter(
				batchEngineTaskItemDelegate, parameters, user);

			Map<String, Serializable> filteredParameters =
				_getFilteredParameters(parameters);

			Sort[] sorts = _getSorts(
				batchEngineTaskItemDelegate, parameters, user);

			Page<?> page = batchEngineTaskItemDelegate.read(
				filter, Pagination.of(1, exportBatchSize), sorts,
				filteredParameters, (String)parameters.get("search"));

			batchEngineExportTask.setTotalItemsCount(
				Math.toIntExact(page.getTotalCount()));

			Collection<?> items = page.getItems();

			while (!items.isEmpty()) {
				batchEngineExportTaskItemWriter.write(items);

				batchEngineExportTask.setProcessedItemsCount(
					batchEngineExportTask.getProcessedItemsCount() +
						items.size());

				if (settings.isPersist()) {
					batchEngineExportTask =
						_batchEngineExportTaskLocalService.
							updateBatchEngineExportTask(batchEngineExportTask);
				}

				if (Thread.interrupted()) {
					throw new InterruptedException();
				}

				if (!page.hasNext() ||
					(batchEngineExportTask.getProcessedItemsCount() >=
						maxItems)) {

					break;
				}

				page = batchEngineTaskItemDelegate.read(
					filter,
					Pagination.of((int)page.getPage() + 1, exportBatchSize),
					sorts, filteredParameters,
					(String)parameters.get("search"));

				items = page.getItems();
			}
		}
		finally {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				oldNestedFieldsContext);
		}

		byte[] content = unsyncByteArrayOutputStream.toByteArray();

		if (settings.isPersist()) {
			batchEngineExportTask.setContent(
				new OutputBlob(
					new UnsyncByteArrayInputStream(content), content.length));

			_batchEngineExportTaskLocalService.updateBatchEngineExportTask(
				batchEngineExportTask);
		}

		return new ByteArrayInputStream(content);
	}

	private BatchEngineExportTaskItemWriter _getBatchEngineExportTaskItemWriter(
			BatchEngineExportTask batchEngineExportTask,
			Map<String, Serializable> parameters, Settings settings,
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream)
		throws Exception {

		BatchEngineExportTaskItemWriterBuilder
			batchEngineExportTaskItemWriterBuilder =
				new BatchEngineExportTaskItemWriterBuilder();

		BatchEngineTaskContentType batchEngineTaskContentType =
			BatchEngineTaskContentType.valueOf(
				batchEngineExportTask.getContentType());

		OutputStream outputStream = unsyncByteArrayOutputStream;

		if (settings.isCompressContent()) {
			outputStream = _getZipOutputStream(
				batchEngineTaskContentType, unsyncByteArrayOutputStream);
		}

		return batchEngineExportTaskItemWriterBuilder.
			batchEngineTaskContentType(
				batchEngineTaskContentType
			).columnDescriptorProvider(
				_columnDescriptorProvider
			).companyId(
				batchEngineExportTask.getCompanyId()
			).csvFileColumnDelimiter(
				GetterUtil.getString(
					_getCSVFileColumnDelimiter(
						batchEngineExportTask.getCompanyId()),
					StringPool.COMMA)
			).fieldNames(
				batchEngineExportTask.getFieldNamesList()
			).itemClass(
				_itemClassRegistry.getItemClass(
					batchEngineExportTask.getClassName())
			).outputStream(
				outputStream
			).parameters(
				parameters
			).taskItemDelegateName(
				batchEngineExportTask.getTaskItemDelegateName()
			).userId(
				batchEngineExportTask.getUserId()
			).build();
	}

	private String _getCSVFileColumnDelimiter(long companyId) throws Exception {
		BatchEngineTaskCompanyConfiguration
			batchEngineTaskCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					BatchEngineTaskCompanyConfiguration.class, companyId);

		return batchEngineTaskCompanyConfiguration.csvFileColumnDelimiter();
	}

	private int _getExportBatchSize(long companyId)
		throws ConfigurationException {

		BatchEngineTaskCompanyConfiguration
			batchEngineTaskCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					BatchEngineTaskCompanyConfiguration.class, companyId);

		return batchEngineTaskCompanyConfiguration.exportBatchSize();
	}

	private Filter _getFilter(
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			Map<String, Serializable> parameters, User user)
		throws Exception {

		String filterString = (String)parameters.get("filter");

		if (Validator.isNull(filterString)) {
			return null;
		}

		EntityModel entityModel = batchEngineTaskItemDelegate.getEntityModel(
			_toMultivaluedMap(parameters));

		if (entityModel == null) {
			return null;
		}

		FilterParser filterParser = _filterParserProvider.provide(entityModel);

		com.liferay.portal.odata.filter.Filter oDataFilter =
			new com.liferay.portal.odata.filter.Filter(
				filterParser.parse(filterString));

		return _expressionConvert.convert(
			oDataFilter.getExpression(),
			LocaleUtil.fromLanguageId(user.getLanguageId()), entityModel);
	}

	private Map<String, Serializable> _getFilteredParameters(
		Map<String, Serializable> parameters) {

		Map<String, Serializable> filteredParameters = new HashMap<>(
			parameters);

		filteredParameters.remove("filter");
		filteredParameters.remove("search");
		filteredParameters.remove("sort");

		return filteredParameters;
	}

	private Map<String, Serializable> _getParameters(
		BatchEngineExportTask batchEngineExportTask) {

		Map<String, Serializable> parameters =
			batchEngineExportTask.getParameters();

		if (parameters == null) {
			parameters = new HashMap<>();
		}

		return parameters;
	}

	private Sort[] _getSorts(
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			Map<String, Serializable> parameters, User user)
		throws Exception {

		String sortString = (String)parameters.get("sort");

		if (Validator.isNull(sortString)) {
			return null;
		}

		EntityModel entityModel = batchEngineTaskItemDelegate.getEntityModel(
			_toMultivaluedMap(parameters));

		if (entityModel == null) {
			return null;
		}

		SortParser sortParser = _sortParserProvider.provide(entityModel);

		if (sortParser == null) {
			return null;
		}

		com.liferay.portal.odata.sort.Sort oDataSort =
			new com.liferay.portal.odata.sort.Sort(
				sortParser.parse(sortString));

		List<SortField> sortFields = oDataSort.getSortFields();

		Sort[] sorts = new Sort[sortFields.size()];

		for (int i = 0; i < sortFields.size(); i++) {
			SortField sortField = sortFields.get(i);

			sorts[i] = new Sort(
				sortField.getSortableFieldName(
					LocaleUtil.fromLanguageId(user.getLanguageId())),
				!sortField.isAscending());
		}

		return sorts;
	}

	private ZipOutputStream _getZipOutputStream(
			BatchEngineTaskContentType batchEngineTaskContentType,
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream)
		throws Exception {

		ZipOutputStream zipOutputStream = new ZipOutputStream(
			unsyncByteArrayOutputStream);

		ZipEntry zipEntry = new ZipEntry(
			"export." + batchEngineTaskContentType.getFileExtension());

		zipOutputStream.putNextEntry(zipEntry);

		return zipOutputStream;
	}

	private Map<String, List<String>> _toMultivaluedMap(
		Map<String, Serializable> parameterMap) {

		Map<String, List<String>> multivaluedMap = new HashMap<>();

		parameterMap.forEach(
			(key, value) -> multivaluedMap.put(
				key, Collections.singletonList(String.valueOf(value))));

		return multivaluedMap;
	}

	private void _updateBatchEngineExportTask(
		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus,
		BatchEngineExportTask batchEngineExportTask, String errorMessage,
		boolean persist) {

		batchEngineExportTask.setEndTime(new Date());
		batchEngineExportTask.setErrorMessage(errorMessage);
		batchEngineExportTask.setExecuteStatus(
			batchEngineTaskExecuteStatus.toString());

		if (persist) {
			batchEngineExportTask =
				_batchEngineExportTaskLocalService.updateBatchEngineExportTask(
					batchEngineExportTask);
		}

		BatchEngineTaskCallbackUtil.sendCallback(
			batchEngineExportTask.getCallbackURL(),
			batchEngineExportTask.getExecuteStatus(),
			batchEngineExportTask.getBatchEngineExportTaskId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineExportTaskExecutorImpl.class);

	@Reference
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Reference
	private BatchEngineTaskItemDelegateRegistry
		_batchEngineTaskItemDelegateRegistry;

	@Reference
	private ColumnDescriptorProvider _columnDescriptorProvider;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(result.class.name=com.liferay.portal.kernel.search.filter.Filter)"
	)
	private ExpressionConvert<Filter> _expressionConvert;

	@Reference
	private FilterParserProvider _filterParserProvider;

	@Reference
	private ItemClassRegistry _itemClassRegistry;

	@Reference
	private SortParserProvider _sortParserProvider;

	@Reference
	private UserLocalService _userLocalService;

}