/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.seo.studio.web.internal.constants.SEOStudioFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(
	property = "frontend.data.set.name=" + SEOStudioFDSNames.AFFECTED_PAGES_SECTION,
	service = FDSView.class
)
public class AffectedPagesSectionSEOStudioTableFDSView
	extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"r_seoStudioPageToSEOStudioScanInsights_seoStudioPage.title",
			"title",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"titleCellRenderer"
			).setSortable(
				true
			)
		).add(
			"r_seoStudioPageToSEOStudioScanInsights_seoStudioPage.author",
			"author",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"authorCellRenderer")
		).add(
			"r_seoStudioPageToSEOStudioScanInsights_seoStudioPage.type", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"r_seoStudioPageToSEOStudioScanInsights_seoStudioPage.pageURL",
			"friendly-url"
		).add(
			"monthlyTraffic", "monthly-traffic"
		).add(
			"state", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"stateCellRenderer")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}