/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexWriter;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.suggest.SpellCheckIndexWriter;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.solr8.configuration.SolrConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.solr8.configuration.SolrConfiguration",
	property = "search.engine.impl=Solr", service = IndexWriter.class
)
public class SolrIndexWriter extends BaseIndexWriter {

	@Override
	public void addDocument(SearchContext searchContext, Document document) {
		try {
			IndexDocumentRequest indexDocumentRequest =
				new IndexDocumentRequest(_defaultCollection, document);

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				indexDocumentRequest.setRefresh(true);
			}

			_searchEngineAdapter.execute(indexDocumentRequest);
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void addDocuments(
		SearchContext searchContext, Collection<Document> documents) {

		try {
			BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				bulkDocumentRequest.setRefresh(true);
			}

			documents.forEach(
				document -> {
					IndexDocumentRequest indexDocumentRequest =
						new IndexDocumentRequest(_defaultCollection, document);

					bulkDocumentRequest.addBulkableDocumentRequest(
						indexDocumentRequest);
				});

			BulkDocumentResponse bulkDocumentResponse =
				_searchEngineAdapter.execute(bulkDocumentRequest);

			if (bulkDocumentResponse.hasErrors()) {
				if (_logExceptionsOnly) {
					_log.error("Bulk add failed");
				}
				else {
					throw new SystemException("Bulk add failed");
				}
			}
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void commit(SearchContext searchContext) {
		RefreshIndexRequest refreshIndexRequest = new RefreshIndexRequest(
			_defaultCollection);

		try {
			_searchEngineAdapter.execute(refreshIndexRequest);
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void deleteDocument(SearchContext searchContext, String uid) {
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			_defaultCollection, uid);

		if (PortalRunMode.isTestMode() || searchContext.isCommitImmediately()) {
			deleteDocumentRequest.setRefresh(true);
		}

		try {
			_searchEngineAdapter.execute(deleteDocumentRequest);
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void deleteDocuments(
		SearchContext searchContext, Collection<String> uids) {

		try {
			BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				bulkDocumentRequest.setRefresh(true);
			}

			uids.forEach(
				uid -> {
					DeleteDocumentRequest deleteDocumentRequest =
						new DeleteDocumentRequest(_defaultCollection, uid);

					bulkDocumentRequest.addBulkableDocumentRequest(
						deleteDocumentRequest);
				});

			BulkDocumentResponse bulkDocumentResponse =
				_searchEngineAdapter.execute(bulkDocumentRequest);

			if (bulkDocumentResponse.hasErrors()) {
				if (_logExceptionsOnly) {
					_log.error("Bulk delete failed");
				}
				else {
					throw new SystemException("Bulk delete failed");
				}
			}
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void deleteEntityDocuments(
		SearchContext searchContext, String className) {

		try {
			BooleanQuery booleanQuery = new BooleanQuery();

			long companyId = searchContext.getCompanyId();

			if (companyId > 0) {
				booleanQuery.add(
					new TermQuery(Field.COMPANY_ID, String.valueOf(companyId)),
					BooleanClauseOccur.MUST);
			}

			booleanQuery.add(
				new TermQuery(Field.ENTRY_CLASS_NAME, className),
				BooleanClauseOccur.MUST);

			DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
				new DeleteByQueryDocumentRequest(
					booleanQuery, _defaultCollection);

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				deleteByQueryDocumentRequest.setRefresh(true);
			}

			_searchEngineAdapter.execute(deleteByQueryDocumentRequest);
		}
		catch (Exception exception) {
			if (_logExceptionsOnly) {
				_log.error(exception);
			}
			else {
				if (exception instanceof RuntimeException) {
					throw (RuntimeException)exception;
				}

				throw new SystemException(exception.getMessage(), exception);
			}
		}
	}

	@Override
	public void partiallyUpdateDocument(
		SearchContext searchContext, Document document) {

		try {
			UpdateDocumentRequest updateDocumentRequest =
				new UpdateDocumentRequest(
					_defaultCollection, document.getUID(), document);

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				updateDocumentRequest.setRefresh(true);
			}

			_searchEngineAdapter.execute(updateDocumentRequest);
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void partiallyUpdateDocuments(
		SearchContext searchContext, Collection<Document> documents) {

		try {
			BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				bulkDocumentRequest.setRefresh(true);
			}

			documents.forEach(
				document -> {
					UpdateDocumentRequest updateDocumentRequest =
						new UpdateDocumentRequest(
							_defaultCollection, document.getUID(), document);

					bulkDocumentRequest.addBulkableDocumentRequest(
						updateDocumentRequest);
				});

			BulkDocumentResponse bulkDocumentResponse =
				_searchEngineAdapter.execute(bulkDocumentRequest);

			if (bulkDocumentResponse.hasErrors()) {
				if (_logExceptionsOnly) {
					_log.error("Bulk partial update failed");
				}
				else {
					throw new SystemException("Bulk partial update failed");
				}
			}
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Override
	public void updateDocument(SearchContext searchContext, Document document) {
		updateDocuments(searchContext, Collections.singleton(document));
	}

	@Override
	public void updateDocuments(
		SearchContext searchContext, Collection<Document> documents) {

		try {
			BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

			if (PortalRunMode.isTestMode() ||
				searchContext.isCommitImmediately()) {

				bulkDocumentRequest.setRefresh(true);
			}

			documents.forEach(
				document -> {
					DeleteDocumentRequest deleteDocumentRequest =
						new DeleteDocumentRequest(
							_defaultCollection, document.getUID());

					bulkDocumentRequest.addBulkableDocumentRequest(
						deleteDocumentRequest);

					IndexDocumentRequest indexDocumentRequest =
						new IndexDocumentRequest(_defaultCollection, document);

					bulkDocumentRequest.addBulkableDocumentRequest(
						indexDocumentRequest);
				});

			BulkDocumentResponse bulkDocumentResponse =
				_searchEngineAdapter.execute(bulkDocumentRequest);

			if (bulkDocumentResponse.hasErrors()) {
				String bulkDocumentResponseFailureMessages =
					_getBulkDocumentResponseFailureMessages(
						bulkDocumentResponse);

				String errorMessage =
					"Update failed: " + bulkDocumentResponseFailureMessages;

				if (_logExceptionsOnly) {
					_log.error(errorMessage);
				}
				else {
					throw new SystemException(errorMessage);
				}
			}
		}
		catch (RuntimeException runtimeException) {
			if (_logExceptionsOnly) {
				_log.error(runtimeException);
			}
			else {
				throw runtimeException;
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_solrConfiguration = ConfigurableUtil.createConfigurable(
			SolrConfiguration.class, properties);

		_defaultCollection = _solrConfiguration.defaultCollection();
		_logExceptionsOnly = _solrConfiguration.logExceptionsOnly();
	}

	@Override
	protected SpellCheckIndexWriter getSpellCheckIndexWriter() {
		return _spellCheckIndexWriter;
	}

	private String _getBulkDocumentResponseFailureMessages(
		BulkDocumentResponse bulkDocumentResponse) {

		StringBundler sb = new StringBundler();

		for (BulkDocumentItemResponse bulkDocumentItemResponse :
				bulkDocumentResponse.getBulkDocumentItemResponses()) {

			if (bulkDocumentItemResponse.getFailureMessage() == null) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(bulkDocumentItemResponse.getFailureMessage());
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SolrIndexWriter.class);

	private volatile String _defaultCollection;
	private volatile boolean _logExceptionsOnly;

	@Reference(target = "(search.engine.impl=Solr)")
	private SearchEngineAdapter _searchEngineAdapter;

	private volatile SolrConfiguration _solrConfiguration;

	@Reference(target = "(search.engine.impl=Solr)")
	private SpellCheckIndexWriter _spellCheckIndexWriter;

}