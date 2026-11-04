/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.model.listener;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.storage.RankingStorageAdapter;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Almir Ferreira
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onBeforeRemove(Group group) {
		if (Objects.equals(
				_searchEngineInformation.getVendorString(), "Solr")) {

			return;
		}

		try {
			RankingIndexName rankingIndexName =
				_rankingIndexNameBuilder.getRankingIndexName(
					group.getCompanyId());

			List<Ranking> rankings =
				_rankingIndexReader.fetchByGroupExternalReferenceCode(
					group.getExternalReferenceCode(), rankingIndexName);

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