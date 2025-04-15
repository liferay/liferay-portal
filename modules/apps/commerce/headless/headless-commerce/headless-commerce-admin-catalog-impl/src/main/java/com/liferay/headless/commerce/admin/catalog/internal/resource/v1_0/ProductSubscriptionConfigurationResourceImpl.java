/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSubscriptionConfiguration;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductSubscriptionConfigurationUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductSubscriptionConfigurationResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
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
	properties = "OSGI-INF/liferay/rest/v1_0/product-subscription-configuration.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductSubscriptionConfigurationResource.class
)
@CTAware
public class ProductSubscriptionConfigurationResourceImpl
	extends BaseProductSubscriptionConfigurationResourceImpl {

	@Override
	public ProductSubscriptionConfiguration
			getProductByExternalReferenceCodeSubscriptionConfiguration(
				String externalReferenceCode)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.
				fetchCPDefinitionByCProductExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return _toProductSubscriptionConfiguration(
			cpDefinition.getCPDefinitionId());
	}

	@NestedField(
		parentClass = Product.class, value = "subscriptionConfiguration"
	)
	@Override
	public ProductSubscriptionConfiguration
			getProductIdSubscriptionConfiguration(
				@NestedFieldId(value = "productId") Long id)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _toProductSubscriptionConfiguration(
			cpDefinition.getCPDefinitionId());
	}

	@Override
	public Response
			patchProductByExternalReferenceCodeSubscriptionConfiguration(
				String externalReferenceCode,
				ProductSubscriptionConfiguration
					productSubscriptionConfiguration)
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

		_updateProductSubscriptionConfiguration(
			cpDefinition, productSubscriptionConfiguration);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchProductIdSubscriptionConfiguration(
			Long id,
			ProductSubscriptionConfiguration productSubscriptionConfiguration)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		_updateProductSubscriptionConfiguration(
			cpDefinition, productSubscriptionConfiguration);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	private ProductSubscriptionConfiguration
			_toProductSubscriptionConfiguration(Long cpDefinitionId)
		throws Exception {

		return _productSubscriptionConfigurationDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpDefinitionId, contextAcceptLanguage.getPreferredLocale()));
	}

	private ProductSubscriptionConfiguration
			_updateProductSubscriptionConfiguration(
				CPDefinition cpDefinition,
				ProductSubscriptionConfiguration
					productSubscriptionConfiguration)
		throws Exception {

		cpDefinition =
			ProductSubscriptionConfigurationUtil.
				updateCPDefinitionSubscriptionInfo(
					_cpDefinitionService, productSubscriptionConfiguration,
					cpDefinition,
					_serviceContextHelper.getServiceContext(
						cpDefinition.getGroupId()));

		return _toProductSubscriptionConfiguration(
			cpDefinition.getCPDefinitionId());
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductSubscriptionConfigurationDTOConverter)"
	)
	private DTOConverter<CPDefinition, ProductSubscriptionConfiguration>
		_productSubscriptionConfigurationDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}