/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.search;

import com.liferay.exportimport.report.internal.search.spi.model.index.contributor.ExportImportReportEntryModelIndexerWriterContributor;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = ModelSearchConfigurator.class)
public class ExportImportReportEntryModelSearchConfigurator
	implements ModelSearchConfigurator<ExportImportReportEntry> {

	@Override
	public String getClassName() {
		return ExportImportReportEntry.class.getName();
	}

	@Override
	public String[] getDefaultSelectedFieldNames() {
		return new String[] {
			Field.COMPANY_ID, Field.UID, Field.ENTRY_CLASS_NAME,
			Field.ENTRY_CLASS_PK
		};
	}

	@Override
	public ModelIndexerWriterContributor<ExportImportReportEntry>
		getModelIndexerWriterContributor() {

		return _modelIndexWriterContributor;
	}

	@Activate
	protected void activate() {
		_modelIndexWriterContributor =
			new ExportImportReportEntryModelIndexerWriterContributor(
				_dynamicQueryBatchIndexingActionableFactory,
				_exportImportReportEntryLocalService);
	}

	@Reference
	private DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	private ModelIndexerWriterContributor<ExportImportReportEntry>
		_modelIndexWriterContributor;

}