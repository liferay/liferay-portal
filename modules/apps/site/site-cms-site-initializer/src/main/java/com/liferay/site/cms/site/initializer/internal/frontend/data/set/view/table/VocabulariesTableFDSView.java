/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(property = "frontend.data.set.name=test", service = FDSView.class)
public class VocabulariesTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"name", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"edit"
			).setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"numberOfTaxonomyCategories", "categories",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"view-categories"
			).setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"assetTypes", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"customVocabularyRenderer"
			).setSortable(
				true
			)
		).add(
			"scopeKey", "space",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"spaceTableCellRenderer")
		).add(
			"dateModified", "modified",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dateTime"
			).setSortable(
				true
			)
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}