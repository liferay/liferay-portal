/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductOptionUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductOptionValueUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.ProductOptionResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Zoltán Takács
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/product-option.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ProductOptionResource.class
)
@CTAware
public class ProductOptionResourceImpl extends BaseProductOptionResourceImpl {

	@Override
	public Response deleteProductOption(Long id) throws Exception {
		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(id);

		_cpDefinitionOptionRelService.deleteCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId());

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<ProductOption>
			getProductByExternalReferenceCodeProductOptionsPage(
				String externalReferenceCode, String search,
				Pagination pagination, Sort[] sorts)
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

		BaseModelSearchResult<CPDefinitionOptionRel>
			cpDefinitionOptionRelBaseModelSearchResult =
				_cpDefinitionOptionRelService.searchCPDefinitionOptionRels(
					cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
					cpDefinition.getCPDefinitionId(), search,
					pagination.getStartPosition(), pagination.getEndPosition(),
					sorts);

		return Page.of(
			transform(
				cpDefinitionOptionRelBaseModelSearchResult.getBaseModels(),
				cpDefinitionOptionRel -> _toProductOption(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId())),
			pagination,
			_cpDefinitionOptionRelService.searchCPDefinitionOptionRelsCount(
				cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
				cpDefinition.getCPDefinitionId(), search));
	}

	@NestedField(parentClass = Product.class, value = "productOptions")
	@Override
	public Page<ProductOption> getProductIdProductOptionsPage(
			@NestedFieldId(value = "productId") Long id, String search,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		BaseModelSearchResult<CPDefinitionOptionRel>
			cpDefinitionOptionRelBaseModelSearchResult =
				_cpDefinitionOptionRelService.searchCPDefinitionOptionRels(
					cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
					cpDefinition.getCPDefinitionId(), search,
					pagination.getStartPosition(), pagination.getEndPosition(),
					sorts);

		return Page.of(
			transform(
				cpDefinitionOptionRelBaseModelSearchResult.getBaseModels(),
				cpDefinitionOptionRel -> _toProductOption(
					cpDefinitionOptionRel.getCPDefinitionOptionRelId())),
			pagination,
			_cpDefinitionOptionRelService.searchCPDefinitionOptionRelsCount(
				cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
				cpDefinition.getCPDefinitionId(), search));
	}

	@Override
	public ProductOption getProductOption(Long id) throws Exception {
		return _toProductOption(GetterUtil.getLong(id));
	}

	@Override
	public Response patchProductOption(Long id, ProductOption productOption)
		throws Exception {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRel(id);

		Map<String, String> nameMap = productOption.getName();

		if ((cpDefinitionOptionRel != null) && (nameMap == null)) {
			nameMap = LanguageUtils.getLanguageIdMap(
				cpDefinitionOptionRel.getNameMap());
		}

		Map<String, String> descriptionMap = productOption.getDescription();

		if ((cpDefinitionOptionRel != null) && (descriptionMap == null)) {
			descriptionMap = LanguageUtils.getLanguageIdMap(
				cpDefinitionOptionRel.getDescriptionMap());
		}

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpDefinitionOptionRel.getGroupId());

		serviceContext.setExpandoBridgeAttributes(
			_getExpandoBridgeAttributes(productOption));

		_cpDefinitionOptionRelService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			_getOptionId(cpDefinitionOptionRel.getCPOptionId(), productOption),
			LanguageUtils.getLocalizedMap(nameMap),
			LanguageUtils.getLocalizedMap(descriptionMap),
			GetterUtil.get(
				productOption.getFieldType(),
				cpDefinitionOptionRel.getCommerceOptionTypeKey()),
			GetterUtil.get(
				productOption.getInfoItemServiceKey(),
				cpDefinitionOptionRel.getCommerceOptionTypeKey()),
			GetterUtil.get(
				productOption.getPriority(),
				cpDefinitionOptionRel.getPriority()),
			GetterUtil.get(
				productOption.getDefinedExternally(),
				cpDefinitionOptionRel.isDefinedExternally()),
			GetterUtil.get(
				productOption.getFacetable(),
				cpDefinitionOptionRel.isFacetable()),
			GetterUtil.get(
				productOption.getRequired(),
				cpDefinitionOptionRel.isRequired()),
			GetterUtil.get(
				productOption.getSkuContributor(),
				cpDefinitionOptionRel.isSkuContributor()),
			GetterUtil.get(
				productOption.getPriceType(),
				cpDefinitionOptionRel.getPriceType()),
			GetterUtil.get(
				productOption.getTypeSettings(),
				cpDefinitionOptionRel.getTypeSettings()),
			serviceContext);

		ProductOptionValue[] productOptionValues =
			productOption.getProductOptionValues();

		if (productOptionValues != null) {
			for (ProductOptionValue productOptionValue : productOptionValues) {
				ProductOptionValueUtil.addOrUpdateCPDefinitionOptionValueRel(
					_cpDefinitionOptionValueRelService, _cpInstanceService,
					productOptionValue,
					cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
					_serviceContextHelper.getServiceContext(
						cpDefinitionOptionRel.getGroupId()));
			}
		}

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<ProductOption>
			postProductByExternalReferenceCodeProductOptionsPage(
				String externalReferenceCode, ProductOption[] productOptions)
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

		return Page.of(
			_addOrUpdateProductOptions(cpDefinition, productOptions));
	}

	@Override
	public Page<ProductOption> postProductIdProductOptionsPage(
			Long id, ProductOption[] productOptions)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return Page.of(
			_addOrUpdateProductOptions(cpDefinition, productOptions));
	}

	private List<ProductOption> _addOrUpdateProductOptions(
			CPDefinition cpDefinition, ProductOption[] productOptions)
		throws Exception {

		for (ProductOption productOption : productOptions) {
			ServiceContext serviceContext =
				_serviceContextHelper.getServiceContext(
					cpDefinition.getGroupId());

			serviceContext.setExpandoBridgeAttributes(
				_getExpandoBridgeAttributes(productOption));

			CPDefinitionOptionRel cpDefinitionOptionRel =
				ProductOptionUtil.addOrUpdateCPDefinitionOptionRel(
					_cpDefinitionOptionRelService, _cpOptionService,
					productOption, cpDefinition.getCPDefinitionId(),
					serviceContext);

			serviceContext.setExpandoBridgeAttributes(null);

			ProductOptionValue[] productOptionValues =
				productOption.getProductOptionValues();

			if (productOptionValues != null) {
				for (ProductOptionValue productOptionValue :
						productOptionValues) {

					ProductOptionValueUtil.
						addOrUpdateCPDefinitionOptionValueRel(
							_cpDefinitionOptionValueRelService,
							_cpInstanceService, productOptionValue,
							cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
							serviceContext);
				}
			}
		}

		return transform(
			_cpDefinitionOptionRelService.getCPDefinitionOptionRels(
				cpDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			cpDefinitionOptionRel -> _toProductOption(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId()));
	}

	private Map<String, Serializable> _getExpandoBridgeAttributes(
		ProductOption productOption) {

		Map<String, Serializable> expandoBridgeAttributes =
			CustomFieldsUtil.toMap(
				CPDefinitionOptionRel.class.getName(),
				contextCompany.getCompanyId(), productOption.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale());

		if (expandoBridgeAttributes == null) {
			expandoBridgeAttributes = new HashMap<>();
		}

		return expandoBridgeAttributes;
	}

	private long _getOptionId(long defaultOptionId, ProductOption productOption)
		throws Exception {

		long optionId = productOption.getOptionId();

		if (optionId > 0) {
			return optionId;
		}

		CPOption cpOption =
			_cpOptionService.fetchCPOptionByExternalReferenceCode(
				productOption.getOptionExternalReferenceCode(),
				contextCompany.getCompanyId());

		if (cpOption != null) {
			return cpOption.getCPOptionId();
		}

		return defaultOptionId;
	}

	private ProductOption _toProductOption(Long cpDefinitionOptionRelId)
		throws Exception {

		return _productOptionDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpDefinitionOptionRelId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	@Reference
	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

	@Reference
	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private CPOptionService _cpOptionService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.ProductOptionDTOConverter)"
	)
	private DTOConverter<CPDefinitionOptionRel, ProductOption>
		_productOptionDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}