/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.frontend.data.set.view.table;

import com.liferay.commerce.fragment.internal.constants.CommerceFragmentFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "frontend.data.set.name=" + CommerceFragmentFDSNames.PENDING_ACCOUNT_ORDERS,
	service = FDSView.class
)
public class PendingAccountEntryCommerceOrderFragmentTableFDSView
	extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"id", "order-id",
			fdsTableSchemaField -> {
				fdsTableSchemaField.setContentRenderer(
					"pendingOrderIdDataRenderer");
				fdsTableSchemaField.setSortable(true);
			}
		).add(
			"orderStatusInfo.label_i18n", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"label")
		).add(
			"modifiedDate", "last-modified",
			fdsTableSchemaField -> {
				fdsTableSchemaField.setContentRenderer("date");
				fdsTableSchemaField.setSortable(true);
			}
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}