/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.converter;

import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.headless.admin.taxonomy.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.action.DTOActionProvider;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.Taxonomy",
		"dto.class.name=com.liferay.asset.kernel.model.AssetCategory",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class TaxonomyCategoryDTOConverter
	implements DTOConverter<AssetCategory, TaxonomyCategory> {

	@Override
	public String getContentType() {
		return TaxonomyCategory.class.getSimpleName();
	}

	@Override
	public TaxonomyCategory toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		AssetCategory assetCategory =
			_assetCategoryLocalService.getAssetCategory(
				(Long)dtoConverterContext.getId());

		return _toTaxonomyCategory(dtoConverterContext, assetCategory);
	}

	@Override
	public TaxonomyCategory toDTO(
			DTOConverterContext dtoConverterContext,
			AssetCategory assetCategory)
		throws Exception {

		return _toTaxonomyCategory(dtoConverterContext, assetCategory);
	}

	private ParentTaxonomyCategory _toParentTaxonomyCategory(
		AssetCategory parentAssetCategory,
		DTOConverterContext dtoConverterContext) {

		return new ParentTaxonomyCategory() {
			{
				setExternalReferenceCode(
					parentAssetCategory::getExternalReferenceCode);
				setId(parentAssetCategory::getCategoryId);
				setName(
					() -> parentAssetCategory.getTitle(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						parentAssetCategory.getTitleMap()));
			}
		};
	}

	private TaxonomyCategory _toTaxonomyCategory(
			DTOConverterContext dtoConverterContext,
			AssetCategory assetCategory)
		throws Exception {

		return new TaxonomyCategory() {
			{
				setActions(
					() -> _dtoActionProvider.getActions(
						assetCategory.getGroupId(),
						assetCategory.getCategoryId(),
						dtoConverterContext.getUriInfo(),
						dtoConverterContext.getUserId()));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						assetCategory.getAvailableLanguageIds()));
				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(
							assetCategory.getUserId())));
				setDateCreated(assetCategory::getCreateDate);
				setDateModified(assetCategory::getModifiedDate);
				setDescription(
					() -> assetCategory.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						assetCategory.getDescriptionMap()));
				setExternalReferenceCode(
					assetCategory::getExternalReferenceCode);
				setId(() -> String.valueOf(assetCategory.getCategoryId()));
				setName(
					() -> assetCategory.getTitle(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						assetCategory.getTitleMap()));
				setNumberOfTaxonomyCategories(
					() -> _assetCategoryService.getChildCategoriesCount(
						assetCategory.getCategoryId()));
				setParentTaxonomyCategory(
					() -> {
						if (assetCategory.getParentCategory() == null) {
							return null;
						}

						return _toParentTaxonomyCategory(
							assetCategory.getParentCategory(),
							dtoConverterContext);
					});
				setParentTaxonomyVocabulary(
					() -> {
						if (assetCategory.getVocabularyId() == 0) {
							return null;
						}

						AssetVocabulary assetVocabulary =
							_assetVocabularyService.fetchVocabulary(
								assetCategory.getVocabularyId());

						if (assetVocabulary == null) {
							return null;
						}

						return new ParentTaxonomyVocabulary() {
							{
								setExternalReferenceCode(
									assetVocabulary::getExternalReferenceCode);
								setId(assetCategory::getVocabularyId);
								setName(
									() -> assetVocabulary.getTitle(
										dtoConverterContext.getLocale()));
								setName_i18n(
									() -> LocalizedMapUtil.getI18nMap(
										dtoConverterContext.
											isAcceptAllLanguages(),
										assetVocabulary.getTitleMap()));
							}
						};
					});
				setSiteId(assetCategory::getGroupId);
				setTaxonomyCategoryProperties(
					() -> TransformUtil.transformToArray(
						_assetCategoryPropertyLocalService.
							getCategoryProperties(
								assetCategory.getCategoryId()),
						assetCategoryProperties -> _toTaxonomyCategoryProperty(
							assetCategoryProperties),
						TaxonomyCategoryProperty.class));
				setTaxonomyCategoryUsageCount(
					() -> NestedFieldsSupplier.<Integer>supply(
						"taxonomyCategoryUsageCount",
						fieldName -> {
							UriInfo uriInfo = dtoConverterContext.getUriInfo();

							if (uriInfo != null) {
								MultivaluedMap<String, String> queryParameters =
									uriInfo.getQueryParameters();

								if (StringUtil.contains(
										queryParameters.getFirst(
											"restrictFields"),
										"taxonomyCategoryUsageCount")) {

									return null;
								}
							}

							return (int)_assetEntryLocalService.searchCount(
								assetCategory.getCompanyId(),
								new long[] {assetCategory.getGroupId()},
								assetCategory.getUserId(), null, -1, null,
								String.valueOf(assetCategory.getCategoryId()),
								null, false, false,
								new int[] {
									WorkflowConstants.STATUS_APPROVED,
									WorkflowConstants.STATUS_PENDING,
									WorkflowConstants.STATUS_SCHEDULED
								},
								false);
						}));
				setTaxonomyVocabularyId(assetCategory::getVocabularyId);
			}
		};
	}

	private TaxonomyCategoryProperty _toTaxonomyCategoryProperty(
		AssetCategoryProperty assetCategoryProperty) {

		return new TaxonomyCategoryProperty() {
			{
				setExternalReferenceCode(
					assetCategoryProperty::getExternalReferenceCode);
				setKey(assetCategoryProperty::getKey);
				setValue(assetCategoryProperty::getValue);
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference(
		target = "(dto.class.name=com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory)"
	)
	private DTOActionProvider _dtoActionProvider;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}