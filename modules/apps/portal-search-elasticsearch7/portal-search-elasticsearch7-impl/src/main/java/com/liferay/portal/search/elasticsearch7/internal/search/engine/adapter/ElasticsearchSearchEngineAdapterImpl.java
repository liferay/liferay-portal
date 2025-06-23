/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.ccr.CCRRequest;
import com.liferay.portal.search.engine.adapter.ccr.CCRRequestExecutor;
import com.liferay.portal.search.engine.adapter.ccr.CCRResponse;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.ClusterRequestExecutor;
import com.liferay.portal.search.engine.adapter.cluster.ClusterResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkableDocumentRequest;
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
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	property = "search.engine.impl=Elasticsearch",
	service = SearchEngineAdapter.class
)
public class ElasticsearchSearchEngineAdapterImpl
	implements SearchEngineAdapter {

	@Override
	public <T extends CCRResponse> T execute(CCRRequest<T> ccrRequest) {
		try {
			return ccrRequest.accept(_ccrRequestExecutor);
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
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

		if (SearchContext.isBatchMode() &&
			(documentRequest instanceof BulkableDocumentRequest ||
			 documentRequest instanceof BulkDocumentRequest)) {

			BulkDocumentRequest bulkDocumentRequest =
				_bulkDocumentRequest.get();

			if (bulkDocumentRequest == null) {
				bulkDocumentRequest = new BulkDocumentRequest();

				_bulkDocumentRequest.set(bulkDocumentRequest);

				BulkDocumentRequest finalBulkDocumentRequest =
					bulkDocumentRequest;

				SearchContext.registerBatchModeSyncCallable(
					() -> {
						List<BulkableDocumentRequest<?>>
							bulkableDocumentRequests =
								finalBulkDocumentRequest.
									getBulkableDocumentRequests();

						if (bulkableDocumentRequests.isEmpty()) {
							return null;
						}

						try {
							finalBulkDocumentRequest.accept(
								_documentRequestExecutor);
						}
						catch (RuntimeException runtimeException) {
							throw _getRuntimeException(runtimeException);
						}
						finally {
							_bulkDocumentRequest.remove();
						}

						return null;
					});
			}

			if (documentRequest instanceof BulkDocumentRequest) {
				BulkDocumentRequest incomingBulkDocumentRequest =
					(BulkDocumentRequest)documentRequest;

				for (BulkableDocumentRequest<?> bulkableDocumentRequest :
						incomingBulkDocumentRequest.
							getBulkableDocumentRequests()) {

					bulkDocumentRequest.addBulkableDocumentRequest(
						bulkableDocumentRequest);
				}
			}
			else {
				bulkDocumentRequest.addBulkableDocumentRequest(
					(BulkableDocumentRequest)documentRequest);
			}

			List<BulkableDocumentRequest<?>> bulkableDocumentRequests =
				bulkDocumentRequest.getBulkableDocumentRequests();

			if (bulkableDocumentRequests.size() < _HIBERNATE_JDBC_BATCH_SIZE) {
				return null;
			}

			try {
				S documentResponse = documentRequest.accept(
					_documentRequestExecutor);

				bulkableDocumentRequests.clear();

				return documentResponse;
			}
			catch (RuntimeException runtimeException) {
				throw _getRuntimeException(runtimeException);
			}
		}

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
			QueryBuilder queryBuilder = _queryTranslator.translate(query, null);

			return queryBuilder.toString();
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Activate
	protected void activate() {
		_queryTranslator = new ElasticsearchQueryTranslator(_indexNameBuilder);
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

		if (name.startsWith("org.elasticsearch")) {
			RuntimeException runtimeException2 = new RuntimeException(
				name + ": " + runtimeException1.toString());

			runtimeException2.setStackTrace(runtimeException1.getStackTrace());

			for (Throwable throwable : runtimeException1.getSuppressed()) {
				runtimeException2.addSuppressed(throwable);
			}

			return runtimeException2;
		}

		return runtimeException1;
	}

	private static final int _HIBERNATE_JDBC_BATCH_SIZE = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE));

	private static final ThreadLocal<BulkDocumentRequest> _bulkDocumentRequest =
		new CentralizedThreadLocal<>(
			ElasticsearchSearchEngineAdapterImpl.class.getName() +
				"._bulkDocumentRequest");

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private CCRRequestExecutor _ccrRequestExecutor;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private ClusterRequestExecutor _clusterRequestExecutor;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private DocumentRequestExecutor _documentRequestExecutor;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private IndexRequestExecutor _indexRequestExecutor;

	private QueryTranslator<QueryBuilder> _queryTranslator;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private SearchRequestExecutor _searchRequestExecutor;

	@Reference(target = "(search.engine.impl=Elasticsearch)")
	private SnapshotRequestExecutor _snapshotRequestExecutor;

	private boolean _throwOriginalExceptions;

}