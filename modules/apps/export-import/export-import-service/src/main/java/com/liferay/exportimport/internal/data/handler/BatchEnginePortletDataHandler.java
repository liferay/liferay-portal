/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.exportimport.internal.lar.PortletDataContextThreadLocal;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletPreferences;

import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		BatchEngineExportTaskLocalService batchEngineExportTaskLocalService,
		BatchEngineImportTaskExecutor batchEngineImportTaskExecutor,
		BatchEngineImportTaskService batchEngineImportTaskService,
		GroupLocalService groupLocalService,
		StagingGroupHelper stagingGroupHelper) {

		_batchEngineExportTaskExecutor = batchEngineExportTaskExecutor;
		_batchEngineExportTaskLocalService = batchEngineExportTaskLocalService;
		_batchEngineImportTaskExecutor = batchEngineImportTaskExecutor;
		_batchEngineImportTaskService = batchEngineImportTaskService;
		_groupLocalService = groupLocalService;
		_stagingGroupHelper = stagingGroupHelper;
	}

	public void exportDeletionSystemEvents(
		PortletDataContext portletDataContext) {

		for (Registration registration :
				_getActiveRegistrations(portletDataContext)) {

			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			Map<String, String> map =
				(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
					exportImportDescriptor.getItemClassName() +
						BATCH_DELETE_CLASS_NAME_POSTFIX);

			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			for (String key : map.keySet()) {
				jsonArray.put(
					JSONUtil.put("externalReferenceCode", String.valueOf(key)));
			}

			portletDataContext.addZipEntry(
				_normalize(
					registration.getDeletionsFileName(),
					portletDataContext.getScopeGroupId()),
				jsonArray.toString());
		}
	}

	@Override
	public String[] getClassNames() {
		return TransformUtil.transformToArray(
			_registrations, Registration::getClassName, String.class);
	}

	@Override
	public String getName() {
		return getPortletId();
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public boolean isBatch() {
		return true;
	}

	public void registerExportImportVulcanBatchEngineTaskItemDelegate(
		String className,
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		String taskItemDelegateName) {

		String fileNamePrefix = GetterUtil.getString(
			taskItemDelegateName, className);

		_registrations.add(
			new Registration() {

				@Override
				public String getClassName() {
					return className;
				}

				@Override
				public String getDeletionsFileName() {
					return fileNamePrefix + "_deletions.json";
				}

				@Override
				public ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor getExportImportDescriptor() {

					return exportImportDescriptor;
				}

				@Override
				public String getFileName() {
					return fileNamePrefix + ".json";
				}

				@Override
				public String getTaskItemDelegateName() {
					return taskItemDelegateName;
				}

			});

		if (ExportImportVulcanBatchEngineTaskItemDelegate.Scope.COMPANY.equals(
				exportImportDescriptor.getScope())) {

			setDataLevel(DataLevel.PORTAL);
		}
		else if (ExportImportVulcanBatchEngineTaskItemDelegate.Scope.DEPOT.
					equals(exportImportDescriptor.getScope())) {

			setDataLevel(DataLevel.DEPOT);
		}
		else if (ExportImportVulcanBatchEngineTaskItemDelegate.Scope.SITE.
					equals(exportImportDescriptor.getScope())) {

			setDataLevel(DataLevel.SITE);
		}

		_updateDeletionSystemEventStagedModelTypes();
		_updateExportControls();
	}

	public void unregisterExportImportVulcanBatchEngineTaskItemDelegate(
		String className, String taskItemDelegateName) {

		Iterator<Registration> iterator = _registrations.iterator();

		while (iterator.hasNext()) {
			Registration registration = iterator.next();

			if (Objects.equals(registration.getClassName(), className) &&
				Objects.equals(
					registration.getTaskItemDelegateName(),
					taskItemDelegateName)) {

				iterator.remove();

				_updateDeletionSystemEventStagedModelTypes();
				_updateExportControls();

				return;
			}
		}
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.getZipReader() == null) {
			return portletPreferences;
		}

		for (Registration registration :
				_getActiveRegistrations(portletDataContext)) {

			InputStream inputStream =
				portletDataContext.getZipEntryAsInputStream(
					_normalize(
						registration.getDeletionsFileName(),
						portletDataContext.getSourceGroupId()));

			if (inputStream == null) {
				return portletPreferences;
			}

			BatchEngineImportTask batchEngineDeleteTask =
				_batchEngineImportTaskService.addBatchEngineImportTask(
					null, portletDataContext.getCompanyId(), _getUserId(), 100,
					null, registration.getClassName(),
					_getBytes(
						_normalize(
							registration.getFileName(),
							portletDataContext.getSourceGroupId()),
						inputStream),
					"JSON", BatchEngineTaskExecuteStatus.INITIAL.name(),
					Collections.emptyMap(),
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_CONTINUE,
					BatchEngineTaskOperation.DELETE.name(),
					HashMapBuilder.<String, Serializable>put(
						"createStrategy", CreateStrategy.UPSERT.getDBOperation()
					).build(),
					registration.getTaskItemDelegateName());

			_batchEngineImportTaskExecutor.execute(batchEngineDeleteTask);
		}

		return portletPreferences;
	}

	@Override
	protected String doExportData(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		try (SafeCloseable safeCloseable =
				PortletDataContextThreadLocal.
					setPortletDataContextWithSafeCloseable(
						portletDataContext)) {

			List<Registration> activeRegistrations = _getActiveRegistrations(
				portletDataContext);

			for (Registration registration : activeRegistrations) {
				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor exportImportDescriptor =
						registration.getExportImportDescriptor();

				if ((activeRegistrations.size() > 1) &&
					!portletDataContext.getBooleanParameter(
						getPortletId(),
						exportImportDescriptor.getItemClassName())) {

					continue;
				}

				BatchEngineExportTaskExecutor.Result result =
					_executeExportTask(
						Integer.MAX_VALUE, portletDataContext, registration);

				if (result == null) {
					continue;
				}

				portletDataContext.addZipEntry(
					_normalize(
						registration.getFileName(),
						portletDataContext.getScopeGroupId()),
					result.getInputStream());

				ManifestSummary manifestSummary =
					portletDataContext.getManifestSummary();

				BatchEngineExportTask batchEngineExportTask =
					result.getBatchEngineExportTask();

				manifestSummary.addModelAdditionCount(
					_getStagedModelType(registration),
					batchEngineExportTask.getProcessedItemsCount());
			}

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

		List<Registration> activeRegistrations = _getActiveRegistrations(
			portletDataContext);

		for (Registration registration : activeRegistrations) {
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			if ((activeRegistrations.size() > 1) &&
				!portletDataContext.getBooleanParameter(
					getPortletId(),
					exportImportDescriptor.getItemClassName())) {

				continue;
			}

			String normalizedFileName = _normalize(
				registration.getFileName(),
				portletDataContext.getSourceGroupId());

			InputStream inputStream =
				portletDataContext.getZipEntryAsInputStream(normalizedFileName);

			if (inputStream == null) {
				continue;
			}

			BatchEngineImportTask batchEngineImportTask =
				_batchEngineImportTaskService.addBatchEngineImportTask(
					null, portletDataContext.getCompanyId(), _getUserId(), 100,
					null, registration.getClassName(),
					_getBytes(normalizedFileName, inputStream), "JSON",
					BatchEngineTaskExecuteStatus.INITIAL.name(),
					Collections.emptyMap(),
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_CONTINUE,
					BatchEngineTaskOperation.CREATE.name(),
					BatchEnginePortletDataHandlerUtil.buildImportParameters(
						registration.getExportImportDescriptor(),
						portletDataContext,
						_getSiteExternalReferenceCode(portletDataContext)),
					registration.getTaskItemDelegateName());

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
		}

		return portletPreferences;
	}

	@Override
	protected void doPrepareManifestSummary(
		PortletDataContext portletDataContext,
		PortletPreferences portletPreferences) {

		for (Registration registration :
				_getActiveRegistrations(portletDataContext)) {

			try (SafeCloseable safeCloseable =
					PortletDataContextThreadLocal.
						setPortletDataContextWithSafeCloseable(
							portletDataContext)) {

				BatchEngineExportTaskExecutor.Result result =
					_executeExportTask(1, portletDataContext, registration);

				if (result == null) {
					continue;
				}

				BatchEngineExportTask batchEngineExportTask =
					result.getBatchEngineExportTask();

				ManifestSummary manifestSummary =
					portletDataContext.getManifestSummary();

				manifestSummary.addModelAdditionCount(
					_getStagedModelType(registration),
					batchEngineExportTask.getTotalItemsCount());
			}
		}
	}

	@Override
	protected long getExportModelCount(
		ManifestSummary manifestSummary,
		PortletDataHandlerControl[] portletDataHandlerControls) {

		if (_registrations.size() == 1) {
			return super.getExportModelCount(
				manifestSummary,
				new PortletDataHandlerControl[] {
					_getPortletDataHandlerControl(_registrations.get(0))
				});
		}

		return super.getExportModelCount(
			manifestSummary, portletDataHandlerControls);
	}

	protected static final TransactionConfig transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	private BatchEngineExportTaskExecutor.Result _executeExportTask(
		int maxItems, PortletDataContext portletDataContext,
		Registration registration) {

		return _batchEngineExportTaskExecutor.execute(
			_batchEngineExportTaskLocalService.createBatchEngineExportTask(
				0L, null, portletDataContext.getCompanyId(), _getUserId(), null,
				registration.getClassName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyList(),
				BatchEnginePortletDataHandlerUtil.buildExportParameters(
					registration.getExportImportDescriptor(),
					portletDataContext,
					_getSiteExternalReferenceCode(portletDataContext)),
				registration.getTaskItemDelegateName()),
			new BatchEngineExportTaskExecutor.Settings() {

				@Override
				public int getMaxItems() {
					return maxItems;
				}

				@Override
				public boolean isCompressContent() {
					return false;
				}

				@Override
				public boolean isPersist() {
					return false;
				}

			});
	}

	private List<Registration> _getActiveRegistrations(
		PortletDataContext portletDataContext) {

		return ListUtil.filter(
			_registrations,
			registration -> {
				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor exportImportDescriptor =
						registration.getExportImportDescriptor();

				return exportImportDescriptor.isActive(portletDataContext);
			});
	}

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

	private PortletDataHandlerControl _getPortletDataHandlerControl(
		Registration registration) {

		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor = registration.getExportImportDescriptor();

		return new PortletDataHandlerBoolean(
			getPortletId(), exportImportDescriptor.getItemClassName(),
			exportImportDescriptor.getLabel(), true, false, null,
			exportImportDescriptor.getItemClassName(), null);
	}

	private String _getSiteExternalReferenceCode(
		PortletDataContext portletDataContext) {

		Group group = _groupLocalService.fetchGroup(
			portletDataContext.getScopeGroupId());

		if ((group != null) && !_stagingGroupHelper.isCompanyGroup(group)) {
			return group.getExternalReferenceCode();
		}

		return null;
	}

	private StagedModelType _getStagedModelType(Registration registration) {
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor = registration.getExportImportDescriptor();

		return new StagedModelType(exportImportDescriptor.getItemClassName());
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

	private void _updateDeletionSystemEventStagedModelTypes() {
		setDeletionSystemEventStagedModelTypes(
			TransformUtil.transformToArray(
				_registrations, this::_getStagedModelType,
				StagedModelType.class));
	}

	private void _updateExportControls() {
		if (_registrations.size() > 1) {
			setExportControls(
				TransformUtil.transformToArray(
					_registrations, this::_getPortletDataHandlerControl,
					PortletDataHandlerControl.class));

			setEmptyControlsAllowed(false);
		}
		else {
			setEmptyControlsAllowed(true);
			setExportControls();
		}
	}

	private final BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;
	private final BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;
	private final BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;
	private final BatchEngineImportTaskService _batchEngineImportTaskService;
	private final GroupLocalService _groupLocalService;
	private final List<Registration> _registrations = new ArrayList<>();
	private final StagingGroupHelper _stagingGroupHelper;

	private interface Registration {

		public String getClassName();

		public String getDeletionsFileName();

		public
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				getExportImportDescriptor();

		public String getFileName();

		public String getTaskItemDelegateName();

	}

}