/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.model.listener;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.storage.RankingStorageAdapter;
import com.liferay.search.experiences.model.SXPBlueprint;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Almir Ferreira
 */
@Component(enabled = false, service = ModelListener.class)
public class SXPBlueprintModelListener extends BaseModelListener<SXPBlueprint> {

	@Override
	public void onBeforeRemove(SXPBlueprint sxpBlueprint) {
		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return;
		}

		try {
			RankingIndexName rankingIndexName =
				_rankingIndexNameBuilder.getRankingIndexName(
					sxpBlueprint.getCompanyId());

			List<Ranking> rankings =
				_rankingIndexReader.fetchBySXPBlueprintExternalReferenceCode(
					rankingIndexName, sxpBlueprint.getExternalReferenceCode());

			if (rankings == null) {
				return;
			}

			for (Ranking ranking : rankings) {
				Ranking.Builder rankingBuilder = _rankingBuilderFactory.builder(
					ranking);

				rankingBuilder.status(
					ResultRankingsConstants.STATUS_NOT_APPLICABLE);

				_rankingStorageAdapter.update(
					rankingBuilder.build(), rankingIndexName);
			}
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Reference
	private RankingBuilderFactory _rankingBuilderFactory;

	@Reference
	private RankingIndexNameBuilder _rankingIndexNameBuilder;

	@Reference
	private RankingIndexReader _rankingIndexReader;

	@Reference
	private RankingStorageAdapter _rankingStorageAdapter;

	@Reference
	private SearchEngineInformation _searchEngineInformation;

}