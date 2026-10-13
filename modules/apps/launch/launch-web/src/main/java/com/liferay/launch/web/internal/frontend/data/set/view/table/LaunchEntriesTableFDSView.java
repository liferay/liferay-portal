/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.launch.web.internal.constants.LaunchFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "frontend.data.set.name=" + LaunchFDSNames.LAUNCH_ENTRIES,
	service = FDSView.class
)
public class LaunchEntriesTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"className", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryTitle")
		).add(
			"versionKey", "version",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryVersion")
		).add(
			"baseVersionKey", "base-version",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryBaseVersion")
		).add(
			"creator", "author",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryAuthor")
		).add(
			"classPK", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryType")
		).add(
			"externalReferenceCode", "space",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntrySpace")
		).add(
			"dateModified", "modified",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryModified")
		).add(
			"id", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"launchEntryStatus")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}