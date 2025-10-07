/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 * @author Marco Galluzzi
 * @author Roberto Díaz
 * @author Sam Ziemer
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.HOME_RECENT_ASSETS_SECTION,
	service = FDSView.class
)
public class HomeRecentAssetsContentsSectionTableFDSView
	extends BaseContentsSectionTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"embedded.title", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"actionLink"
			).setContentRenderer(
				"assetRenderer"
			)
		).build();
	}

	@Override
	public boolean isDefault() {
		return true;
	}

}