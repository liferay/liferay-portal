/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.DocumentBulkSelection;
import com.liferay.bulk.rest.dto.v1_0.TaxonomyCategory;
import com.liferay.bulk.rest.dto.v1_0.TaxonomyVocabulary;
import com.liferay.bulk.rest.internal.selection.v1_0.BulkActionBulkSelectionFactory;
import com.liferay.bulk.rest.internal.selection.v1_0.DocumentBulkSelectionFactory;
import com.liferay.bulk.rest.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/taxonomy-vocabulary.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxonomyVocabularyResource.class
)
@CTAware
public class TaxonomyVocabularyResourceImpl
	extends BaseTaxonomyVocabularyResourceImpl {

	@Override
	public Page<TaxonomyVocabulary>
			postSiteTaxonomyVocabulariesCommonPageObject(
				Long siteId, String blueprintExternalReferenceCode,
				Boolean emptySearch, String entryClassNames, String scope,
				String search, Filter filter, Sort[] sorts, Object object)
		throws Exception {

		Map<AssetVocabulary, List<AssetCategory>> assetCategoriesMap =
			_getAssetCategoriesMap(
				_getBulkSelection(
					blueprintExternalReferenceCode, emptySearch,
					entryClassNames, scope, search, filter, sorts, object),
				siteId);

		return Page.of(
			transform(
				assetCategoriesMap.entrySet(),
				entry -> _toTaxonomyVocabulary(
					entry.getValue(), entry.getKey(), siteId)));
	}

	private Set<AssetCategory> _getAssetCategories(
			BulkSelection<?> bulkSelection, PermissionChecker permissionChecker)
		throws Exception {

		Set<AssetCategory> assetCategories = new HashSet<>();

		AtomicBoolean flag = new AtomicBoolean(true);

		BulkSelection<AssetEntry> assetEntryBulkSelection =
			bulkSelection.toAssetEntryBulkSelection();

		assetEntryBulkSelection.forEach(
			assetEntry -> {
				if (ModelResourcePermissionUtil.contains(
						permissionChecker, assetEntry.getGroupId(),
						assetEntry.getClassName(), assetEntry.getClassPK(),
						ActionKeys.UPDATE)) {

					List<AssetCategory> assetEntryAssetCategories =
						_assetCategoryLocalService.getCategories(
							assetEntry.getClassName(), assetEntry.getClassPK());

					if (flag.get()) {
						flag.set(false);

						assetCategories.addAll(assetEntryAssetCategories);
					}
					else {
						assetCategories.retainAll(assetEntryAssetCategories);
					}
				}
			});

		return assetCategories;
	}

	private Map<AssetVocabulary, List<AssetCategory>> _getAssetCategoriesMap(
			BulkSelection<?> bulkSelection, Long siteId)
		throws Exception {

		Map<AssetVocabulary, List<AssetCategory>> assetCategoriesMap =
			new HashMap<>();

		Map<Long, List<AssetCategory>> assetVocabularyIdAssetCategoriesMap =
			new HashMap<>();

		for (AssetCategory assetCategory :
				_getAssetCategories(
					bulkSelection,
					PermissionCheckerFactoryUtil.create(contextUser))) {

			List<AssetCategory> assetCategories =
				assetVocabularyIdAssetCategoriesMap.computeIfAbsent(
					assetCategory.getVocabularyId(), key -> new ArrayList<>());

			assetCategories.add(assetCategory);
		}

		for (AssetVocabulary assetVocabulary : _getAssetVocabularies(siteId)) {
			assetCategoriesMap.put(
				assetVocabulary,
				assetVocabularyIdAssetCategoriesMap.computeIfAbsent(
					assetVocabulary.getVocabularyId(),
					key -> new ArrayList<>()));
		}

		return assetCategoriesMap;
	}

	private List<AssetVocabulary> _getAssetVocabularies(Long siteId)
		throws Exception {

		return transform(
			_assetVocabularyLocalService.getGroupVocabularies(
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(siteId)),
			assetVocabulary -> {
				if (!assetVocabulary.isAssociatedToClassNameId(
						_getClassNameId())) {

					return null;
				}

				int count =
					_assetCategoryLocalService.getVocabularyCategoriesCount(
						assetVocabulary.getVocabularyId());

				if (count > 0) {
					return assetVocabulary;
				}

				return null;
			});
	}

	private BulkActionBulkSelectionFactory _getBulkActionBulkSelectionFactory(
		String blueprintExternalReferenceCode, BulkAction bulkAction,
		Boolean emptySearch, String entryClassNames, Filter filter,
		String scope, String search, Sort[] sorts) {

		return new BulkActionBulkSelectionFactory.Builder(
		).acceptLanguage(
			contextAcceptLanguage
		).blueprintExternalReferenceCode(
			blueprintExternalReferenceCode
		).bulkAction(
			bulkAction
		).bulkSelectionFactoryRegistry(
			_bulkSelectionFactoryRegistry
		).company(
			contextCompany
		).emptySearch(
			emptySearch
		).entryClassNames(
			entryClassNames
		).filter(
			filter
		).filterFactory(
			_filterFactory
		).groupLocalService(
			_groupLocalService
		).httpServletRequest(
			contextHttpServletRequest
		).localization(
			_localization
		).objectDefinitionLocalService(
			_objectDefinitionLocalService
		).objectEntryLocalService(
			_objectEntryLocalService
		).scope(
			scope
		).search(
			search
		).searcher(
			_searcher
		).searchRequestBuilderFactory(
			_searchRequestBuilderFactory
		).sorts(
			sorts
		).user(
			contextUser
		).build();
	}

	private BulkSelection<?> _getBulkSelection(
		String blueprintExternalReferenceCode, Boolean emptySearch,
		String entryClassNames, String scope, String search, Filter filter,
		Sort[] sorts, Object object) {

		Map<String, Object> map = (Map<String, Object>)object;

		if (map.containsKey("documentIds")) {
			return _documentBulkSelectionFactory.create(
				DocumentBulkSelection.toDTO(
					_jsonFactory.toString(_jsonFactory.createJSONObject(map))));
		}

		BulkActionBulkSelectionFactory bulkActionBulkSelectionFactory =
			_getBulkActionBulkSelectionFactory(
				blueprintExternalReferenceCode,
				BulkAction.toDTO(
					_jsonFactory.toString(_jsonFactory.createJSONObject(map))),
				emptySearch, entryClassNames, filter, scope, search, sorts);

		return bulkActionBulkSelectionFactory.create();
	}

	private long _getClassNameId() {
		return _classNameLocalService.getClassNameId(
			DLFileEntry.class.getName());
	}

	private TaxonomyVocabulary _toTaxonomyVocabulary(
		List<AssetCategory> assetCategories, AssetVocabulary assetVocabulary,
		long siteId) {

		return new TaxonomyVocabulary() {
			{
				setMultiValued(assetVocabulary::isMultiValued);
				setName(assetVocabulary::getName);
				setRequired(
					() -> assetVocabulary.isRequired(
						_getClassNameId(),
						AssetCategoryConstants.ALL_CLASS_TYPE_PK, siteId));
				setTaxonomyCategories(
					() -> transformToArray(
						assetCategories,
						assetCategory -> new TaxonomyCategory() {
							{
								setTaxonomyCategoryId(
									assetCategory::getCategoryId);
								setTaxonomyCategoryName(assetCategory::getName);
							}
						},
						TaxonomyCategory.class));
				setTaxonomyVocabularyId(assetVocabulary::getVocabularyId);
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DocumentBulkSelectionFactory _documentBulkSelectionFactory;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Localization _localization;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}