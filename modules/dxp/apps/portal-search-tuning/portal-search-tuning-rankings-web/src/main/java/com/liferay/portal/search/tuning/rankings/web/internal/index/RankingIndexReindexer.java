/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index;

import com.liferay.json.storage.service.JSONStorageEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.SyncReindexManager;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.RankingPinBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = "search.index.category=search-tuning",
	service = IndexReindexer.class
)
public class RankingIndexReindexer implements IndexReindexer {

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		if (!searchCapabilities.isResultRankingsSupported() ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		RankingIndexName rankingIndexName =
			rankingIndexNameBuilder.getRankingIndexName(companyId);

		Date date = null;

		if (_isExecuteSyncReindex(executionMode)) {
			date = new Date();

			Thread.sleep(1000);

			try {
				RankingIndexCreatorUtil.createIfNotExists(
					_searchEngineAdapter, rankingIndexName);
			}
			catch (RuntimeException runtimeException) {
				_log.error(
					"Unable to create index " + rankingIndexName.getIndexName(),
					runtimeException);

				return;
			}
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Deleting and creating index " +
						rankingIndexName.getIndexName());
			}

			try {
				RankingIndexCreatorUtil.deleteIfExists(
					_searchEngineAdapter, rankingIndexName);

				RankingIndexCreatorUtil.create(
					_searchEngineAdapter, rankingIndexName);
			}
			catch (RuntimeException runtimeException) {
				_log.error(
					"Unable to delete or create index " +
						rankingIndexName.getIndexName(),
					runtimeException);

				return;
			}
		}

		List<Long> classPKs = jsonStorageEntryLocalService.getClassPKs(
			companyId, classNameLocalService.getClassNameId(Ranking.class),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int sendStatusInterval = Math.max(100, classPKs.size() / 20);

		for (int i = 0; i < classPKs.size(); i++) {
			_rankingIndexWriter.create(
				rankingIndexName, _buildRanking(classPKs.get(i)));

			if ((i % sendStatusInterval) == 0) {
				ReindexStatusMessageSenderUtil.sendStatusMessage(
					RankingIndexReindexer.class.getName(), i + 1,
					classPKs.size());
			}
		}

		if (_isExecuteSyncReindex(executionMode)) {
			SyncReindexManager syncReindexManager =
				_syncReindexManagerSnapshot.get();

			syncReindexManager.deleteStaleDocuments(
				rankingIndexName.getIndexName(), date, Collections.emptySet());
		}
	}

	@Activate
	protected void activate() {
		_rankingIndexWriter = new RankingIndexWriter(_searchEngineAdapter);
	}

	@Reference
	protected ClassNameLocalService classNameLocalService;

	@Reference
	protected JSONStorageEntryLocalService jsonStorageEntryLocalService;

	@Reference
	protected RankingBuilderFactory rankingBuilderFactory;

	@Reference
	protected RankingIndexNameBuilder rankingIndexNameBuilder;

	@Reference
	protected RankingPinBuilderFactory rankingPinBuilderFactory;

	@Reference
	protected SearchCapabilities searchCapabilities;

	private Ranking _buildRanking(long classPK) throws Exception {
		JSONObject jsonObject = jsonStorageEntryLocalService.getJSONObject(
			classNameLocalService.getClassNameId(Ranking.class), classPK);

		Ranking.Builder rankingBuilder = rankingBuilderFactory.builder();

		rankingBuilder.aliases(
			JSONUtil.toStringList(jsonObject.getJSONArray("aliases"))
		).groupExternalReferenceCode(
			jsonObject.getString("groupExternalReferenceCode")
		).hiddenDocumentIds(
			JSONUtil.toStringList(jsonObject.getJSONArray("hiddenDocumentIds"))
		).rankingDocumentId(
			jsonObject.getString("rankingDocumentId")
		).indexName(
			jsonObject.getString("indexName")
		).name(
			jsonObject.getString("name")
		).pins(
			_getPins(jsonObject.getJSONArray("pins"))
		).queryString(
			jsonObject.getString("queryString")
		).status(
			_getStatus(jsonObject)
		).sxpBlueprintExternalReferenceCode(
			jsonObject.getString("sxpBlueprintExternalReferenceCode")
		);

		return rankingBuilder.build();
	}

	private List<Ranking.Pin> _getPins(JSONArray jsonArray) throws Exception {
		List<Ranking.Pin> pins = new ArrayList<>();

		JSONUtil.toList(
			jsonArray,
			jsonObject -> pins.add(
				rankingPinBuilderFactory.builder(
				).documentId(
					jsonObject.getString("documentId")
				).position(
					jsonObject.getInt("position")
				).build()));

		return pins;
	}

	private String _getStatus(JSONObject jsonObject) {
		String status = jsonObject.getString(RankingFields.STATUS);

		if (!Validator.isBlank(status)) {
			return status;
		}

		if (jsonObject.getBoolean("inactive")) {
			return ResultRankingsConstants.STATUS_INACTIVE;
		}

		return ResultRankingsConstants.STATUS_ACTIVE;
	}

	private boolean _isExecuteSyncReindex(ExecutionMode executionMode) {
		if ((_syncReindexManagerSnapshot.get() != null) &&
			(executionMode != null) &&
			executionMode.equals(ExecutionMode.SYNC)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingIndexReindexer.class);

	private static final Snapshot<SyncReindexManager>
		_syncReindexManagerSnapshot = new Snapshot<>(
			RankingIndexReindexer.class, SyncReindexManager.class, null, true);

	private RankingIndexWriter _rankingIndexWriter;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}