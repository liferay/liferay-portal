/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.storage;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.storage.SynonymSetsDatabaseImporter;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.DocumentToSynonymSetTranslatorUtil;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.storage.helper.SynonymSetJSONStorageHelper;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SynonymSetsDatabaseImporter.class)
public class SynonymSetsDatabaseImporterImpl
	implements SynonymSetsDatabaseImporter {

	@Override
	public void populateDatabase(long companyId) {
		try {
			_populateDatabase(companyId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to import synonym sets from the index to the ",
						"database for company id ", companyId, ". Make sure ",
						"the search engine is connected, then run the synonym ",
						"sets database importer Groovy script"),
					exception);
			}
		}
	}

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	@Reference
	protected SynonymSetIndexNameBuilder synonymSetIndexNameBuilder;

	@Reference(
		target = "(component.name=com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReindexer)"
	)
	protected IndexReindexer synonymSetIndexReindexer;

	@Reference
	protected SynonymSetJSONStorageHelper synonymSetJSONStorageHelper;

	private boolean _isStandardFormat(String id) {
		String[] parts = StringUtil.split(id, "_PORTLET_");

		if (parts.length == 2) {
			return true;
		}

		return false;
	}

	private void _populateDatabase(long companyId) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		SynonymSetIndexName synonymSetIndexName =
			synonymSetIndexNameBuilder.getSynonymSetIndexName(companyId);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Importing documents from " +
					synonymSetIndexName.getIndexName());
		}

		searchSearchRequest.setIndexNames(synonymSetIndexName.getIndexName());

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setQuery(QueriesUtil.matchAll());

		SearchSearchResponse searchSearchResponse = searchEngineAdapter.execute(
			searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		for (SearchHit searchHit : searchHitsList) {
			if (_isStandardFormat(searchHit.getId())) {
				continue;
			}

			SynonymSet synonymSet =
				DocumentToSynonymSetTranslatorUtil.translate(
					searchHit.getDocument(), searchHit.getId());

			if (_log.isInfoEnabled()) {
				_log.info(
					"Adding database entry for document with ID " +
						synonymSet.getSynonymSetDocumentId());
			}

			synonymSetJSONStorageHelper.addJSONStorageEntry(
				companyId, synonymSetIndexName.getIndexName(),
				synonymSet.getSynonyms());
		}

		if (_log.isInfoEnabled()) {
			_log.info("Reindexing " + synonymSetIndexName.getIndexName());
		}

		try {
			synonymSetIndexReindexer.reindex(
				companyId, IndexReindexer.ExecutionMode.FULL);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to reindex " + synonymSetIndexName.getIndexName(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SynonymSetsDatabaseImporterImpl.class);

}