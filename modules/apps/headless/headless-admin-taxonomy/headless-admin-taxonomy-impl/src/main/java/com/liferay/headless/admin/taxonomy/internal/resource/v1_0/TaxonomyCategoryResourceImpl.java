/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.resource.v1_0;

import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.headless.admin.taxonomy.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.internal.odata.entity.v1_0.CategoryEntityModel;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ContentLanguageUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.asset.model.impl.AssetCategoryImpl;
import com.liferay.portlet.asset.service.permission.AssetCategoriesPermission;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.sql.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/taxonomy-category.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxonomyCategoryResource.class
)
public class TaxonomyCategoryResourceImpl
	extends BaseTaxonomyCategoryResourceImpl {

	@Override
	public void deleteTaxonomyCategory(String taxonomyCategoryId)
		throws Exception {

		AssetCategory assetCategory =
			_assetCategoryLocalService.getAssetCategory(
				GetterUtil.getLong(taxonomyCategoryId));

		_assetCategoryService.deleteCategory(assetCategory.getCategoryId());
	}

	@Override
	public void deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
			Long taxonomyVocabularyId, String externalReferenceCode)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		AssetCategory assetCategory =
			_assetCategoryLocalService.getAssetCategoryByExternalReferenceCode(
				externalReferenceCode, assetVocabulary.getGroupId());

		_assetCategoryService.deleteCategory(assetCategory.getCategoryId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<TaxonomyCategory> getTaxonomyCategoriesRankedPage(
		Long siteId, Pagination pagination) {

		DynamicQuery dynamicQuery = _assetCategoryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", contextCompany.getCompanyId()));

		if (siteId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("groupId", siteId));
		}

		dynamicQuery.addOrder(OrderFactoryUtil.desc("assetCount"));
		dynamicQuery.setProjection(_getProjectionList(), true);

		return Page.of(
			transform(
				transform(
					_assetCategoryLocalService.dynamicQuery(
						dynamicQuery, pagination.getStartPosition(),
						pagination.getEndPosition()),
					this::_toAssetCategory),
				this::_toTaxonomyCategory),
			pagination, _getTotalCount(siteId));
	}

	@Override
	public Page<TaxonomyCategory> getTaxonomyCategoryTaxonomyCategoriesPage(
			String parentTaxonomyCategoryId, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Map<String, Map<String, String>> actions = null;

		if (!Objects.equals(parentTaxonomyCategoryId, "0")) {
			AssetCategory assetCategory = _getAssetCategory(
				parentTaxonomyCategoryId);

			parentTaxonomyCategoryId = String.valueOf(
				assetCategory.getCategoryId());

			actions = HashMapBuilder.<String, Map<String, String>>put(
				"add-category",
				addAction(
					ActionKeys.ADD_CATEGORY, assetCategory.getCategoryId(),
					"postTaxonomyCategoryTaxonomyCategory",
					assetCategory.getUserId(), AssetCategory.class.getName(),
					assetCategory.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, assetCategory.getCategoryId(),
					"getTaxonomyCategoryTaxonomyCategoriesPage",
					assetCategory.getUserId(), AssetCategory.class.getName(),
					assetCategory.getGroupId())
			).build();
		}

		String taxonomyCategoryId = parentTaxonomyCategoryId;

		return _getCategoriesPage(
			actions, aggregation,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						Field.ASSET_PARENT_CATEGORY_ID, taxonomyCategoryId),
					BooleanClauseOccur.MUST);
			},
			filter, search, pagination, sorts);
	}

	@Override
	public TaxonomyCategory patchTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(taxonomyCategoryId);

		if (!ArrayUtil.contains(
				assetCategory.getAvailableLanguageIds(),
				contextAcceptLanguage.getPreferredLanguageId())) {

			throw new BadRequestException(
				StringBundler.concat(
					"Unable to patch taxonomy category with language ",
					LocaleUtil.toW3cLanguageId(
						contextAcceptLanguage.getPreferredLanguageId()),
					" because it is only available in the following languages ",
					LocaleUtil.toW3cLanguageIds(
						assetCategory.getAvailableLanguageIds())));
		}

		long assetVocabularyId = _getAssetVocabularyId(
			assetCategory, taxonomyCategory);

		assetCategory = _assetCategoryService.updateCategory(
			assetCategory.getCategoryId(),
			_getParentAssetCategoryId(
				assetCategory, assetVocabularyId, taxonomyCategory),
			LocalizedMapUtil.patchLocalizedMap(
				assetCategory.getTitleMap(),
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getName(), taxonomyCategory.getName_i18n()),
			LocalizedMapUtil.patchLocalizedMap(
				assetCategory.getDescriptionMap(),
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getDescription(),
				taxonomyCategory.getDescription_i18n()),
			assetVocabularyId,
			_merge(
				_assetCategoryPropertyLocalService.getCategoryProperties(
					assetCategory.getCategoryId()),
				taxonomyCategory.getTaxonomyCategoryProperties()),
			ServiceContextBuilder.create(
				assetCategory.getGroupId(), contextHttpServletRequest,
				taxonomyCategory.getViewableByAsString()
			).build());

		return _toTaxonomyCategory(assetCategory);
	}

	@Override
	public TaxonomyCategory postTaxonomyCategoryTaxonomyCategory(
			String parentTaxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(
			parentTaxonomyCategoryId);

		return _addTaxonomyCategory(
			taxonomyCategory.getExternalReferenceCode(),
			assetCategory.getGroupId(), assetCategory.getDefaultLanguageId(),
			taxonomyCategory, assetCategory.getCategoryId(),
			assetCategory.getVocabularyId());
	}

	@Override
	protected TaxonomyCategory doGetTaxonomyCategory(String taxonomyCategoryId)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(taxonomyCategoryId);

		ContentLanguageUtil.addContentLanguageHeader(
			assetCategory.getAvailableLanguageIds(),
			assetCategory.getDefaultLanguageId(), contextHttpServletResponse,
			contextAcceptLanguage.getPreferredLocale());

		return _toTaxonomyCategory(assetCategory);
	}

	@Override
	protected Page<TaxonomyCategory>
			doGetTaxonomyVocabularyTaxonomyCategoriesPage(
				Long taxonomyVocabularyId, Boolean flatten, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		return _getCategoriesPage(
			HashMapBuilder.put(
				"add-category",
				addAction(
					ActionKeys.ADD_CATEGORY, assetVocabulary,
					"postTaxonomyVocabularyTaxonomyCategory")
			).put(
				"createBatch",
				addAction(
					ActionKeys.VIEW, assetVocabulary,
					"postTaxonomyVocabularyTaxonomyCategoryBatch")
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, assetVocabulary,
					"deleteTaxonomyCategoryBatch")
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, assetVocabulary,
					"getTaxonomyVocabularyTaxonomyCategoriesPage")
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, assetVocabulary,
					"putTaxonomyCategoryBatch")
			).build(),
			aggregation,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				if (!GetterUtil.getBoolean(flatten)) {
					booleanFilter.add(
						new TermFilter(
							Field.ASSET_PARENT_CATEGORY_ID,
							String.valueOf(
								AssetCategoryConstants.
									DEFAULT_PARENT_CATEGORY_ID)),
						BooleanClauseOccur.MUST);
				}

				booleanFilter.add(
					new TermFilter(
						Field.ASSET_VOCABULARY_ID,
						String.valueOf(taxonomyVocabularyId)),
					BooleanClauseOccur.MUST);
			},
			filter, search, pagination, sorts);
	}

	@Override
	protected TaxonomyCategory
			doGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				Long taxonomyVocabularyId, String externalReferenceCode)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		return _toTaxonomyCategory(
			_assetCategoryService.getAssetCategoryByExternalReferenceCode(
				assetVocabulary.getGroupId(), externalReferenceCode));
	}

	@Override
	protected TaxonomyCategory doPostTaxonomyVocabularyTaxonomyCategory(
			Long taxonomyVocabularyId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		return _addTaxonomyCategory(
			taxonomyCategory.getExternalReferenceCode(),
			assetVocabulary.getGroupId(),
			assetVocabulary.getDefaultLanguageId(), taxonomyCategory, 0,
			assetVocabulary.getVocabularyId());
	}

	@Override
	protected TaxonomyCategory doPutTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		return _toTaxonomyCategory(
			_updateAssetCategory(
				_getAssetCategory(taxonomyCategoryId), taxonomyCategory));
	}

	@Override
	protected TaxonomyCategory
			doPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				Long taxonomyVocabularyId, String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					externalReferenceCode, assetVocabulary.getGroupId());

		if (assetCategory != null) {
			return _toTaxonomyCategory(
				_updateAssetCategory(assetCategory, taxonomyCategory));
		}

		return _addTaxonomyCategory(
			externalReferenceCode, assetVocabulary.getGroupId(),
			assetVocabulary.getDefaultLanguageId(), taxonomyCategory, 0,
			assetVocabulary.getVocabularyId());
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		AssetCategory assetCategory = _getAssetCategory((String)id);

		return assetCategory.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return AssetCategoriesPermission.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return AssetCategory.class.getName();
	}

	private TaxonomyCategory _addTaxonomyCategory(
			String externalReferenceCode, long groupId, String languageId,
			TaxonomyCategory taxonomyCategory, long taxonomyCategoryId,
			long taxonomyVocabularyId)
		throws Exception {

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyCategory.getName(), taxonomyCategory.getName_i18n());
		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyCategory.getDescription(),
			taxonomyCategory.getDescription_i18n());

		LocalizedMapUtil.validateI18n(
			true, LocaleUtil.fromLanguageId(languageId), "Taxonomy category",
			titleMap, new HashSet<>(descriptionMap.keySet()));

		return _toTaxonomyCategory(
			_assetCategoryService.addCategory(
				externalReferenceCode, groupId, taxonomyCategoryId, titleMap,
				descriptionMap, taxonomyVocabularyId,
				_toStringArray(
					taxonomyCategory.getTaxonomyCategoryProperties()),
				ServiceContextBuilder.create(
					groupId, contextHttpServletRequest,
					taxonomyCategory.getViewableByAsString()
				).build()));
	}

	private AssetCategory _getAssetCategory(String taxonomyCategoryId)
		throws Exception {

		return _assetCategoryService.getCategory(
			GetterUtil.getLong(taxonomyCategoryId));
	}

	private long _getAssetVocabularyId(
			AssetCategory assetCategory, TaxonomyCategory taxonomyCategory)
		throws Exception {

		if ((taxonomyCategory.getTaxonomyVocabularyId() != null) &&
			(taxonomyCategory.getTaxonomyVocabularyId() !=
				assetCategory.getVocabularyId())) {

			_assetVocabularyService.getVocabulary(
				taxonomyCategory.getTaxonomyVocabularyId());

			return taxonomyCategory.getTaxonomyVocabularyId();
		}

		return assetCategory.getVocabularyId();
	}

	private Page<TaxonomyCategory> _getCategoriesPage(
			Map<String, Map<String, String>> actions, Aggregation aggregation,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Filter filter, String keywords, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer, filter,
			AssetCategory.class.getName(), keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ASSET_CATEGORY_ID),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toTaxonomyCategory(
				_assetCategoryService.getCategory(
					GetterUtil.getLong(
						document.get(Field.ASSET_CATEGORY_ID)))));
	}

	private long _getParentAssetCategoryId(
			AssetCategory assetCategory, long assetVocabularyId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		ParentTaxonomyCategory parentTaxonomyCategory =
			taxonomyCategory.getParentTaxonomyCategory();

		if ((parentTaxonomyCategory != null) &&
			(parentTaxonomyCategory.getId() > 0)) {

			AssetCategory existingAssetCategory =
				_assetCategoryLocalService.getAssetCategory(
					parentTaxonomyCategory.getId());

			if (assetVocabularyId != existingAssetCategory.getVocabularyId()) {
				throw new BadRequestException(
					StringBundler.concat(
						"Taxonomy category ", assetCategory.getCategoryId(),
						" and its parent taxonomy category must belong to the ",
						" same taxonomy vocabulary"));
			}

			return parentTaxonomyCategory.getId();
		}
		else if (parentTaxonomyCategory == null) {
			return assetCategory.getParentCategoryId();
		}

		return AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;
	}

	private ProjectionList _getProjectionList() {
		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(
			ProjectionFactoryUtil.alias(
				ProjectionFactoryUtil.sqlProjection(
					StringBundler.concat(
						"COALESCE((select count(assetEntryId) assetCount from ",
						"AssetEntryAssetCategoryRel where assetCategoryId = ",
						"this_.categoryId group by assetCategoryId), 0) AS ",
						"assetCount"),
					new String[] {"assetCount"}, new Type[] {Type.INTEGER}),
				"assetCount"));
		projectionList.add(ProjectionFactoryUtil.property("categoryId"));
		projectionList.add(ProjectionFactoryUtil.property("companyId"));
		projectionList.add(ProjectionFactoryUtil.property("createDate"));
		projectionList.add(ProjectionFactoryUtil.property("description"));
		projectionList.add(
			ProjectionFactoryUtil.property("externalReferenceCode"));
		projectionList.add(ProjectionFactoryUtil.property("groupId"));
		projectionList.add(ProjectionFactoryUtil.property("modifiedDate"));
		projectionList.add(ProjectionFactoryUtil.property("name"));
		projectionList.add(ProjectionFactoryUtil.property("parentCategoryId"));
		projectionList.add(ProjectionFactoryUtil.property("userId"));
		projectionList.add(ProjectionFactoryUtil.property("vocabularyId"));

		return projectionList;
	}

	private long _getTotalCount(Long siteId) {
		DynamicQuery dynamicQuery = _assetCategoryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", contextCompany.getCompanyId()));

		if (siteId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("groupId", siteId));
		}

		dynamicQuery.add(
			RestrictionsFactoryUtil.sqlRestriction(
				"exists (select 1 from AssetEntryAssetCategoryRel where " +
					"assetCategoryId = this_.categoryId)"));

		return _assetCategoryLocalService.dynamicQueryCount(dynamicQuery);
	}

	private String[] _merge(
		List<AssetCategoryProperty> assetCategoryProperties,
		TaxonomyCategoryProperty[] taxonomyCategoryProperties) {

		Map<String, String> map = new HashMap<>();

		if (taxonomyCategoryProperties != null) {
			for (TaxonomyCategoryProperty taxonomyCategoryProperty :
					taxonomyCategoryProperties) {

				map.put(
					taxonomyCategoryProperty.getKey(),
					taxonomyCategoryProperty.getValue());
			}
		}

		for (AssetCategoryProperty assetCategoryProperty :
				assetCategoryProperties) {

			map.put(
				assetCategoryProperty.getKey(),
				assetCategoryProperty.getValue());
		}

		String[] strings = new String[map.size()];

		int index = 0;

		for (Map.Entry<String, String> entry : map.entrySet()) {
			strings[index] = entry.getKey() + ":" + entry.getValue();

			index++;
		}

		return strings;
	}

	private AssetCategory _toAssetCategory(Object[] assetCategory) {
		return new AssetCategoryImpl() {
			{
				setCategoryId((long)assetCategory[1]);
				setCompanyId((long)assetCategory[2]);
				setCreateDate(_toDate((Timestamp)assetCategory[3]));
				setDescription((String)assetCategory[4]);
				setExternalReferenceCode((String)assetCategory[5]);
				setGroupId((long)assetCategory[6]);
				setModifiedDate(_toDate((Timestamp)assetCategory[7]));
				setName((String)assetCategory[8]);
				setParentCategoryId((long)assetCategory[9]);
				setUserId((long)assetCategory[10]);
				setVocabularyId((long)assetCategory[11]);
			}
		};
	}

	private Date _toDate(Timestamp timestamp) {
		return new Date(timestamp.getTime());
	}

	private String[] _toStringArray(
		TaxonomyCategoryProperty[] taxonomyCategoryProperties) {

		return transform(
			taxonomyCategoryProperties,
			taxonomyCategoryProperty -> StringBundler.concat(
				taxonomyCategoryProperty.getKey(), StringPool.COLON,
				taxonomyCategoryProperty.getValue()),
			String.class);
	}

	private TaxonomyCategory _toTaxonomyCategory(AssetCategory assetCategory)
		throws Exception {

		return _taxonomyCategoryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, assetCategory.getCategoryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			assetCategory);
	}

	private AssetCategory _updateAssetCategory(
			AssetCategory assetCategory, TaxonomyCategory taxonomyCategory)
		throws Exception {

		long assetVocabularyId = _getAssetVocabularyId(
			assetCategory, taxonomyCategory);

		long parentAssetCategoryId = _getParentAssetCategoryId(
			assetCategory, assetVocabularyId, taxonomyCategory);

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyCategory.getName(), taxonomyCategory.getName_i18n(),
			assetCategory.getTitleMap());
		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyCategory.getDescription(),
			taxonomyCategory.getDescription_i18n(),
			assetCategory.getDescriptionMap());

		LocalizedMapUtil.validateI18n(
			false,
			LocaleUtil.fromLanguageId(assetCategory.getDefaultLanguageId()),
			"Taxonomy category", titleMap,
			new HashSet<>(descriptionMap.keySet()));

		assetCategory.setTitleMap(titleMap);
		assetCategory.setDescriptionMap(descriptionMap);

		return _assetCategoryService.updateCategory(
			assetCategory.getCategoryId(), parentAssetCategoryId, titleMap,
			descriptionMap, assetVocabularyId,
			_toStringArray(taxonomyCategory.getTaxonomyCategoryProperties()),
			ServiceContextBuilder.create(
				assetCategory.getGroupId(), contextHttpServletRequest,
				taxonomyCategory.getViewableByAsString()
			).build());
	}

	private static final EntityModel _entityModel = new CategoryEntityModel();

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.taxonomy.internal.dto.v1_0.converter.TaxonomyCategoryDTOConverter)"
	)
	private DTOConverter<AssetCategory, TaxonomyCategory>
		_taxonomyCategoryDTOConverter;

}