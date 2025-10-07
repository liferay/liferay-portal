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
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.BULK_ACTION_TASK_REPORT_SECTION,
	service = FDSView.class
)
public class BulkActionTaskReportTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"id", "id",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"actionName", "action-name",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"externalReferenceCode", "external-reference-code",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"dateCreated", "create-date",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dateTime"
			).setSortable(
				true
			)
		).add(
			"completionDate", "completed-date",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dateTime"
			).setSortable(
				true
			)
		).add(
			"creator.name", "author-name",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"type", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"numberOfItems", "number-of-items",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"executionStatus", "execution-status"
		).build();
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Reference
	protected FDSTableSchemaBuilderFactory fdsTableSchemaBuilderFactory;

}