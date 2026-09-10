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
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.exportimport.internal.lar.ExportImportDescriptorThreadLocal;
import com.liferay.exportimport.internal.lar.PortletDataContextImpl;
import com.liferay.exportimport.internal.lar.PortletDataContextThreadLocal;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.systemevent.SystemEventExtraDataContributor;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletPreferences;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Vendel Toreki
 * @author Alejandro Tardín
 */
public class BatchEnginePortletDataHandler extends BasePortletDataHandler {

	public static final String SCHEMA_VERSION = "4.0.0";

	public BatchEnginePortletDataHandler(
		BatchEngineExportTaskExecutor batchEngineExportTaskExecutor,
		BatchEngineExportTaskLocalService batchEngineExportTaskLocalService,
		BatchEngineImportTaskExecutor batchEngineImportTaskExecutor,
		BatchEngineImportTaskService batchEngineImportTaskService,
		ChangesetEntryLocalService changesetEntryLocalService,
		ClassNameLocalService classNameLocalService,
		ExportImportHelper exportImportHelper,
		GroupLocalService groupLocalService,
		StagingGroupHelper stagingGroupHelper) {

		_batchEngineExportTaskExecutor = batchEngineExportTaskExecutor;
		_batchEngineExportTaskLocalService = batchEngineExportTaskLocalService;
		_batchEngineImportTaskExecutor = batchEngineImportTaskExecutor;
		_batchEngineImportTaskService = batchEngineImportTaskService;
		_changesetEntryLocalService = changesetEntryLocalService;
		_classNameLocalService = classNameLocalService;
		_exportImportHelper = exportImportHelper;
		_groupLocalService = groupLocalService;
		_stagingGroupHelper = stagingGroupHelper;
	}

	public void exportDeletionSystemEvents(
			String className, PortletDataContext portletDataContext,
			List<SystemEvent> systemEvents)
		throws Exception {

		List<Registration> activeRegistrations = _getActiveRegistrations(
			portletDataContext);

		Map<String, List<String>> typedExternalReferenceCodesMap =
			new HashMap<>();
		List<String> untypedExternalReferenceCodes = new ArrayList<>();

		for (SystemEvent systemEvent : systemEvents) {
			String externalReferenceCode =
				systemEvent.getClassExternalReferenceCode();

			if (externalReferenceCode == null) {
				continue;
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				systemEvent.getExtraData());

			String type = jsonObject.getString("type");

			if (Validator.isNull(type)) {
				untypedExternalReferenceCodes.add(externalReferenceCode);
			}
			else {
				typedExternalReferenceCodesMap.computeIfAbsent(
					type, __ -> new ArrayList<>()
				).add(
					externalReferenceCode
				);
			}
		}

		ManifestSummary manifestSummary =
			portletDataContext.getManifestSummary();

		for (Registration registration : activeRegistrations) {
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			if (!StringUtil.equals(
					className, exportImportDescriptor.getModelClassName()) ||
				((activeRegistrations.size() > 1) &&
				 !portletDataContext.getBooleanParameter(
					 getPortletId(), exportImportDescriptor.getKey()))) {

				continue;
			}

			List<String> externalReferenceCodes = new ArrayList<>(
				typedExternalReferenceCodesMap.getOrDefault(
					exportImportDescriptor.getKey(), Collections.emptyList()));

			externalReferenceCodes.addAll(untypedExternalReferenceCodes);

			if (externalReferenceCodes.isEmpty()) {
				continue;
			}

			portletDataContext.addZipEntry(
				_normalize(
					registration.getDeletionsFileName(),
					portletDataContext.getScopeGroupId()),
				JSONUtil.toJSONArray(
					externalReferenceCodes,
					externalReferenceCode -> JSONUtil.put(
						"externalReferenceCode", externalReferenceCode)
				).toString());

			manifestSummary.addModelDeletionCount(
				exportImportDescriptor.getKey(), externalReferenceCodes.size());
		}

		manifestSummary.addModelDeletionCount(className, systemEvents.size());
	}

	@Override
	public String[] getClassNames() {
		Set<String> classNames = new LinkedHashSet<>();

		for (Registration registration : _registrations) {
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			classNames.add(exportImportDescriptor.getModelClassName());
		}

		return classNames.toArray(new String[0]);
	}

	@Override
	public String getDescription(Locale locale) {
		return _getSoleProperty(
			exportImportDescriptor -> exportImportDescriptor.getDescription(
				locale));
	}

	public String getKey() {
		return _getSoleProperty(
			ExportImportVulcanBatchEngineTaskItemDelegate.
				ExportImportDescriptor::getKey);
	}

	@Override
	public int getRank() {
		if (_registrations.isEmpty()) {
			return super.getRank();
		}

		int rank = Integer.MAX_VALUE;

		for (Registration registration : _registrations) {
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			if (exportImportDescriptor.getRank() < rank) {
				rank = exportImportDescriptor.getRank();
			}
		}

		return rank;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public String getSectionKey() {
		Set<String> sectionKeys = SetUtil.fromList(
			TransformUtil.transform(
				_registrations,
				registration -> {
					ExportImportVulcanBatchEngineTaskItemDelegate.
						ExportImportDescriptor exportImportDescriptor =
							registration.getExportImportDescriptor();

					return exportImportDescriptor.getSectionKey();
				}));

		if (sectionKeys.isEmpty()) {
			return super.getSectionKey();
		}

		Iterator<String> iterator = sectionKeys.iterator();

		return iterator.next();
	}

	@Override
	public String getTag(Locale locale) {
		return _getSoleProperty(
			exportImportDescriptor -> exportImportDescriptor.getTag(locale));
	}

	@Override
	public String getTitle(Locale locale) {
		String labelLanguageKey = _getSoleProperty(
			ExportImportVulcanBatchEngineTaskItemDelegate.
				ExportImportDescriptor::getLabelLanguageKey);

		if (labelLanguageKey == null) {
			return super.getTitle(locale);
		}

		return LanguageUtil.get(locale, labelLanguageKey);
	}

	@Override
	public boolean isBatch() {
		return true;
	}

	@Override
	public boolean isConfigurationEnabled() {
		long companyId = CompanyThreadLocal.getCompanyId();

		PortletDataContext portletDataContext = new PortletDataContextImpl(
			null, false);

		portletDataContext.setCompanyId(companyId);

		List<Registration> activeRegistrations = _getActiveRegistrations(
			portletDataContext, true);

		return !activeRegistrations.isEmpty();
	}

	@Override
	public boolean isHidden() {
		return Boolean.TRUE.equals(
			_getSoleProperty(
				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor::isHidden));
	}

	public boolean isMissingPortletSupported() {
		return Boolean.TRUE.equals(
			_getSoleProperty(
				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor::isMissingPortletSupported));
	}

	@Override
	public boolean isStaged() {
		return !StringUtil.startsWith(
			getPortletId(), ObjectPortletKeys.OBJECT_DEFINITIONS);
	}

	public void registerExportImportVulcanBatchEngineTaskItemDelegate(
		String batchEngineClassName, BundleContext bundleContext,
		long companyId,
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		String taskItemDelegateName) {

		_registrations.add(
			new Registration() {

				@Override
				public String getBatchEngineClassName() {
					return batchEngineClassName;
				}

				@Override
				public String getDeletionsFileName() {
					return exportImportDescriptor.getKey() + "_deletions.json";
				}

				@Override
				public ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor getExportImportDescriptor() {

					return exportImportDescriptor;
				}

				@Override
				public String getFileName() {
					return exportImportDescriptor.getKey() + ".json";
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

		setPublishToLiveByDefault(true);
		_updateDeletionSystemEventStagedModelTypes();
		_updatePortletDataHandlerControls();

		_updateSystemEventExtraDataContributor(bundleContext, companyId);
	}

	public ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
		unregisterExportImportVulcanBatchEngineTaskItemDelegate(
			BundleContext bundleContext, long companyId, String key) {

		Iterator<Registration> iterator = _registrations.iterator();

		while (iterator.hasNext()) {
			Registration registration = iterator.next();

			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			if (StringUtil.equals(key, exportImportDescriptor.getKey())) {
				iterator.remove();

				_updateDeletionSystemEventStagedModelTypes();
				_updatePortletDataHandlerControls();

				_updateSystemEventExtraDataContributor(
					bundleContext, companyId);

				return exportImportDescriptor;
			}
		}

		return null;
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
				continue;
			}

			BatchEngineImportTask batchEngineDeleteTask =
				_batchEngineImportTaskService.addBatchEngineImportTask(
					null, portletDataContext.getCompanyId(), _getUserId(), 100,
					null, registration.getBatchEngineClassName(),
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
					BatchEnginePortletDataHandlerUtil.buildDeleteParameters(
						registration.getExportImportDescriptor(),
						_groupLocalService, portletDataContext,
						_stagingGroupHelper),
					registration.getTaskItemDelegateName());

			_batchEngineImportTaskExecutor.execute(batchEngineDeleteTask);
		}

		return portletPreferences;
	}

	@Override
	protected String doExportData(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		try (SafeCloseable safeCloseable1 =
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
						getPortletId(), exportImportDescriptor.getKey())) {

					continue;
				}

				try (SafeCloseable safeCloseable2 =
						ExportImportDescriptorThreadLocal.
							setExportImportDescriptorWithSafeCloseable(
								exportImportDescriptor)) {

					BatchEngineExportTaskExecutor.Result result =
						_executeExportTask(
							Integer.MAX_VALUE, portletDataContext,
							registration);

					if (result == null) {
						continue;
					}

					BatchEngineExportTask batchEngineExportTask =
						result.getBatchEngineExportTask();

					if (batchEngineExportTask.getTotalItemsCount() == 0) {
						continue;
					}

					portletDataContext.addZipEntry(
						_normalize(
							registration.getFileName(),
							portletDataContext.getScopeGroupId()),
						result.getInputStream());

					ManifestSummary manifestSummary =
						portletDataContext.getManifestSummary();

					manifestSummary.addModelAdditionCount(
						exportImportDescriptor.getKey(),
						batchEngineExportTask.getProcessedItemsCount());
				}
			}

			if (_stagingGroupHelper.isCompanyGroup(
					portletDataContext.getCompanyId(),
					portletDataContext.getScopeGroupId())) {

				portletDataContext.setValidateExistingDataHandler(
					!isMissingPortletSupported() || (getKey() == null));
			}
			else {
				portletDataContext.setValidateExistingDataHandler(true);
			}

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
					getPortletId(), exportImportDescriptor.getKey())) {

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
					null, registration.getBatchEngineClassName(),
					_getBytes(normalizedFileName, inputStream), "JSON",
					BatchEngineTaskExecuteStatus.INITIAL.name(),
					Collections.emptyMap(),
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_CONTINUE,
					BatchEngineTaskOperation.CREATE.name(),
					BatchEnginePortletDataHandlerUtil.buildImportParameters(
						registration.getExportImportDescriptor(),
						_groupLocalService, portletDataContext,
						_stagingGroupHelper),
					registration.getTaskItemDelegateName());

			try (SafeCloseable safeCloseable1 =
					ExportImportDescriptorThreadLocal.
						setExportImportDescriptorWithSafeCloseable(
							exportImportDescriptor);
				SafeCloseable safeCloseable2 =
					PortletDataContextThreadLocal.
						setPortletDataContextWithSafeCloseable(
							portletDataContext)) {

				_batchEngineImportTaskExecutor.execute(batchEngineImportTask);
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
			PortletPreferences portletPreferences)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				PortletDataContextThreadLocal.
					setPortletDataContextWithSafeCloseable(
						portletDataContext)) {

			ExportImportThreadLocal.setPortletExportInProcess(true);

			ManifestSummary manifestSummary =
				portletDataContext.getManifestSummary();
			Set<String> sharedClassNames = _getSharedClassNames();

			for (Registration registration :
					_getActiveRegistrations(portletDataContext)) {

				BatchEngineExportTaskExecutor.Result result =
					_executeExportTask(1, portletDataContext, registration);

				if (result == null) {
					continue;
				}

				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor exportImportDescriptor =
						registration.getExportImportDescriptor();
				BatchEngineExportTask batchEngineExportTask =
					result.getBatchEngineExportTask();

				manifestSummary.addModelAdditionCount(
					exportImportDescriptor.getKey(),
					batchEngineExportTask.getTotalItemsCount());

				String type = null;

				if (sharedClassNames.contains(
						exportImportDescriptor.getModelClassName())) {

					type = exportImportDescriptor.getKey();
				}

				manifestSummary.addModelDeletionCount(
					exportImportDescriptor.getKey(),
					_exportImportHelper.getModelDeletionCount(
						portletDataContext,
						new StagedModelType(
							exportImportDescriptor.getModelClassName(),
							StagedModelType.REFERRER_CLASS_NAME_ALL),
						type));
			}

			for (String modelClassName : getClassNames()) {
				manifestSummary.addModelDeletionCount(
					modelClassName,
					_exportImportHelper.getModelDeletionCount(
						portletDataContext,
						new StagedModelType(
							modelClassName,
							StagedModelType.REFERRER_CLASS_NAME_ALL)));
			}
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
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
				registration.getBatchEngineClassName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyList(),
				BatchEnginePortletDataHandlerUtil.buildExportParameters(
					_changesetEntryLocalService, _classNameLocalService,
					registration.getExportImportDescriptor(),
					_groupLocalService, portletDataContext,
					_stagingGroupHelper),
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

		return _getActiveRegistrations(
			portletDataContext, ExportImportThreadLocal.isStagingInProcess());
	}

	private List<Registration> _getActiveRegistrations(
		PortletDataContext portletDataContext, boolean stagingSupportedOnly) {

		return ListUtil.filter(
			_registrations,
			registration -> {
				ExportImportVulcanBatchEngineTaskItemDelegate.
					ExportImportDescriptor exportImportDescriptor =
						registration.getExportImportDescriptor();

				if (!exportImportDescriptor.isActive(portletDataContext) ||
					(stagingSupportedOnly &&
					 !exportImportDescriptor.isStagingSupported())) {

					return false;
				}

				return true;
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
			getPortletId(), exportImportDescriptor.getKey(),
			exportImportDescriptor.getLabelLanguageKey(), true, false, null,
			exportImportDescriptor.getKey(), null);
	}

	private Set<String> _getSharedClassNames() {
		Set<String> sharedClassNames = new HashSet<>();

		Set<String> classNames = new HashSet<>();

		for (Registration registration : _registrations) {
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			String modelClassName = exportImportDescriptor.getModelClassName();

			if (!classNames.add(modelClassName)) {
				sharedClassNames.add(modelClassName);
			}
		}

		return sharedClassNames;
	}

	private <T> T _getSoleProperty(
		Function
			<ExportImportVulcanBatchEngineTaskItemDelegate.
				ExportImportDescriptor,
			 T> function) {

		if (_registrations.size() != 1) {
			return null;
		}

		Registration registration = _registrations.get(0);

		return function.apply(registration.getExportImportDescriptor());
	}

	private String _getSystemEventType(
		BaseModel<?> baseModel, Set<String> sharedClassNames) {

		for (Registration registration : _registrations) {
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				exportImportDescriptor =
					registration.getExportImportDescriptor();

			String modelClassName = exportImportDescriptor.getModelClassName();

			if (!StringUtil.equals(
					baseModel.getModelClassName(), modelClassName) ||
				!sharedClassNames.contains(modelClassName)) {

				continue;
			}

			Function<BaseModel<?>, Boolean> applicableModelFunction =
				exportImportDescriptor.getApplicableModelFunction();

			if (applicableModelFunction == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"ExportImportDescriptor with key ",
							exportImportDescriptor.getKey(),
							"  declares shared model class ", modelClassName,
							" but does not implement ",
							"getApplicableModelFunction"));
				}

				continue;
			}

			if (applicableModelFunction.apply(baseModel)) {
				return exportImportDescriptor.getKey();
			}
		}

		return null;
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
				Arrays.asList(getClassNames()),
				className -> new StagedModelType(
					className, StagedModelType.REFERRER_CLASS_NAME_ALL),
				StagedModelType.class));
	}

	private void _updatePortletDataHandlerControls() {
		if (_registrations.size() > 1) {
			setEmptyControlsAllowed(false);
			setExportPortletDataHandlerControls(
				TransformUtil.transformToArray(
					_registrations, this::_getPortletDataHandlerControl,
					PortletDataHandlerControl.class));
			setStagingPortletDataHandlerControls(
				getExportPortletDataHandlerControls());
		}
		else {
			setEmptyControlsAllowed(true);
			setExportPortletDataHandlerControls();
			setStagingPortletDataHandlerControls();
		}
	}

	private void _updateSystemEventExtraDataContributor(
		BundleContext bundleContext, long companyId) {

		Set<String> sharedClassNames = _getSharedClassNames();

		if (_serviceRegistration == null) {
			if ((_registrations.size() > 1) && !sharedClassNames.isEmpty()) {
				SystemEventExtraDataContributor
					systemEventExtraDataContributor =
						(baseModel, extraData) -> {
							if (baseModel == null) {
								return extraData;
							}

							String type = _getSystemEventType(
								baseModel, _getSharedClassNames());

							if (type != null) {
								JSONObject jsonObject =
									JSONFactoryUtil.createJSONObject(extraData);

								jsonObject.put("type", type);

								return jsonObject.toString();
							}

							return extraData;
						};

				_serviceRegistration = bundleContext.registerService(
					SystemEventExtraDataContributor.class,
					systemEventExtraDataContributor,
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId", String.valueOf(companyId)
					).put(
						"jakarta.portlet.name", getPortletId()
					).build());
			}
		}
		else if ((_registrations.size() <= 1) || sharedClassNames.isEmpty()) {
			try {
				_serviceRegistration.unregister();
			}
			finally {
				_serviceRegistration = null;
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEnginePortletDataHandler.class);

	private final BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;
	private final BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;
	private final BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;
	private final BatchEngineImportTaskService _batchEngineImportTaskService;
	private final ChangesetEntryLocalService _changesetEntryLocalService;
	private final ClassNameLocalService _classNameLocalService;
	private final ExportImportHelper _exportImportHelper;
	private final GroupLocalService _groupLocalService;
	private final List<Registration> _registrations = new ArrayList<>();
	private ServiceRegistration<?> _serviceRegistration;
	private final StagingGroupHelper _stagingGroupHelper;

	private interface Registration {

		public String getBatchEngineClassName();

		public String getDeletionsFileName();

		public
			ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
				getExportImportDescriptor();

		public String getFileName();

		public String getTaskItemDelegateName();

	}

}