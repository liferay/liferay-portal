/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.index.contributor;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPSpecificationOption",
	service = ModelDocumentContributor.class
)
public class CPSpecificationOptionModelDocumentContributor
	implements ModelDocumentContributor<CPSpecificationOption> {

	@Override
	public void contribute(
		Document document, CPSpecificationOption cpSpecificationOption) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce product specification option " +
					cpSpecificationOption);
		}

		CPOptionCategory cpOptionCategory =
			_cpOptionCategoryLocalService.fetchCPOptionCategory(
				cpSpecificationOption.getCPOptionCategoryId());

		if (cpOptionCategory != null) {
			document.addKeyword(
				CPField.CP_OPTION_CATEGORY_ID,
				cpOptionCategory.getCPOptionCategoryId());
		}

		document.addKeyword(
			CPField.FACETABLE, cpSpecificationOption.isFacetable());
		document.addText(CPField.KEY, cpSpecificationOption.getKey());
		document.addKeyword(CPField.VISIBLE, cpSpecificationOption.isVisible());
		document.addNumberSortable(
			Field.PRIORITY, cpSpecificationOption.getPriority());

		String[] languageIds = _localization.getAvailableLanguageIds(
			cpSpecificationOption.getTitle());

		for (String languageId : languageIds) {
			if (cpOptionCategory != null) {
				document.addText(
					_localization.getLocalizedName(
						CPField.CP_OPTION_CATEGORY_TITLE, languageId),
					cpOptionCategory.getTitle(languageId));
			}

			String title = cpSpecificationOption.getTitle(languageId);

			document.addText(Field.CONTENT, title);

			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				cpSpecificationOption.getDescription(languageId));
			document.addText(
				_localization.getLocalizedName(Field.TITLE, languageId), title);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce product specification option " +
					cpSpecificationOption + " indexed successfully");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionModelDocumentContributor.class);

	@Reference
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	@Reference
	private Localization _localization;

}