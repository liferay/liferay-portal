/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.account.constants.AccountConstants;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Category;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Status;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Commerce.Admin.Catalog",
		"default=true",
		"dto.class.name=com.liferay.commerce.product.model.CPDefinition",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class ProductDTOConverter
	implements DTOConverter<CPDefinition, Product> {

	@Override
	public String getContentType() {
		return Product.class.getSimpleName();
	}

	@Override
	public Product toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDefinition cpDefinition = _cpDefinitionService.getCPDefinition(
			(Long)dtoConverterContext.getId());

		CProduct cProduct = cpDefinition.getCProduct();

		Locale locale = dtoConverterContext.getLocale();

		CPType cpType = _getCPType(cpDefinition.getProductTypeName());

		return new Product() {
			{
				setActions(dtoConverterContext::getActions);
				setActive(() -> !cpDefinition.isInactive());
				setCatalogExternalReferenceCode(
					() -> {
						CommerceCatalog commerceCatalog =
							cpDefinition.getCommerceCatalog();

						if (commerceCatalog == null) {
							return null;
						}

						return commerceCatalog.getExternalReferenceCode();
					});
				setCatalogId(() -> _getCommerceCatalogId(cpDefinition));
				setCategories(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							cpDefinition.getModelClassName(),
							cpDefinition.getCPDefinitionId()),
						assetCategory -> _toCategory(assetCategory),
						Category.class));
				setCreateDate(cpDefinition::getCreateDate);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPDefinition.class.getName(),
						cpDefinition.getCPDefinitionId(),
						cpDefinition.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getDescriptionMap()));
				setDisplayDate(cpDefinition::getDisplayDate);
				setExpando(
					() -> {
						ExpandoBridge expandoBridge =
							cpDefinition.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setExpirationDate(cpDefinition::getExpirationDate);
				setExternalReferenceCode(cProduct::getExternalReferenceCode);
				setId(cpDefinition::getCPDefinitionId);
				setMetaDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getMetaDescriptionMap()));
				setMetaKeyword(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getMetaKeywordsMap()));
				setMetaTitle(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getMetaTitleMap()));
				setModifiedDate(cpDefinition::getModifiedDate);
				setName(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getNameMap()));
				setProductAccountGroupFilter(
					cpDefinition::isAccountGroupFilterEnabled);
				setProductChannelFilter(cpDefinition::isChannelFilterEnabled);
				setProductId(cProduct::getCProductId);
				setProductStatus(cpDefinition::getStatus);
				setProductType(cpType::getName);
				setProductTypeI18n(() -> cpType.getLabel(locale));
				setShortDescription(
					() -> LanguageUtils.getLanguageIdMap(
						cpDefinition.getShortDescriptionMap()));
				setSkuFormatted(
					() -> _getSku(
						cpDefinition, dtoConverterContext.getLocale()));
				setTags(
					() -> TransformUtil.transformToArray(
						_assetTagService.getTags(
							cpDefinition.getModelClassName(),
							cpDefinition.getCPDefinitionId()),
						AssetTag::getName, String.class));
				setThumbnail(
					() -> cpDefinition.getDefaultImageThumbnailSrc(
						AccountConstants.ACCOUNT_ENTRY_ID_ADMIN));
				setUrls(
					() -> LanguageUtils.getLanguageIdMap(
						_cpDefinitionService.getUrlTitleMap(
							cpDefinition.getCPDefinitionId())));
				setVersion(cpDefinition::getVersion);
				setWorkflowStatusInfo(
					() -> {
						ResourceBundle resourceBundle =
							LanguageResources.getResourceBundle(locale);

						String productStatusLabel =
							WorkflowConstants.getStatusLabel(
								cpDefinition.getStatus());

						String productStatusLabelI18n = _language.get(
							resourceBundle,
							WorkflowConstants.getStatusLabel(
								cpDefinition.getStatus()));

						return _toStatus(
							cpDefinition.getStatus(), productStatusLabel,
							productStatusLabelI18n);
					});
			}
		};
	}

	private long _getCommerceCatalogId(CPDefinition cpDefinition) {
		CommerceCatalog commerceCatalog = cpDefinition.getCommerceCatalog();

		if (commerceCatalog == null) {
			return 0;
		}

		return commerceCatalog.getCommerceCatalogId();
	}

	private CPType _getCPType(String name) {
		return _cpTypeRegistry.getCPType(name);
	}

	private String _getSku(CPDefinition cpDefinition, Locale locale) {
		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		if (cpInstances.isEmpty()) {
			return StringPool.BLANK;
		}

		if (cpInstances.size() > 1) {
			return _language.get(locale, "multiple-skus");
		}

		CPInstance cpInstance = cpInstances.get(0);

		return cpInstance.getSku();
	}

	private Category _toCategory(AssetCategory assetCategory) {
		return new Category() {
			{
				setExternalReferenceCode(
					assetCategory::getExternalReferenceCode);
				setId(assetCategory::getCategoryId);
				setName(assetCategory::getName);
				setVocabulary(
					() -> {
						AssetVocabulary assetVocabulary =
							_assetVocabularyLocalService.fetchAssetVocabulary(
								assetCategory.getVocabularyId());

						if (assetVocabulary == null) {
							return null;
						}

						return assetVocabulary.getName();
					});
			}
		};
	}

	private Status _toStatus(
		int statusCode, String productStatusLabel,
		String productStatusLabelI18n) {

		return new Status() {
			{
				setCode(() -> statusCode);
				setLabel(() -> productStatusLabel);
				setLabel_i18n(() -> productStatusLabelI18n);
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagService _assetTagService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private Language _language;

}