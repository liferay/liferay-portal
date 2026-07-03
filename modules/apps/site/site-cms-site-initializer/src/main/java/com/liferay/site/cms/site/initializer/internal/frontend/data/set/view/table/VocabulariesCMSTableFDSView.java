/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.frontend.data.set.view.table.LinkFDSTableSchemaField;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.VOCABULARIES,
	service = FDSView.class
)
public class VocabulariesCMSTableFDSView extends BaseCMSTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		String scopeKey = "space";

		if (FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-86291")) {

			scopeKey = "scope";
		}

		return fdsTableSchemaBuilder.add(
			"name", "title",
			fdsTableSchemaField -> fdsTableSchemaField.setActionId(
				"edit"
			).setContentRenderer(
				"simpleActionLinkTableCellRenderer"
			).setSortable(
				true
			)
		).add(
			_addViewCategoriesLinkFDSTableSchemaField()
		).add(
			"assetTypes", "type",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"customVocabularyRenderer"
			).setSortable(
				true
			)
		).add(
			"scopeKey", scopeKey,
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"scopeTableCellRenderer")
		).add(
			addDateFDSTableSchemaField("dateModified", "modified")
		).build();
	}

	private LinkFDSTableSchemaField
		_addViewCategoriesLinkFDSTableSchemaField() {

		LinkFDSTableSchemaField linkFDSTableSchemaField =
			new LinkFDSTableSchemaField();

		linkFDSTableSchemaField.setActionId(
			"view-categories"
		).setContentRenderer(
			"actionLink"
		).setFieldName(
			"numberOfTaxonomyCategories"
		).setLabel(
			"categories"
		).setSortable(
			true
		);

		linkFDSTableSchemaField.setDecoration("underline");
		linkFDSTableSchemaField.setDisplayType("unstyled");

		return linkFDSTableSchemaField;
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}