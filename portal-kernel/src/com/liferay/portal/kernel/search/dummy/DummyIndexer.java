/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search.dummy;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collection;

/**
 * @author Brian Wing Shun Chan
 */
public class DummyIndexer implements Indexer<Object> {

	@Override
	public void delete(long companyId, String uid) {
	}

	@Override
	public void delete(Object object) {
	}

	@Override
	public String getClassName() {
		return StringPool.BLANK;
	}

	@Override
	public Document getDocument(Object object) {
		return null;
	}

	@Override
	public BooleanFilter getFacetBooleanFilter(
		String className, SearchContext searchContext) {

		return null;
	}

	@Override
	public BooleanQuery getFullQuery(SearchContext searchContext) {
		return null;
	}

	@Override
	public IndexerPostProcessor[] getIndexerPostProcessors() {
		return new IndexerPostProcessor[0];
	}

	@Override
	public String[] getSearchClassNames() {
		return new String[0];
	}

	@Override
	public String getSortField(String orderByCol) {
		return StringPool.BLANK;
	}

	@Override
	public Summary getSummary(
		Document document, String snippet, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		return null;
	}

	@Override
	public boolean hasPermission(
		PermissionChecker permissionChecker, String entryClassName,
		long entryClassPK, String actionId) {

		return false;
	}

	@Override
	public boolean isCommitImmediately() {
		return false;
	}

	@Override
	public boolean isFilterSearch() {
		return false;
	}

	@Override
	public boolean isIndexerEnabled() {
		return false;
	}

	@Override
	public boolean isPermissionAware() {
		return false;
	}

	@Override
	public boolean isStagingAware() {
		return false;
	}

	@Override
	public boolean isVisible(long classPK, int status) throws Exception {
		return true;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #postProcessContextBooleanFilter(BooleanFilter,
	 *             SearchContext)}
	 */
	@Deprecated
	@Override
	public void postProcessContextQuery(
		BooleanQuery contextQuery, SearchContext searchContext) {
	}

	@Override
	public void postProcessSearchQuery(
		BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
		SearchContext searchContext) {
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #postProcessSearchQuery(BooleanQuery, BooleanFilter,
	 *             SearchContext)}
	 */
	@Deprecated
	@Override
	public void postProcessSearchQuery(
		BooleanQuery searchQuery, SearchContext searchContext) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void reindex(Collection<Object> objects) {
	}

	@Override
	public void reindex(Object object) {
	}

	@Override
	public void reindex(String className, long classPK) {
	}

	@Override
	public void reindex(String[] ids) {
	}

	@Override
	public Hits search(SearchContext searchContext) {
		return null;
	}

	@Override
	public Hits search(
		SearchContext searchContext, String... selectedFieldNames) {

		return null;
	}

	@Override
	public long searchCount(SearchContext searchContext) {
		return 0;
	}

	@Override
	public void setIndexerEnabled(boolean indexerEnabled) {
	}

}