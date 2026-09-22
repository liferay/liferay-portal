/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;

/**
 * @author Stefano Motta
 */
public abstract class BasePIMTableFDSView extends BaseTableFDSView {

	public DateTimeFDSTableSchemaField getDateFDSTableSchemaField(
		String fieldName, String label) {

		DateTimeFDSTableSchemaField dateTimeFDSTableSchemaField =
			new DateTimeFDSTableSchemaField();

		dateTimeFDSTableSchemaField.setContentRenderer(
			"dateTime"
		).setFieldName(
			fieldName
		).setLabel(
			label
		).setLocalizeLabel(
			true
		).setSortable(
			true
		);

		dateTimeFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return dateTimeFDSTableSchemaField;
	}

}