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
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;

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
		String className, String itemClassName, String taskItemDelegateName) {

		_batchEngineExportTaskExecutor = batchEngineExportTaskExecutor;
		_batchEngineExportTaskService = batchEngineExportTaskService;
		_batchEngineImportTaskExecutor = batchEngineImportTaskExecutor;
		_batchEngineImportTaskService = batchEngineImportTaskService;
		_className = className;
		_itemClassName = itemClassName;
		_taskItemDelegateName = taskItemDelegateName;

		_deletionsFileName = taskItemDelegateName + "_deletions.json";
		_fileName = taskItemDelegateName + ".json";

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
			_deletionsFileName, jsonArray.toString());
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
	public boolean isCompany() {
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
			_deletionsFileName);

		if (inputStream == null) {
			return portletPreferences;
		}

		BatchEngineImportTask batchEngineDeleteTask =
			_batchEngineImportTaskService.addBatchEngineImportTask(
				null, portletDataContext.getCompanyId(), _getUserId(), 100,
				null, _className, _getBytes(_fileName, inputStream), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
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

		BatchEngineExportTaskExecutor.Result result =
			_batchEngineExportTaskExecutor.execute(
				_batchEngineExportTaskService.addBatchEngineExportTask(
					null, portletDataContext.getCompanyId(), _getUserId(), null,
					_className, "JSON",
					BatchEngineTaskExecuteStatus.INITIAL.name(),
					Collections.emptyList(),
					BatchEnginePortletDataHandlerUtil.buildParameters(
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

		portletDataContext.addZipEntry(_fileName, result.getInputStream());

		portletDataContext.setValidateExistingDataHandler(true);

		return getExportDataRootElementString(
			addExportDataRootElement(portletDataContext));
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		InputStream inputStream = portletDataContext.getZipEntryAsInputStream(
			_fileName);

		if (inputStream == null) {
			return portletPreferences;
		}

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskService.addBatchEngineImportTask(
				null, portletDataContext.getCompanyId(), _getUserId(), 100,
				null, _className, _getBytes(_fileName, inputStream), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyMap(),
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE,
				BatchEngineTaskOperation.CREATE.name(),
				HashMapBuilder.<String, Serializable>put(
					"batchRestrictFields",
					() -> {
						if (!MapUtil.getBoolean(
								portletDataContext.getParameterMap(),
								PortletDataHandlerKeys.PERMISSIONS)) {

							return "permissions";
						}

						return null;
					}
				).put(
					"createStrategy", CreateStrategy.UPSERT.getDBOperation()
				).put(
					"importCreatorStrategy",
					() -> {
						if (!UserIdStrategy.CURRENT_USER_ID.equals(
								MapUtil.getString(
									portletDataContext.getParameterMap(),
									PortletDataHandlerKeys.USER_ID_STRATEGY))) {

							return null;
						}

						return BatchEngineImportTaskConstants.
							IMPORT_CREATOR_STRATEGY_KEEP_CREATOR;
					}
				).build(),
				_taskItemDelegateName);

		try {
			BatchEngineImportTask finalBatchEngineImportTask =
				batchEngineImportTask;

			TransactionInvokerUtil.invoke(
				transactionConfig,
				() -> {
					_batchEngineImportTaskExecutor.execute(
						finalBatchEngineImportTask);

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