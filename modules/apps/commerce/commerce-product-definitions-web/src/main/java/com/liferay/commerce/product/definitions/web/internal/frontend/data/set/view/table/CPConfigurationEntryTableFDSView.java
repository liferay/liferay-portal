/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.view.table;

import com.liferay.commerce.product.definitions.web.internal.constants.CPConfigurationFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "frontend.data.set.name=" + CPConfigurationFDSNames.PRODUCT_CONFIGURATIONS,
	service = FDSView.class
)
public class CPConfigurationEntryTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"entityName", "name",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"cpConfigurationEntryDataRenderer"
			).setSortable(
				true
			)
		).add(
			"purchasable", "purchasable",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"cpConfigurationEntryDataRenderer"
			).setSortable(
				true
			)
		).add(
			"productShippingConfiguration.shippable", "shippable",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"cpConfigurationEntryDataRenderer"
			).setSortable(
				true
			)
		).add(
			"minOrderQuantity", "min-order-quantity",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"cpConfigurationEntryDataRenderer"
			).setSortable(
				true
			)
		).add(
			"maxOrderQuantity", "max-order-quantity",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"cpConfigurationEntryDataRenderer"
			).setSortable(
				true
			)
		).add(
			"multipleOrderQuantity", "multiple-order-quantity",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"cpConfigurationEntryDataRenderer"
			).setSortable(
				true
			)
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}