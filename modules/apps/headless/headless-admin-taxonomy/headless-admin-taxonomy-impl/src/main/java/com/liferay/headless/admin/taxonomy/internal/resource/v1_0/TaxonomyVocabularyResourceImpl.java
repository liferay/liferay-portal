/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.resource.v1_0;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetType;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.taxonomy.internal.odata.entity.v1_0.VocabularyEntityModel;
import com.liferay.headless.admin.taxonomy.internal.util.TaxonomyGroupUtil;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.action.DTOActionProvider;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ContentLanguageUtil;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.asset.service.permission.AssetCategoriesPermission;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Arrays;
import java.util.Collections;
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
	properties = "OSGI-INF/liferay/rest/v1_0/taxonomy-vocabulary.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxonomyVocabularyResource.class
)
public class TaxonomyVocabularyResourceImpl
	extends BaseTaxonomyVocabularyResourceImpl {

	@Override
	public void deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, assetLibraryId);

		_assetVocabularyService.deleteVocabulary(
			assetVocabulary.getVocabularyId());
	}

	@Override
	public void deleteSiteTaxonomyVocabularyByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, siteId);

		_assetVocabularyService.deleteVocabulary(
			assetVocabulary.getVocabularyId());
	}

	@Override
	public void deleteTaxonomyVocabulary(Long taxonomyVocabularyId)
		throws Exception {

		_assetVocabularyService.deleteVocabulary(taxonomyVocabularyId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public TaxonomyVocabulary patchTaxonomyVocabulary(
			Long taxonomyVocabularyId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		if (!ArrayUtil.contains(
				assetVocabulary.getAvailableLanguageIds(),
				contextAcceptLanguage.getPreferredLanguageId())) {

			throw new BadRequestException(
				StringBundler.concat(
					"Unable to patch taxonomy vocabulary with language ",
					LocaleUtil.toW3cLanguageId(
						contextAcceptLanguage.getPreferredLanguageId()),
					" because it is only available in the following languages ",
					StringUtil.merge(
						LocaleUtil.toW3cLanguageIds(
							assetVocabulary.getAvailableLanguageIds()))));
		}

		AssetType[] assetTypes = taxonomyVocabulary.getAssetTypes();

		if (assetTypes == null) {
			assetTypes = _getAssetTypes(
				new AssetVocabularySettingsHelper(
					assetVocabulary.getSettings()),
				assetVocabulary.getGroupId());
		}

		assetVocabulary = _assetVocabularyService.updateVocabulary(
			assetVocabulary.getVocabularyId(), null,
			LocalizedMapUtil.patchLocalizedMap(
				assetVocabulary.getTitleMap(),
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyVocabulary.getName(),
				taxonomyVocabulary.getName_i18n()),
			LocalizedMapUtil.patchLocalizedMap(
				assetVocabulary.getDescriptionMap(),
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyVocabulary.getDescription(),
				taxonomyVocabulary.getDescription_i18n()),
			_getSettings(
				assetTypes, assetVocabulary.getGroupId(),
				GetterUtil.getBoolean(
					taxonomyVocabulary.getMultiValued(), true)),
			ServiceContextBuilder.create(
				assetVocabulary.getGroupId(), contextHttpServletRequest,
				taxonomyVocabulary.getViewableByAsString()
			).build());

		return _toTaxonomyVocabulary(assetVocabulary);
	}

	@Override
	protected Page<TaxonomyVocabulary>
			doGetAssetLibraryTaxonomyVocabulariesPage(
				Long assetLibraryId, String search, Aggregation aggregation,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getTaxonomyVocabulariesPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_VOCABULARY,
					"postAssetLibraryTaxonomyVocabulary",
					AssetCategoriesPermission.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_VOCABULARY,
					"postAssetLibraryTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, assetLibraryId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getAssetLibraryTaxonomyVocabulariesPage",
					AssetCategoriesPermission.RESOURCE_NAME, assetLibraryId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, null)
			).build(),
			assetLibraryId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	protected TaxonomyVocabulary
			doGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		return _toTaxonomyVocabulary(
			_assetVocabularyService.getAssetVocabularyByExternalReferenceCode(
				assetLibraryId, externalReferenceCode));
	}

	@Override
	protected Page<TaxonomyVocabulary> doGetSiteTaxonomyVocabulariesPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getTaxonomyVocabulariesPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_VOCABULARY, "postSiteTaxonomyVocabulary",
					AssetCategoriesPermission.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_VOCABULARY,
					"postSiteTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteTaxonomyVocabulariesPage",
					AssetCategoriesPermission.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, null)
			).build(),
			siteId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	protected TaxonomyVocabulary
			doGetSiteTaxonomyVocabularyByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception {

		return _toTaxonomyVocabulary(
			_assetVocabularyService.getAssetVocabularyByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	protected Page<TaxonomyVocabulary> doGetTaxonomyVocabulariesPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_VOCABULARY, "postTaxonomyVocabulary",
					AssetCategoriesPermission.RESOURCE_NAME,
					TaxonomyGroupUtil.getCMSGroupId(
						contextCompany.getCompanyId()))
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_VOCABULARY, "postTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME,
					TaxonomyGroupUtil.getCMSGroupId(
						contextCompany.getCompanyId()))
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getTaxonomyVocabulariesPage",
					AssetCategoriesPermission.RESOURCE_NAME,
					TaxonomyGroupUtil.getCMSGroupId(
						contextCompany.getCompanyId()))
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putTaxonomyVocabularyBatch",
					AssetCategoriesPermission.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
			},
			filter, AssetVocabulary.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ASSET_VOCABULARY_ID),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);

				BooleanFilter booleanFilter = new BooleanFilter();

				booleanFilter.addRequiredTerm(
					Field.GROUP_ID,
					TaxonomyGroupUtil.getCMSGroupId(
						contextCompany.getCompanyId()));

				searchContext.setBooleanClauses(
					new BooleanClause[] {
						BooleanClauseFactoryUtil.create(
							new BooleanQueryImpl() {
								{
									if (filter != null) {
										booleanFilter.add(
											filter, BooleanClauseOccur.MUST);
									}

									setPreBooleanFilter(booleanFilter);
								}
							},
							BooleanClauseOccur.MUST.getName())
					});

				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toTaxonomyVocabulary(
				_assetVocabularyService.getVocabulary(
					GetterUtil.getLong(
						document.get(Field.ASSET_VOCABULARY_ID)))));
	}

	@Override
	protected TaxonomyVocabulary doGetTaxonomyVocabulary(
			Long taxonomyVocabularyId)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		ContentLanguageUtil.addContentLanguageHeader(
			assetVocabulary.getAvailableLanguageIds(),
			assetVocabulary.getDefaultLanguageId(), contextHttpServletResponse,
			contextAcceptLanguage.getPreferredLocale());

		return _toTaxonomyVocabulary(assetVocabulary);
	}

	@Override
	protected TaxonomyVocabulary doPostAssetLibraryTaxonomyVocabulary(
			Long assetLibraryId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return postSiteTaxonomyVocabulary(assetLibraryId, taxonomyVocabulary);
	}

	@Override
	protected TaxonomyVocabulary doPostSiteTaxonomyVocabulary(
			Long siteId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return _toTaxonomyVocabulary(
			_addAssetVocabulary(
				taxonomyVocabulary.getExternalReferenceCode(), siteId,
				taxonomyVocabulary));
	}

	@Override
	protected TaxonomyVocabulary doPostTaxonomyVocabulary(
			TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			taxonomyVocabulary.getExternalReferenceCode(),
			TaxonomyGroupUtil.getCMSGroupId(contextCompany.getCompanyId()),
			taxonomyVocabulary);

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary.getVocabularyId(),
			_getAssetLibraryGroupIds(taxonomyVocabulary));

		return _toTaxonomyVocabulary(assetVocabulary);
	}

	@Override
	protected TaxonomyVocabulary
			doPutAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, assetLibraryId);

		if (assetVocabulary != null) {
			return _toTaxonomyVocabulary(
				_updateVocabulary(assetVocabulary, taxonomyVocabulary));
		}

		return _toTaxonomyVocabulary(
			_addAssetVocabulary(
				externalReferenceCode, assetLibraryId, taxonomyVocabulary));
	}

	@Override
	protected TaxonomyVocabulary
			doPutSiteTaxonomyVocabularyByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, siteId);

		if (assetVocabulary != null) {
			return _toTaxonomyVocabulary(
				_updateVocabulary(assetVocabulary, taxonomyVocabulary));
		}

		return _toTaxonomyVocabulary(
			_addAssetVocabulary(
				externalReferenceCode, siteId, taxonomyVocabulary));
	}

	@Override
	protected TaxonomyVocabulary doPutTaxonomyVocabulary(
			Long taxonomyVocabularyId, TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		return _toTaxonomyVocabulary(
			_updateVocabulary(assetVocabulary, taxonomyVocabulary));
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			(Long)id);

		return assetVocabulary.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return AssetCategoriesPermission.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return AssetVocabulary.class.getName();
	}

	private static void _map(String assetType, String className) {
		_assetTypeTypeToClassNames.put(assetType, className);
		_classNameToAssetTypeTypes.put(className, assetType);
	}

	private AssetVocabulary _addAssetVocabulary(
			String externalReferenceCode, Long siteId,
			TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyVocabulary.getName(), taxonomyVocabulary.getName_i18n());
		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyVocabulary.getDescription(),
			taxonomyVocabulary.getDescription_i18n());

		LocalizedMapUtil.validateI18n(
			true, LocaleUtil.getSiteDefault(), "Taxonomy vocabulary", titleMap,
			new HashSet<>(descriptionMap.keySet()));

		boolean internalVisibilityType =
			TaxonomyVocabulary.VisibilityType.INTERNAL.equals(
				taxonomyVocabulary.getVisibilityType());

		return _assetVocabularyService.addVocabulary(
			externalReferenceCode, siteId,
			titleMap.get(LocaleUtil.getSiteDefault()), null, titleMap,
			descriptionMap,
			_getSettings(
				taxonomyVocabulary.getAssetTypes(), siteId,
				GetterUtil.getBoolean(
					taxonomyVocabulary.getMultiValued(), true)),
			internalVisibilityType ? 1 : 0,
			ServiceContextBuilder.create(
				siteId, contextHttpServletRequest,
				taxonomyVocabulary.getViewableByAsString()
			).build());
	}

	private AssetLibrary[] _getAssetLibraries(AssetVocabulary assetVocabulary) {
		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyId(
					assetVocabulary.getVocabularyId());

		if (assetVocabularyGroupRels.isEmpty()) {
			return null;
		}

		return transformToArray(
			assetVocabularyGroupRels,
			assetVocabularyGroupRel -> {
				Group group = groupLocalService.fetchGroup(
					assetVocabularyGroupRel.getGroupId());

				return new AssetLibrary() {
					{
						setId(assetVocabularyGroupRel::getGroupId);
						setName(
							() -> {
								if (group == null) {
									return null;
								}

								return group.getName(
									contextAcceptLanguage.getPreferredLocale());
							});
						setName_i18n(
							() -> {
								if (group == null) {
									return null;
								}

								return LocalizedMapUtil.getI18nMap(
									contextAcceptLanguage.
										isAcceptAllLanguages(),
									group.getNameMap());
							});
					}
				};
			},
			AssetLibrary.class);
	}

	private long[] _getAssetLibraryGroupIds(
			TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		return TaxonomyGroupUtil.getAssetLibraryGroupIds(
			taxonomyVocabulary.getAssetLibraries());
	}

	private AssetType _getAssetType(
		long groupId, long classNameId, long classTypePK,
		long[] requiredClassNameIds) {

		return new AssetType() {
			{
				setRequired(
					() -> ArrayUtil.contains(
						requiredClassNameIds, classNameId));

				setSubtype(
					() -> {
						if (classTypePK ==
								AssetCategoryConstants.ALL_CLASS_TYPE_PK) {

							return "AllAssetSubtypes";
						}

						AssetRendererFactory<?> assetRendererFactory =
							AssetRendererFactoryRegistryUtil.
								getAssetRendererFactoryByClassNameId(
									classNameId);

						ClassTypeReader classTypeReader =
							assetRendererFactory.getClassTypeReader();

						List<ClassType> classTypes =
							classTypeReader.getAvailableClassTypes(
								new long[] {
									groupId, contextCompany.getGroupId()
								},
								contextAcceptLanguage.getPreferredLocale());

						if (ListUtil.isEmpty(classTypes)) {
							return "AllAssetSubtypes";
						}

						for (ClassType classType : classTypes) {
							if (classType.getClassTypeId() == classTypePK) {
								return classType.getName();
							}
						}

						throw new InternalServerErrorException();
					});
				setType(
					() -> {
						if (classNameId ==
								AssetCategoryConstants.ALL_CLASS_NAME_ID) {

							return "AllAssetTypes";
						}

						String assetTypeType = _classNameToAssetTypeTypes.get(
							_portal.getClassName(classNameId));

						if (assetTypeType != null) {
							return assetTypeType;
						}

						return _getModelResource(
							AssetRendererFactoryRegistryUtil.
								getAssetRendererFactoryByClassNameId(
									classNameId));
					});
				setTypeId(() -> classNameId);
			}
		};
	}

	private AssetType[] _getAssetTypes(
		AssetVocabularySettingsHelper assetVocabularySettingsHelper,
		long groupId) {

		long[] classNameIds = assetVocabularySettingsHelper.getClassNameIds();

		if (ArrayUtil.isEmpty(classNameIds)) {
			return new AssetType[0];
		}

		AssetType[] assetTypes = new AssetType[classNameIds.length];

		long[] classTypePKs = assetVocabularySettingsHelper.getClassTypePKs();
		long[] requiredClassNameIds =
			assetVocabularySettingsHelper.getRequiredClassNameIds();

		for (int i = 0; i < classNameIds.length; i++) {
			long classNameId = classNameIds[i];
			long classTypePK = classTypePKs[i];

			assetTypes[i] = _getAssetType(
				groupId, classNameId, classTypePK, requiredClassNameIds);
		}

		return assetTypes;
	}

	private String _getAvailableAssetTypes(
		List<AssetRendererFactory<?>> categorizableAssetRenderFactories) {

		List<String> assetTypes = ListUtil.concat(
			transform(
				categorizableAssetRenderFactories,
				assetRenderedFactory -> {
					String assetTypeType = _classNameToAssetTypeTypes.get(
						assetRenderedFactory.getClassName());

					if (assetTypeType != null) {
						return assetTypeType;
					}

					return _getModelResource(assetRenderedFactory);
				}),
			Collections.singletonList("AllAssetTypes"));

		return Arrays.toString(assetTypes.toArray());
	}

	private long _getClassNameId(String assetTypeType) {
		if (Objects.equals(assetTypeType, "AllAssetTypes")) {
			return AssetCategoryConstants.ALL_CLASS_NAME_ID;
		}

		String className = null;

		List<AssetRendererFactory<?>> categorizableAssetRenderFactories =
			ListUtil.filter(
				AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
					contextCompany.getCompanyId()),
				AssetRendererFactory::isCategorizable);

		for (AssetRendererFactory<?> assetRendererFactory :
				categorizableAssetRenderFactories) {

			if (assetTypeType.equals(_getModelResource(assetRendererFactory))) {
				className = assetRendererFactory.getClassName();

				break;
			}
		}

		if (className == null) {
			className = _assetTypeTypeToClassNames.get(assetTypeType);
		}

		if (className == null) {
			throw new BadRequestException(
				StringBundler.concat(
					"Asset type ", assetTypeType,
					" not available, the supported asset types are: ",
					_getAvailableAssetTypes(
						categorizableAssetRenderFactories)));
		}

		return _portal.getClassNameId(className);
	}

	private long _getClassTypePK(
		long classNameId, String subtype, long groupId) {

		if (Objects.equals(subtype, "AllAssetSubtypes") ||
			(classNameId == AssetCategoryConstants.ALL_CLASS_NAME_ID) ||
			(subtype == null)) {

			return AssetCategoryConstants.ALL_CLASS_TYPE_PK;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(classNameId);

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		List<ClassType> classTypes = classTypeReader.getAvailableClassTypes(
			new long[] {groupId, contextCompany.getGroupId()},
			contextAcceptLanguage.getPreferredLocale());

		if (ListUtil.isEmpty(classTypes)) {
			return AssetCategoryConstants.ALL_CLASS_TYPE_PK;
		}

		for (ClassType classType : classTypes) {
			if (Objects.equals(classType.getName(), subtype)) {
				return classType.getClassTypeId();
			}
		}

		throw new BadRequestException("Invalid subtype " + subtype);
	}

	private String _getModelResource(
		AssetRendererFactory<?> assetRendererFactory) {

		return ResourceActionsUtil.getModelResource(
			contextAcceptLanguage.getPreferredLocale(),
			assetRendererFactory.getClassName());
	}

	private String _getSettings(
		AssetType[] assetTypes, long groupId, boolean multiValued) {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		if (ArrayUtil.isEmpty(assetTypes)) {
			return assetVocabularySettingsHelper.toString();
		}

		long[] classNameIds = new long[assetTypes.length];
		long[] classTypePKs = new long[assetTypes.length];
		boolean[] requiredClassNameIds = new boolean[assetTypes.length];

		for (int i = 0; i < assetTypes.length; i++) {
			AssetType assetType = assetTypes[i];

			long classNameId = _getClassNameId(assetType.getType());

			classNameIds[i] = classNameId;

			classTypePKs[i] = _getClassTypePK(
				classNameId, assetType.getSubtype(), groupId);

			requiredClassNameIds[i] = assetType.getRequired();
		}

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			classNameIds, classTypePKs, requiredClassNameIds);

		assetVocabularySettingsHelper.setMultiValued(multiValued);

		return assetVocabularySettingsHelper.toString();
	}

	private Page<TaxonomyVocabulary> _getTaxonomyVocabulariesPage(
			Map<String, Map<String, String>> actions, Long groupId,
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions,
			booleanQuery -> {
			},
			filter, AssetVocabulary.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ASSET_VOCABULARY_ID),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(
					SiteConnectedGroupGroupProviderUtil.
						getCurrentAndAncestorSiteAndDepotGroupIds(groupId));
			},
			sorts,
			document -> _toTaxonomyVocabulary(
				_assetVocabularyService.getVocabulary(
					GetterUtil.getLong(
						document.get(Field.ASSET_VOCABULARY_ID)))));
	}

	private TaxonomyVocabulary _toTaxonomyVocabulary(
		AssetVocabulary assetVocabulary) {

		Group group = groupLocalService.fetchGroup(
			assetVocabulary.getGroupId());

		return new TaxonomyVocabulary() {
			{
				setActions(
					() -> _dtoActionProvider.getActions(
						assetVocabulary.getGroupId(),
						assetVocabulary.getVocabularyId(), contextUriInfo,
						contextUser.getUserId()));
				setAssetLibraries(() -> _getAssetLibraries(assetVocabulary));
				setAssetLibraryKey(
					() -> {
						if (group == null) {
							return null;
						}

						return GroupUtil.getAssetLibraryKey(group);
					});
				setAssetTypes(
					() -> _getAssetTypes(
						new AssetVocabularySettingsHelper(
							assetVocabulary.getSettings()),
						assetVocabulary.getGroupId()));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						assetVocabulary.getAvailableLanguageIds()));
				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(
							assetVocabulary.getUserId())));
				setDateCreated(assetVocabulary::getCreateDate);
				setDateModified(assetVocabulary::getModifiedDate);
				setDescription(
					() -> assetVocabulary.getDescription(
						contextAcceptLanguage.getPreferredLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						contextAcceptLanguage.isAcceptAllLanguages(),
						assetVocabulary.getDescriptionMap()));
				setExternalReferenceCode(
					assetVocabulary::getExternalReferenceCode);
				setId(assetVocabulary::getVocabularyId);
				setMultiValued(assetVocabulary::isMultiValued);
				setName(
					() -> assetVocabulary.getTitle(
						contextAcceptLanguage.getPreferredLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						contextAcceptLanguage.isAcceptAllLanguages(),
						assetVocabulary.getTitleMap()));
				setNumberOfTaxonomyCategories(
					() -> {
						List<AssetCategory> assetCategories =
							assetVocabulary.getCategories();

						if (assetCategories != null) {
							return assetCategories.size();
						}

						return 0;
					});
				setSiteId(
					() -> {
						if (group == null) {
							return null;
						}

						return GroupUtil.getSiteId(group);
					});
				setVisibilityType(
					() -> (assetVocabulary.getVisibilityType() == 1) ?
						TaxonomyVocabulary.VisibilityType.INTERNAL :
							TaxonomyVocabulary.VisibilityType.PUBLIC);
			}
		};
	}

	private AssetVocabulary _updateVocabulary(
			AssetVocabulary assetVocabulary,
			TaxonomyVocabulary taxonomyVocabulary)
		throws Exception {

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyVocabulary.getName(), taxonomyVocabulary.getName_i18n(),
			assetVocabulary.getTitleMap());
		Map<Locale, String> descriptionMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(),
			taxonomyVocabulary.getDescription(),
			taxonomyVocabulary.getDescription_i18n(),
			assetVocabulary.getDescriptionMap());

		LocalizedMapUtil.validateI18n(
			false, LocaleUtil.getSiteDefault(), "Taxonomy vocabulary", titleMap,
			new HashSet<>(descriptionMap.keySet()));

		if (FeatureFlagManagerUtil.isEnabled("LPD-17564") &&
			ArrayUtil.isNotEmpty(taxonomyVocabulary.getAssetLibraries())) {

			_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
				assetVocabulary.getVocabularyId(),
				_getAssetLibraryGroupIds(taxonomyVocabulary));
		}

		return _assetVocabularyService.updateVocabulary(
			assetVocabulary.getVocabularyId(), null, titleMap, descriptionMap,
			_getSettings(
				taxonomyVocabulary.getAssetTypes(),
				assetVocabulary.getGroupId(),
				taxonomyVocabulary.getMultiValued()),
			ServiceContextBuilder.create(
				assetVocabulary.getGroupId(), contextHttpServletRequest,
				taxonomyVocabulary.getViewableByAsString()
			).build());
	}

	private static final Map<String, String> _assetTypeTypeToClassNames =
		new HashMap<>();
	private static final Map<String, String> _classNameToAssetTypeTypes =
		new HashMap<>();
	private static final EntityModel _entityModel = new VocabularyEntityModel();

	static {
		_map("BlogPosting", "com.liferay.blogs.model.BlogsEntry");
		_map(
			"Document",
			"com.liferay.document.library.kernel.model.DLFileEntry");
		_map(
			"KnowledgeBaseArticle",
			"com.liferay.knowledge.base.model.KBArticle");
		_map("Organization", Organization.class.getName());
		_map("StructuredContent", "com.liferay.journal.model.JournalArticle");
		_map("UserAccount", User.class.getName());
		_map("WebPage", Layout.class.getName());
		_map("WebSite", Group.class.getName());
		_map("WikiPage", "com.liferay.wiki.model.WikiPage");
	}

	@Reference
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference(
		target = "(dto.class.name=com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary)"
	)
	private DTOActionProvider _dtoActionProvider;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}