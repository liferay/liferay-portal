/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.impl;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.internal.util.ExportImportReportEntryUtil;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.base.ExportImportReportEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 * @author Jonathan McCann
 */
@Component(
	property = "model.class.name=com.liferay.exportimport.report.model.ExportImportReportEntry",
	service = AopService.class
)
public class ExportImportReportEntryLocalServiceImpl
	extends ExportImportReportEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ExportImportReportEntry addExportImportReportEntry(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long classPK, long exportImportConfigurationId,
		int type, String message, String errorStacktrace,
		String modelNameLanguageKey) {

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntryPersistence.create(
				counterLocalService.increment());

		exportImportReportEntry.setGroupId(groupId);
		exportImportReportEntry.setCompanyId(companyId);
		exportImportReportEntry.setClassExternalReferenceCode(
			classExternalReferenceCode);
		exportImportReportEntry.setClassNameId(classNameId);
		exportImportReportEntry.setClassPK(classPK);
		exportImportReportEntry.setExportImportConfigurationId(
			exportImportConfigurationId);
		exportImportReportEntry.setErrorMessage(
			_getMessage(
				classExternalReferenceCode, groupId, message,
				modelNameLanguageKey, type));
		exportImportReportEntry.setErrorStacktrace(errorStacktrace);
		exportImportReportEntry.setModelNameLanguageKey(modelNameLanguageKey);
		exportImportReportEntry.setOrigin(
			ExportImportReportEntryUtil.getOrigin());
		exportImportReportEntry.setType(type);
		exportImportReportEntry.setStatus(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED);

		return exportImportReportEntryPersistence.update(
			exportImportReportEntry);
	}

	@Override
	public List<ExportImportReportEntry> getExportImportReportEntries(
		long companyId, long exportImportConfigurationId) {

		return exportImportReportEntryPersistence.findByC_E(
			companyId, exportImportConfigurationId);
	}

	@Override
	public int getExportImportReportEntriesCount(
		long companyId, long exportImportConfigurationId) {

		return exportImportReportEntryPersistence.countByC_E(
			companyId, exportImportConfigurationId);
	}

	@Override
	public ExportImportReportEntry getOrAddExportImportReportEntry(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long classPK, long exportImportConfigurationId,
		int type, String message, String errorStacktrace,
		String modelNameLanguageKey) {

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntryPersistence.fetchByG_C_C_C_E_T(
				groupId, companyId, classExternalReferenceCode, classNameId,
				exportImportConfigurationId, type, false);

		if (exportImportReportEntry != null) {
			return exportImportReportEntry;
		}

		return exportImportReportEntryLocalService.addExportImportReportEntry(
			groupId, companyId, classExternalReferenceCode, classNameId,
			classPK, exportImportConfigurationId, type, message,
			errorStacktrace, modelNameLanguageKey);
	}

	@CTAware(onProduction = true)
	@Override
	public void resolveEmptyExportImportReportEntries(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId)
		throws PortalException {

		_resolveExportImportReportEntries(
			groupId, companyId, classExternalReferenceCode, classNameId,
			ExportImportReportEntryConstants.TYPE_EMPTY);
	}

	@CTAware(onProduction = true)
	@Override
	public void resolveMissingReferenceExportImportReportEntries(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId)
		throws PortalException {

		_resolveExportImportReportEntries(
			groupId, companyId, classExternalReferenceCode, classNameId,
			ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE);
	}

	private String _getMessage(
		String classExternalReferenceCode, long groupId, String message,
		String modelNameLanguageKey, int type) {

		if (Validator.isNotNull(message)) {
			return message;
		}

		if (type == ExportImportReportEntryConstants.TYPE_EMPTY) {
			return _language.format(
				LocaleUtil.US,
				"the-x-with-external-reference-code-x-was-not-found-an-empty-" +
					"shell-was-created",
				new String[] {
					modelNameLanguageKey, classExternalReferenceCode
				});
		}

		if (type == ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE) {
			return _language.format(
				LocaleUtil.US,
				"missing-reference-entity-x-with-external-reference-code-x-" +
					"in-scope-x-was-not-found-please-ensure-the-referenced-" +
						"entity-is-imported",
				new String[] {
					GetterUtil.getString(modelNameLanguageKey, "<unknown>"),
					GetterUtil.getString(
						classExternalReferenceCode, "<unknown>"),
					String.valueOf(groupId)
				});
		}

		return message;
	}

	private void _resolveExportImportReportEntries(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId, int type)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property classExternalReferenceCodeProperty =
					PropertyFactoryUtil.forName("classExternalReferenceCode");

				dynamicQuery.add(
					classExternalReferenceCodeProperty.eq(
						classExternalReferenceCode));

				Property classNameIdProperty = PropertyFactoryUtil.forName(
					"classNameId");

				dynamicQuery.add(classNameIdProperty.eq(classNameId));

				Property typeProperty = PropertyFactoryUtil.forName("type");

				dynamicQuery.add(typeProperty.eq(type));

				Property statusProperty = PropertyFactoryUtil.forName("status");

				dynamicQuery.add(
					statusProperty.eq(
						ExportImportReportEntryConstants.STATUS_UNRESOLVED));
			});
		actionableDynamicQuery.setCompanyId(companyId);
		actionableDynamicQuery.setGroupId(groupId);

		Long exportImportConfigurationId =
			ExportImportThreadLocal.getExportImportConfigurationId();

		actionableDynamicQuery.setPerformActionMethod(
			(ExportImportReportEntry exportImportReportEntry) -> {
				long exportImportReportEntryId =
					exportImportReportEntry.getExportImportReportEntryId();

				try {
					if ((GetterUtil.getLong(exportImportConfigurationId) !=
							0) &&
						(exportImportConfigurationId ==
							exportImportReportEntry.
								getExportImportConfigurationId())) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								"Deleting report entry " +
									exportImportReportEntryId);
						}

						exportImportReportEntryLocalService.
							deleteExportImportReportEntry(
								exportImportReportEntryId);
					}
					else {
						if (_log.isDebugEnabled()) {
							_log.debug(
								"Resolving report entry " +
									exportImportReportEntryId);
						}

						exportImportReportEntry.setStatus(
							ExportImportReportEntryConstants.STATUS_RESOLVED);

						updateExportImportReportEntry(exportImportReportEntry);
					}
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to resolve the export/import report " +
								"entry " + exportImportReportEntryId,
							exception);
					}
				}
			});

		actionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportReportEntryLocalServiceImpl.class);

	@Reference
	private Language _language;

}