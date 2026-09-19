/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductOptionResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-option.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductOptionResource.class
)
@CTAware
public class ProductOptionResourceImpl extends BaseProductOptionResourceImpl {

	@Override
	public Page<ProductOption>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Pagination pagination)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					channelExternalReferenceCode,
					contextCompany.getCompanyId());

		CProduct cProduct =
			_cProductLocalService.getCProductByExternalReferenceCode(
				productExternalReferenceCode, contextCompany.getCompanyId());

		return getChannelProductProductOptionsPage(
			commerceChannel.getCommerceChannelId(), cProduct.getCProductId(),
			pagination);
	}

	@NestedField(parentClass = Product.class, value = "productOptions")
	@Override
	public Page<ProductOption> getChannelProductProductOptionsPage(
			Long channelId, @NestedFieldId(value = "productId") Long productId,
			Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				productId, true);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId(), pagination.getStartPosition(),
				pagination.getEndPosition());

		int totalCount =
			_cpDefinitionOptionRelLocalService.getCPDefinitionOptionRelsCount(
				cpDefinition.getCPDefinitionId());

		return Page.of(
			_toProductOptions(cpDefinitionOptionRels), pagination, totalCount);
	}

	private List<ProductOption> _toProductOptions(
			List<CPDefinitionOptionRel> cpDefinitionOptionRels)
		throws Exception {

		return transform(
			cpDefinitionOptionRels,
			cpDefinitionOptionRel -> _productOptionDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Reference(target = DTOConverterConstants.PRODUCT_OPTION_DTO_CONVERTER)
	private DTOConverter<CPDefinitionOptionRel, ProductOption>
		_productOptionDTOConverter;

}