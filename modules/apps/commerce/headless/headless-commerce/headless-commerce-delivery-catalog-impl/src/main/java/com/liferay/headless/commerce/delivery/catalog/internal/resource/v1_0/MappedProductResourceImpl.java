/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.MappedProductDTOConverterContext;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.commerce.delivery.catalog.internal.util.v1_0.AccountUtil;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.MappedProductResource;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/mapped-product.properties",
	scope = ServiceScope.PROTOTYPE, service = MappedProductResource.class
)
public class MappedProductResourceImpl extends BaseMappedProductResourceImpl {

	@Override
	public Page<MappedProduct> getChannelProductMappedProductsPage(
			Long channelId, Long productId, Long accountId, String currencyCode,
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		CPDefinition cpDefinition =
			_cpDefinitionLocalService.fetchCPDefinitionByCProductId(
				productId, true);

		if (cpDefinition == null) {
			throw new NoSuchCPDefinitionException(
				"Unable to find product with ID " + productId);
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		accountId = AccountUtil.getAccountId(
			contextCompany.getCompanyId(), commerceChannel.getGroupId(),
			contextUser.getUserId(), _accountEntryLocalService,
			_accountEntryService, accountId, _commerceAccountHelper, null);

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), accountId,
			commerceChannel.getGroupId(), cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = _commerceContextFactory.create(
			accountId, commerceChannel.getGroupId(), currencyCode, 0,
			contextCompany.getCompanyId());

		return _getMappedProductsPage(
			commerceContext, cpDefinition.getCPDefinitionId(), pagination,
			search, sorts);
	}

	private Page<MappedProduct> _getMappedProductsPage(
			CommerceContext commerceContext, long cpDefinitionId,
			Pagination pagination, String search, Sort[] sorts)
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
				commerceContext,
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	private MappedProduct _toMappedProduct(
			CommerceContext commerceContext, long csDiagramEntryId)
		throws Exception {

		return _mappedProductDTOConverter.toDTO(
			new MappedProductDTOConverterContext(
				commerceContext, contextCompany.getCompanyId(),
				csDiagramEntryId, contextAcceptLanguage.getPreferredLocale()));
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference(target = DTOConverterConstants.MAPPED_PRODUCT_DTO_CONVERTER)
	private DTOConverter<CSDiagramEntry, MappedProduct>
		_mappedProductDTOConverter;

}