/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.resource.v2_0;

import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelService;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.internal.util.v2_0.DiscountProductUtil;
import com.liferay.headless.commerce.admin.pricing.resource.v2_0.DiscountProductResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v2_0/discount-product.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = DiscountProductResource.class
)
public class DiscountProductResourceImpl
	extends BaseDiscountProductResourceImpl {

	@Override
	public void deleteDiscountProduct(Long id) throws Exception {
		_commerceDiscountRelService.deleteCommerceDiscountRel(id);
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

	@NestedField(parentClass = Discount.class, value = "discountProducts")
	@Override
	public Page<DiscountProduct> getDiscountIdDiscountProductsPage(
			Long id, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Locale locale = contextAcceptLanguage.getPreferredLocale();

		String languageId = LocaleUtil.toLanguageId(locale);

		List<CommerceDiscountRel> commerceDiscountRels =
			_commerceDiscountRelService.getCPDefinitionsByCommerceDiscountId(
				id, search, languageId, pagination.getStartPosition(),
				pagination.getEndPosition());

		int totalCount =
			_commerceDiscountRelService.
				getCPDefinitionsByCommerceDiscountIdCount(
					id, search, languageId);

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
				discountProduct, commerceDiscount, _serviceContextHelper);

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
				_serviceContextHelper);

		return _toDiscountProduct(
			commerceDiscountRel.getCommerceDiscountRelId());
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceDiscountRel commerceDiscountRel)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE", commerceDiscountRel.getCommerceDiscountRelId(),
				"deleteDiscountProduct",
				_commerceDiscountRelModelResourcePermission)
		).build();
	}

	private DiscountProduct _toDiscountProduct(Long commerceDiscountRelId)
		throws Exception {

		CommerceDiscountRel commerceDiscountRel =
			_commerceDiscountRelService.getCommerceDiscountRel(
				commerceDiscountRelId);

		return _discountProductDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceDiscountRel), _dtoConverterRegistry,
				commerceDiscountRelId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
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
	private CProductLocalService _cProductLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscountRel)"
	)
	private ModelResourcePermission<CommerceDiscountRel>
		_commerceDiscountRelModelResourcePermission;

	@Reference
	private CommerceDiscountRelService _commerceDiscountRelService;

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.pricing.internal.dto.v2_0.converter.DiscountProductDTOConverter)"
	)
	private DTOConverter<CommerceDiscountRel, DiscountProduct>
		_discountProductDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}