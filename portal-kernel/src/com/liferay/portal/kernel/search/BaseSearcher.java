/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 * @author László Csontos
 */
public abstract class BaseSearcher extends BaseIndexer<Object> {

	@Override
	public String getClassName() {
		return StringPool.BLANK;
	}

	@Override
	public IndexerPostProcessor[] getIndexerPostProcessors() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		String[] classNames = getSearchClassNames();

		if (ArrayUtil.isEmpty(classNames)) {
			return;
		}

		for (String className : classNames) {
			Indexer<?> indexer = IndexerRegistryUtil.getIndexer(className);

			if (indexer == null) {
				continue;
			}

			indexer.postProcessSearchQuery(
				searchQuery, fullQueryBooleanFilter, searchContext);
		}
	}

	@Override
	protected void doDelete(Object object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Document doGetDocument(Object object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), added strictly to support
	 *             backwards compatibility of {@link
	 *             Indexer#postProcessSearchQuery(BooleanQuery, SearchContext)}
	 */
	@Deprecated
	@Override
	protected void doPostProcessSearchQuery(
			Indexer<?> indexer, BooleanQuery searchQuery,
			SearchContext searchContext)
		throws Exception {
	}

	@Override
	protected void doReindex(Object object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		throw new UnsupportedOperationException();
	}

}