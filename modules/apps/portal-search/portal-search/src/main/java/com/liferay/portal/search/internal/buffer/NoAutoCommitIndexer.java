/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collection;
import java.util.Objects;

/**
 * @author André de Oliveira
 * @author Michael C. Han
 */
public class NoAutoCommitIndexer<T> implements Indexer<T> {

	public NoAutoCommitIndexer(Indexer<T> indexer) {
		_indexer = indexer;
	}

	@Override
	public void delete(long companyId, String uid) throws SearchException {
		_indexer.delete(companyId, uid);
	}

	@Override
	public void delete(T object) throws SearchException {
		_indexer.delete(object);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NoAutoCommitIndexer<?>)) {
			return false;
		}

		NoAutoCommitIndexer<?> noAutoCommitIndexer =
			(NoAutoCommitIndexer<?>)object;

		return Objects.equals(_indexer, noAutoCommitIndexer._indexer);
	}

	@Override
	public String getClassName() {
		return _indexer.getClassName();
	}

	@Override
	public Document getDocument(T object) throws SearchException {
		return _indexer.getDocument(object);
	}

	@Override
	public BooleanFilter getFacetBooleanFilter(
			String className, SearchContext searchContext)
		throws Exception {

		return _indexer.getFacetBooleanFilter(className, searchContext);
	}

	@Override
	public BooleanQuery getFullQuery(SearchContext searchContext)
		throws SearchException {

		return _indexer.getFullQuery(searchContext);
	}

	@Override
	public IndexerPostProcessor[] getIndexerPostProcessors() {
		return _indexer.getIndexerPostProcessors();
	}

	@Override
	public String[] getSearchClassNames() {
		return _indexer.getSearchClassNames();
	}

	@Override
	public String getSortField(String orderByCol) {
		return _indexer.getSortField(orderByCol);
	}

	@Override
	public Summary getSummary(
			Document document, String snippet, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws SearchException {

		return _indexer.getSummary(
			document, snippet, portletRequest, portletResponse);
	}

	@Override
	public int hashCode() {
		return _indexer.hashCode();
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		return _indexer.hasPermission(
			permissionChecker, entryClassName, entryClassPK, actionId);
	}

	@Override
	public boolean isCommitImmediately() {
		return false;
	}

	@Override
	public boolean isFilterSearch() {
		return _indexer.isFilterSearch();
	}

	@Override
	public boolean isIndexerEnabled() {
		return _indexer.isIndexerEnabled();
	}

	@Override
	public boolean isPermissionAware() {
		return _indexer.isPermissionAware();
	}

	@Override
	public boolean isStagingAware() {
		return _indexer.isStagingAware();
	}

	@Override
	public boolean isVisible(long classPK, int status) throws Exception {
		return _indexer.isVisible(classPK, status);
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		_indexer.postProcessContextBooleanFilter(
			contextBooleanFilter, searchContext);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #postProcessContextBooleanFilter(BooleanFilter,
	 *             SearchContext)}
	 */
	@Deprecated
	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		_indexer.postProcessContextQuery(contextQuery, searchContext);
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		_indexer.postProcessSearchQuery(
			searchQuery, fullQueryBooleanFilter, searchContext);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #postProcessSearchQuery(BooleanQuery, BooleanFilter,
	 *             SearchContext)}
	 */
	@Deprecated
	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		_indexer.postProcessSearchQuery(searchQuery, searchContext);
	}

	@Override
	public void reindex(Collection<T> objects) throws SearchException {
		_indexer.reindex(objects);
	}

	@Override
	public void reindex(String className, long classPK) throws SearchException {
		_indexer.reindex(className, classPK);
	}

	@Override
	public void reindex(String[] ids) throws SearchException {
		_indexer.reindex(ids);
	}

	@Override
	public void reindex(T object) throws SearchException {
		_indexer.reindex(object);
	}

	@Override
	public Hits search(SearchContext searchContext) throws SearchException {
		return _indexer.search(searchContext);
	}

	@Override
	public Hits search(
			SearchContext searchContext, String... selectedFieldNames)
		throws SearchException {

		return _indexer.search(searchContext, selectedFieldNames);
	}

	@Override
	public long searchCount(SearchContext searchContext)
		throws SearchException {

		return _indexer.searchCount(searchContext);
	}

	@Override
	public void setIndexerEnabled(boolean indexerEnabled) {
		_indexer.setIndexerEnabled(indexerEnabled);
	}

	private final Indexer<T> _indexer;

}