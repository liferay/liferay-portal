/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.mcp.server.web.internal.constants.MCPServerWebFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "frontend.data.set.name=" + MCPServerWebFDSNames.DATA_MASKS,
	service = FDSView.class
)
public class DataMasksTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"name", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"maskType.name", "type"
		).add(
			"description", "description"
		).add(
			"dateModified", "last-modified",
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