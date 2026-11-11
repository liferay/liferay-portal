/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseDateFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jan Brychta
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.TASKS_SECTION,
	service = FDSView.class
)
public class TasksSectionCMSTableFDSView extends BaseCMSTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"embedded.objectReviewed.assetTitle", "asset-title",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"actionLinkWorkflowTask"
			).setContentRenderer(
				"simpleActionLinkTableCellRenderer"
			)
		).add(
			"embedded.objectReviewed.assetType", "asset-type"
		).add(
			"embedded.label", "task"
		).add(
			"embedded.assigneePerson.name", "assignee",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"assigneeTableCellRenderer")
		).add(
			_getDueDateFDSTableSchemaField()
		).add(
			"completed", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"workflowStateTableCellRenderer")
		).add(
			addDateFDSTableSchemaField("dateModified", "last-activity-date")
		).build();
	}

	@Reference
	protected FDSTableSchemaBuilderFactory fdsTableSchemaBuilderFactory;

	private BaseDateFDSTableSchemaField _getDueDateFDSTableSchemaField() {
		BaseDateFDSTableSchemaField baseDateFDSTableSchemaField =
			new BaseDateFDSTableSchemaField();

		baseDateFDSTableSchemaField.setContentRenderer(
			"dueDateTableCellRenderer"
		).setFieldName(
			"embedded.dateDue"
		).setLabel(
			"due-date"
		).setLocalizeLabel(
			true
		);

		baseDateFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return baseDateFDSTableSchemaField;
	}

}