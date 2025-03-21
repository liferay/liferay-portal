/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerPermissionPostFilter;
import com.liferay.portal.search.indexer.IndexerSearcher;
import com.liferay.portal.search.indexer.IndexerSummaryBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Michael C. Han
 */
public class DefaultIndexer<T extends BaseModel<?>> implements Indexer<T> {

	public DefaultIndexer(
		ModelSearchSettings modelSearchSettings,
		IndexerDocumentBuilder indexerDocumentBuilder,
		IndexerSearcher indexerSearcher, IndexerWriter<T> indexerWriter,
		IndexerPermissionPostFilter indexerPermissionPostFilter,
		IndexerQueryBuilderImpl indexerQueryBuilderImpl,
		IndexerSummaryBuilder indexerSummaryBuilder, String className) {

		_modelSearchSettings = modelSearchSettings;
		_indexerDocumentBuilder = indexerDocumentBuilder;
		_indexerSearcher = indexerSearcher;
		_indexerWriter = indexerWriter;
		_indexerPermissionPostFilter = indexerPermissionPostFilter;
		_indexerQueryBuilderImpl = indexerQueryBuilderImpl;
		_indexerSummaryBuilder = indexerSummaryBuilder;
		_className = className;
	}

	@Override
	public void delete(long companyId, String uid) throws SearchException {
		_indexerWriter.delete(companyId, uid);
	}

	@Override
	public void delete(T baseModel) throws SearchException {
		if (baseModel instanceof CTModel<?>) {
			CTModel<?> ctModel = (CTModel<?>)baseModel;

			if ((ctModel.getCtCollectionId() == 0) &&
				!CTCollectionThreadLocal.isProductionMode()) {

				return;
			}
		}

		_indexerWriter.delete(baseModel);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if (!(object instanceof Indexer<?>)) {
			return false;
		}

		Indexer<?> indexer = (Indexer<?>)object;

		return Objects.equals(getClassName(), indexer.getClassName());
	}

	@Override
	public String getClassName() {
		return _modelSearchSettings.getClassName();
	}

	@Override
	public long getCompanyId() {
		return _modelSearchSettings.getCompanyId();
	}

	@Override
	public Document getDocument(T baseModel) throws SearchException {
		return _indexerDocumentBuilder.getDocument(baseModel);
	}

	@Override
	public BooleanFilter getFacetBooleanFilter(
			String className, SearchContext searchContext)
		throws Exception {

		return null;
	}

	@Override
	public BooleanQuery getFullQuery(SearchContext searchContext)
		throws SearchException {

		return _indexerQueryBuilderImpl.getQuery(searchContext);
	}

	@Override
	public IndexerPostProcessor[] getIndexerPostProcessors() {
		List<IndexerPostProcessor> indexerPostProcessors =
			IndexerRegistryUtil.getIndexerPostProcessors(this);

		return indexerPostProcessors.toArray(new IndexerPostProcessor[0]);
	}

	@Override
	public String[] getSearchClassNames() {
		return _modelSearchSettings.getSearchClassNames();
	}

	@Override
	public String getSortField(String orderByCol) {
		return StringPool.BLANK;
	}

	@Override
	public Summary getSummary(Document document, Locale locale, String snippet)
		throws SearchException {

		return _indexerSummaryBuilder.getSummary(document, snippet, locale);
	}

	@Override
	public Summary getSummary(
			Document document, String snippet, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws SearchException {

		return _indexerSummaryBuilder.getSummary(
			document, snippet, _getLocale(portletRequest));
	}

	@Override
	public int hashCode() {
		String[] searchClassNames = getSearchClassNames();

		StringBundler sb = new StringBundler(searchClassNames.length);

		for (String searchClassName : searchClassNames) {
			sb.append(searchClassName);
		}

		return HashUtil.hash(0, sb.toString());
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		return _indexerPermissionPostFilter.hasPermission(
			permissionChecker, entryClassPK);
	}

	@Override
	public boolean isCommitImmediately() {
		return false;
	}

	@Override
	public boolean isFilterSearch() {
		return !_modelSearchSettings.isSearchResultPermissionFilterSuppressed();
	}

	@Override
	public boolean isIndexerEnabled() {
		return _indexerWriter.isEnabled();
	}

	@Override
	public boolean isPermissionAware() {
		if (_modelSearchSettings.isPermissionAware() &&
			_indexerPermissionPostFilter.isPermissionAware()) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isStagingAware() {
		return _modelSearchSettings.isStagingAware();
	}

	@Override
	public boolean isVisible(long classPK, int status) throws Exception {
		return _indexerPermissionPostFilter.isVisible(classPK, status);
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		_indexerQueryBuilderImpl.addPreFiltersFromModel(
			contextBooleanFilter, _modelSearchSettings, searchContext);
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		_indexerQueryBuilderImpl.addSearchTermsFromModel(
			searchQuery, searchContext);
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {
	}

	@Override
	public void reindex(Collection<T> objects) throws SearchException {
		_indexerWriter.reindex(objects);
	}

	@Override
	public void reindex(String className, long classPK) throws SearchException {
		_indexerWriter.reindex(classPK);
	}

	@Override
	public void reindex(String[] ids) throws SearchException {
		_indexerWriter.reindex(ids);
	}

	@Override
	public void reindex(T baseModel) throws SearchException {
		_indexerWriter.reindex(baseModel);
	}

	@Override
	public void reindex(T baseModel, boolean notify) throws SearchException {
		_indexerWriter.reindex(baseModel, notify);
	}

	@Override
	public Hits search(SearchContext searchContext) throws SearchException {
		return _indexerSearcher.search(searchContext);
	}

	@Override
	public Hits search(
			SearchContext searchContext, String... selectedFieldNames)
		throws SearchException {

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(selectedFieldNames);

		return search(searchContext);
	}

	@Override
	public long searchCount(SearchContext searchContext)
		throws SearchException {

		return _indexerSearcher.searchCount(searchContext);
	}

	@Override
	public void setIndexerEnabled(boolean indexerEnabled) {
		_indexerWriter.setEnabled(indexerEnabled);
	}

	private Locale _getLocale(PortletRequest portletRequest) {
		if (portletRequest != null) {
			return portletRequest.getLocale();
		}

		return LocaleUtil.getMostRelevantLocale();
	}

	private final String _className;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private final IndexerPermissionPostFilter _indexerPermissionPostFilter;
	private final IndexerQueryBuilderImpl _indexerQueryBuilderImpl;
	private final IndexerSearcher _indexerSearcher;
	private final IndexerSummaryBuilder _indexerSummaryBuilder;
	private final IndexerWriter<T> _indexerWriter;
	private final ModelSearchSettings _modelSearchSettings;

}