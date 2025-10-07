/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.search.experiences.web.internal.blueprint.admin.constants.SXPBlueprintAdminFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	enabled = false,
	property = "frontend.data.set.name=" + SXPBlueprintAdminFDSNames.SXP_BLUEPRINTS,
	service = FDSView.class
)
public class SXPBlueprintsTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		fdsTableSchemaBuilder.add(
			"title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"actionLink"
			).setSortable(
				true
			)
		).add(
			"description", "description"
		);

		if (FeatureFlagManagerUtil.isEnabled("LPS-129412")) {
			fdsTableSchemaBuilder.add(
				"collectionProvider", "collection-provider",
				fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
					"collectionProviderCellRenderer"));
		}

		return fdsTableSchemaBuilder.add(
			"id", "id"
		).add(
			"userName", "author"
		).add(
			"createDate", "created",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dateTime"
			).setSortable(
				true
			)
		).add(
			"modifiedDate", "modified",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"dateTime"
			).setSortable(
				true
			)
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}