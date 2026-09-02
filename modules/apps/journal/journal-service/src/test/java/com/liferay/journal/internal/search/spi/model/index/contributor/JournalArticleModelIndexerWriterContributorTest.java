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
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.batch.BatchIndexingHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		_setUpJournalArticles(
			WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.STATUS_APPROVED);
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

	@Test
	public void testModelDeletedWithAllVersionsApproved() throws Exception {
		_setUpJournalServiceConfiguration(true);

		_setUpJournalArticles(_getStatuses());

		Mockito.when(
			_journalArticleLocalService.fetchLatestArticle(
				Mockito.anyLong(), Mockito.any(int[].class))
		).thenReturn(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 2)
		);

		JournalArticleModelIndexerWriterContributor
			journalArticleModelIndexerWriterContributor =
				_createJournalArticleModelIndexerWriterContributor();

		journalArticleModelIndexerWriterContributor.modelDeleted(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 1));

		Mockito.verify(
			_indexer
		).reindex(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 2), false
		);

		Mockito.verifyNoMoreInteractions(_indexer);
	}

	@Test
	public void testModelIndexedWithAllVersionsApproved() throws Exception {
		_setUpJournalServiceConfiguration(true);

		_setUpJournalArticles(_getStatuses());

		JournalArticleModelIndexerWriterContributor
			journalArticleModelIndexerWriterContributor =
				_createJournalArticleModelIndexerWriterContributor();

		journalArticleModelIndexerWriterContributor.modelIndexed(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 1));

		Mockito.verify(
			_indexer
		).reindex(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 2), false
		);

		Mockito.verifyNoMoreInteractions(_indexer);
	}

	@Test
	public void testModelIndexedWithOnlyFirstAndLastVersionsApproved()
		throws Exception {

		_setUpJournalServiceConfiguration(true);

		int[] statuses = _getStatuses();

		for (int i = 1; i < (_JOURNAL_ARTICLE_VERSION_COUNT - 1); i++) {
			statuses[i] = WorkflowConstants.STATUS_DRAFT;
		}

		_setUpJournalArticles(statuses);

		JournalArticleModelIndexerWriterContributor
			journalArticleModelIndexerWriterContributor =
				_createJournalArticleModelIndexerWriterContributor();

		journalArticleModelIndexerWriterContributor.modelIndexed(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 1));

		Mockito.verify(
			_indexer
		).reindex(
			_journalArticles.get(0), false
		);

		Mockito.verify(
			_indexer
		).reindex(
			_journalArticles.get(_JOURNAL_ARTICLE_VERSION_COUNT - 2), false
		);

		Mockito.verifyNoMoreInteractions(_indexer);
	}

	private JournalArticleModelIndexerWriterContributor
		_createJournalArticleModelIndexerWriterContributor() {

		return new JournalArticleModelIndexerWriterContributor(
			Mockito.mock(BatchIndexingHelper.class), _configurationProvider,
			_journalArticleLocalService,
			Mockito.mock(JournalArticleResourceLocalService.class));
	}

	private int[] _getStatuses() {
		int[] statuses = new int[_JOURNAL_ARTICLE_VERSION_COUNT];

		Arrays.fill(statuses, WorkflowConstants.STATUS_APPROVED);

		return statuses;
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
		).thenAnswer(
			invocationOnMock -> {
				List<JournalArticle> journalArticles = new ArrayList<>(
					_journalArticles);

				Collections.reverse(journalArticles);

				return journalArticles;
			}
		);
	}

	private void _setUpJournalArticles(int... statuses) {
		_journalArticles = new ArrayList<>(statuses.length);

		double version = 1.0;

		for (int i = 0; i < statuses.length; i++) {
			JournalArticle journalArticle = Mockito.mock(JournalArticle.class);

			Mockito.when(
				journalArticle.getArticleId()
			).thenReturn(
				"articleId"
			);

			Mockito.when(
				journalArticle.getId()
			).thenReturn(
				(long)i
			);

			Mockito.when(
				journalArticle.getStatus()
			).thenReturn(
				statuses[i]
			);

			Mockito.when(
				journalArticle.getVersion()
			).thenReturn(
				version
			);

			version += 0.1;

			_journalArticles.add(journalArticle);
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
				_createJournalArticleModelIndexerWriterContributor();

		journalArticleModelIndexerWriterContributor.modelDeleted(
			deletedVersionJournalArticle);

		Mockito.verify(
			_indexer, Mockito.times(expectedTimes)
		).reindex(
			latestIndexableArticle, false
		);
	}

	private static final int _JOURNAL_ARTICLE_VERSION_COUNT = 50;

	private ConfigurationProvider _configurationProvider;
	private Indexer<JournalArticle> _indexer;
	private MockedStatic<IndexerRegistryUtil> _indexerRegistryUtilMockedStatic;
	private JournalArticleLocalService _journalArticleLocalService;
	private List<JournalArticle> _journalArticles;
	private final JournalServiceConfiguration _journalServiceConfiguration =
		Mockito.mock(JournalServiceConfiguration.class);

}