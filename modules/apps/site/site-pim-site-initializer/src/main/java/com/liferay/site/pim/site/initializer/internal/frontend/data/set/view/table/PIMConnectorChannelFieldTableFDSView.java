/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(
	property = "frontend.data.set.name=" + PIMFDSNames.FIELD_MAPPINGS,
	service = FDSView.class
)
public class PIMConnectorChannelFieldTableFDSView extends BasePIMTableFDSView {

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
				"boolean"
			).setSortable(
				true
			)
		).add(
			"sourceAttributes", "source-attributes",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"sourceAttributesTableCellRenderer"
			).setSortable(
				true
			)
		).add(
			"status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"label"
			).setSortable(
				true
			)
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}