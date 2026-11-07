/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.util;

import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;

/**
 * @author David Arques
 */
public class ContentDashboardSearchRequestBuilderUtil {

	public static SearchRequestBuilder builder(
		ContentDashboardItemFactoryRegistry contentDashboardItemFactoryRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		SearchContext searchContext,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		if (ArrayUtil.isEmpty(searchContext.getEntryClassNames())) {
			searchContext.setEntryClassNames(
				TransformUtil.transformToArray(
					contentDashboardItemFactoryRegistry.getClassNames(),
					className ->
						infoSearchClassMapperRegistry.getSearchClassName(
							className),
					String.class));
		}
		else {
			searchContext.setEntryClassNames(
				TransformUtil.transform(
					searchContext.getEntryClassNames(),
					className ->
						infoSearchClassMapperRegistry.getSearchClassName(
							className),
					String.class));
		}

		return searchRequestBuilderFactory.builder(
			searchContext
		).emptySearchEnabled(
			true
		).entryClassNames(
			searchContext.getEntryClassNames()
		).fields(
			Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.ROOT_ENTRY_CLASS_PK, Field.UID
		).highlightEnabled(
			false
		);
	}

}