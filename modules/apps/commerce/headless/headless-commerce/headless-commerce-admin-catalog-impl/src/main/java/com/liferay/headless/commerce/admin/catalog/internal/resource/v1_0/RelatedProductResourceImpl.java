/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.RelatedProduct;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.RelatedProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.RelatedProductResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
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
	properties = "OSGI-INF/liferay/rest/v1_0/related-product.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = RelatedProductResource.class
)
@CTAware
public class RelatedProductResourceImpl extends BaseRelatedProductResourceImpl {

	@Override
	public Response deleteRelatedProduct(Long id) throws Exception {
		_cpDefinitionLinkService.deleteCPDefinitionLink(id);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Page<RelatedProduct>
			getProductByExternalReferenceCodeRelatedProductsPage(
				String externalReferenceCode, String type,
				Pagination pagination)
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

		return _getRelatedProductPage(cpDefinition, type, pagination);
	}

	@NestedField(parentClass = Product.class, value = "relatedProducts")
	@Override
	public Page<RelatedProduct> getProductIdRelatedProductsPage(
			@NestedFieldId(value = "productId") Long id, String type,
			Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _getRelatedProductPage(cpDefinition, type, pagination);
	}

	@Override
	public RelatedProduct getRelatedProduct(Long id) throws Exception {
		return _toRelatedProduct(GetterUtil.getLong(id));
	}

	@Override
	public RelatedProduct postProductByExternalReferenceCodeRelatedProduct(
			String externalReferenceCode, RelatedProduct relatedProduct)
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

		return _addOrUpdateRelatedProduct(cpDefinition, relatedProduct);
	}

	@Override
	public RelatedProduct postProductIdRelatedProduct(
			Long id, RelatedProduct relatedProduct)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionService.fetchCPDefinitionByCProductId(id);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + id);
		}

		return _addOrUpdateRelatedProduct(cpDefinition, relatedProduct);
	}

	private RelatedProduct _addOrUpdateRelatedProduct(
			CPDefinition cpDefinition, RelatedProduct relatedProduct)
		throws Exception {

		CPDefinitionLink cpDefinitionLink =
			RelatedProductUtil.addOrUpdateCPDefinitionLink(
				_cpDefinitionLinkService, _cpDefinitionService, relatedProduct,
				cpDefinition.getCPDefinitionId(),
				_serviceContextHelper.getServiceContext(
					cpDefinition.getGroupId()));

		return _toRelatedProduct(cpDefinitionLink.getCPDefinitionLinkId());
	}

	private Page<RelatedProduct> _getRelatedProductPage(
			CPDefinition cpDefinition, String type, Pagination pagination)
		throws Exception {

		List<CPDefinitionLink> cpDefinitionLinks;
		int totalCount;

		if (Validator.isNull(type)) {
			cpDefinitionLinks = _cpDefinitionLinkService.getCPDefinitionLinks(
				cpDefinition.getCPDefinitionId(), pagination.getStartPosition(),
				pagination.getEndPosition());

			totalCount = _cpDefinitionLinkService.getCPDefinitionLinksCount(
				cpDefinition.getCPDefinitionId());
		}
		else {
			cpDefinitionLinks = _cpDefinitionLinkService.getCPDefinitionLinks(
				cpDefinition.getCPDefinitionId(), type,
				pagination.getStartPosition(), pagination.getEndPosition(),
				null);

			totalCount = _cpDefinitionLinkService.getCPDefinitionLinksCount(
				cpDefinition.getCPDefinitionId(), type);
		}

		return Page.of(
			_toRelatedProducts(cpDefinitionLinks), pagination, totalCount);
	}

	private RelatedProduct _toRelatedProduct(Long cpDefinitionLinkId)
		throws Exception {

		return _relatedProductDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				cpDefinitionLinkId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<RelatedProduct> _toRelatedProducts(
			List<CPDefinitionLink> cpDefinitionLinks)
		throws Exception {

		return transform(
			cpDefinitionLinks,
			cpDefinitionLink -> _toRelatedProduct(
				cpDefinitionLink.getCPDefinitionLinkId()));
	}

	@Reference
	private CPDefinitionLinkService _cpDefinitionLinkService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.RelatedProductDTOConverter)"
	)
	private DTOConverter<CPDefinitionLink, RelatedProduct>
		_relatedProductDTOConverter;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}