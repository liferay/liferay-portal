/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductTaxConfigurationUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductTaxConfigurationResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;

import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-tax-configuration.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductTaxConfigurationResource.class
)
@CTAware
public class ProductTaxConfigurationResourceImpl
	extends BaseProductTaxConfigurationResourceImpl {

	@Override
	public ProductTaxConfiguration
			getProductByExternalReferenceCodeTaxConfiguration(
				String externalReferenceCode)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return _toProductTaxConfiguration(cpDefinition.getCPDefinitionId());
	}

	@NestedField(parentClass = Product.class, value = "taxConfiguration")
	@Override
	public ProductTaxConfiguration getProductIdTaxConfiguration(
			@NestedFieldId(value = "productId") Long id)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _toProductTaxConfiguration(cpDefinition.getCPDefinitionId());
	}

	@Override
	public Response patchProductByExternalReferenceCodeTaxConfiguration(
			String externalReferenceCode,
			ProductTaxConfiguration productTaxConfiguration)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		_updateProductTaxConfiguration(cpDefinition, productTaxConfiguration);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchProductIdTaxConfiguration(
			Long id, ProductTaxConfiguration productTaxConfiguration)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		_updateProductTaxConfiguration(cpDefinition, productTaxConfiguration);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	private ProductTaxConfiguration _toProductTaxConfiguration(
			Long cpDefinitionId)
		throws Exception {

		return _productTaxConfigurationDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpDefinitionId, contextAcceptLanguage.getPreferredLocale()));
	}

	private ProductTaxConfiguration _updateProductTaxConfiguration(
			CPDefinition cpDefinition,
			ProductTaxConfiguration productTaxConfiguration)
		throws Exception {

		cpDefinition =
			ProductTaxConfigurationUtil.updateCPDefinitionTaxCategoryInfo(
				_cpDefinitionService, productTaxConfiguration, cpDefinition);

		return _toProductTaxConfiguration(cpDefinition.getCPDefinitionId());
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductTaxConfigurationDTOConverter)"
	)
	private DTOConverter<CPDefinition, ProductTaxConfiguration>
		_productTaxConfigurationDTOConverter;

}