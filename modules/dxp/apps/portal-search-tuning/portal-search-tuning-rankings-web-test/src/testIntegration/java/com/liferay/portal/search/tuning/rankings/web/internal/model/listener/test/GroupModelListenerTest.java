/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.storage.RankingStorageAdapter;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlag(enable = false, value = "LPD-6368")
	@Test
	public void testOnBeforeRemove() throws Exception {
		Group group = GroupTestUtil.addGroup();
		String queryString = RandomTestUtil.randomString();
		RankingIndexName rankingIndexName =
			_rankingIndexNameBuilder.getRankingIndexName(
				TestPropsValues.getCompanyId());

		String rankingDocumentId = _rankingStorageAdapter.create(
			_rankingBuilderFactory.builder(
			).groupExternalReferenceCode(
				group.getExternalReferenceCode()
			).name(
				queryString
			).queryString(
				queryString
			).status(
				ResultRankingsConstants.STATUS_ACTIVE
			).build(),
			rankingIndexName);

		try {
			_searchEngineAdapter.execute(
				new RefreshIndexRequest(rankingIndexName.getIndexName()));

			_groupLocalService.deleteGroup(group);

			Ranking ranking = _rankingIndexReader.fetch(
				rankingDocumentId, rankingIndexName);

			Assert.assertEquals(
				ResultRankingsConstants.STATUS_NOT_APPLICABLE,
				ranking.getStatus());
		}
		finally {
			_rankingStorageAdapter.delete(rankingDocumentId, rankingIndexName);
		}
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private RankingBuilderFactory _rankingBuilderFactory;

	@Inject
	private RankingIndexNameBuilder _rankingIndexNameBuilder;

	@Inject
	private RankingIndexReader _rankingIndexReader;

	@Inject
	private RankingStorageAdapter _rankingStorageAdapter;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

}