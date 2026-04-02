/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.upgrade.v3_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.upgrade.BaseRankingUpgradeProcessTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class JSONStorageEntryRankingUpgradeProcessTest
	extends BaseRankingUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeProcess() throws Exception {
		long classPK = counterLocalService.increment();

		addRanking(rankingClassNameId, classPK);

		runUpgrade();

		JSONObject rankingJSONObject =
			jsonStorageEntryLocalService.getJSONObject(
				rankingClassNameId, classPK);

		Assert.assertNotNull(rankingJSONObject);

		Assert.assertFalse(rankingJSONObject.has("inactive"));
		Assert.assertTrue(rankingJSONObject.has("status"));
		Assert.assertEquals(
			ResultRankingsConstants.STATUS_ACTIVE,
			rankingJSONObject.getString("status"));

		_indexReindexer.reindex(companyId, IndexReindexer.ExecutionMode.FULL);

		Assert.assertNotNull(
			_rankingIndexReader.fetch(
				Ranking.class.getName() + "_PORTLET_" + classPK,
				rankingIndexName));
	}

	@Override
	protected String getUpgradeStepClassName() {
		return "com.liferay.portal.search.tuning.rankings.web.internal." +
			"upgrade.v3_0_0.JSONStorageEntryRankingUpgradeProcess";
	}

	@Inject(
		filter = "(&(component.name=com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReindexer))"
	)
	private IndexReindexer _indexReindexer;

	@Inject
	private RankingIndexReader _rankingIndexReader;

}