/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eduardo García
 */
public class FolderSearcher extends BaseSearcher {

	public static Indexer<?> getInstance() {
		return new FolderSearcher();
	}

	public FolderSearcher() {
		setDefaultSelectedFieldNames(Field.TITLE, Field.UID);
		setFilterSearch(true);
		setPermissionAware(true);

		List<String> folderClassNames = new ArrayList<>();

		for (Indexer<?> indexer : IndexerRegistryUtil.getIndexers()) {
			if (!(indexer instanceof FolderIndexer)) {
				continue;
			}

			FolderIndexer folderIndexer = (FolderIndexer)indexer;

			for (String folderClassName : folderIndexer.getFolderClassNames()) {
				folderClassNames.add(folderClassName);
			}
		}

		_classNames = folderClassNames.toArray(new String[0]);
	}

	@Override
	public String[] getSearchClassNames() {
		return _classNames;
	}

	@Override
	protected BooleanQuery createFullQuery(
			BooleanFilter fullQueryBooleanFilter, SearchContext searchContext)
		throws Exception {

		TermsFilter entryClassPKTermsFilter = new TermsFilter(
			Field.ENTRY_CLASS_PK);

		entryClassPKTermsFilter.addValues(
			ArrayUtil.toStringArray(searchContext.getFolderIds()));

		fullQueryBooleanFilter.add(
			entryClassPKTermsFilter, BooleanClauseOccur.MUST);

		return super.createFullQuery(fullQueryBooleanFilter, searchContext);
	}

	private final String[] _classNames;

}