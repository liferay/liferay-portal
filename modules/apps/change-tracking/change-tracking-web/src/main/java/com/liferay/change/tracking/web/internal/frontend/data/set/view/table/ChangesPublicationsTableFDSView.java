/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
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
 * @author Noor Najjar
 */
@Component(
	property = "frontend.data.set.name=" + PublicationsFDSNames.PUBLICATIONS_CHANGES,
	service = FDSView.class
)
public class ChangesPublicationsTableFDSView
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
			).setSortable(
				true
			)
		).add(
			"ownerName", "user",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"siteName", "site",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"typeName", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			"status", "status",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"status"
			).setSortable(
				true
			)
		).add(
			"changeType", "changed",
			fdsTableSchemaField -> fdsTableSchemaField.setSortable(true)
		).add(
			addDateFDSTableSchemaField("dateModified", "last-modified")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}