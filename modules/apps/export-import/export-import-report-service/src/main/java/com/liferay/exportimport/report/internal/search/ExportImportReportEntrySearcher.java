/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.search;

import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Field;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = "model.class.name=com.liferay.exportimport.report.model.ExportImportReportEntry",
	service = BaseSearcher.class
)
public class ExportImportReportEntrySearcher extends BaseSearcher {

	public static final String CLASS_NAME =
		ExportImportReportEntry.class.getName();

	public ExportImportReportEntrySearcher() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.UID, Field.ENTRY_CLASS_NAME,
			Field.ENTRY_CLASS_PK);
		setFilterSearch(true);
		setPermissionAware(false);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

}