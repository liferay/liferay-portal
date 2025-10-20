/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.spi.model.query.contributor.HighlightFieldNamesQueryConfigContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = "indexer.class.name=com.liferay.message.boards.model.MBMessage",
	service = HighlightFieldNamesQueryConfigContributor.class
)
public class MBMessageHighlightFieldNamesQueryConfigContributor
	implements HighlightFieldNamesQueryConfigContributor {

	@Override
	public String[] getHighlightFieldNames(SearchContext searchContext) {
		return searchLocalizationHelper.getLocalizedFieldNames(
			new String[] {Field.CONTENT, Field.TITLE}, searchContext);
	}

	@Reference
	protected SearchLocalizationHelper searchLocalizationHelper;

}