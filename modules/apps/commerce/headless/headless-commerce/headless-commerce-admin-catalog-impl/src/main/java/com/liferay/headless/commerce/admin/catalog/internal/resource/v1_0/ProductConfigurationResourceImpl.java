/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.service.CPDAvailabilityEstimateService;
import com.liferay.commerce.service.CPDefinitionInventoryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationList;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductShippingConfiguration;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductTaxConfiguration;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductConfigurationDTOConverterContext;
import com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0.ProductConfigurationEntityModel;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductConfigurationUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductConfigurationResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-configuration.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductConfigurationResource.class
)
@CTAware
public class ProductConfigurationResourceImpl
	extends BaseProductConfigurationResourceImpl {

	@Override
	public void deleteProductConfiguration(Long id) throws Exception {
		_cpConfigurationEntryService.deleteCPConfigurationEntry(id);
	}

	@Override
	public void deleteProductConfigurationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryService.
				getCPConfigurationEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		deleteProductConfiguration(
			cpConfigurationEntry.getCPConfigurationEntryId());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public ProductConfiguration getProductByExternalReferenceCodeConfiguration(
			String externalReferenceCode)
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

		return getProductIdConfiguration(cpDefinition.getCProductId());
	}

	@Override
	public ProductConfiguration getProductConfiguration(Long id)
		throws Exception {

		return _toProductConfiguration(
			_cpConfigurationEntryService.getCPConfigurationEntry(id), false);
	}

	@Override
	public ProductConfiguration getProductConfigurationByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryService.
				getCPConfigurationEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getProductConfiguration(
			cpConfigurationEntry.getCPConfigurationEntryId());
	}

	@Override
	public Page<ProductConfiguration>
			getProductConfigurationListByExternalReferenceCodeProductConfigurationsPage(
				String externalReferenceCode, String search,
				Boolean showDifferences, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.
				getCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getProductConfigurationListIdProductConfigurationsPage(
			cpConfigurationList.getCPConfigurationListId(), search,
			showDifferences, filter, pagination, sorts);
	}

	@NestedField(
		parentClass = ProductConfigurationList.class,
		value = "productConfigurations"
	)
	@Override
	public Page<ProductConfiguration>
			getProductConfigurationListIdProductConfigurationsPage(
				Long id, String search, Boolean showDifferences, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.getCPConfigurationList(id);

		long companyId = cpConfigurationList.getCompanyId();

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CPConfigurationEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(Field.CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					CPField.CP_CONFIGURATION_LIST_ID,
					cpConfigurationList.getCPConfigurationListId());
				searchContext.setAttribute(
					Field.CLASS_NAME_ID,
					_portal.getClassNameId(CPDefinition.class));
				searchContext.setCompanyId(companyId);
			},
			sorts,
			document -> _toProductConfiguration(
				_cpConfigurationEntryService.getCPConfigurationEntry(
					GetterUtil.getLong(document.get(Field.CLASS_PK))),
				GetterUtil.getBoolean(showDifferences)));
	}

	@NestedField(parentClass = Product.class, value = "productConfiguration")
	@Override
	public ProductConfiguration getProductIdConfiguration(
			@NestedFieldId(value = "productId") Long id)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _toProductConfiguration(cpDefinition.getCPDefinitionId());
	}

	@Override
	public Response patchProductByExternalReferenceCodeConfiguration(
			String externalReferenceCode,
			ProductConfiguration productConfiguration)
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

		return patchProductIdConfiguration(
			cpDefinition.getCProductId(), productConfiguration);
	}

	@Override
	public ProductConfiguration patchProductConfiguration(
			Long id, ProductConfiguration productConfiguration)
		throws Exception {

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryService.getCPConfigurationEntry(id);

		ProductShippingConfiguration productShippingConfiguration =
			_getProductShippingConfiguration(productConfiguration);

		ProductTaxConfiguration productTaxConfiguration =
			_getProductTaxConfiguration(productConfiguration);

		return _toProductConfiguration(
			_cpConfigurationEntryService.updateCPConfigurationEntry(
				GetterUtil.getString(
					productConfiguration.getExternalReferenceCode(),
					cpConfigurationEntry.getExternalReferenceCode()),
				cpConfigurationEntry.getCPConfigurationEntryId(),
				GetterUtil.getLong(
					productTaxConfiguration.getId(),
					cpConfigurationEntry.getCPTaxCategoryId()),
				ProductConfigurationUtil.getAllowedOrderQuantities(
					productConfiguration.getAllowedOrderQuantities(),
					cpConfigurationEntry.getAllowedOrderQuantities()),
				GetterUtil.getBoolean(
					productConfiguration.getAllowBackOrder(),
					cpConfigurationEntry.isBackOrders()),
				GetterUtil.getLong(
					productConfiguration.getAvailabilityEstimateId(),
					cpConfigurationEntry.getCommerceAvailabilityEstimateId()),
				GetterUtil.getString(
					productConfiguration.getInventoryEngine(),
					cpConfigurationEntry.getCPDefinitionInventoryEngine()),
				GetterUtil.getDouble(
					productShippingConfiguration.getDepth(),
					cpConfigurationEntry.getDepth()),
				GetterUtil.getBoolean(
					productConfiguration.getDisplayAvailability(),
					cpConfigurationEntry.isDisplayAvailability()),
				GetterUtil.getBoolean(
					productConfiguration.getDisplayStockQuantity(),
					cpConfigurationEntry.isDisplayStockQuantity()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getFreeShipping(),
					cpConfigurationEntry.isFreeShipping()),
				GetterUtil.getDouble(
					productShippingConfiguration.getHeight(),
					cpConfigurationEntry.getHeight()),
				GetterUtil.getString(
					productConfiguration.getLowStockAction(),
					cpConfigurationEntry.getLowStockActivity()),
				BigDecimalUtil.get(
					productConfiguration.getMaxOrderQuantity(),
					cpConfigurationEntry.getMaxOrderQuantity()),
				BigDecimalUtil.get(
					productConfiguration.getMinOrderQuantity(),
					cpConfigurationEntry.getMinOrderQuantity()),
				BigDecimalUtil.get(
					productConfiguration.getMinStockQuantity(),
					cpConfigurationEntry.getMinStockQuantity()),
				BigDecimalUtil.get(
					productConfiguration.getMultipleOrderQuantity(),
					cpConfigurationEntry.getMultipleOrderQuantity()),
				GetterUtil.getBoolean(
					productConfiguration.getPurchasable(),
					cpConfigurationEntry.isPurchasable()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getShippable(),
					cpConfigurationEntry.isShippable()),
				GetterUtil.getDouble(
					productShippingConfiguration.getShippingExtraPrice(),
					cpConfigurationEntry.getShippingExtraPrice()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getShippingSeparately(),
					cpConfigurationEntry.isShipSeparately()),
				GetterUtil.getBoolean(
					!_isTaxable(productTaxConfiguration),
					cpConfigurationEntry.isTaxExempt()),
				GetterUtil.getBoolean(
					productConfiguration.getVisible(),
					cpConfigurationEntry.isVisible()),
				GetterUtil.getDouble(
					productShippingConfiguration.getWeight(),
					cpConfigurationEntry.getWeight()),
				GetterUtil.getDouble(
					productShippingConfiguration.getWidth(),
					cpConfigurationEntry.getWidth())),
			false);
	}

	@Override
	public ProductConfiguration
			patchProductConfigurationByExternalReferenceCode(
				String externalReferenceCode,
				ProductConfiguration productConfiguration)
		throws Exception {

		CPConfigurationEntry cpConfigurationEntry =
			_cpConfigurationEntryService.
				getCPConfigurationEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return patchProductConfiguration(
			cpConfigurationEntry.getCPConfigurationEntryId(),
			productConfiguration);
	}

	@Override
	public Response patchProductIdConfiguration(
			Long id, ProductConfiguration productConfiguration)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		CPConfigurationEntry masterCPConfigurationEntry =
			cpDefinition.fetchMasterCPConfigurationEntry();

		if (masterCPConfigurationEntry != null) {
			ProductShippingConfiguration productShippingConfiguration =
				_getProductShippingConfiguration(productConfiguration);

			ProductTaxConfiguration productTaxConfiguration =
				_getProductTaxConfiguration(productConfiguration);

			_cpConfigurationEntryService.updateCPConfigurationEntry(
				GetterUtil.getString(
					productConfiguration.getExternalReferenceCode(),
					masterCPConfigurationEntry.getExternalReferenceCode()),
				masterCPConfigurationEntry.getCPConfigurationEntryId(),
				GetterUtil.getLong(
					productTaxConfiguration.getId(),
					masterCPConfigurationEntry.getCPTaxCategoryId()),
				ProductConfigurationUtil.getAllowedOrderQuantities(
					productConfiguration.getAllowedOrderQuantities(),
					masterCPConfigurationEntry.getAllowedOrderQuantities()),
				GetterUtil.getBoolean(
					productConfiguration.getAllowBackOrder(),
					masterCPConfigurationEntry.isBackOrders()),
				GetterUtil.getLong(
					productConfiguration.getAvailabilityEstimateId(),
					masterCPConfigurationEntry.
						getCommerceAvailabilityEstimateId()),
				GetterUtil.getString(
					productConfiguration.getInventoryEngine(),
					masterCPConfigurationEntry.
						getCPDefinitionInventoryEngine()),
				GetterUtil.getDouble(
					productShippingConfiguration.getDepth(),
					masterCPConfigurationEntry.getDepth()),
				GetterUtil.getBoolean(
					productConfiguration.getDisplayAvailability(),
					masterCPConfigurationEntry.isDisplayAvailability()),
				GetterUtil.getBoolean(
					productConfiguration.getDisplayStockQuantity(),
					masterCPConfigurationEntry.isDisplayStockQuantity()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getFreeShipping(),
					masterCPConfigurationEntry.isFreeShipping()),
				GetterUtil.getDouble(
					productShippingConfiguration.getHeight(),
					masterCPConfigurationEntry.getHeight()),
				GetterUtil.getString(
					productConfiguration.getLowStockAction(),
					masterCPConfigurationEntry.getLowStockActivity()),
				BigDecimalUtil.get(
					productConfiguration.getMaxOrderQuantity(),
					masterCPConfigurationEntry.getMaxOrderQuantity()),
				BigDecimalUtil.get(
					productConfiguration.getMinOrderQuantity(),
					masterCPConfigurationEntry.getMinOrderQuantity()),
				BigDecimalUtil.get(
					productConfiguration.getMinStockQuantity(),
					masterCPConfigurationEntry.getMinStockQuantity()),
				BigDecimalUtil.get(
					productConfiguration.getMultipleOrderQuantity(),
					masterCPConfigurationEntry.getMultipleOrderQuantity()),
				GetterUtil.getBoolean(
					productConfiguration.getPurchasable(),
					masterCPConfigurationEntry.isPurchasable()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getShippable(),
					masterCPConfigurationEntry.isShippable()),
				GetterUtil.getDouble(
					productShippingConfiguration.getShippingExtraPrice(),
					masterCPConfigurationEntry.getShippingExtraPrice()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getShippingSeparately(),
					masterCPConfigurationEntry.isShipSeparately()),
				GetterUtil.getBoolean(
					!_isTaxable(productTaxConfiguration),
					masterCPConfigurationEntry.isTaxExempt()),
				GetterUtil.getBoolean(
					productConfiguration.getVisible(),
					masterCPConfigurationEntry.isVisible()),
				GetterUtil.getDouble(
					productShippingConfiguration.getWeight(),
					masterCPConfigurationEntry.getWeight()),
				GetterUtil.getDouble(
					productShippingConfiguration.getWidth(),
					masterCPConfigurationEntry.getWidth()));
		}

		ProductConfigurationUtil.updateCPDefinitionInventory(
			_cpDefinitionInventoryService, productConfiguration,
			cpDefinition.getCPDefinitionId());

		ProductConfigurationUtil.updateCPDAvailabilityEstimate(
			_cpdAvailabilityEstimateService, productConfiguration,
			cpDefinition.getCPDefinitionId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public ProductConfiguration
			postProductConfigurationListByExternalReferenceCodeProductConfiguration(
				String externalReferenceCode,
				ProductConfiguration productConfiguration)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.
				getCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return postProductConfigurationListIdProductConfiguration(
			cpConfigurationList.getCPConfigurationListId(),
			productConfiguration);
	}

	@Override
	public ProductConfiguration
			postProductConfigurationListIdProductConfiguration(
				Long id, ProductConfiguration productConfiguration)
		throws Exception {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListService.getCPConfigurationList(id);

		long classNameId = _portal.getClassNameId(CPDefinition.class.getName());
		long classPK = GetterUtil.getLong(productConfiguration.getEntityId());

		ProductConfiguration.EntityType entityType =
			productConfiguration.getEntityType();

		if ((entityType == null) ||
			Objects.equals(entityType.getValue(), "product")) {

			CPDefinition cpDefinition =
				_cpDefinitionService.
					fetchCPDefinitionByCProductExternalReferenceCode(
						GetterUtil.getString(
							productConfiguration.
								getEntityExternalReferenceCode()),
						contextCompany.getCompanyId());

			if (cpDefinition == null) {
				cpDefinition = _cpDefinitionService.getCPDefinition(classPK);
			}

			classPK = cpDefinition.getCPDefinitionId();
		}
		else if (Objects.equals(entityType.getValue(), "template")) {
			classNameId = _portal.getClassNameId(
				CPConfigurationList.class.getName());
			classPK = id;
		}

		ProductShippingConfiguration productShippingConfiguration =
			_getProductShippingConfiguration(productConfiguration);

		ProductTaxConfiguration productTaxConfiguration =
			_getProductTaxConfiguration(productConfiguration);

		return _toProductConfiguration(
			_cpConfigurationEntryService.addCPConfigurationEntry(
				GetterUtil.getString(
					productConfiguration.getExternalReferenceCode()),
				cpConfigurationList.getGroupId(), classNameId, classPK, id,
				GetterUtil.getLong(productTaxConfiguration.getId()),
				ProductConfigurationUtil.getAllowedOrderQuantities(
					productConfiguration.getAllowedOrderQuantities(), null),
				GetterUtil.getBoolean(productConfiguration.getAllowBackOrder()),
				GetterUtil.getLong(
					productConfiguration.getAvailabilityEstimateId()),
				GetterUtil.getString(productConfiguration.getInventoryEngine()),
				GetterUtil.getDouble(productShippingConfiguration.getDepth()),
				GetterUtil.getBoolean(
					productConfiguration.getDisplayAvailability()),
				GetterUtil.getBoolean(
					productConfiguration.getDisplayStockQuantity()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getFreeShipping(), true),
				GetterUtil.getDouble(productShippingConfiguration.getHeight()),
				GetterUtil.getString(productConfiguration.getLowStockAction()),
				BigDecimalUtil.get(
					productConfiguration.getMaxOrderQuantity(),
					new BigDecimal(1000)),
				BigDecimalUtil.get(
					productConfiguration.getMinOrderQuantity(), BigDecimal.ONE),
				BigDecimalUtil.get(
					productConfiguration.getMinStockQuantity(), BigDecimal.ONE),
				BigDecimalUtil.get(
					productConfiguration.getMultipleOrderQuantity(),
					BigDecimal.ONE),
				GetterUtil.getBoolean(
					productConfiguration.getPurchasable(), true),
				GetterUtil.getBoolean(
					productShippingConfiguration.getShippable(), true),
				GetterUtil.getDouble(
					productShippingConfiguration.getShippingExtraPrice()),
				GetterUtil.getBoolean(
					productShippingConfiguration.getShippingSeparately(), true),
				!GetterUtil.getBoolean(
					productTaxConfiguration.getTaxable(), true),
				GetterUtil.getBoolean(productConfiguration.getVisible(), true),
				GetterUtil.getDouble(productShippingConfiguration.getWeight()),
				GetterUtil.getDouble(productShippingConfiguration.getWidth())),
			false);
	}

	private Map<String, Map<String, String>> _getActions(
		CPConfigurationEntry cpConfigurationEntry) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			() -> {
				if (cpConfigurationEntry.isMaster()) {
					return null;
				}

				return addAction(
					"UPDATE", cpConfigurationEntry.getCPConfigurationEntryId(),
					"deleteProductConfiguration",
					_cpConfigurationEntryModelResourcePermission);
			}
		).put(
			"get",
			() -> addAction(
				"VIEW", cpConfigurationEntry.getCPConfigurationEntryId(),
				"getProductConfiguration",
				_cpConfigurationEntryModelResourcePermission)
		).put(
			"update",
			() -> addAction(
				"UPDATE", cpConfigurationEntry.getCPConfigurationEntryId(),
				"patchProductConfiguration",
				_cpConfigurationEntryModelResourcePermission)
		).build();
	}

	private ProductShippingConfiguration _getProductShippingConfiguration(
		ProductConfiguration productConfiguration) {

		ProductShippingConfiguration productShippingConfiguration =
			productConfiguration.getProductShippingConfiguration();

		if (productShippingConfiguration == null) {
			return new ProductShippingConfiguration();
		}

		return productShippingConfiguration;
	}

	private ProductTaxConfiguration _getProductTaxConfiguration(
		ProductConfiguration productConfiguration) {

		ProductTaxConfiguration productTaxConfiguration =
			productConfiguration.getProductTaxConfiguration();

		if (productTaxConfiguration == null) {
			return new ProductTaxConfiguration();
		}

		return productTaxConfiguration;
	}

	private boolean _isTaxable(
		ProductTaxConfiguration productTaxConfiguration) {

		if (productTaxConfiguration.getTaxable() == null) {
			return true;
		}

		return productTaxConfiguration.getTaxable();
	}

	private ProductConfiguration _toProductConfiguration(
			CPConfigurationEntry cpConfigurationEntry, boolean showDifferences)
		throws Exception {

		return _productConfigurationDTOConverter.toDTO(
			new ProductConfigurationDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(cpConfigurationEntry),
				cpConfigurationEntry.getCPConfigurationEntryId(),
				_dtoConverterRegistry, null,
				contextAcceptLanguage.getPreferredLocale(), showDifferences,
				contextUriInfo, contextUser));
	}

	private ProductConfiguration _toProductConfiguration(Long cpDefinitionId)
		throws Exception {

		return _productConfigurationDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, cpDefinitionId,
				contextAcceptLanguage.getPreferredLocale(), null, null));
	}

	private static final EntityModel _entityModel =
		new ProductConfigurationEntityModel();

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPConfigurationEntry)"
	)
	private ModelResourcePermission<CPConfigurationEntry>
		_cpConfigurationEntryModelResourcePermission;

	@Reference
	private CPConfigurationEntryService _cpConfigurationEntryService;

	@Reference
	private CPConfigurationListService _cpConfigurationListService;

	@Reference
	private CPDAvailabilityEstimateService _cpdAvailabilityEstimateService;

	@Reference
	private CPDefinitionInventoryService _cpDefinitionInventoryService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductConfigurationDTOConverter)"
	)
	private DTOConverter<CPDefinitionInventory, ProductConfiguration>
		_productConfigurationDTOConverter;

}