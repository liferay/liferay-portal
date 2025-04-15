/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v1_0;

import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.internal.util.v1_0.DiscountProductUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v1_0.DiscountProductResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/discount-product.properties",
	scope = ServiceScope.PROTOTYPE, service = DiscountProductResource.class
)
public class DiscountProductResourceImpl
	extends BaseDiscountProductResourceImpl {

	@Override
	public Response deleteDiscountProduct(Long id) throws Exception {
		_commerceDiscountRelService.deleteCommerceDiscountRel(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<DiscountProduct>
			getDiscountByExternalReferenceCodeDiscountProductsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		List<CommerceDiscountRel> commerceDiscountRels =
			_commerceDiscountRelService.getCommerceDiscountRels(
				commerceDiscount.getCommerceDiscountId(),
				CPDefinition.class.getName(), pagination.getStartPosition(),
				pagination.getEndPosition(), null);

		int totalCount =
			_commerceDiscountRelService.getCommerceDiscountRelsCount(
				commerceDiscount.getCommerceDiscountId(),
				CPDefinition.class.getName());

		return Page.of(
			_toDiscountProducts(commerceDiscountRels), pagination, totalCount);
	}

	@Override
	public Page<DiscountProduct> getDiscountIdDiscountProductsPage(
			Long id, Pagination pagination)
		throws Exception {

		List<CommerceDiscountRel> commerceDiscountRels =
			_commerceDiscountRelService.getCommerceDiscountRels(
				id, CPDefinition.class.getName(), pagination.getStartPosition(),
				pagination.getEndPosition(), null);

		int totalCount =
			_commerceDiscountRelService.getCommerceDiscountRelsCount(
				id, CPDefinition.class.getName());

		return Page.of(
			_toDiscountProducts(commerceDiscountRels), pagination, totalCount);
	}

	@Override
	public DiscountProduct postDiscountByExternalReferenceCodeDiscountProduct(
			String externalReferenceCode, DiscountProduct discountProduct)
		throws Exception {

		CommerceDiscount commerceDiscount =
			_commerceDiscountService.
				fetchCommerceDiscountByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceDiscount == null) {
			throw new NoSuchDiscountException(
				"Unable to find discount with external reference code " +
					externalReferenceCode);
		}

		CommerceDiscountRel commerceDiscountRel =
			DiscountProductUtil.addCommerceDiscountRel(
				_cProductLocalService, _commerceDiscountRelService,
				discountProduct, commerceDiscount,
				_serviceContextHelper.getServiceContext());

		return _toDiscountProduct(
			commerceDiscountRel.getCommerceDiscountRelId());
	}

	@Override
	public DiscountProduct postDiscountIdDiscountProduct(
			Long id, DiscountProduct discountProduct)
		throws Exception {

		CommerceDiscountRel commerceDiscountRel =
			DiscountProductUtil.addCommerceDiscountRel(
				_cProductLocalService, _commerceDiscountRelService,
				discountProduct,
				_commerceDiscountService.getCommerceDiscount(id),
				_serviceContextHelper.getServiceContext());

		return _toDiscountProduct(
			commerceDiscountRel.getCommerceDiscountRelId());
	}

	private DiscountProduct _toDiscountProduct(Long commerceDiscountRelId)
		throws Exception {

		return _discountProductDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceDiscountRelId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<DiscountProduct> _toDiscountProducts(
			List<CommerceDiscountRel> commerceDiscountRels)
		throws Exception {

		return transform(
			commerceDiscountRels,
			commerceDiscountRel -> _toDiscountProduct(
				commerceDiscountRel.getCommerceDiscountRelId()));
	}

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v1_0.converter.DiscountProductDTOConverter)"
	)
	private DTOConverter<CommerceDiscountRel, DiscountProduct>
		_discountProductDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}