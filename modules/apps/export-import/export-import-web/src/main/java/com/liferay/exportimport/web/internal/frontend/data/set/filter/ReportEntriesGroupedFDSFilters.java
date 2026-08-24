/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.frontend.data.set.filter;

import com.liferay.exportimport.web.internal.constants.ExportImportFDSNames;
import com.liferay.frontend.data.set.filter.GroupedFDSFilters;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

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
	service = GroupedFDSFilters.class
)
public class ReportEntriesGroupedFDSFilters implements GroupedFDSFilters {

	@Override
	public JSONArray getGroupedFDSFiltersJSONArray(
		HttpServletRequest httpServletRequest) {

		return JSONUtil.putAll(
			JSONUtil.put(
				LanguageUtil.get(httpServletRequest, "filters"),
				JSONUtil.putAll(
					"classExternalReferenceCode", "modelName", "type/code")));
	}

}