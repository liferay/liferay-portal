/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.storage;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.storage.RankingsDatabaseImporter;
import com.liferay.portal.search.tuning.rankings.web.internal.index.DocumentToRankingTranslatorUtil;
import com.liferay.portal.search.tuning.rankings.web.internal.storage.helper.RankingJSONStorageHelper;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = RankingsDatabaseImporter.class)
public class RankingsDatabaseImporterImpl implements RankingsDatabaseImporter {

	@Override
	public void populateDatabase(long companyId) {
		try {
			_populateDatabase(companyId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to import rankings from the index to the ",
						"database for company id ", companyId, ". Make sure ",
						"the search engine is connected, then run the ",
						"rankings database importer Groovy script"),
					exception);
			}
		}
	}

	@Reference
	protected RankingIndexNameBuilder rankingIndexNameBuilder;

	@Reference(
		target = "(component.name=com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReindexer)"
	)
	protected IndexReindexer rankingIndexReindexer;

	@Reference
	protected RankingJSONStorageHelper rankingJSONStorageHelper;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private boolean _isStandardFormat(String id) {
		String[] parts = StringUtil.split(id, "_PORTLET_");

		if (parts.length == 2) {
			return true;
		}

		return false;
	}

	private void _populateDatabase(long companyId) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		RankingIndexName rankingIndexName =
			rankingIndexNameBuilder.getRankingIndexName(companyId);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Importing documents from " + rankingIndexName.getIndexName());
		}

		searchSearchRequest.setIndexNames(rankingIndexName.getIndexName());

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setQuery(QueriesUtil.matchAll());
		searchSearchRequest.setSelectedFieldNames(StringPool.BLANK);

		SearchSearchResponse searchSearchResponse = searchEngineAdapter.execute(
			searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		for (SearchHit searchHit : searchHitsList) {
			if (_isStandardFormat(searchHit.getId())) {
				continue;
			}

			Ranking ranking = DocumentToRankingTranslatorUtil.translate(
				_rankingBuilderFactory, searchHit.getDocument(),
				searchHit.getId());

			if (_log.isInfoEnabled()) {
				_log.info(
					"Adding database entry for document with ID " +
						ranking.getRankingDocumentId());
			}

			rankingJSONStorageHelper.addJSONStorageEntry(ranking);
		}

		if (_log.isInfoEnabled()) {
			_log.info("Reindexing " + rankingIndexName.getIndexName());
		}

		try {
			rankingIndexReindexer.reindex(
				companyId, IndexReindexer.ExecutionMode.FULL);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to reindex " + rankingIndexName.getIndexName(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingsDatabaseImporterImpl.class);

	@Reference
	private RankingBuilderFactory _rankingBuilderFactory;

}