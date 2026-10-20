/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.empty.model;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryPersistence;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = EmptyModelManager.class)
public class EmptyModelManagerImpl implements EmptyModelManager {

	@Override
	public <T, E extends PortalException> T getOrAddEmptyModel(
			Class<T> clazz, long companyId,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			String modelNameLanguageKey)
		throws E {

		if (!LazyReferencingThreadLocal.isEnabled()) {
			return getByExternalReferenceCodeUnsafeBiFunction.apply(
				externalReferenceCode, companyId);
		}

		T model = fetchByExternalReferenceCodeBiFunction.apply(
			externalReferenceCode, companyId);

		if (model != null) {
			return model;
		}

		try (SafeCloseable safeCloseable =
				EmptyModelThreadLocal.setEmptyModelWithSafeCloseable(true)) {

			if (ExportImportThreadLocal.isImportInProcess()) {
				_exportImportReportEntryLocalService.
					getOrAddExportImportReportEntry(
						0L, companyId, externalReferenceCode,
						_classNameLocalService.getClassNameId(clazz.getName()),
						0,
						GetterUtil.getLong(
							ExportImportThreadLocal.
								getExportImportConfigurationId()),
						ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
						modelNameLanguageKey);
			}

			return emptyModelUnsafeSupplier.get();
		}
	}

	@Override
	public <T, E extends Exception> T getOrAddEmptyModel(
			String className, Long companyId,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId, String modelNameLanguageKey)
		throws E {

		if (!LazyReferencingThreadLocal.isEnabled()) {
			return getByExternalReferenceCodeUnsafeBiFunction.apply(
				externalReferenceCode, groupId);
		}

		T model = fetchByExternalReferenceCodeBiFunction.apply(
			externalReferenceCode, groupId);

		if (model != null) {
			return model;
		}

		try (SafeCloseable safeCloseable =
				EmptyModelThreadLocal.setEmptyModelWithSafeCloseable(true)) {

			if (ExportImportThreadLocal.isImportInProcess()) {
				Group group = _groupLocalService.fetchGroup(groupId);

				if (group != null) {
					companyId = group.getCompanyId();
				}

				_exportImportReportEntryLocalService.
					getOrAddExportImportReportEntry(
						groupId, companyId, externalReferenceCode,
						_classNameLocalService.getClassNameId(className), 0,
						GetterUtil.getLong(
							ExportImportThreadLocal.
								getExportImportConfigurationId()),
						ExportImportReportEntryConstants.TYPE_EMPTY, null, null,
						modelNameLanguageKey);
			}

			return emptyModelUnsafeSupplier.get();
		}
	}

	@Override
	public boolean isEmptyModel() {
		return EmptyModelThreadLocal.isEmptyModel();
	}

	@Override
	public void reportMissingReference(
		String className, String externalReferenceCode, long groupId) {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return;
		}

		_exportImportReportEntryLocalService.getOrAddExportImportReportEntry(
			groupId, group.getCompanyId(), externalReferenceCode,
			_classNameLocalService.getClassNameId(className), 0,
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE, null, null,
			className);
	}

	@Override
	public void resolveMissingReference(
		String className, long companyId, String externalReferenceCode,
		long groupId) {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryPersistence.fetchByG_C_C_C_E_T(
				groupId, companyId, externalReferenceCode,
				_classNameLocalService.getClassNameId(className),
				GetterUtil.getLong(
					ExportImportThreadLocal.getExportImportConfigurationId()),
				ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE, false);

		if (exportImportReportEntry == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Deleting resolved missing reference report entry " +
					exportImportReportEntry.getExportImportReportEntryId());
		}

		_exportImportReportEntryLocalService.deleteExportImportReportEntry(
			exportImportReportEntry);
	}

	@Override
	public int solveEmptyModel(
		String classExternalReferenceCode, String className, long companyId,
		long groupId, int status,
		Supplier<Integer> updatedModelStatusSupplier) {

		if (status != WorkflowConstants.STATUS_EMPTY) {
			return status;
		}

		try {
			_exportImportReportEntryLocalService.
				resolveEmptyExportImportReportEntries(
					groupId, companyId, classExternalReferenceCode,
					_classNameLocalService.getClassNameId(className));
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to resolve the export/import report entries for \"",
					"the class external reference code ",
					classExternalReferenceCode, "\" and class name \"",
					className, "\""),
				exception);
		}

		return updatedModelStatusSupplier.get();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmptyModelManagerImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private ExportImportReportEntryPersistence
		_exportImportReportEntryPersistence;

	@Reference
	private GroupLocalService _groupLocalService;

}