/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.impl;

import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.base.ExportImportReportEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

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

	@Override
	public ExportImportReportEntry addErrorExportImportReportEntry(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long exportImportConfigurationId, String error,
		String errorStacktrace) {

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntryPersistence.create(
				counterLocalService.increment());

		exportImportReportEntry.setGroupId(groupId);
		exportImportReportEntry.setCompanyId(companyId);
		exportImportReportEntry.setClassExternalReferenceCode(
			classExternalReferenceCode);
		exportImportReportEntry.setClassNameId(classNameId);
		exportImportReportEntry.setExportImportConfigurationId(
			exportImportConfigurationId);
		exportImportReportEntry.setError(error);
		exportImportReportEntry.setErrorStacktrace(errorStacktrace);
		exportImportReportEntry.setType(
			ExportImportReportEntryConstants.TYPE_ERROR);

		return exportImportReportEntryPersistence.update(
			exportImportReportEntry);
	}

	@Override
	public ExportImportReportEntry addIncompleteExportImportReportEntry(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long exportImportConfigurationId) {

		ExportImportReportEntry exportImportReportEntry =
			exportImportReportEntryPersistence.create(
				counterLocalService.increment());

		exportImportReportEntry.setGroupId(groupId);
		exportImportReportEntry.setCompanyId(companyId);
		exportImportReportEntry.setClassExternalReferenceCode(
			classExternalReferenceCode);
		exportImportReportEntry.setClassNameId(classNameId);
		exportImportReportEntry.setExportImportConfigurationId(
			exportImportConfigurationId);
		exportImportReportEntry.setType(
			ExportImportReportEntryConstants.TYPE_INCOMPLETE);

		return exportImportReportEntryPersistence.update(
			exportImportReportEntry);
	}

	@Override
	public List<ExportImportReportEntry> getExportImportReportEntries(
		long companyId, long exportImportConfigurationId) {

		return exportImportReportEntryPersistence.findByC_E(
			companyId, exportImportConfigurationId);
	}

}