/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ExportImportReportEntry service. Represents a row in the &quot;ExportImportReportEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Carlos Correa
 * @see ExportImportReportEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.exportimport.report.model.impl.ExportImportReportEntryImpl"
)
@ProviderType
public interface ExportImportReportEntry
	extends ExportImportReportEntryModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.exportimport.report.model.impl.ExportImportReportEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ExportImportReportEntry, Long>
		EXPORT_IMPORT_REPORT_ENTRY_ID_ACCESSOR =
			new Accessor<ExportImportReportEntry, Long>() {

				@Override
				public Long get(
					ExportImportReportEntry exportImportReportEntry) {

					return exportImportReportEntry.
						getExportImportReportEntryId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<ExportImportReportEntry> getTypeClass() {
					return ExportImportReportEntry.class;
				}

			};

}