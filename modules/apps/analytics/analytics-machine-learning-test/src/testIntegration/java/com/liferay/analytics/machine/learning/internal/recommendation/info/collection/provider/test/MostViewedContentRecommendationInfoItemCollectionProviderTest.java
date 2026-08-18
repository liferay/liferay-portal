/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.recommendation.info.collection.provider.test;

import com.liferay.analytics.machine.learning.content.MostViewedContentRecommendation;
import com.liferay.analytics.machine.learning.content.MostViewedContentRecommendationManager;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@FeatureFlag("LRAC-14771")
@RunWith(Arquillian.class)
public class MostViewedContentRecommendationInfoItemCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		_initialTotalCount =
			_mostViewedContentRecommendationManager.
				getMostViewedContentRecommendationsCount(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId());

		_addMostViewedContentRecommendations();
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetCollectionInfoPage() throws Exception {
		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId())) {

			InfoCollectionProvider<AssetEntry> infoCollectionProvider =
				_infoItemServiceRegistry.getInfoItemService(
					InfoCollectionProvider.class,
					StringBundler.concat(
						"com.liferay.analytics.machine.learning.internal.",
						"recommendation.info.collection.provider.",
						"MostViewedContentRecommendationInfoItemCollection",
						"Provider"));

			Assert.assertNotNull(infoCollectionProvider);

			IdempotentRetryAssert.retryAssert(
				3, TimeUnit.SECONDS,
				() -> {
					InfoPage<AssetEntry> infoPage =
						infoCollectionProvider.getCollectionInfoPage(
							new CollectionQuery());

					Assert.assertEquals(
						_initialTotalCount + _ENTRIES_COUNT,
						infoPage.getTotalCount());
				});
		}
	}

	private void _addMostViewedContentRecommendations() throws Exception {
		for (int i = 0; i < _ENTRIES_COUNT; i++) {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			int rank = RandomTestUtil.randomInt(1, 10);

			float score = 1.0F - (rank / 10.0F);

			_mostViewedContentRecommendationManager.
				addMostViewedContentRecommendation(
					_createMostViewedContentRecommendation(
						journalArticle.getResourcePrimKey(), score));
		}
	}

	private MostViewedContentRecommendation
		_createMostViewedContentRecommendation(
			long recommendedEntryClassPK, float score) {

		MostViewedContentRecommendation mostViewedContentRecommendation =
			new MostViewedContentRecommendation();

		mostViewedContentRecommendation.setCompanyId(_group.getCompanyId());
		mostViewedContentRecommendation.setRecommendedEntryClassPK(
			recommendedEntryClassPK);
		mostViewedContentRecommendation.setScore(score);

		return mostViewedContentRecommendation;
	}

	private static final int _ENTRIES_COUNT = 3;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private long _initialTotalCount;

	@Inject
	private MostViewedContentRecommendationManager
		_mostViewedContentRecommendationManager;

}