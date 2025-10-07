/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.frontend.data.set.view.table;

import com.liferay.change.tracking.web.internal.constants.PublicationsFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(
	property = "frontend.data.set.name=" + PublicationsFDSNames.PUBLICATIONS_TIMELINE_HISTORY,
	service = FDSView.class
)
public class TimelineHistoryPublicationsTableFDSView
	extends BasePublicationsTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"view-change"
			).setContentRenderer(
				"actionLink"
			).setLocalizeLabel(
				true
			).setSortable(
				true
			)
		).add(
			"changeType", "changed",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"ctCollectionName", "publication",
			fdsTableSchemaField -> fdsTableSchemaField.setLocalizeLabel(
				true
			).setSortable(
				true
			)
		).add(
			"ctCollectionStatus", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"status"
			).setLocalizeLabel(
				true
			).setSortable(
				true
			)
		).add(
			"ctCollectionStatusUserName", "published-by",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			addDateFDSTableSchemaField(
				"ctCollectionStatusDate", "published-date")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}