/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.spi.model.query.contributor.HighlightFieldNamesQueryConfigContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.Layout",
	service = HighlightFieldNamesQueryConfigContributor.class
)
public class LayoutHighlightFieldNamesQueryConfigContributor
	implements HighlightFieldNamesQueryConfigContributor {

	@Override
	public String[] getHighlightFieldNames(SearchContext searchContext) {
		List<String> prefixes = new ArrayList<>(
			Arrays.asList(Field.NAME, Field.TITLE));

		if (!GetterUtil.getBoolean(
				searchContext.getAttribute("searchOnlyByTitle"))) {

			prefixes.add(Field.CONTENT);
		}

		return _searchLocalizationHelper.getLocalizedFieldNames(
			prefixes.toArray(new String[0]), searchContext);
	}

	@Reference
	private SearchLocalizationHelper _searchLocalizationHelper;

}