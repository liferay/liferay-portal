/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.script.ScriptTranslator;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.script.ScriptBuilder;
import com.liferay.portal.search.script.ScriptType;
import com.liferay.portal.search.script.Scripts;

import java.io.IOException;

import java.util.Map;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dylan Rebelak
 */
@Component(service = UpdateByQueryDocumentRequestExecutor.class)
public class UpdateByQueryDocumentRequestExecutorImpl
	implements UpdateByQueryDocumentRequestExecutor {

	@Override
	public UpdateByQueryDocumentResponse execute(
		UpdateByQueryDocumentRequest updateByQueryDocumentRequest) {

		UpdateByQueryRequest updateByQueryRequest = createUpdateByQueryRequest(
			updateByQueryDocumentRequest);

		BulkByScrollResponse bulkByScrollResponse = getBulkByScrollResponse(
			updateByQueryRequest, updateByQueryDocumentRequest);

		TimeValue timeValue = bulkByScrollResponse.getTook();

		return new UpdateByQueryDocumentResponse(
			bulkByScrollResponse.getUpdated(), timeValue.getMillis());
	}

	@Activate
	protected void activate() {
		_legacyQueryTranslator = new ElasticsearchQueryTranslator(
			_indexNameBuilder);
	}

	protected UpdateByQueryRequest createUpdateByQueryRequest(
		UpdateByQueryDocumentRequest updateByQueryDocumentRequest) {

		UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest();

		updateByQueryRequest.indices(
			updateByQueryDocumentRequest.getIndexNames());

		if (updateByQueryDocumentRequest.getPortalSearchQuery() != null) {
			QueryBuilder queryBuilder = _queryTranslator.translate(
				updateByQueryDocumentRequest.getPortalSearchQuery());

			updateByQueryRequest.setQuery(queryBuilder);
		}
		else {
			@SuppressWarnings("deprecation")
			QueryBuilder queryBuilder = _legacyQueryTranslator.translate(
				updateByQueryDocumentRequest.getQuery(), null);

			updateByQueryRequest.setQuery(queryBuilder);
		}

		updateByQueryRequest.setRefresh(
			updateByQueryDocumentRequest.isRefresh());

		if (updateByQueryDocumentRequest.getScript() != null) {
			updateByQueryRequest.setScript(
				_scriptTranslator.translate(
					updateByQueryDocumentRequest.getScript()));
		}
		else if (updateByQueryDocumentRequest.getScriptJSONObject() != null) {
			ScriptBuilder scriptBuilder = _scripts.builder();

			JSONObject scriptJSONObject =
				updateByQueryDocumentRequest.getScriptJSONObject();

			if (scriptJSONObject.has("idOrCode")) {
				scriptBuilder.idOrCode(scriptJSONObject.getString("idOrCode"));
			}

			if (scriptJSONObject.has("language")) {
				scriptBuilder.language(scriptJSONObject.getString("language"));
			}

			if (scriptJSONObject.has("optionsMap")) {
				scriptBuilder.options(
					(Map<String, String>)scriptJSONObject.get("optionsMap"));
			}

			if (scriptJSONObject.has("parametersMap")) {
				scriptBuilder.parameters(
					(Map<String, Object>)scriptJSONObject.get("parametersMap"));
			}

			if (scriptJSONObject.has("scriptType")) {
				scriptBuilder.scriptType(
					(ScriptType)scriptJSONObject.get("scriptType"));
			}

			updateByQueryRequest.setScript(
				_scriptTranslator.translate(scriptBuilder.build()));
		}

		return updateByQueryRequest;
	}

	protected BulkByScrollResponse getBulkByScrollResponse(
		UpdateByQueryRequest updateByQueryRequest,
		UpdateByQueryDocumentRequest updateByQueryDocumentRequest) {

		RestHighLevelClient restHighLevelClient =
			_elasticsearchClientResolver.getRestHighLevelClient(
				updateByQueryDocumentRequest.getConnectionId(),
				updateByQueryDocumentRequest.isPreferLocalCluster());

		try {
			return restHighLevelClient.updateByQuery(
				updateByQueryRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Reference
	private ElasticsearchClientResolver _elasticsearchClientResolver;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	private com.liferay.portal.kernel.search.query.QueryTranslator<QueryBuilder>
		_legacyQueryTranslator;
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new com.liferay.portal.search.elasticsearch7.internal.query.
			ElasticsearchQueryTranslator();

	@Reference
	private Scripts _scripts;

	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}