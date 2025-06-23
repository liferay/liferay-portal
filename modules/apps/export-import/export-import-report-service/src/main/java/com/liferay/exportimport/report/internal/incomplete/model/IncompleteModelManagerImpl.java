/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.incomplete.model;

import com.liferay.exportimport.kernel.incomplete.model.IncompleteModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.function.BiFunction;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = IncompleteModelManager.class)
public class IncompleteModelManagerImpl implements IncompleteModelManager {

	@Override
	public <T, E extends Exception> T getOrAddIncompleteModel(
			Class<T> clazz, long companyId, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			UnsafeSupplier<T, E> incompleteModelUnsafeSupplier)
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
				IncompleteModelThreadLocal.setIncompleteModelWithSafeCloseable(
					true)) {

			_exportImportReportEntryLocalService.
				addIncompleteExportImportReportEntry(
					0L, companyId, externalReferenceCode,
					_classNameLocalService.getClassNameId(clazz.getName()),
					GetterUtil.getLong(
						ExportImportThreadLocal.
							getExportImportConfigurationId()));

			return incompleteModelUnsafeSupplier.get();
		}
	}

	@Override
	public <T, E extends Exception> T getOrAddIncompleteModel(
			Class<T> clazz, String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId, UnsafeSupplier<T, E> incompleteModelUnsafeSupplier)
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

		Group group = _groupLocalService.fetchGroup(groupId);

		try (SafeCloseable safeCloseable =
				IncompleteModelThreadLocal.setIncompleteModelWithSafeCloseable(
					true)) {

			_exportImportReportEntryLocalService.
				addIncompleteExportImportReportEntry(
					groupId, group.getCompanyId(), externalReferenceCode,
					_classNameLocalService.getClassNameId(clazz.getName()),
					GetterUtil.getLong(
						ExportImportThreadLocal.
							getExportImportConfigurationId()));

			return incompleteModelUnsafeSupplier.get();
		}
	}

	@Override
	public boolean isIncompleteModel() {
		return IncompleteModelThreadLocal.isIncompleteModel();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}