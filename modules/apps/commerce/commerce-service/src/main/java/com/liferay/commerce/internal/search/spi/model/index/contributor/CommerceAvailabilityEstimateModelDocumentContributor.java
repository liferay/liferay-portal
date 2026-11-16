/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.index.contributor;

import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matyas Wollner
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.model.CommerceAvailabilityEstimate",
	service = ModelDocumentContributor.class
)
public class CommerceAvailabilityEstimateModelDocumentContributor
	implements ModelDocumentContributor<CommerceAvailabilityEstimate> {

	@Override
	public void contribute(
		Document document,
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		document.addKeyword(
			CPField.EXTERNAL_REFERENCE_CODE,
			commerceAvailabilityEstimate.getExternalReferenceCode(), true);
		document.addNumberSortable(
			Field.PRIORITY, commerceAvailabilityEstimate.getPriority());

		String defaultLanguageId = _localization.getDefaultLanguageId(
			commerceAvailabilityEstimate.getTitle());

		for (String languageId :
				_localization.getAvailableLanguageIds(
					commerceAvailabilityEstimate.getTitle())) {

			String title = commerceAvailabilityEstimate.getTitle(languageId);

			document.addText(
				_localization.getLocalizedName(Field.TITLE, languageId), title);

			if (!languageId.equals(defaultLanguageId)) {
				continue;
			}

			document.addText(Field.TITLE, title);
		}
	}

	@Reference
	private Localization _localization;

}