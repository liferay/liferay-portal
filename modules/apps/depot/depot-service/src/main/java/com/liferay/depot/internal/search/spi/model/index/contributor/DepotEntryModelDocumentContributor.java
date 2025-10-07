/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.search.spi.model.index.contributor;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "indexer.class.name=com.liferay.depot.model.DepotEntry",
	service = ModelDocumentContributor.class
)
public class DepotEntryModelDocumentContributor
	implements ModelDocumentContributor<DepotEntry> {

	@Override
	public void contribute(Document document, DepotEntry depotEntry) {
		Group group = _groupLocalService.fetchGroup(depotEntry.getGroupId());

		document.addText(Field.DESCRIPTION, group.getDescription());

		document.addDate(Field.MODIFIED_DATE, depotEntry.getModifiedDate());
		document.addText(Field.NAME, group.getName());
		document.addKeyword(Field.TYPE, depotEntry.getType());

		for (Locale locale :
				_language.getAvailableLocales(depotEntry.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			document.addText(
				_localization.getLocalizedName(Field.DESCRIPTION, languageId),
				group.getDescription(locale));
			document.addText(
				_localization.getLocalizedName(Field.NAME, languageId),
				group.getName(locale));
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

}