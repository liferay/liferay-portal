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
 * @author Stefano Motta
 */
@Component(
	property = "frontend.data.set.name=" + CommerceOrderFDSNames.ATTACHMENTS,
	service = FDSView.class
)
public class CommerceOrderAttachmentTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"commerceOrderAttachmentTitleDataRenderer"
			).setSortable(
				true
			)
		).add(
			"extension", "extension"
		).add(
			"typeLabel", "type"
		).add(
			"priority", "priority",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"dsRequestStatus", "signature-status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dsRequestStatusDataRenderer")
		).add(
			_getDateFDSTableSchemaField("dateModified", "modified-date")
		).add(
			"restricted", "restricted",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"commerceOrderAttachmentRestrictedDataRenderer")
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
		).setSortable(
			true
		);

		dateFDSTableSchemaField.setTimeZoneBehavior(
			FDSTimeZoneBehaviorConstants.APPLY_THEME_DISPLAY_TIME_ZONE);

		return dateFDSTableSchemaField;
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}