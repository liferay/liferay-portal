/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Category;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.CategoryResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/category.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = CategoryResource.class
)
@CTAware
public class CategoryResourceImpl extends BaseCategoryResourceImpl {

	@Override
	public Page<Category> getProductByExternalReferenceCodeCategoriesPage(
			String externalReferenceCode, Pagination pagination)
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

		List<AssetCategory> assetCategories =
			_assetCategoryService.getCategories(
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClass()),
				cpDefinition.getCPDefinitionId(), pagination.getStartPosition(),
				pagination.getEndPosition());

		int totalCount = _assetCategoryService.getCategoriesCount(
			_classNameLocalService.getClassNameId(cpDefinition.getModelClass()),
			cpDefinition.getCPDefinitionId());

		return Page.of(
			_toProductCategories(
				assetCategories, contextAcceptLanguage.getPreferredLocale()),
			pagination, totalCount);
	}

	@NestedField(parentClass = Product.class, value = "categories")
	@Override
	public Page<Category> getProductIdCategoriesPage(
			@NestedFieldId(value = "productId") Long id, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		List<AssetCategory> assetCategories =
			_assetCategoryService.getCategories(
				_classNameLocalService.getClassNameId(
					cpDefinition.getModelClass()),
				cpDefinition.getCPDefinitionId(), pagination.getStartPosition(),
				pagination.getEndPosition());

		int totalCount = _assetCategoryService.getCategoriesCount(
			_classNameLocalService.getClassNameId(cpDefinition.getModelClass()),
			cpDefinition.getCPDefinitionId());

		return Page.of(
			_toProductCategories(
				assetCategories, contextAcceptLanguage.getPreferredLocale()),
			pagination, totalCount);
	}

	@Override
	public Response patchProductByExternalReferenceCodeCategory(
			String externalReferenceCode, Category[] categories)
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

		_updateProductCategories(cpDefinition, categories);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchProductIdCategory(Long id, Category[] categories)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		_updateProductCategories(cpDefinition, categories);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	private List<Category> _toProductCategories(
			List<AssetCategory> assetCategories, Locale locale)
		throws Exception {

		return transform(
			assetCategories,
			category -> _categoryDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					category.getCategoryId(), locale)));
	}

	private void _updateProductCategories(
			CPDefinition cpDefinition, Category[] categories)
		throws Exception {

		long[] assetCategoryIds = new long[0];

		for (Category category : categories) {
			AssetCategory assetCategory = _assetCategoryService.fetchCategory(
				category.getId());

			if (assetCategory == null) {
				throw new NoSuchCategoryException(
					"Unable to find Category with ID: " + category.getId());
			}

			assetCategoryIds = ArrayUtil.append(
				assetCategoryIds, assetCategory.getCategoryId());
		}

		ServiceContext serviceContext = _serviceContextHelper.getServiceContext(
			cpDefinition.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		_cpDefinitionService.updateCPDefinitionCategorization(
			cpDefinition.getCPDefinitionId(), serviceContext);
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.CategoryDTOConverter)"
	)
	private DTOConverter<AssetCategory, Category> _categoryDTOConverter;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}