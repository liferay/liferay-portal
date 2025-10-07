/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
public abstract class BaseContentsSectionTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"embedded.title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"actionLink"
			).setContentRenderer(
				"simpleActionLinkTableCellRenderer"
			).setSortable(
				true
			)
		).add(
			"embedded.systemProperties.objectDefinitionBrief.label", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"assetTypeTableCellRenderer")
		).add(
			"embedded.scopeKey", "space",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"spaceTableCellRenderer")
		).add(
			"embedded.creator.name", "author",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"authorTableCellRenderer")
		).add(
			"dateModified", "modified",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dateTime"
			).setSortable(
				true
			)
		).add(
			"embedded.status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"status")
		).build();
	}

	@Reference
	protected FDSTableSchemaBuilderFactory fdsTableSchemaBuilderFactory;

}