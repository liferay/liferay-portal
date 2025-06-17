/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.exportimport.data.handler;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.batch.engine.internal.lar.PortletDataContextThreadLocal;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.portlet.PortletPreferences;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Vendel Toreki
 * @author Alejandro Tardín
 */
public class BatchEnginePortletDataHandler extends BasePortletDataHandler {

	public static final String BATCH_DELETE_CLASS_NAME_POSTFIX =
		"_batchDeleteExternalReferenceCodes";

	public static final String SCHEMA_VERSION = "4.0.0";

	public BatchEnginePortletDataHandler(
		BatchEngineExportTaskExecutor batchEngineExportTaskExecutor,
		BatchEngineExportTaskService batchEngineExportTaskService,
		BatchEngineImportTaskExecutor batchEngineImportTaskExecutor,
		BatchEngineImportTaskService batchEngineImportTaskService,
		String className, String itemClassName,
		ExportImportVulcanBatchEngineTaskItemDelegate.Scope scope,
		String taskItemDelegateName) {

		_batchEngineExportTaskExecutor = batchEngineExportTaskExecutor;
		_batchEngineExportTaskService = batchEngineExportTaskService;
		_batchEngineImportTaskExecutor = batchEngineImportTaskExecutor;
		_batchEngineImportTaskService = batchEngineImportTaskService;
		_className = className;
		_itemClassName = itemClassName;
		_taskItemDelegateName = taskItemDelegateName;

		String fileNamePrefix = GetterUtil.getString(
			taskItemDelegateName, className);

		_deletionsFileName = fileNamePrefix + "_deletions.json";

		_fileName = fileNamePrefix + ".json";

		if (ExportImportVulcanBatchEngineTaskItemDelegate.Scope.COMPANY.equals(
				scope)) {

			setDataLevel(DataLevel.PORTAL);
		}
		else {
			setDataLevel(DataLevel.SITE);
		}

		setEmptyControlsAllowed(true);
	}

	public void exportDeletionSystemEvents(
		PortletDataContext portletDataContext) {

		Map<String, String> map =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				_itemClassName + BATCH_DELETE_CLASS_NAME_POSTFIX);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (String key : map.keySet()) {
			jsonArray.put(
				JSONUtil.put("externalReferenceCode", String.valueOf(key)));
		}

		portletDataContext.addZipEntry(
			_normalize(
				_deletionsFileName, portletDataContext.getScopeGroupId()),
			jsonArray.toString());
	}

	@Override
	public String[] getClassNames() {
		return new String[] {_className};
	}

	@Override
	public StagedModelType[] getDeletionSystemEventStagedModelTypes() {
		return new StagedModelType[] {new StagedModelType(_itemClassName)};
	}

	@Override
	public String getName() {
		return _className + StringPool.POUND + _taskItemDelegateName;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean isBatch() {
		return true;
	}

	@Override
	public boolean isModelCountSupported() {
		return false;
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.getZipReader() == null) {
			return portletPreferences;
		}

		InputStream inputStream = portletDataContext.getZipEntryAsInputStream(
			_normalize(
				_deletionsFileName, portletDataContext.getSourceGroupId()));

		if (inputStream == null) {
			return portletPreferences;
		}

		BatchEngineImportTask batchEngineDeleteTask =
			_batchEngineImportTaskService.addBatchEngineImportTask(
				null, portletDataContext.getCompanyId(), _getUserId(), 100,
				null, _className,
				_getBytes(
					_normalize(
						_fileName, portletDataContext.getSourceGroupId()),
					inputStream),
				"JSON", BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyMap(),
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE,
				BatchEngineTaskOperation.DELETE.name(),
				HashMapBuilder.<String, Serializable>put(
					"createStrategy", CreateStrategy.UPSERT.getDBOperation()
				).build(),
				_taskItemDelegateName);

		_batchEngineImportTaskExecutor.execute(batchEngineDeleteTask);

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		try (SafeCloseable safeCloseable =
				PortletDataContextThreadLocal.
					setPortletDataContextWithSafeCloseable(
						portletDataContext)) {

			BatchEngineExportTaskExecutor.Result result =
				_batchEngineExportTaskExecutor.execute(
					_batchEngineExportTaskService.addBatchEngineExportTask(
						null, portletDataContext.getCompanyId(), _getUserId(),
						null, _className, "JSON",
						BatchEngineTaskExecuteStatus.INITIAL.name(),
						Collections.emptyList(),
						BatchEnginePortletDataHandlerUtil.buildExportParameters(
							portletDataContext),
						_taskItemDelegateName),
					new BatchEngineExportTaskExecutor.Settings() {

						@Override
						public boolean isCompressContent() {
							return false;
						}

						@Override
						public boolean isPersistContent() {
							return false;
						}

					});

			portletDataContext.addZipEntry(
				_normalize(_fileName, portletDataContext.getScopeGroupId()),
				result.getInputStream());

			portletDataContext.setValidateExistingDataHandler(true);

			return getExportDataRootElementString(
				addExportDataRootElement(portletDataContext));
		}
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		String normalizedFileName = _normalize(
			_fileName, portletDataContext.getSourceGroupId());

		InputStream inputStream = portletDataContext.getZipEntryAsInputStream(
			normalizedFileName);

		if (inputStream == null) {
			return portletPreferences;
		}

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskService.addBatchEngineImportTask(
				null, portletDataContext.getCompanyId(), _getUserId(), 100,
				null, _className, _getBytes(normalizedFileName, inputStream),
				"JSON", BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyMap(),
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE,
				BatchEngineTaskOperation.CREATE.name(),
				BatchEnginePortletDataHandlerUtil.buildImportParameters(
					portletDataContext),
				_taskItemDelegateName);

		try {
			BatchEngineImportTask finalBatchEngineImportTask =
				batchEngineImportTask;

			TransactionInvokerUtil.invoke(
				transactionConfig,
				() -> {
					try (SafeCloseable safeCloseable =
							PortletDataContextThreadLocal.
								setPortletDataContextWithSafeCloseable(
									portletDataContext)) {

						_batchEngineImportTaskExecutor.execute(
							finalBatchEngineImportTask);
					}

					return null;
				});
		}
		catch (Throwable throwable) {
			throw new PortletDataException(throwable);
		}

		batchEngineImportTask =
			_batchEngineImportTaskService.getBatchEngineImportTask(
				batchEngineImportTask.getBatchEngineImportTaskId());

		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
			BatchEngineTaskExecuteStatus.valueOf(
				batchEngineImportTask.getExecuteStatus());

		if (batchEngineTaskExecuteStatus ==
				BatchEngineTaskExecuteStatus.FAILED) {

			throw new PortletDataException(
				"Unable to import batch data: " +
					batchEngineImportTask.getErrorMessage());
		}

		return portletPreferences;
	}

	@Override
	protected long getExportModelCount(
		ManifestSummary manifestSummary,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		// TODO LPD-45048

		return 0;
	}

	protected static final TransactionConfig transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	private byte[] _getBytes(String fileName, InputStream inputStream)
		throws Exception {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				unsyncByteArrayOutputStream)) {

			ZipEntry zipEntry = new ZipEntry(fileName);

			zipOutputStream.putNextEntry(zipEntry);

			StreamUtil.transfer(inputStream, zipOutputStream, false);
		}

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private long _getUserId() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.getUserId();
	}

	private String _normalize(String fileName, long groupId) {
		return StringBundler.concat(
			StringPool.FORWARD_SLASH, ExportImportPathUtil.PATH_PREFIX_GROUP,
			StringPool.FORWARD_SLASH, groupId, StringPool.FORWARD_SLASH,
			fileName);
	}

	private final BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;
	private final BatchEngineExportTaskService _batchEngineExportTaskService;
	private final BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;
	private final BatchEngineImportTaskService _batchEngineImportTaskService;
	private final String _className;
	private final String _deletionsFileName;
	private final String _fileName;
	private final String _itemClassName;
	private final String _taskItemDelegateName;

}