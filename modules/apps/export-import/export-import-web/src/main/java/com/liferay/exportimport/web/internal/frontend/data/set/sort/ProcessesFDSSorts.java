/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.frontend.data.set.sort;

import com.liferay.exportimport.web.internal.constants.ExportImportFDSNames;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.staging.constants.StagingProcessesFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Raposo
 */
@Component(
	property = {
		"frontend.data.set.name=" + ExportImportFDSNames.COMPANY_EXPORT_PROCESSES,
		"frontend.data.set.name=" + ExportImportFDSNames.COMPANY_IMPORT_PROCESSES,
		"frontend.data.set.name=" + ExportImportFDSNames.EXPORT_PROCESSES,
		"frontend.data.set.name=" + ExportImportFDSNames.IMPORT_PROCESSES,
		"frontend.data.set.name=" + StagingProcessesFDSNames.PUBLISH_PROCESSES
	},
	service = FDSSorts.class
)
public class ProcessesFDSSorts implements FDSSorts {

	@Override
	public List<FDSSortItem> getFDSSortItems(
		HttpServletRequest httpServletRequest) {

		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setActive(
				true
			).setDirection(
				"desc"
			).setKey(
				"dateCreated"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "creation-date")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"dateCompleted"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "completion-date")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"name"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "title")
			).build()
		).build();
	}

}