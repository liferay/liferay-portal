/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.headless.cmp.dto.v1_0.ContentCoverage;
import com.liferay.headless.cmp.dto.v1_0.ContentCoverageEntry;
import com.liferay.headless.cmp.dto.v1_0.FunnelStage;
import com.liferay.headless.cmp.dto.v1_0.Persona;
import com.liferay.headless.cmp.resource.v1_0.ContentCoverageResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.FilterAggregationResult;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Fábio Alves
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/content-coverage.properties",
	scope = ServiceScope.PROTOTYPE, service = ContentCoverageResource.class
)
public class ContentCoverageResourceImpl
	extends BaseContentCoverageResourceImpl {

	@Override
	public ContentCoverage getProjectContentCoverage(Long projectId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(projectId);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			_createLinkedObjectEntriesBooleanQuery(objectEntry),
			_createTermsQuery("cms_section", "contents", "files"),
			_createTermsQuery(
				Field.STATUS,
				ArrayUtil.toStringArray(CMSWorkflowConstants.STATUSES)),
			QueriesUtil.term("rootDescendantNode", false));

		List<AssetCategory> assetCategories =
			_assetCategoryService.getCategories(
				objectEntry.getModelClassName(), projectId);

		List<AssetCategory> funnelStageAssetCategories =
			_filterAssetCategoriesByVocabulary(
				assetCategories, "L_CMP_FUNNEL_STAGE");

		List<Long> funnelStageAssetCategoryIds = _toAssetCategoryIds(
			funnelStageAssetCategories);

		List<AssetCategory> personaAssetCategories =
			_filterAssetCategoriesByVocabulary(
				assetCategories, "L_CMP_PERSONAS");

		List<Long> personaAssetCategoryIds = _toAssetCategoryIds(
			personaAssetCategories);

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				contextCompany.getCompanyId()
			).emptySearchEnabled(
				true
			).query(
				booleanQuery
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_ANY)
			);

		if (!funnelStageAssetCategories.isEmpty() ||
			!personaAssetCategories.isEmpty()) {

			for (long funnelStageAssetCategoryId :
					funnelStageAssetCategoryIds) {

				for (long personaAssetCategoryId : personaAssetCategoryIds) {
					BooleanQuery filterBooleanQuery =
						QueriesUtil.booleanQuery();

					_addAssetCategoryQueryClauses(
						funnelStageAssetCategories, funnelStageAssetCategoryId,
						filterBooleanQuery);
					_addAssetCategoryQueryClauses(
						personaAssetCategories, personaAssetCategoryId,
						filterBooleanQuery);

					searchRequestBuilder.addAggregation(
						_aggregations.filter(
							_getAggregationName(
								funnelStageAssetCategoryId,
								personaAssetCategoryId),
							filterBooleanQuery));
				}
			}
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		return new ContentCoverage() {
			{
				setAssetCount(searchResponse::getCount);
				setContentCoverageEntries(
					() -> _toContentCoverageEntries(
						funnelStageAssetCategoryIds, personaAssetCategoryIds,
						searchResponse));
				setFunnelStages(
					() -> transformToArray(
						funnelStageAssetCategories,
						assetCategory -> new FunnelStage() {
							{
								setDescription(
									() -> assetCategory.getDescription(
										contextAcceptLanguage.
											getPreferredLocale()));
								setExternalReferenceCode(
									assetCategory::getExternalReferenceCode);
								setId(assetCategory::getCategoryId);
								setName(
									() -> assetCategory.getTitle(
										contextAcceptLanguage.
											getPreferredLocale()));
							}
						},
						FunnelStage.class));
				setPersonas(
					() -> transformToArray(
						personaAssetCategories,
						assetCategory -> new Persona() {
							{
								setDescription(
									() -> assetCategory.getDescription(
										contextAcceptLanguage.
											getPreferredLocale()));
								setExternalReferenceCode(
									assetCategory::getExternalReferenceCode);
								setId(assetCategory::getCategoryId);
								setName(
									() -> assetCategory.getTitle(
										contextAcceptLanguage.
											getPreferredLocale()));
							}
						},
						Persona.class));
			}
		};
	}

	private void _addAssetCategoryQueryClauses(
		List<AssetCategory> assetCategories, long assetCategoryId,
		BooleanQuery booleanQuery) {

		if (assetCategoryId != _UNDEFINED_ASSET_CATEGORY_ID) {
			booleanQuery.addMustQueryClauses(
				QueriesUtil.term(
					Field.ASSET_INTERNAL_CATEGORY_IDS, assetCategoryId));

			return;
		}

		if (assetCategories.isEmpty()) {
			return;
		}

		booleanQuery.addMustNotQueryClauses(
			_createTermsQuery(
				Field.ASSET_INTERNAL_CATEGORY_IDS,
				transformToArray(
					assetCategories,
					assetCategory -> String.valueOf(
						assetCategory.getCategoryId()),
					String.class)));
	}

	private BooleanQuery _createLinkedObjectEntriesBooleanQuery(
			ObjectEntry objectEntry)
		throws Exception {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addShouldQueryClauses(
			QueriesUtil.term(
				"cmpProjectObjectEntryIds",
				String.valueOf(objectEntry.getObjectEntryId())));

		long[] relatedCMPTaskObjectEntryIds = _getRelatedCMPTaskObjectEntryIds(
			objectEntry);

		if (relatedCMPTaskObjectEntryIds.length > 0) {
			booleanQuery.addShouldQueryClauses(
				_createTermsQuery(
					"cmpTaskObjectEntryIds",
					ArrayUtil.toStringArray(relatedCMPTaskObjectEntryIds)));
		}

		booleanQuery.setMinimumShouldMatch(1);

		return booleanQuery;
	}

	private TermsQuery _createTermsQuery(String fieldName, String... values) {
		TermsQuery termsQuery = QueriesUtil.terms(fieldName);

		termsQuery.addValues(values);

		return termsQuery;
	}

	private List<AssetCategory> _filterAssetCategoriesByVocabulary(
		List<AssetCategory> assetCategories,
		String assetVocabularyExternalReferenceCode) {

		return ListUtil.filter(
			assetCategories,
			assetCategory -> {
				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.fetchAssetVocabulary(
						assetCategory.getVocabularyId());

				if (assetVocabulary == null) {
					return false;
				}

				return Objects.equals(
					assetVocabulary.getExternalReferenceCode(),
					assetVocabularyExternalReferenceCode);
			});
	}

	private String _getAggregationName(
		long funnelStageAssetCategoryId, long personaAssetCategoryId) {

		return StringBundler.concat(
			funnelStageAssetCategoryId, StringPool.UNDERLINE,
			personaAssetCategoryId);
	}

	private long[] _getRelatedCMPTaskObjectEntryIds(ObjectEntry objectEntry)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_CMP_PROJECT_TO_L_CMP_TASKS",
					objectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return new long[0];
		}

		return transformToLongArray(
			_objectEntryLocalService.getOneToManyObjectEntries(
				objectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null, false,
				objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			ObjectEntry::getObjectEntryId);
	}

	private List<Long> _toAssetCategoryIds(
		List<AssetCategory> assetCategories) {

		List<Long> assetCategoryIds = transform(
			assetCategories, AssetCategoryModel::getCategoryId);

		assetCategoryIds.add(_UNDEFINED_ASSET_CATEGORY_ID);

		return assetCategoryIds;
	}

	private ContentCoverageEntry[] _toContentCoverageEntries(
		List<Long> funnelStageAssetCategoryIds,
		List<Long> personaAssetCategoryIds, SearchResponse searchResponse) {

		List<ContentCoverageEntry> contentCoverageEntries = new ArrayList<>();

		for (long funnelStageAssetCategoryId : funnelStageAssetCategoryIds) {
			for (long personaAssetCategoryId : personaAssetCategoryIds) {
				FilterAggregationResult filterAggregationResult =
					(FilterAggregationResult)
						searchResponse.getAggregationResult(
							_getAggregationName(
								funnelStageAssetCategoryId,
								personaAssetCategoryId));

				if ((filterAggregationResult == null) ||
					(filterAggregationResult.getDocCount() == 0)) {

					continue;
				}

				contentCoverageEntries.add(
					new ContentCoverageEntry() {
						{
							setAssetCount(filterAggregationResult::getDocCount);
							setFunnelStageId(() -> funnelStageAssetCategoryId);
							setPersonaId(() -> personaAssetCategoryId);
						}
					});
			}
		}

		return contentCoverageEntries.toArray(new ContentCoverageEntry[0]);
	}

	private static final long _UNDEFINED_ASSET_CATEGORY_ID = -1;

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}