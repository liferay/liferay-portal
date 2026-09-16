/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductVirtualSettingsResource;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-virtual-settings.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductVirtualSettingsResource.class
)
public class ProductVirtualSettingsResourceImpl
	extends BaseProductVirtualSettingsResourceImpl {

	@Override
	public ProductVirtualSettings
			getProductByExternalReferenceCodeProductVirtualSettings(
				String externalReferenceCode)
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

		return _toProductVirtualSettings(cpDefinition.getCPDefinitionId());
	}

	@NestedField(parentClass = Product.class, value = "productVirtualSettings")
	@Override
	public ProductVirtualSettings getProductIdProductVirtualSettings(
			@NestedFieldId(value = "productId") Long id)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		if (!VirtualCPTypeConstants.NAME.equals(
				cpDefinition.getProductTypeName())) {

			return null;
		}

		return _toProductVirtualSettings(cpDefinition.getCPDefinitionId());
	}

	private ProductVirtualSettings _toProductVirtualSettings(
			Long cpDefinitionId)
		throws Exception {

		return _productVirtualSettingsDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpDefinitionId, contextAcceptLanguage.getPreferredLocale()));
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductVirtualSettingsDTOConverter)"
	)
	private DTOConverter<CPDefinition, ProductVirtualSettings>
		_productVirtualSettingsDTOConverter;

}