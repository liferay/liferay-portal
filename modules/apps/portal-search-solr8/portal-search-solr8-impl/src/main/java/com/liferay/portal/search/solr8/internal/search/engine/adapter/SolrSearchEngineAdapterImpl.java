/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.ccr.CCRRequest;
import com.liferay.portal.search.engine.adapter.ccr.CCRResponse;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequestExecutor;
import com.liferay.portal.search.engine.adapter.cluster.ClusterResponse;
import com.liferay.portal.search.engine.adapter.document.DocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DocumentRequestExecutor;
import com.liferay.portal.search.engine.adapter.document.DocumentResponse;
import com.liferay.portal.search.engine.adapter.index.IndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndexRequestExecutor;
import com.liferay.portal.search.engine.adapter.index.IndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchResponse;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRequestExecutor;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotResponse;
import com.liferay.portal.search.solr8.internal.query.SolrQueryTranslator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = "search.engine.impl=Solr", service = SearchEngineAdapter.class
)
public class SolrSearchEngineAdapterImpl implements SearchEngineAdapter {

	@Override
	public <T extends CCRResponse> T execute(CCRRequest<T> ccrRequest) {
		return null;
	}

	@Override
	public <T extends ClusterResponse> T execute(
		ClusterRequest<T> clusterRequest) {

		try {
			return _clusterRequestExecutor.execute(clusterRequest);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Override
	public <S extends DocumentResponse> S execute(
		DocumentRequest<S> documentRequest) {

		try {
			return documentRequest.accept(_documentRequestExecutor);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Override
	public <U extends IndexResponse> U execute(IndexRequest<U> indexRequest) {
		try {
			return indexRequest.accept(_indexRequestExecutor);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Override
	public <V extends SearchResponse> V execute(
		SearchRequest<V> searchRequest) {

		try {
			return searchRequest.accept(_searchRequestExecutor);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Override
	public <W extends SnapshotResponse> W execute(
		SnapshotRequest<W> snapshotRequest) {

		try {
			return snapshotRequest.accept(_snapshotRequestExecutor);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Override
	public String getQueryString(Query query) {
		try {
			return _queryTranslator.translate(query, null);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	protected void setThrowOriginalExceptions(boolean throwOriginalExceptions) {
		_throwOriginalExceptions = throwOriginalExceptions;
	}

	private RuntimeException _getRuntimeException(
		RuntimeException runtimeException1) {

		if (_throwOriginalExceptions) {
			return runtimeException1;
		}

		Class<?> clazz = runtimeException1.getClass();

		String name = clazz.getName();

		if (name.startsWith("org.apache.solr")) {
			RuntimeException runtimeException2 = new RuntimeException(
				name + ": " + runtimeException1.toString());

			runtimeException2.setStackTrace(runtimeException1.getStackTrace());

			return runtimeException2;
		}

		return runtimeException1;
	}

	@Reference(target = "(search.engine.impl=Solr)")
	private ClusterRequestExecutor _clusterRequestExecutor;

	@Reference(target = "(search.engine.impl=Solr)")
	private DocumentRequestExecutor _documentRequestExecutor;

	@Reference(target = "(search.engine.impl=Solr)")
	private IndexRequestExecutor _indexRequestExecutor;

	private final QueryTranslator<String> _queryTranslator =
		new SolrQueryTranslator();

	@Reference(target = "(search.engine.impl=Solr)")
	private SearchRequestExecutor _searchRequestExecutor;

	@Reference(target = "(search.engine.impl=Solr)")
	private SnapshotRequestExecutor _snapshotRequestExecutor;

	private boolean _throwOriginalExceptions;

}