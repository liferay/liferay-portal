/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.MappedProductUtil;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.MappedProductResource;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/mapped-product.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = MappedProductResource.class
)
@CTAware
public class MappedProductResourceImpl extends BaseMappedProductResourceImpl {

	@Override
	public void deleteMappedProduct(Long mappedProductId) throws Exception {
		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.getCSDiagramEntry(mappedProductId);

		_csDiagramEntryService.deleteCSDiagramEntry(csDiagramEntry);
	}

	@Override
	public MappedProduct
			getProductByExternalReferenceCodeMappedProductBySequence(
				String externalReferenceCode, String sequence)
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

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.fetchCSDiagramEntry(
				cpDefinition.getCPDefinitionId(), sequence);

		return _toMappedProduct(csDiagramEntry.getCSDiagramEntryId());
	}

	@Override
	public Page<MappedProduct>
			getProductByExternalReferenceCodeMappedProductsPage(
				String externalReferenceCode, String search,
				Pagination pagination, Sort[] sorts)
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

		return _getMappedProductsPage(
			cpDefinition.getCPDefinitionId(), pagination, search, sorts);
	}

	@Override
	public MappedProduct getProductIdMappedProductBySequence(
			Long productId, String sequence)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(
				productId, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.fetchCSDiagramEntry(
				cpDefinition.getCPDefinitionId(), sequence);

		return _toMappedProduct(csDiagramEntry.getCSDiagramEntryId());
	}

	@NestedField(parentClass = Product.class, value = "mappedProducts")
	@Override
	public Page<MappedProduct> getProductIdMappedProductsPage(
			Long productId, String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(
				productId, false);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		return _getMappedProductsPage(
			cpDefinition.getCPDefinitionId(), pagination, search, sorts);
	}

	@Override
	public MappedProduct patchMappedProduct(
			Long mappedProductId, MappedProduct mappedProduct)
		throws Exception {

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.getCSDiagramEntry(mappedProductId);

		CPDefinition cpDefinition = csDiagramEntry.getCPDefinition();

		MappedProductUtil.updateCSDiagramEntry(
			contextCompany.getCompanyId(), csDiagramEntry,
			_csDiagramEntryService, cpDefinition.getGroupId(),
			contextAcceptLanguage.getPreferredLocale(), mappedProduct,
			_serviceContextHelper);

		return _toMappedProduct(mappedProductId);
	}

	@Override
	public MappedProduct postProductByExternalReferenceCodeMappedProduct(
			String externalReferenceCode, MappedProduct mappedProduct)
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

		CSDiagramEntry csDiagramEntry = MappedProductUtil.addCSDiagramEntry(
			contextCompany.getCompanyId(), cpDefinition.getCPDefinitionId(),
			_cpDefinitionService, _cpInstanceService, _csDiagramEntryService,
			cpDefinition.getGroupId(),
			contextAcceptLanguage.getPreferredLocale(), mappedProduct,
			_serviceContextHelper);

		return _toMappedProduct(csDiagramEntry.getCSDiagramEntryId());
	}

	@Override
	public MappedProduct postProductIdMappedProduct(
			Long productId, MappedProduct mappedProduct)
		throws Exception {

		CPDefinition cpDefinition = ProductUtil.fetchCPDefinitionByCProductId(
			_cpDefinitionService, productId);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		CSDiagramEntry csDiagramEntry = MappedProductUtil.addCSDiagramEntry(
			contextCompany.getCompanyId(), cpDefinition.getCPDefinitionId(),
			_cpDefinitionService, _cpInstanceService, _csDiagramEntryService,
			cpDefinition.getGroupId(),
			contextAcceptLanguage.getPreferredLocale(), mappedProduct,
			_serviceContextHelper);

		return _toMappedProduct(csDiagramEntry.getCSDiagramEntryId());
	}

	private Map<String, Map<String, String>> _getActions(long csDiagramEntryId)
		throws Exception {

		CSDiagramEntry csDiagramEntry =
			_csDiagramEntryService.getCSDiagramEntry(csDiagramEntryId);

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"UPDATE", csDiagramEntry.getCPDefinitionId(),
				"deleteMappedProduct", _cpDefinitionModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE", csDiagramEntry.getCPDefinitionId(),
				"patchMappedProduct", _cpDefinitionModelResourcePermission)
		).build();
	}

	private Page<MappedProduct> _getMappedProductsPage(
			long cpDefinitionId, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null,
			booleanQuery -> {
			},
			null, CSDiagramEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setAttribute(
					CPField.CP_DEFINITION_ID, cpDefinitionId);
			},
			sorts,
			document -> _toMappedProduct(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	private MappedProduct _toMappedProduct(long csDiagramEntryId)
		throws Exception {

		return _mappedProductDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(csDiagramEntryId), _dtoConverterRegistry,
				csDiagramEntryId, contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser));
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private CSDiagramEntryService _csDiagramEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(target = DTOConverterConstants.MAPPED_PRODUCT_DTO_CONVERTER)
	private DTOConverter<CSDiagramEntry, MappedProduct>
		_mappedProductDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}