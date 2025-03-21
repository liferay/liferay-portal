/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.results.builder;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.tuning.rankings.helper.RankingHelper;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.web.internal.util.RankingResultUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;

/**
 * @author André de Oliveira
 * @author Bryan Engler
 */
public class RankingGetHiddenResultsBuilder {

	public RankingGetHiddenResultsBuilder(
		DLAppLocalService dlAppLocalService,
		FastDateFormatFactory fastDateFormatFactory, Queries queries,
		RankingHelper rankingHelper, RankingIndexName rankingIndexName,
		RankingIndexReader rankingIndexReader, ResourceActions resourceActions,
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		SearchEngineAdapter searchEngineAdapter) {

		_dlAppLocalService = dlAppLocalService;
		_fastDateFormatFactory = fastDateFormatFactory;
		_queries = queries;
		_rankingHelper = rankingHelper;
		_rankingIndexName = rankingIndexName;
		_rankingIndexReader = rankingIndexReader;
		_resourceActions = resourceActions;
		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_searchEngineAdapter = searchEngineAdapter;
	}

	public JSONObject build() {
		Ranking ranking = _rankingIndexReader.fetch(
			_rankingId, _rankingIndexName);

		if (ranking == null) {
			return JSONUtil.put(
				"documents", JSONFactoryUtil.createJSONArray()
			).put(
				"total", 0
			);
		}

		List<String> ids = _rankingHelper.translateDocumentIds(
			ranking.getHiddenDocumentIds());

		List<String> paginatedIds = _paginateIds(ids);

		return JSONUtil.put(
			"documents", buildDocuments(paginatedIds, ranking)
		).put(
			"total", ids.size()
		);
	}

	public RankingGetHiddenResultsBuilder from(int from) {
		_from = from;

		return this;
	}

	public RankingGetHiddenResultsBuilder rankingId(String rankingId) {
		_rankingId = rankingId;

		return this;
	}

	public RankingGetHiddenResultsBuilder size(int size) {
		_size = size;

		return this;
	}

	protected JSONArray buildDocuments(List<String> ids, Ranking ranking) {
		return JSONUtil.toJSONArray(
			TransformUtil.transform(
				ids,
				id -> _getDocument(
					ranking.getIndexName(), id, LIFERAY_DOCUMENT_TYPE)),
			this::translate, _log);
	}

	protected JSONObject translate(Document document) {
		RankingJSONBuilder rankingJSONBuilder = new RankingJSONBuilder(
			_dlAppLocalService, _fastDateFormatFactory, _resourceActions,
			_resourceRequest);

		return rankingJSONBuilder.deleted(
			_isAssetDeleted(document)
		).document(
			document
		).hidden(
			true
		).viewURL(
			_getViewURL(document)
		).build();
	}

	protected static final String LIFERAY_DOCUMENT_TYPE = "_doc";

	private Document _getDocument(String indexName, String id, String type) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			indexName, id);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setType(type);

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (!getDocumentResponse.isExists()) {
			return null;
		}

		return getDocumentResponse.getDocument();
	}

	private String _getViewURL(Document document) {
		return RankingResultUtil.getRankingResultViewURL(
			document, _resourceRequest, _resourceResponse, true);
	}

	private boolean _isAssetDeleted(Document document) {
		return RankingResultUtil.isAssetDeleted(document);
	}

	private List<String> _paginateIds(List<String> ids) {
		int end = _from + _size;

		return ListUtil.subList(ids, _from, end);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingGetHiddenResultsBuilder.class.getName());

	private final DLAppLocalService _dlAppLocalService;
	private final FastDateFormatFactory _fastDateFormatFactory;
	private int _from;
	private final Queries _queries;
	private final RankingHelper _rankingHelper;
	private String _rankingId;
	private final RankingIndexName _rankingIndexName;
	private final RankingIndexReader _rankingIndexReader;
	private final ResourceActions _resourceActions;
	private final ResourceRequest _resourceRequest;
	private final ResourceResponse _resourceResponse;
	private final SearchEngineAdapter _searchEngineAdapter;
	private int _size;

}