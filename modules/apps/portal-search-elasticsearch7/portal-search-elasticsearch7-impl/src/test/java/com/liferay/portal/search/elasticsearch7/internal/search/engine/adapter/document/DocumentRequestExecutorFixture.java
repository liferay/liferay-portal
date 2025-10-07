/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.engine.adapter.document.DocumentRequestExecutor;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.geolocation.GeoBuildersImpl;
import com.liferay.portal.search.internal.script.ScriptsImpl;
import com.liferay.portal.search.script.Scripts;

/**
 * @author Dylan Rebelak
 */
public class DocumentRequestExecutorFixture {

	public DocumentRequestExecutor getDocumentRequestExecutor() {
		return _documentRequestExecutor;
	}

	public void setUp() {
		_documentRequestExecutor = _createDocumentRequestExecutor(
			_elasticsearchClientResolver);
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	private BulkDocumentRequestExecutor _createBulkDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		BulkDocumentRequestExecutor bulkDocumentRequestExecutor =
			new BulkDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			bulkDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			bulkDocumentRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return bulkDocumentRequestExecutor;
	}

	private DeleteByQueryDocumentRequestExecutor
		_createDeleteByQueryDocumentRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		DeleteByQueryDocumentRequestExecutor
			deleteByQueryDocumentRequestExecutor =
				new DeleteByQueryDocumentRequestExecutorImpl();

		ElasticsearchQueryTranslatorFixture
			legacyElasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			deleteByQueryDocumentRequestExecutor,
			"_elasticsearchClientResolver", elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			deleteByQueryDocumentRequestExecutor, "_legacyQueryTranslator",
			legacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());
		ReflectionTestUtil.setFieldValue(
			deleteByQueryDocumentRequestExecutor, "_queryTranslator",
			new ElasticsearchQueryTranslator());

		return deleteByQueryDocumentRequestExecutor;
	}

	private DeleteDocumentRequestExecutor _createDeleteDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		DeleteDocumentRequestExecutor deleteDocumentRequestExecutor =
			new DeleteDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			deleteDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			deleteDocumentRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return deleteDocumentRequestExecutor;
	}

	private DocumentRequestExecutor _createDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		DocumentRequestExecutor documentRequestExecutor =
			new ElasticsearchDocumentRequestExecutor();

		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator =
				new ElasticsearchBulkableDocumentRequestTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_bulkDocumentRequestExecutor",
			_createBulkDocumentRequestExecutor(
				elasticsearchClientResolver,
				elasticsearchBulkableDocumentRequestTranslator));

		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_deleteByQueryDocumentRequestExecutor",
			_createDeleteByQueryDocumentRequestExecutor(
				elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_deleteDocumentRequestExecutor",
			_createDeleteDocumentRequestExecutor(
				elasticsearchClientResolver,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_getDocumentRequestExecutor",
			_createGetDocumentRequestExecutor(
				elasticsearchClientResolver,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_indexDocumentRequestExecutor",
			_createIndexDocumentRequestExecutor(
				elasticsearchClientResolver,
				elasticsearchBulkableDocumentRequestTranslator));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_updateByQueryDocumentRequestExecutor",
			_createUpdateByQueryDocumentRequestExecutor(
				elasticsearchClientResolver));
		ReflectionTestUtil.setFieldValue(
			documentRequestExecutor, "_updateDocumentRequestExecutor",
			_createUpdateDocumentRequestExecutor(
				elasticsearchClientResolver,
				elasticsearchBulkableDocumentRequestTranslator));

		return documentRequestExecutor;
	}

	private GetDocumentRequestExecutor _createGetDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		GetDocumentRequestExecutor getDocumentRequestExecutor =
			new GetDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_documentBuilderFactory",
			new DocumentBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			getDocumentRequestExecutor, "_geoBuilders", new GeoBuildersImpl());

		return getDocumentRequestExecutor;
	}

	private IndexDocumentRequestExecutor _createIndexDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		IndexDocumentRequestExecutor indexDocumentRequestExecutor =
			new IndexDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			indexDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			indexDocumentRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return indexDocumentRequestExecutor;
	}

	private UpdateByQueryDocumentRequestExecutor
		_createUpdateByQueryDocumentRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		UpdateByQueryDocumentRequestExecutor
			updateByQueryDocumentRequestExecutor =
				new UpdateByQueryDocumentRequestExecutorImpl();

		ElasticsearchQueryTranslatorFixture
			lecacyElasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor,
			"_elasticsearchClientResolver", elasticsearchClientResolver);
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor, "_legacyQueryTranslator",
			lecacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor, "_queryTranslator",
			new ElasticsearchQueryTranslator());
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutor, "_scripts", _scripts);

		return updateByQueryDocumentRequestExecutor;
	}

	private UpdateDocumentRequestExecutor _createUpdateDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchBulkableDocumentRequestTranslator
			elasticsearchBulkableDocumentRequestTranslator) {

		UpdateDocumentRequestExecutor updateDocumentRequestExecutor =
			new UpdateDocumentRequestExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			updateDocumentRequestExecutor,
			"_elasticsearchBulkableDocumentRequestTranslator",
			elasticsearchBulkableDocumentRequestTranslator);
		ReflectionTestUtil.setFieldValue(
			updateDocumentRequestExecutor, "_elasticsearchClientResolver",
			elasticsearchClientResolver);

		return updateDocumentRequestExecutor;
	}

	private static final Scripts _scripts = new ScriptsImpl();

	private DocumentRequestExecutor _documentRequestExecutor;
	private ElasticsearchClientResolver _elasticsearchClientResolver;

}