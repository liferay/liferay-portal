/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.site.dsr.site.initializer.internal.constants.DSRSiteInitializerFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "frontend.data.set.name=" + DSRSiteInitializerFDSNames.DSR_ROOM,
	service = FDSView.class
)
public class RoomSectionTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"embedded.name", "name",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"actionLink"
			).setContentRenderer(
				"roomNameTableCellRenderer"
			)
		).add(
			"embedded.creator.name", "owner"
		).add(
			_getDateFDSTableSchemaField("embedded.dateCreated", "creation-date")
		).add(
			_getDateFDSTableSchemaField(
				"embedded.dateModified", "last-modified")
		).add(
			"embedded.trend", "trend"
		).add(
			"embedded.status.label_i18n", "status"
		).build();
	}

	private DateTimeFDSTableSchemaField _getDateFDSTableSchemaField(
		String fieldName, String label) {

		DateTimeFDSTableSchemaField dateFDSTableSchemaField =
			new DateTimeFDSTableSchemaField();

		dateFDSTableSchemaField.setContentRenderer(
			"dateTime"
		).setFieldName(
			fieldName
		).setLabel(
			label
		).setLocalizeLabel(
			true
		);

		dateFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return dateFDSTableSchemaField;
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}