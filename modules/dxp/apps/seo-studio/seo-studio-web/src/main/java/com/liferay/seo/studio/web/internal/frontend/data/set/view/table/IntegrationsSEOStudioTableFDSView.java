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
 * @author Kiana Suetani
 */
@Component(
	property = "frontend.data.set.name=" + SEOStudioFDSNames.INTEGRATIONS,
	service = FDSView.class
)
public class IntegrationsSEOStudioTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"name", "integration",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"integrationNameCellRenderer")
		).add(
			"state", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"integrationStatusCellRenderer")
		).add(
			"dateModified", "date-added",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"date")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}