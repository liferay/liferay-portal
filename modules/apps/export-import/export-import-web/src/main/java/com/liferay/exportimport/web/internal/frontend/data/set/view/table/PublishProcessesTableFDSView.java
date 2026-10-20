/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.staging.constants.StagingProcessesFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Raposo
 */
@Component(
	property = "frontend.data.set.name=" + StagingProcessesFDSNames.PUBLISH_PROCESSES,
	service = FDSView.class
)
public class PublishProcessesTableFDSView extends BaseProcessesTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"name", "name",
			fdsTableSchemaField -> {
				fdsTableSchemaField.setContentRenderer("processTitleRenderer");
				fdsTableSchemaField.setSortable(true);
			}
		).add(
			getDateFDSTableSchemaField(
				"dateTime", "dateCreated", "creation-date")
		).add(
			getDateFDSTableSchemaField(
				"processCompletionDateRenderer", "dateCompleted",
				"completion-date")
		).add(
			"type", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"publishProcessTypeRenderer")
		).add(
			"creator.name", "author",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"processAuthorRenderer")
		).add(
			"status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"publishProcessStatusRenderer")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}