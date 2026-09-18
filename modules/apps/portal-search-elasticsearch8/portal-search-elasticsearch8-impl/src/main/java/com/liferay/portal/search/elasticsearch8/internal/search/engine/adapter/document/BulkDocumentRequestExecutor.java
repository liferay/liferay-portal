/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.bulk.OperationType;
import co.elastic.clients.elasticsearch.core.bulk.UpdateOperation;
import co.elastic.clients.json.JsonData;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.elasticsearch8.internal.util.SetterUtil;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.BulkableDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;

import java.util.List;

/**
 * @author Michael C. Han
 */
public class BulkDocumentRequestExecutor {

	public BulkDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		int numberOfTries, int waitInSeconds) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
		_numberOfTries = numberOfTries;
		_waitInSeconds = waitInSeconds;
	}

	public BulkDocumentResponse execute(
		BulkDocumentRequest bulkDocumentRequest) {

		BulkResponse bulkResponse = _getBulkResponse(
			bulkDocumentRequest, createBulkRequest(bulkDocumentRequest));

		JsonpUtil.logBulkResponse(bulkResponse, _log);

		return createBulkDocumentResponse(bulkResponse);
	}

	protected BulkDocumentResponse createBulkDocumentResponse(
		BulkResponse bulkResponse) {

		BulkDocumentResponse bulkDocumentResponse = new BulkDocumentResponse(
			bulkResponse.took());

		String failureMessage = null;
		int rejectedItemsCount = 0;

		List<BulkResponseItem> bulkResponseItems = bulkResponse.items();

		for (BulkResponseItem bulkResponseItem : bulkResponseItems) {
			BulkDocumentItemResponse bulkDocumentItemResponse =
				new BulkDocumentItemResponse();

			bulkDocumentItemResponse.setId(bulkResponseItem.id());
			bulkDocumentItemResponse.setIndex(bulkResponseItem.index());
			bulkDocumentItemResponse.setStatus(bulkResponseItem.status());
			bulkDocumentItemResponse.setType(
				_getType(bulkResponseItem.operationType()));

			SetterUtil.setNotNullLong(
				bulkDocumentItemResponse::setVersion,
				bulkResponseItem.version());

			ErrorCause errorCause = bulkResponseItem.error();

			if (errorCause != null) {
				if (errorCause.causedBy() != null) {
					ErrorCause causedByErrorCause = errorCause.causedBy();

					bulkDocumentItemResponse.setFailureMessage(
						causedByErrorCause.reason());
					bulkDocumentItemResponse.setCause(
						new Exception(JsonpUtil.toString(causedByErrorCause)));
				}
				else {
					bulkDocumentItemResponse.setFailureMessage(
						errorCause.reason());
					bulkDocumentItemResponse.setCause(
						new Exception(JsonpUtil.toString(errorCause)));
				}

				bulkDocumentResponse.setErrors(true);
			}

			if (bulkResponseItem.status() == _STATUS_CODE_TOO_MANY_REQUESTS) {
				rejectedItemsCount++;

				if ((failureMessage == null) && (errorCause != null)) {
					failureMessage = errorCause.reason();
				}
			}

			bulkDocumentResponse.addBulkDocumentItemResponse(
				bulkDocumentItemResponse);
		}

		if (rejectedItemsCount > 0) {
			if (failureMessage == null) {
				failureMessage = "Unidentified cause";
			}

			throw new RuntimeException(
				StringBundler.concat(
					"Unable to index ", rejectedItemsCount, "/",
					bulkResponseItems.size(), " bulk items: ", failureMessage));
		}

		return bulkDocumentResponse;
	}

	protected BulkRequest createBulkRequest(
		BulkDocumentRequest bulkDocumentRequest) {

		BulkRequest.Builder builder = new BulkRequest.Builder();

		if (bulkDocumentRequest.isRefresh()) {
			builder.refresh(Refresh.True);
		}

		for (BulkableDocumentRequest<?> bulkableDocumentRequest :
				bulkDocumentRequest.getBulkableDocumentRequests()) {

			if (bulkableDocumentRequest instanceof DeleteDocumentRequest) {
				DeleteOperation deleteOperation =
					ElasticsearchBulkableDocumentRequestTranslatorUtil.
						translate(
							(DeleteDocumentRequest)bulkableDocumentRequest);

				builder.operations(new BulkOperation(deleteOperation));
			}
			else if (bulkableDocumentRequest instanceof IndexDocumentRequest) {
				IndexOperation<JsonData> indexOperation =
					ElasticsearchBulkableDocumentRequestTranslatorUtil.
						translate(
							(IndexDocumentRequest)bulkableDocumentRequest);

				builder.operations(new BulkOperation(indexOperation));
			}
			else if (bulkableDocumentRequest instanceof UpdateDocumentRequest) {
				UpdateOperation<JsonData, JsonData> updateOperation =
					ElasticsearchBulkableDocumentRequestTranslatorUtil.
						translate(
							(UpdateDocumentRequest)bulkableDocumentRequest);

				builder.operations(new BulkOperation(updateOperation));
			}
		}

		return builder.build();
	}

	private BulkResponse _getBulkResponse(
		BulkDocumentRequest bulkDocumentRequest, BulkRequest bulkRequest) {

		ElasticsearchClient elasticsearchClient =
			_elasticsearchClientResolver.getElasticsearchClient(
				bulkDocumentRequest.getConnectionId(),
				bulkDocumentRequest.isPreferLocalCluster());

		for (int i = 0;;) {
			try {
				return elasticsearchClient.bulk(bulkRequest);
			}
			catch (Exception exception) {
				if (i++ >= _numberOfTries) {
					List<BulkOperation> bulkOperations =
						bulkRequest.operations();

					_log.error(
						StringBundler.concat(
							"Unable to get a bulk response for ",
							bulkOperations.size(), " operations"),
						exception);

					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to execute the bulk request: " +
								JsonpUtil.toString(bulkRequest));
					}

					throw new RuntimeException(exception);
				}

				_log.error(
					StringBundler.concat(
						"There was an exception while getting a response from ",
						"the search engine, will retry in ", _waitInSeconds,
						" seconds (", i, "/", _numberOfTries, "). ", exception),
					exception);

				try {
					Thread.sleep(_waitInSeconds * Time.SECOND);
				}
				catch (InterruptedException interruptedException) {
					_log.error(interruptedException);

					throw new RuntimeException(exception);
				}
			}
		}
	}

	private String _getType(OperationType operationType) {
		return operationType.jsonValue();
	}

	private static final int _STATUS_CODE_TOO_MANY_REQUESTS = 429;

	private static final Log _log = LogFactoryUtil.getLog(
		BulkDocumentRequestExecutor.class);

	private final ElasticsearchClientResolver _elasticsearchClientResolver;
	private final int _numberOfTries;
	private final int _waitInSeconds;

}