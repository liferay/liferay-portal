/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
 * @author Pei-Jung Lan
 */
@Component(
	property = "frontend.data.set.name=" + PublicationsFDSNames.PUBLICATIONS_HISTORY,
	service = FDSView.class
)
public class HistoryPublicationsTableFDSView
	extends BasePublicationsTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"name", "publication",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"review-changes"
			).setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			addDateFDSTableSchemaField("datePublished", "published-date")
		).add(
			"ownerName", "published-by"
		).add(
			"status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"customPublicationHistoryStatusRenderer")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}