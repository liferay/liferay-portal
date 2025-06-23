/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.geolocation.GeoBuildersImpl;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.document.OpenSearchDocumentFactoryImpl;
import com.liferay.portal.search.opensearch2.internal.script.ScriptTranslator;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.GetDocumentRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.GetDocumentRequestExecutorImpl;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.IndexDocumentRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.IndexDocumentRequestExecutorImpl;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.OpenSearchDocumentRequestTranslator;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.OpenSearchDocumentRequestTranslatorImpl;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.UpdateDocumentRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.UpdateDocumentRequestExecutorImpl;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index.CreateIndexRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index.DeleteIndexRequestExecutor;

/**
 * @author Adam Brandizzi
 * @author Petteri Karttunen
 */
public class RequestExecutorFixture {

	public RequestExecutorFixture(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public void createIndex(String indexName) {
		_createIndexRequestExecutor.execute(new CreateIndexRequest(indexName));
	}

	public void deleteIndex(String indexName) {
		_deleteIndexRequestExecutor.execute(new DeleteIndexRequest(indexName));
	}

	public CreateIndexRequestExecutor getCreateIndexRequestExecutor() {
		return _createIndexRequestExecutor;
	}

	public DeleteIndexRequestExecutor getDeleteIndexRequestExecutor() {
		return _deleteIndexRequestExecutor;
	}

	public Document getDocumentById(String indexName, String uid) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			indexName, uid);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);

		GetDocumentResponse getDocumentResponse =
			_getDocumentRequestExecutor.execute(getDocumentRequest);

		return getDocumentResponse.getDocument();
	}

	public GetDocumentRequestExecutor getGetDocumentRequestExecutor() {
		return _getDocumentRequestExecutor;
	}

	public IndexDocumentRequestExecutor getIndexDocumentRequestExecutor() {
		return _indexDocumentRequestExecutor;
	}

	public UpdateDocumentRequestExecutor getUpdateDocumentRequestExecutor() {
		return _updateDocumentRequestExecutor;
	}

	public IndexDocumentResponse indexDocument(
		com.liferay.portal.kernel.search.Document document, String indexName) {

		return _indexDocumentRequestExecutor.execute(
			new IndexDocumentRequest(indexName, document));
	}

	public IndexDocumentResponse indexDocument(
		Document document, String indexName) {

		return _indexDocumentRequestExecutor.execute(
			new IndexDocumentRequest(indexName, document));
	}

	public void setUp() {
		OpenSearchDocumentRequestTranslator
			openSearchDocumentRequestTranslator =
				_createOpenSearchDocumentRequestTranslator();

		_createIndexRequestExecutor = new CreateIndexRequestExecutor(
			new JSONFactoryImpl(), _openSearchConnectionManager);
		_deleteIndexRequestExecutor = new DeleteIndexRequestExecutor(
			_openSearchConnectionManager);
		_getDocumentRequestExecutor = _createGetDocumentRequestExecutor(
			openSearchDocumentRequestTranslator);
		_indexDocumentRequestExecutor = _createIndexDocumentRequestExecutor(
			openSearchDocumentRequestTranslator);
		_updateDocumentRequestExecutor = _createUpdateDocumentRequestExecutor(
			openSearchDocumentRequestTranslator);
	}

	private GetDocumentRequestExecutor _createGetDocumentRequestExecutor(
		OpenSearchDocumentRequestTranslator
			openSearchDocumentRequestTranslator) {

		GetDocumentRequestExecutor getDocumentRequestExecutor =
			new GetDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_documentBuilderFactory",
			new DocumentBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_geoBuilders", new GeoBuildersImpl());
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_openSearchConnectionManager",
			_openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_openSearchDocumentRequestTranslator",
			openSearchDocumentRequestTranslator);

		return getDocumentRequestExecutor;
	}

	private IndexDocumentRequestExecutor _createIndexDocumentRequestExecutor(
		OpenSearchDocumentRequestTranslator
			openSearchDocumentRequestTranslator) {

		IndexDocumentRequestExecutor indexDocumentRequestExecutor =
			new IndexDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			indexDocumentRequestExecutor, "_openSearchConnectionManager",
			_openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			indexDocumentRequestExecutor,
			"_openSearchDocumentRequestTranslator",
			openSearchDocumentRequestTranslator);

		return indexDocumentRequestExecutor;
	}

	private OpenSearchDocumentRequestTranslator
		_createOpenSearchDocumentRequestTranslator() {

		OpenSearchDocumentRequestTranslator
			openSearchDocumentRequestTranslator =
				new OpenSearchDocumentRequestTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			openSearchDocumentRequestTranslator, "openSearchDocumentFactory",
			new OpenSearchDocumentFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			openSearchDocumentRequestTranslator, "_scriptTranslator",
			new ScriptTranslator());

		return openSearchDocumentRequestTranslator;
	}

	private UpdateDocumentRequestExecutor _createUpdateDocumentRequestExecutor(
		OpenSearchDocumentRequestTranslator
			openSearchDocumentRequestTranslator) {

		UpdateDocumentRequestExecutor updateDocumentRequestExecutor =
			new UpdateDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			updateDocumentRequestExecutor, "_openSearchConnectionManager",
			_openSearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			updateDocumentRequestExecutor,
			"_openSearchDocumentRequestTranslator",
			openSearchDocumentRequestTranslator);

		return updateDocumentRequestExecutor;
	}

	private CreateIndexRequestExecutor _createIndexRequestExecutor;
	private DeleteIndexRequestExecutor _deleteIndexRequestExecutor;
	private GetDocumentRequestExecutor _getDocumentRequestExecutor;
	private IndexDocumentRequestExecutor _indexDocumentRequestExecutor;
	private final OpenSearchConnectionManager _openSearchConnectionManager;
	private UpdateDocumentRequestExecutor _updateDocumentRequestExecutor;

}