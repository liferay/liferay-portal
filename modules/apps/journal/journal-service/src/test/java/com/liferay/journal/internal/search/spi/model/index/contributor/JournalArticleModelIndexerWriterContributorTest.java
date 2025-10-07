/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.index.contributor;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.batch.BatchIndexingHelper;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class JournalArticleModelIndexerWriterContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpConfigurationProvider();
		_setUpIndexer();
		_setUpIndexerRegistryUtil();
		_setUpJournalArticleLocalService();
		_setUpJournalArticles();
		_setUpPortal();
	}

	@After
	public void tearDown() {
		_indexerRegistryUtilMockedStatic.close();
	}

	@Test
	public void testModelDeletedIndexAllArticleVersionsDisabled()
		throws Exception {

		_setUpJournalServiceConfiguration(false);

		_testModelDeleted(_journalArticles.get(0), _journalArticles.get(2), 0);
		_testModelDeleted(_journalArticles.get(1), _journalArticles.get(2), 0);
		_testModelDeleted(_journalArticles.get(2), _journalArticles.get(1), 1);
	}

	@Test
	public void testModelDeletedIndexAllArticleVersionsEnabled()
		throws Exception {

		_setUpJournalServiceConfiguration(true);

		_testModelDeleted(_journalArticles.get(0), _journalArticles.get(2), 1);
		_testModelDeleted(_journalArticles.get(1), _journalArticles.get(2), 1);
		_testModelDeleted(_journalArticles.get(2), _journalArticles.get(1), 1);
	}

	private void _setUpConfigurationProvider() throws Exception {
		_configurationProvider = Mockito.mock(ConfigurationProvider.class);

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				Mockito.any(Class.class), Mockito.anyLong())
		).thenReturn(
			_journalServiceConfiguration
		);
	}

	private void _setUpIndexer() {
		_indexer = Mockito.mock(Indexer.class);
	}

	private void _setUpIndexerRegistryUtil() {
		_indexerRegistryUtilMockedStatic = Mockito.mockStatic(
			IndexerRegistryUtil.class);

		Mockito.when(
			IndexerRegistryUtil.nullSafeGetIndexer(JournalArticle.class)
		).thenReturn(
			_indexer
		);
	}

	private void _setUpJournalArticleLocalService() {
		_journalArticleLocalService = Mockito.mock(
			JournalArticleLocalService.class);

		Mockito.when(
			_journalArticleLocalService.getArticles(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.any(ArticleVersionComparator.class))
		).thenReturn(
			_journalArticles
		);
	}

	private void _setUpJournalArticles() {
		double version = 1.0;
		long id = 0;

		for (JournalArticle journalArticle : _journalArticles) {
			Mockito.when(
				journalArticle.getArticleId()
			).thenReturn(
				"articleId"
			);

			Mockito.when(
				journalArticle.getId()
			).thenReturn(
				id++
			);

			Mockito.when(
				journalArticle.getVersion()
			).thenReturn(
				version
			);
			version += 0.1;
		}
	}

	private void _setUpJournalServiceConfiguration(
		boolean indexAllArticleVersionsEnabled) {

		Mockito.when(
			_journalServiceConfiguration.indexAllArticleVersionsEnabled()
		).thenReturn(
			indexAllArticleVersionsEnabled
		);
	}

	private void _setUpPortal() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getClassNameId(Mockito.any(Class.class))
		).thenReturn(
			-1L
		);

		portalUtil.setPortal(portal);
	}

	private void _testModelDeleted(
			JournalArticle deletedVersionJournalArticle,
			JournalArticle latestIndexableArticle, int expectedTimes)
		throws Exception {

		Mockito.clearInvocations(_indexer);

		Mockito.when(
			_journalArticleLocalService.fetchLatestArticle(
				Mockito.anyLong(), Mockito.any(int[].class))
		).thenReturn(
			latestIndexableArticle
		);

		JournalArticleModelIndexerWriterContributor
			journalArticleModelIndexerWriterContributor =
				new JournalArticleModelIndexerWriterContributor(
					Mockito.mock(BatchIndexingHelper.class),
					_configurationProvider,
					Mockito.mock(
						DynamicQueryBatchIndexingActionableFactory.class),
					_journalArticleLocalService,
					Mockito.mock(JournalArticleResourceLocalService.class));

		journalArticleModelIndexerWriterContributor.modelDeleted(
			deletedVersionJournalArticle);

		Mockito.verify(
			_indexer, Mockito.times(expectedTimes)
		).reindex(
			latestIndexableArticle, false
		);
	}

	private ConfigurationProvider _configurationProvider;
	private Indexer<JournalArticle> _indexer;
	private MockedStatic<IndexerRegistryUtil> _indexerRegistryUtilMockedStatic;
	private JournalArticleLocalService _journalArticleLocalService;
	private final List<JournalArticle> _journalArticles = ListUtil.fromArray(
		Mockito.mock(JournalArticle.class), Mockito.mock(JournalArticle.class),
		Mockito.mock(JournalArticle.class));
	private final JournalServiceConfiguration _journalServiceConfiguration =
		Mockito.mock(JournalServiceConfiguration.class);

}