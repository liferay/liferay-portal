/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.frontend.data.set.filter;

import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.web.internal.constants.ExportImportFDSNames;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Magdalena Jedraszak
 */
@Component(
	property = {
		"frontend.data.set.name=" + ExportImportFDSNames.COMPANY_IMPORT_REPORT_ENTRIES,
		"frontend.data.set.name=" + ExportImportFDSNames.IMPORT_REPORT_ENTRIES,
		"frontend.data.set.name=" + ExportImportFDSNames.PUBLISH_REPORT_ENTRIES
	},
	service = FDSFilter.class
)
public class TypeSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "type/code";
	}

	@Override
	public String getLabel() {
		return "type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "empty"),
				ExportImportReportEntryConstants.TYPE_EMPTY),
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "error"),
				ExportImportReportEntryConstants.TYPE_ERROR),
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "missing-reference"),
				ExportImportReportEntryConstants.TYPE_MISSING_REFERENCE));
	}

}