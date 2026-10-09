/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.index.contributor;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mahmoud Azzam
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPTaxCategory",
	service = ModelDocumentContributor.class
)
public class CPTaxCategoryModelDocumentContributor
	implements ModelDocumentContributor<CPTaxCategory> {

	@Override
	public void contribute(Document document, CPTaxCategory cpTaxCategory) {
		document.addDateSortable(
			Field.CREATE_DATE, cpTaxCategory.getCreateDate());
		document.addText(Field.DESCRIPTION, cpTaxCategory.getDescription());
		document.addText(Field.NAME, cpTaxCategory.getName());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpTaxCategory.getName());

		for (String languageId : languageIds) {
			String name = cpTaxCategory.getName(languageId);

			String description = cpTaxCategory.getDescription(languageId);

			document.addText(Field.CONTENT, description);
			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				description);

			document.addText(
				_localization.getLocalizedName(Field.NAME, languageId), name);

			String cpTaxCategoryDefaultLanguageId =
				_localization.getDefaultLanguageId(cpTaxCategory.getName());

			if (!languageId.equals(cpTaxCategoryDefaultLanguageId)) {
				continue;
			}

			document.addText(Field.NAME, name);
			document.addText("defaultLanguageId", languageId);
		}
	}

	@Reference
	private Localization _localization;

}