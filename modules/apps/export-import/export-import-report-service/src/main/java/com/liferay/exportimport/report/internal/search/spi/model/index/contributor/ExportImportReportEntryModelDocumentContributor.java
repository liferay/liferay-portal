/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.search.spi.model.index.contributor;

import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = "indexer.class.name=com.liferay.exportimport.report.model.ExportImportReportEntry",
	service = ModelDocumentContributor.class
)
public class ExportImportReportEntryModelDocumentContributor
	implements ModelDocumentContributor<ExportImportReportEntry> {

	@Override
	public void contribute(
		Document document, ExportImportReportEntry exportImportReportEntry) {

		document.addKeyword(
			Field.COMPANY_ID, exportImportReportEntry.getCompanyId());
		document.addDate(
			Field.CREATE_DATE, exportImportReportEntry.getCreateDate());
		document.addDate(
			Field.MODIFIED_DATE, exportImportReportEntry.getModifiedDate());
		document.addText(
			"errorMessage", exportImportReportEntry.getErrorMessage());
		document.addNumber(
			"exportImportConfigurationId_long",
			exportImportReportEntry.getExportImportConfigurationId());
		document.addText(
			"errorStacktrace", exportImportReportEntry.getErrorStacktrace());
		document.addText("modelName", exportImportReportEntry.getModelName());
		document.addNumber(
			"origin_integer", exportImportReportEntry.getOrigin());
		document.addNumber("status", exportImportReportEntry.getStatus());
		document.addNumber("type_integer", exportImportReportEntry.getType());
	}

}