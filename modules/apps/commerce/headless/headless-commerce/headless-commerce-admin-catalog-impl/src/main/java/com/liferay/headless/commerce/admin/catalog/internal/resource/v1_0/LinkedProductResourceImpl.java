/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.type.grouped.constants.GroupedCPTypeConstants;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryService;
import com.liferay.commerce.shop.by.diagram.constants.CSDiagramCPTypeConstants;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.LinkedProduct;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.LinkedProductDTOConverterContext;
import com.liferay.headless.commerce.admin.catalog.internal.util.v1_0.ProductUtil;
import com.liferay.headless.commerce.admin.catalog.resource.v1_0.LinkedProductResource;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/linked-product.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = LinkedProductResource.class
)
public class LinkedProductResourceImpl extends BaseLinkedProductResourceImpl {

	@Override
	public Page<LinkedProduct>
			getProductByExternalReferenceCodeLinkedProductsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CPDefinition cpDefinition =
			ProductUtil.fetchCPDefinitionByCProductExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId(),
				_cpDefinitionService);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with external reference code " +
					externalReferenceCode);
		}

		return getProductIdLinkedProductsPage(
			cpDefinition.getCProductId(), pagination);
	}

	@NestedField(parentClass = Product.class, value = "linkedProducts")
	@Override
	public Page<LinkedProduct> getProductIdLinkedProductsPage(
			@NestedFieldId(value = "productId") Long productId,
			Pagination pagination)
		throws Exception {

		int cProductCSDiagramEntriesCount =
			_csDiagramEntryService.getCProductCSDiagramEntriesCount(productId);
		int entryCProductCPDefinitionGroupedEntriesCount =
			_cpDefinitionGroupedEntryService.
				getEntryCProductCPDefinitionGroupedEntriesCount(productId);

		return Page.of(
			ListUtil.concat(
				transform(
					_cpDefinitionGroupedEntryService.
						getEntryCProductCPDefinitionGroupedEntries(
							productId, pagination.getStartPosition(),
							pagination.getEndPosition(), null),
					cpDefinitionGroupedEntry ->
						_linkedProductDTOConverter.toDTO(
							new LinkedProductDTOConverterContext(
								contextAcceptLanguage.isAcceptAllLanguages(),
								null, _dtoConverterRegistry,
								cpDefinitionGroupedEntry.
									getCPDefinitionGroupedEntryId(),
								contextAcceptLanguage.getPreferredLocale(),
								GroupedCPTypeConstants.NAME, contextUriInfo,
								contextUser))),
				transform(
					_csDiagramEntryService.getCProductCSDiagramEntries(
						productId, pagination.getStartPosition(),
						pagination.getEndPosition(), null),
					csDiagramEntry -> _linkedProductDTOConverter.toDTO(
						new LinkedProductDTOConverterContext(
							contextAcceptLanguage.isAcceptAllLanguages(), null,
							_dtoConverterRegistry,
							csDiagramEntry.getCSDiagramEntryId(),
							contextAcceptLanguage.getPreferredLocale(),
							CSDiagramCPTypeConstants.NAME, contextUriInfo,
							contextUser)))),
			pagination,
			entryCProductCPDefinitionGroupedEntriesCount +
				cProductCSDiagramEntriesCount);
	}

	@Reference
	private CPDefinitionGroupedEntryService _cpDefinitionGroupedEntryService;

	@Reference
	private CPDefinitionService _cpDefinitionService;

	@Reference
	private CSDiagramEntryService _csDiagramEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter.LinkedProductDTOConverter)"
	)
	private DTOConverter<CPDefinitionGroupedEntry, LinkedProduct>
		_linkedProductDTOConverter;

}