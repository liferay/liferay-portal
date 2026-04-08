/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueService;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductSpecificationUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
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
	public void deleteProductSpecification(Long id) throws Exception {
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValue(id);

		_cpDefinitionSpecificationOptionValueService.
			deleteCPDefinitionSpecificationOptionValue(
				cpDefinitionSpecificationOptionValue.
					getCPDefinitionSpecificationOptionValueId());
	}

	@Override
	public void deleteProductSpecificationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		deleteProductSpecification(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());
	}

	@Override
	public Page<ProductSpecification>
			getProductByExternalReferenceCodeProductSpecificationsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId(),
					false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return getProductIdProductSpecificationsPage(
			cpDefinition.getCProductId(), pagination);
	}

	@NestedField(parentClass = Product.class, value = "productSpecifications")
	@Override
	public Page<ProductSpecification> getProductIdProductSpecificationsPage(
			@NestedFieldId(value = "productId") Long id, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValues(
						cpDefinition.getCPDefinitionId(), null,
						pagination.getStartPosition(),
						pagination.getEndPosition(), null);

		int totalCount =
			_cpDefinitionSpecificationOptionValueService.
				getCPDefinitionSpecificationOptionValuesCount(
					cpDefinition.getCPDefinitionId(), null);

		return Page.of(
			_toProductSpecifications(
				cpDefinitionSpecificationOptionValues,
				contextAcceptLanguage.getPreferredLocale()),
			pagination, totalCount);
	}

	@Override
	public ProductSpecification getProductSpecification(Long id)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValue(id);

		return _toProductSpecification(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());
	}

	@Override
	public ProductSpecification getProductSpecificationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		return getProductSpecification(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());
	}

	@Override
	public ProductSpecification patchProductSpecification(
			Long id, ProductSpecification productSpecification)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue = _updateProductSpecification(
				id, productSpecification);

		return _toProductSpecification(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());
	}

	@Override
	public ProductSpecification
			patchProductSpecificationByExternalReferenceCode(
				String externalReferenceCode,
				ProductSpecification productSpecification)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		return patchProductSpecification(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId(),
			productSpecification);
	}

	@Override
	public ProductSpecification
			postProductByExternalReferenceCodeProductSpecification(
				String externalReferenceCode,
				ProductSpecification productSpecification)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				_cpDefinitionService, externalReferenceCode,
				contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return postProductIdProductSpecification(
			cpDefinition.getCProductId(), productSpecification);
	}

	@Override
	public ProductSpecification postProductIdProductSpecification(
			Long id, ProductSpecification productSpecification)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateProductSpecification(
			cpDefinition.getCPDefinitionId(), productSpecification);
	}

	private ProductSpecification _addOrUpdateProductSpecification(
			Long id, ProductSpecification productSpecification)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					fetchCPDefinitionSpecificationOptionValueByExternalReferenceCode(
						GetterUtil.getString(
							productSpecification.getExternalReferenceCode()),
						contextCompany.getCompanyId());

		if (cpDefinitionSpecificationOptionValue == null) {
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					fetchCPDefinitionSpecificationOptionValue(
						GetterUtil.getLong(productSpecification.getId()));
		}

		if (cpDefinitionSpecificationOptionValue == null) {
			return _toProductSpecification(
				ProductSpecificationUtil.
					addCPDefinitionSpecificationOptionValue(
						_cpDefinitionSpecificationOptionValueService,
						_cpOptionCategoryService, _cpSpecificationOptionService,
						id, productSpecification,
						_serviceContextHelper.getServiceContext()));
		}

		return _toProductSpecification(
			_updateProductSpecification(
				cpDefinitionSpecificationOptionValue.
					getCPSpecificationOptionId(),
				productSpecification));
	}

	private ProductSpecification _toProductSpecification(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue)
		throws Exception {

		return _toProductSpecification(
			cpDefinitionSpecificationOptionValue.
				getCPDefinitionSpecificationOptionValueId());
	}

	private ProductSpecification _toProductSpecification(
			Long cpDefinitionSpecificationOptionValueId)
		throws Exception {

		return _productSpecificationDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpDefinitionSpecificationOptionValueId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<ProductSpecification> _toProductSpecifications(
		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues,
		Locale locale) {

		return transform(
			cpDefinitionSpecificationOptionValues,
			cpDefinitionSpecificationOptionValue ->
				_productSpecificationDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						cpDefinitionSpecificationOptionValue.
							getCPDefinitionSpecificationOptionValueId(),
						locale)));
	}

	private CPDefinitionSpecificationOptionValue _updateProductSpecification(
			Long id, ProductSpecification productSpecification)
		throws Exception {

		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue =
				_cpDefinitionSpecificationOptionValueService.
					getCPDefinitionSpecificationOptionValue(id);

		return ProductSpecificationUtil.
			updateCPDefinitionSpecificationOptionValue(
				_cpDefinitionSpecificationOptionValueService,
				cpDefinitionSpecificationOptionValue, _cpOptionCategoryService,
				_cpSpecificationOptionService, productSpecification,
				_serviceContextHelper.getServiceContext());
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPDefinitionSpecificationOptionValueService
		_cpDefinitionSpecificationOptionValueService;

	@Reference
	private CPOptionCategoryService _cpOptionCategoryService;

	@Reference
	private CPSpecificationOptionService _cpSpecificationOptionService;

	@Reference(
		target = DTOConverterConstants.PRODUCT_SPECIFICATION_DTO_CONVERTER
	)
	private DTOConverter
		<CPDefinitionSpecificationOptionValue, ProductSpecification>
			_productSpecificationDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}