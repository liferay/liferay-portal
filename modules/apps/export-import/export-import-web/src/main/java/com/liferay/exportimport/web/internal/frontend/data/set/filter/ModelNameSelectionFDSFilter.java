/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.frontend.data.set.filter;

import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.web.internal.constants.ExportImportFDSNames;
import com.liferay.exportimport.web.internal.util.ExportImportConfigurationUtil;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
public class ModelNameSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "modelName";
	}

	@Override
	public String getLabel() {
		return "entity-type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		long exportImportConfigurationId =
			ExportImportConfigurationUtil.getExportImportConfigurationId();

		if (exportImportConfigurationId == 0) {
			return Collections.emptyList();
		}

		DynamicQuery dynamicQuery =
			_exportImportReportEntryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"exportImportConfigurationId", exportImportConfigurationId));
		dynamicQuery.setProjection(
			ProjectionFactoryUtil.distinct(
				ProjectionFactoryUtil.property("modelNameLanguageKey")));

		List<String> modelNameLanguageKeys =
			_exportImportReportEntryLocalService.dynamicQuery(dynamicQuery);

		if (modelNameLanguageKeys == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			modelNameLanguageKeys,
			modelNameLanguageKey -> {
				if (Validator.isBlank(modelNameLanguageKey)) {
					return null;
				}

				String translatedModelName = _language.get(
					locale, modelNameLanguageKey);

				return new SelectionFDSFilterItem(
					translatedModelName, translatedModelName);
			});
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private Language _language;

}