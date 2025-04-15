/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author André de Oliveira
 */
public class FolderTitleLookupImpl implements FolderTitleLookup {

	public FolderTitleLookupImpl(
		FolderSearcher folderSearcher, HttpServletRequest httpServletRequest) {

		_folderSearcher = folderSearcher;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getFolderTitle(long curFolderId) {
		Hits results = _searchFolder(curFolderId);

		if (results.getLength() == 0) {
			return null;
		}

		Document document = results.doc(0);

		Map<String, Field> fieldsMap = document.getFields();

		for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
			if (!_isTitleFieldEntry(entry)) {
				continue;
			}

			Field field = entry.getValue();

			return field.getValue();
		}

		return null;
	}

	private SearchContext _getSearchContext(long curFolderId) {
		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setFolderIds(new long[] {curFolderId});
		searchContext.setGroupIds(new long[0]);
		searchContext.setKeywords(StringPool.BLANK);

		return searchContext;
	}

	private boolean _isTitleFieldEntry(Map.Entry<String, Field> entry) {
		String key = entry.getKey();

		if (!key.startsWith(Field.TITLE) || key.endsWith("_sortable")) {
			return false;
		}

		return true;
	}

	private Hits _searchFolder(long curFolderId) {
		try {
			return _folderSearcher.search(_getSearchContext(curFolderId));
		}
		catch (SearchException searchException) {
			throw new RuntimeException(searchException);
		}
	}

	private final FolderSearcher _folderSearcher;
	private final HttpServletRequest _httpServletRequest;

}