/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;

/**
 * @author Eduardo García
 * @author André de Oliveira
 */
public class FolderSearcher extends BaseSearcher {

	public FolderSearcher(long folderId) {
		_folderId = folderId;

		setDefaultSelectedFieldNames(Field.TITLE, Field.UID);
		setDefaultSelectedLocalizedFieldNames(Field.TITLE);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	@Override
	public String[] getSearchClassNames() {
		return _FOLDER_CLASS_NAMES;
	}

	@Override
	protected BooleanQuery createFullQuery(
			BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {

		TermsFilter termsFilter = new TermsFilter(Field.ENTRY_CLASS_PK);

		termsFilter.addValue(String.valueOf(_folderId));

		booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);

		return super.createFullQuery(booleanFilter, searchContext);
	}

	private static final String[] _FOLDER_CLASS_NAMES = {
		"com.liferay.bookmarks.model.BookmarksFolder",
		"com.liferay.document.library.kernel.model.DLFolder",
		"com.liferay.journal.model.JournalFolder",
		"com.liferay.object.model.ObjectEntryFolder"
	};

	private final long _folderId;

}