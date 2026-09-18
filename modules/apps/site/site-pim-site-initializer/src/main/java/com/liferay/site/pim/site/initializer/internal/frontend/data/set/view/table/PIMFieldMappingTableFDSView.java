/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "frontend.data.set.name=" + PIMFDSNames.FIELD_MAPPINGS,
	service = FDSView.class
)
public class PIMFieldMappingTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"channelField", "channel-field",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"channelFieldTableCellRenderer"
			).setSortable(
				true
			)
		).add(
			"required", "required",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"requiredTableCellRenderer"
			).setSortable(
				true
			)
		).add(
			"sourceAttributes", "source-attribute",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"sourceAttributeTableCellRenderer"
			).setSortable(
				true
			)
		).add(
			"transformationRule", "transformation-rule"
		).add(
			"mapped", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"statusTableCellRenderer"
			).setSortable(
				true
			)
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}