/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;

/**
 * @author Bryan Engler
 */
public interface HighlightFieldNamesQueryConfigContributor {

	public default void contributeHighlightFieldNames(
		SearchContext searchContext) {

		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (!queryConfig.isHighlightEnabled()) {
			return;
		}

		queryConfig.addHighlightFieldNames(
			getHighlightFieldNames(searchContext));
	}

	public String[] getHighlightFieldNames(SearchContext searchContext);

}