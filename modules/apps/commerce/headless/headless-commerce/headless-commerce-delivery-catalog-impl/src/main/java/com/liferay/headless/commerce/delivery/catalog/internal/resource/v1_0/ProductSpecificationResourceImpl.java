/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.ProductSpecificationResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/product-specification.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductSpecificationResource.class
)
@CTAware
public class ProductSpecificationResourceImpl
	extends BaseProductSpecificationResourceImpl {

	@Override
	public Page<ProductSpecification>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductSpecificationsPage(
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

		return getChannelProductProductSpecificationsPage(
			commerceChannel.getCommerceChannelId(), cProduct.getCProductId(),
			pagination);
	}

	@NestedField(parentClass = Product.class, value = "productSpecifications")
	@Override
	public Page<ProductSpecification>
			getChannelProductProductSpecificationsPage(
				Long channelId,
				@NestedFieldId(value = "productId") Long productId,
				Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				productId, true);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValueLocalService.
					getCPDefinitionSpecificationOptionValues(
						cpDefinition.getCPDefinitionId(), true,
						pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_cpDefinitionSpecificationOptionValueLocalService.
				getCPDefinitionSpecificationOptionValuesCount(
					cpDefinition.getCPDefinitionId(), true);

		return Page.of(
			_toProductSpecifications(cpDefinitionSpecificationOptionValues),
			pagination, totalCount);
	}

	private List<ProductSpecification> _toProductSpecifications(
			List<CPDefinitionSpecificationOptionValue>
				cpDefinitionSpecificationOptionValues)
		throws Exception {

		return transform(
			cpDefinitionSpecificationOptionValues,
			cpDefinitionSpecificationOptionValue ->
				_productSpecificationDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						cpDefinitionSpecificationOptionValue.
							getCPDefinitionSpecificationOptionValueId(),
						contextAcceptLanguage.getPreferredLocale())));
	}

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductSpecificationDTOConverter)"
	)
	private DTOConverter
		<CPDefinitionSpecificationOptionValue, ProductSpecification>
			_productSpecificationDTOConverter;

}