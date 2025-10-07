/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.index.contributor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.BatchIndexingHelper;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

import java.util.List;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class JournalArticleModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<JournalArticle> {

	public JournalArticleModelIndexerWriterContributor(
		BatchIndexingHelper batchIndexingHelper,
		ConfigurationProvider configurationProvider,
		DynamicQueryBatchIndexingActionableFactory
			dynamicQueryBatchIndexingActionableFactory,
		JournalArticleLocalService journalArticleLocalService,
		JournalArticleResourceLocalService journalArticleResourceLocalService) {

		_batchIndexingHelper = batchIndexingHelper;
		_configurationProvider = configurationProvider;
		_dynamicQueryBatchIndexingActionableFactory =
			dynamicQueryBatchIndexingActionableFactory;
		_journalArticleLocalService = journalArticleLocalService;
		_journalArticleResourceLocalService =
			journalArticleResourceLocalService;
	}

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		if (_isIndexAllArticleVersions()) {
			batchIndexingActionable.setAddCriteriaMethod(
				dynamicQuery -> {
					Property property = PropertyFactoryUtil.forName(
						"classNameId");

					dynamicQuery.add(
						property.ne(
							PortalUtil.getClassNameId(DDMStructure.class)));
				});
			batchIndexingActionable.setInterval(
				_batchIndexingHelper.getBulkSize(
					JournalArticle.class.getName()));
			batchIndexingActionable.setPerformActionMethod(
				(JournalArticle journalArticle) ->
					batchIndexingActionable.addDocuments(
						modelIndexerWriterDocumentHelper.getDocument(
							journalArticle)));
		}
		else {
			batchIndexingActionable.setAddCriteriaMethod(
				dynamicQuery -> {
					Property resourcePrimKeyProperty =
						PropertyFactoryUtil.forName("resourcePrimKey");

					DynamicQuery journalArticleDynamicQuery =
						_journalArticleLocalService.dynamicQuery();

					journalArticleDynamicQuery.setProjection(
						ProjectionFactoryUtil.property("resourcePrimKey"));

					Property property = PropertyFactoryUtil.forName(
						"classNameId");

					journalArticleDynamicQuery.add(
						property.eq(
							PortalUtil.getClassNameId(DDMStructure.class)));

					dynamicQuery.add(
						resourcePrimKeyProperty.notIn(
							journalArticleDynamicQuery));
				});
			batchIndexingActionable.setInterval(
				_batchIndexingHelper.getBulkSize(
					JournalArticleResource.class.getName()));
			batchIndexingActionable.setPerformActionMethod(
				(JournalArticleResource articleResource) -> {
					JournalArticle latestIndexableArticle =
						_fetchLatestIndexableArticleVersion(
							articleResource.getResourcePrimKey());

					if (latestIndexableArticle == null) {
						return;
					}

					batchIndexingActionable.addDocuments(
						modelIndexerWriterDocumentHelper.getDocument(
							latestIndexableArticle));
				});
		}
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		if (_isIndexAllArticleVersions()) {
			return _dynamicQueryBatchIndexingActionableFactory.
				getBatchIndexingActionable(
					_journalArticleLocalService.
						getIndexableActionableDynamicQuery());
		}

		return _dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				_journalArticleResourceLocalService.
					getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(JournalArticle journalArticle) {
		return journalArticle.getCompanyId();
	}

	@Override
	public IndexerWriterMode getIndexerWriterMode(
		JournalArticle journalArticle) {

		if (PortalUtil.getClassNameId(DDMStructure.class) ==
				journalArticle.getClassNameId()) {

			return IndexerWriterMode.DELETE;
		}

		if (_isIndexAllArticleVersions()) {
			if ((journalArticle.getCtCollectionId() == 0) &&
				!CTCollectionThreadLocal.isProductionMode()) {

				return IndexerWriterMode.SKIP;
			}

			return IndexerWriterMode.UPDATE;
		}

		JournalArticle latestIndexableArticle =
			_fetchLatestIndexableArticleVersion(
				journalArticle.getResourcePrimKey());

		if (journalArticle.getId() == latestIndexableArticle.getId()) {
			return IndexerWriterMode.UPDATE;
		}

		if ((journalArticle.getCtCollectionId() == 0) &&
			!CTCollectionThreadLocal.isProductionMode()) {

			return IndexerWriterMode.SKIP;
		}

		return IndexerWriterMode.DELETE;
	}

	@Override
	public void modelDeleted(JournalArticle journalArticle) {
		if (_isIndexAllArticleVersions()) {
			_reindexOtherArticleVersions(journalArticle);

			return;
		}

		JournalArticle latestIndexableArticle =
			_fetchLatestIndexableArticleVersion(
				journalArticle.getResourcePrimKey());

		if ((latestIndexableArticle != null) &&
			(latestIndexableArticle.getVersion() <
				journalArticle.getVersion())) {

			_reindexOtherArticleVersions(journalArticle);
		}
	}

	@Override
	public void modelIndexed(JournalArticle journalArticle) {
		if (_isIndexAllArticleVersions()) {
			_reindexOtherArticleVersions(journalArticle);

			return;
		}

		JournalArticle latestIndexableArticle =
			_fetchLatestIndexableArticleVersion(
				journalArticle.getResourcePrimKey());

		if ((latestIndexableArticle != null) &&
			(latestIndexableArticle.getVersion() <=
				journalArticle.getVersion())) {

			_reindexOtherArticleVersions(journalArticle);
		}
	}

	private JournalArticle _fetchLatestIndexableArticleVersion(
		long resourcePrimKey) {

		JournalArticle latestIndexableArticle =
			_journalArticleLocalService.fetchLatestArticle(
				resourcePrimKey,
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_IN_TRASH
				});

		if (latestIndexableArticle == null) {
			latestIndexableArticle =
				_journalArticleLocalService.fetchLatestArticle(resourcePrimKey);
		}

		return latestIndexableArticle;
	}

	private boolean _isIndexAllArticleVersions() {
		JournalServiceConfiguration journalServiceConfiguration = null;

		try {
			journalServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return journalServiceConfiguration.indexAllArticleVersionsEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private void _reindexOtherArticleVersions(JournalArticle journalArticle) {
		if (PortalUtil.getClassNameId(DDMStructure.class) ==
				journalArticle.getClassNameId()) {

			return;
		}

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getArticles(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				ArticleVersionComparator.getInstance(false));

		Indexer<JournalArticle> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(JournalArticle.class);

		for (JournalArticle versionJournalArticle : journalArticles) {
			if (Objects.equals(
					versionJournalArticle.getId(), journalArticle.getId())) {

				continue;
			}

			try {
				indexer.reindex(versionJournalArticle, false);
			}
			catch (SearchException searchException) {
				throw new SystemException(searchException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelIndexerWriterContributor.class);

	private final BatchIndexingHelper _batchIndexingHelper;
	private final ConfigurationProvider _configurationProvider;
	private final DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;
	private final JournalArticleLocalService _journalArticleLocalService;
	private final JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

}