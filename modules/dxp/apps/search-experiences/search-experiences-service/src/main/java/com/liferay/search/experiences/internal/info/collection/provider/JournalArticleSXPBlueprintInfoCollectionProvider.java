/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.info.collection.provider;

import com.liferay.asset.util.AssetHelper;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * @author David Nebinger
 * @author Petteri Karttunen
 * @author Joshua Cords
 */
public class JournalArticleSXPBlueprintInfoCollectionProvider
	extends SXPBlueprintInfoCollectionProvider<JournalArticle>
	implements FilteredInfoCollectionProvider<JournalArticle>,
			   SingleFormVariationInfoCollectionProvider<JournalArticle> {

	public JournalArticleSXPBlueprintInfoCollectionProvider(
		AssetHelper assetHelper,
		AssetSubtypeIdentifierBuilder assetSubtypeIdentifierBuilder,
		ClassNameLocalService classNameLocalService,
		DDMStructureService ddmStructureService, GroupService groupService,
		JournalArticleService journalArticleService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SXPBlueprint sxpBlueprint) {

		super(assetHelper, searcher, searchRequestBuilderFactory, sxpBlueprint);

		_assetSubtypeIdentifierBuilder = assetSubtypeIdentifierBuilder;
		_classNameLocalService = classNameLocalService;
		_ddmStructureService = ddmStructureService;
		_groupService = groupService;
		_journalArticleService = journalArticleService;
	}

	@Override
	public InfoPage<JournalArticle> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			SearchResponse searchResponse = getCollectionQuerySearchResponse(
				collectionQuery);

			return InfoPage.of(
				_getJournalArticles(searchResponse.getSearchHits()),
				collectionQuery.getPagination(), searchResponse.getTotalHits());
		}
		catch (Exception exception) {
			_log.error("Unable to get journal articles", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getFormVariationKey() {
		Configuration configuration = Configuration.unsafeToDTO(
			sxpBlueprint.getConfigurationJSON());

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		AssetSubtypeIdentifier assetSubtypeIdentifier =
			_assetSubtypeIdentifierBuilder.searchableAssetType(
				generalConfiguration.getCollectionProviderType()
			).build();

		if (Validator.isBlank(
				assetSubtypeIdentifier.getSubtypeExternalReferenceCode())) {

			return "0";
		}

		Group group;

		try {
			group = _groupService.fetchGroupByExternalReferenceCode(
				assetSubtypeIdentifier.getGroupExternalReferenceCode(),
				sxpBlueprint.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get group with external reference code " +
						assetSubtypeIdentifier.getGroupExternalReferenceCode(),
					exception);
			}

			return "0";
		}

		String subtypeExternalReferenceCode =
			assetSubtypeIdentifier.getSubtypeExternalReferenceCode();

		try {
			DDMStructure ddmStructure =
				_ddmStructureService.fetchStructureByExternalReferenceCode(
					subtypeExternalReferenceCode, group.getGroupId(),
					_classNameLocalService.getClassNameId(
						JournalArticle.class));

			return String.valueOf(ddmStructure.getStructureId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get dynamic data mapping structure with " +
						"external reference code " +
							subtypeExternalReferenceCode,
					exception);
			}
		}

		return "0";
	}

	private List<JournalArticle> _getJournalArticles(SearchHits searchHits) {
		return TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				return _journalArticleService.getLatestArticle(
					document.getLong(Field.ENTRY_CLASS_PK));
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleSXPBlueprintInfoCollectionProvider.class);

	private final AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;
	private final ClassNameLocalService _classNameLocalService;
	private final DDMStructureService _ddmStructureService;
	private final GroupService _groupService;
	private final JournalArticleService _journalArticleService;

}