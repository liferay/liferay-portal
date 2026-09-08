/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.helper.CPDefinitionHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {"default=true", "dto.class.name=CPDefinition"},
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

		ProductDTOConverterContext productDTOConverterContext =
			(ProductDTOConverterContext)dtoConverterContext;

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			(Long)productDTOConverterContext.getId());

		String languageId = _language.getLanguageId(
			productDTOConverterContext.getLocale());

		return new Product() {
			{
				setCatalogName(
					() -> {
						CommerceCatalog commerceCatalog =
							cpDefinition.getCommerceCatalog();

						return commerceCatalog.getName();
					});
				setCreateDate(cpDefinition::getCreateDate);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPDefinition.class.getName(),
						cpDefinition.getCPDefinitionId(),
						cpDefinition.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDescription(() -> cpDefinition.getDescription(languageId));
				setExpando(
					() -> {
						ExpandoBridge expandoBridge =
							cpDefinition.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setExternalReferenceCode(
					() -> {
						CProduct cProduct = cpDefinition.getCProduct();

						return cProduct.getExternalReferenceCode();
					});
				setId(cpDefinition::getCPDefinitionId);
				setMetaDescription(
					() -> cpDefinition.getMetaDescription(languageId));
				setMetaKeyword(() -> cpDefinition.getMetaKeywords(languageId));
				setMetaTitle(() -> cpDefinition.getMetaTitle(languageId));
				setModifiedDate(cpDefinition::getModifiedDate);
				setName(() -> cpDefinition.getName(languageId));
				setProductConfiguration(
					() -> _productConfigurationDTOConverter.toDTO(
						new ProductConfigurationDTOConverterContext(
							productDTOConverterContext.getCommerceContext(),
							cpDefinition.getCPDefinitionId(),
							productDTOConverterContext.getLocale())));
				setProductId(cpDefinition::getCProductId);
				setProductType(cpDefinition::getProductTypeName);
				setShortDescription(
					() -> cpDefinition.getShortDescription(languageId));
				setSlug(() -> cpDefinition.getURL(languageId));
				setTags(
					() -> TransformUtil.transformToArray(
						_assetTagLocalService.getTags(
							cpDefinition.getModelClassName(),
							cpDefinition.getCPDefinitionId()),
						AssetTag::getName, String.class));
				setUrlImage(
					() -> {
						Company company = _companyLocalService.getCompany(
							cpDefinition.getCompanyId());

						String portalURL = _portal.getPortalURL(
							company.getVirtualHostname(),
							_portal.getPortalServerPort(false), true);

						String defaultImageFileURL =
							_cpDefinitionHelper.getDefaultImageFileURL(
								CommerceUtil.getCommerceAccountId(
									productDTOConverterContext.
										getCommerceContext()),
								cpDefinition.getCPDefinitionId());

						return portalURL + defaultImageFileURL;
					});
				setUrls(
					() -> LanguageUtils.getLanguageIdMap(
						_cpDefinitionLocalService.getUrlTitleMap(
							cpDefinition.getCPDefinitionId())));
			}
		};
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductConfigurationDTOConverter)"
	)
	private DTOConverter<CPDefinitionInventory, ProductConfiguration>
		_productConfigurationDTOConverter;

}