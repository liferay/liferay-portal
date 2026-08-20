/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.legacy.query.ElasticsearchQueryVisitor;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.ccr.ElasticsearchCCRRequestExecutor;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.cluster.ElasticsearchClusterRequestExecutor;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document.ElasticsearchDocumentRequestExecutor;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document.configuration.BulkDocumentRequestRetryConfiguration;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.index.ElasticsearchIndexRequestExecutor;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.search.ElasticsearchSearchRequestExecutor;
import com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.snapshot.ElasticsearchSnapshotRequestExecutor;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document.configuration.BulkDocumentRequestRetryConfiguration",
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
						try {
							List<BulkableDocumentRequest<?>>
								bulkableDocumentRequests =
									finalBulkDocumentRequest.
										getBulkableDocumentRequests();

							if (bulkableDocumentRequests.isEmpty()) {
								return null;
							}

							_documentRequestExecutor.executeBulkDocumentRequest(
								finalBulkDocumentRequest);
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

			if (bulkableDocumentRequests.size() < Indexer.DEFAULT_INTERVAL) {
				return null;
			}

			ExecutorService executorService =
				SearchEngineHelperUtil.getDocumentsConsumerExecutorService();

			BulkDocumentRequest transferCopyBulkDocumentRequest =
				bulkDocumentRequest.transferCopy();

			DefaultNoticeableFuture<Void> defaultNoticeableFuture =
				new DefaultNoticeableFuture<>(
					() -> {
						_documentRequestExecutor.executeBulkDocumentRequest(
							transferCopyBulkDocumentRequest);

						return null;
					});

			SearchContext.registerBatchModeSyncFuture(defaultNoticeableFuture);

			executorService.execute(defaultNoticeableFuture);

			return null;
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
			QueryVariant queryVariant =
				ElasticsearchQueryVisitor.INSTANCE.translate(query);

			return queryVariant.toString();
		}
		catch (RuntimeException runtimeException) {
			throw _getRuntimeException(runtimeException);
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		modified(properties);

		_ccrRequestExecutor = new ElasticsearchCCRRequestExecutor(
			_elasticsearchClientResolver);
		_clusterRequestExecutor = new ElasticsearchClusterRequestExecutor(
			_elasticsearchClientResolver);
		_indexRequestExecutor = new ElasticsearchIndexRequestExecutor(
			_elasticsearchClientResolver);
		_searchRequestExecutor = new ElasticsearchSearchRequestExecutor(
			_elasticsearchClientResolver);
		_snapshotRequestExecutor = new ElasticsearchSnapshotRequestExecutor(
			_elasticsearchClientResolver);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		BulkDocumentRequestRetryConfiguration
			bulkDocumentRequestRetryConfiguration =
				ConfigurableUtil.createConfigurable(
					BulkDocumentRequestRetryConfiguration.class, properties);

		_documentRequestExecutor = new ElasticsearchDocumentRequestExecutor(
			_elasticsearchClientResolver,
			bulkDocumentRequestRetryConfiguration.numberOfTries(),
			bulkDocumentRequestRetryConfiguration.waitInSeconds());
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

	private static final ThreadLocal<BulkDocumentRequest> _bulkDocumentRequest =
		new CentralizedThreadLocal<>(
			ElasticsearchSearchEngineAdapterImpl.class.getName() +
				"._bulkDocumentRequest");

	private CCRRequestExecutor _ccrRequestExecutor;
	private ClusterRequestExecutor _clusterRequestExecutor;
	private volatile DocumentRequestExecutor _documentRequestExecutor;

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	private IndexRequestExecutor _indexRequestExecutor;
	private SearchRequestExecutor _searchRequestExecutor;
	private SnapshotRequestExecutor _snapshotRequestExecutor;
	private boolean _throwOriginalExceptions;

}