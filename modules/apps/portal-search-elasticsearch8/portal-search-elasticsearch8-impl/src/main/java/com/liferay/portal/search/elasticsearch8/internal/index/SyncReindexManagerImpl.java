/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.index;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.DateRangeTermQuery;
import com.liferay.portal.search.query.ExistsQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;

import java.text.Format;

import java.util.Date;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SyncReindexManager.class)
public class SyncReindexManagerImpl implements SyncReindexManager {

	@Override
	public void deleteStaleDocuments(
		long companyId, Date date, Set<String> classNames) {

		deleteStaleDocuments(
			_indexNameBuilder.getIndexName(companyId), date, classNames);
	}

	@Override
	public void deleteStaleDocuments(
		String indexName, Date date, Set<String> classNames) {

		if (_log.isInfoEnabled()) {
			_log.info("Deleting stale documents in index " + indexName);
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		if (SetUtil.isNotEmpty(classNames)) {
			TermsQuery termsQuery = QueriesUtil.terms(Field.ENTRY_CLASS_NAME);

			termsQuery.addValues(classNames.toArray());

			booleanQuery.addFilterQueryClauses(termsQuery);
		}

		BooleanQuery timestampBooleanQuery = QueriesUtil.booleanQuery();

		Format format = _fastDateFormatFactory.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		DateRangeTermQuery dateRangeTermQuery = QueriesUtil.dateRangeTerm(
			"timestamp", false, false, null, format.format(date));

		timestampBooleanQuery.addShouldQueryClauses(dateRangeTermQuery);

		BooleanQuery existsBooleanQuery = QueriesUtil.booleanQuery();

		ExistsQuery existsQuery = QueriesUtil.exists("timestamp");

		existsBooleanQuery.addMustNotQueryClauses(existsQuery);

		timestampBooleanQuery.addShouldQueryClauses(existsBooleanQuery);

		booleanQuery.addFilterQueryClauses(timestampBooleanQuery);

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(booleanQuery, indexName);

		deleteByQueryDocumentRequest.setProceedOnConflicts(true);

		_searchEngineAdapter.execute(deleteByQueryDocumentRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SyncReindexManagerImpl.class);

	@Reference
	private FastDateFormatFactory _fastDateFormatFactory;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}