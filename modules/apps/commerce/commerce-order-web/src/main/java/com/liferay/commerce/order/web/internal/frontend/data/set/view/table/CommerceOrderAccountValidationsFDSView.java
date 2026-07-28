/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.view.table;

import com.liferay.commerce.order.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.frontend.data.set.constants.FDSTimeZoneBehaviorConstants;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = "frontend.data.set.name=" + CommerceOrderFDSNames.ACCOUNT_VALIDATIONS,
	service = FDSView.class
)
public class CommerceOrderAccountValidationsFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"resultStatus", "result"
		).add(
			"resultMessage", "message",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"accountValidationResultMessageDataRenderer")
		).add(
			"className", "class-name"
		).add(
			"creator.name", "created-by"
		).add(
			_getDateTimeFDSTableSchemaField("dateCreated", "date")
		).build();
	}

	private DateTimeFDSTableSchemaField _getDateTimeFDSTableSchemaField(
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

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}