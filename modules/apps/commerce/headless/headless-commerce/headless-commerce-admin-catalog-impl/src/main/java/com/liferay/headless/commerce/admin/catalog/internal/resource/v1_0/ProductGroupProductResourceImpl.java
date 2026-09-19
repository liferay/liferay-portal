/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.pricing.exception.NoSuchPricingClassException;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.pricing.service.CommercePricingClassService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductGroupProduct;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductGroupProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductGroupProductResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Alberti
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-group-product.properties",
	scope = ServiceScope.PROTOTYPE, service = ProductGroupProductResource.class
)
@CTAware
public class ProductGroupProductResourceImpl
	extends BaseProductGroupProductResourceImpl {

	@Override
	public void deleteProductGroupProduct(Long id) throws Exception {
		_commercePricingClassCPDefinitionRelService.
			deleteCommercePricingClassCPDefinitionRel(id);
	}

	@Override
	public Page<ProductGroupProduct>
			getProductGroupByExternalReferenceCodeProductGroupProductsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommercePricingClass commercePricingClass =
			_commercePricingClassService.
				fetchCommercePricingClassByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePricingClass == null) {
			throw new NoSuchPricingClassException(
				"Unable to find product group with external reference code " +
					externalReferenceCode);
		}

		List<CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_commercePricingClassCPDefinitionRelService.
					getCommercePricingClassCPDefinitionRels(
						commercePricingClass.getCommercePricingClassId(),
						pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_commercePricingClassCPDefinitionRelService.
				getCommercePricingClassCPDefinitionRelsCount(
					commercePricingClass.getCommercePricingClassId());

		return Page.of(
			_toProductGroupProducts(commercePricingClassCPDefinitionRels),
			pagination, totalCount);
	}

	@Override
	public Page<ProductGroupProduct> getProductGroupIdProductGroupProductsPage(
			Long id, Pagination pagination)
		throws Exception {

		List<CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_commercePricingClassCPDefinitionRelService.
					getCommercePricingClassCPDefinitionRels(
						id, pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_commercePricingClassCPDefinitionRelService.
				getCommercePricingClassCPDefinitionRelsCount(id);

		return Page.of(
			_toProductGroupProducts(commercePricingClassCPDefinitionRels),
			pagination, totalCount);
	}

	@Override
	public ProductGroupProduct
			postProductGroupByExternalReferenceCodeProductGroupProduct(
				String externalReferenceCode,
				ProductGroupProduct productGroupProduct)
		throws Exception {

		CommercePricingClass commercePricingClass =
			_commercePricingClassService.
				fetchCommercePricingClassByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePricingClass == null) {
			throw new NoSuchPricingClassException(
				"Unable to find product group with external reference code " +
					externalReferenceCode);
		}

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				ProductGroupProductUtil.addCommercePricingClassCPDefinitionRel(
					_cProductLocalService,
					_commercePricingClassCPDefinitionRelService,
					productGroupProduct, commercePricingClass,
					_serviceContextHelper);

		return _toProductGroupProduct(
			commercePricingClassCPDefinitionRel.
				getCommercePricingClassCPDefinitionRelId());
	}

	@Override
	public ProductGroupProduct postProductGroupIdProductGroupProduct(
			Long id, ProductGroupProduct productGroupProduct)
		throws Exception {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				ProductGroupProductUtil.addCommercePricingClassCPDefinitionRel(
					_cProductLocalService,
					_commercePricingClassCPDefinitionRelService,
					productGroupProduct,
					_commercePricingClassService.getCommercePricingClass(id),
					_serviceContextHelper);

		return _toProductGroupProduct(
			commercePricingClassCPDefinitionRel.
				getCommercePricingClassCPDefinitionRelId());
	}

	private ProductGroupProduct _toProductGroupProduct(
			Long commercePricingClassCPDefinitionRelId)
		throws Exception {

		return _productGroupProductDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commercePricingClassCPDefinitionRelId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<ProductGroupProduct> _toProductGroupProducts(
			List<CommercePricingClassCPDefinitionRel>
				commercePricingClassCPDefinitionRels)
		throws Exception {

		return transform(
			commercePricingClassCPDefinitionRels,
			commercePricingClassCPDefinitionRel -> _toProductGroupProduct(
				commercePricingClassCPDefinitionRel.
					getCommercePricingClassCPDefinitionRelId()));
	}

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Reference
	private CommercePricingClassService _commercePricingClassService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductGroupProductDTOConverter)"
	)
	private DTOConverter
		<CommercePricingClassCPDefinitionRel, ProductGroupProduct>
			_productGroupProductDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}