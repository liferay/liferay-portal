/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.empty.model;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.internal.util.ExportImportReportEntryUtil;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
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
@Component(service = EmptyModelManager.class)
public class EmptyModelManagerImpl implements EmptyModelManager {

	@Override
	public <T, E extends PortalException> T getOrAddEmptyModel(
			Class<T> clazz, long companyId,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction)
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

			_exportImportReportEntryLocalService.
				addEmptyExportImportReportEntry(
					0L, companyId, externalReferenceCode,
					_classNameLocalService.getClassNameId(clazz.getName()),
					GetterUtil.getLong(
						ExportImportThreadLocal.
							getExportImportConfigurationId()),
					clazz.getName(), ExportImportReportEntryUtil.getOrigin(),
					ObjectDefinitionConstants.SCOPE_COMPANY, null);

			return emptyModelUnsafeSupplier.get();
		}
	}

	@Override
	public <T, E extends PortalException> T getOrAddEmptyModel(
			Class<T> clazz, UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId)
		throws E {

		return getOrAddEmptyModel(
			clazz.getName(), null, emptyModelUnsafeSupplier,
			externalReferenceCode, fetchByExternalReferenceCodeBiFunction,
			getByExternalReferenceCodeUnsafeBiFunction, groupId,
			clazz.getName());
	}

	@Override
	public <T, E extends Exception> T getOrAddEmptyModel(
			String className, Long companyId,
			UnsafeSupplier<T, E> emptyModelUnsafeSupplier,
			String externalReferenceCode,
			BiFunction<String, Long, T> fetchByExternalReferenceCodeBiFunction,
			UnsafeBiFunction<String, Long, T, E>
				getByExternalReferenceCodeUnsafeBiFunction,
			long groupId, String modelName)
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

		if (group != null) {
			companyId = group.getCompanyId();
		}

		try (SafeCloseable safeCloseable =
				EmptyModelThreadLocal.setEmptyModelWithSafeCloseable(true)) {

			_exportImportReportEntryLocalService.
				addEmptyExportImportReportEntry(
					groupId, companyId, externalReferenceCode,
					_classNameLocalService.getClassNameId(className),
					GetterUtil.getLong(
						ExportImportThreadLocal.
							getExportImportConfigurationId()),
					modelName, ExportImportReportEntryUtil.getOrigin(),
					ExportImportReportEntryUtil.getScope(group),
					ExportImportReportEntryUtil.getScopeKey(group));

			return emptyModelUnsafeSupplier.get();
		}
	}

	@Override
	public boolean isEmptyModel() {
		return EmptyModelThreadLocal.isEmptyModel();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}