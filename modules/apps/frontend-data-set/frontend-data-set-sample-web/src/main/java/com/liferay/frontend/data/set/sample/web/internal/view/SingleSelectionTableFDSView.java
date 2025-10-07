/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.view;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Arroyo
 */
@Component(
	enabled = true,
	property = "frontend.data.set.name=" + FDSSampleFDSNames.SINGLE_SELECTION,
	service = FDSView.class
)
public class SingleSelectionTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"id", "id",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"showDetails"
			).setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"creator.name", "author",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"customAuthorTableCellRenderer")
		).add(
			"description", "description"
		).add(
			_addDateTimeFDSTableSchemaField()
		).add(
			"size", "size"
		).add(
			"status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"status")
		).build();
	}

	@Override
	public String getName() {
		return "customizedTable";
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public boolean isQuickActionsEnabled() {
		return true;
	}

	private DateTimeFDSTableSchemaField _addDateTimeFDSTableSchemaField() {
		DateTimeFDSTableSchemaField dateFDSTableSchemaField =
			new DateTimeFDSTableSchemaField();

		dateFDSTableSchemaField.setContentRenderer(
			"dateTime"
		).setFieldName(
			"date"
		).setLabel(
			"date"
		);

		dateFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return dateFDSTableSchemaField;
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private CETManager _cetManager;

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}