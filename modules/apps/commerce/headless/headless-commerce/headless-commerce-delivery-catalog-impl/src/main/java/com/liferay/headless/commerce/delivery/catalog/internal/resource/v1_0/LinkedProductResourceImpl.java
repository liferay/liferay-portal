/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.type.grouped.constants.GroupedCPTypeConstants;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryLocalService;
import com.liferay.commerce.shop.by.diagram.constants.CSDiagramCPTypeConstants;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.LinkedProduct;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Product;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.LinkedProductDTOConverterContext;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.LinkedProductResource;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/linked-product.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = LinkedProductResource.class
)
public class LinkedProductResourceImpl extends BaseLinkedProductResourceImpl {

	@NestedField(parentClass = Product.class, value = "linkedProducts")
	@Override
	public Page<LinkedProduct> getChannelProductLinkedProductsPage(
			@NestedFieldId(value = "channelId") Long channelId,
			@NestedFieldId(value = "productId") Long productId, Long accountId,
			Pagination pagination)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		long selectedAccountId = _getSelectedAccountId(
			accountId, commerceChannel);

		CProduct cProduct = _cProductLocalService.getCProduct(productId);

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), selectedAccountId,
			commerceChannel.getGroupId(),
			cProduct.getPublishedCPDefinitionId());

		List<LinkedProduct> linkedProducts = ListUtil.concat(
			transform(
				_cpDefinitionGroupedEntryLocalService.
					getEntryCProductCPDefinitionGroupedEntries(
						productId, pagination.getStartPosition(),
						pagination.getEndPosition(), null),
				cpDefinitionGroupedEntry -> _linkedProductDTOConverter.toDTO(
					new LinkedProductDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						selectedAccountId, null,
						commerceChannel.getCommerceChannelId(),
						_dtoConverterRegistry,
						cpDefinitionGroupedEntry.
							getCPDefinitionGroupedEntryId(),
						contextAcceptLanguage.getPreferredLocale(),
						GroupedCPTypeConstants.NAME, contextUriInfo,
						contextUser))),
			transform(
				_csDiagramEntryLocalService.getCProductCSDiagramEntries(
					productId, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				csDiagramEntry -> _linkedProductDTOConverter.toDTO(
					new LinkedProductDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						selectedAccountId, null,
						commerceChannel.getCommerceChannelId(),
						_dtoConverterRegistry,
						csDiagramEntry.getCSDiagramEntryId(),
						contextAcceptLanguage.getPreferredLocale(),
						CSDiagramCPTypeConstants.NAME, contextUriInfo,
						contextUser))));

		return Page.of(linkedProducts, pagination, linkedProducts.size());
	}

	private Long _getSelectedAccountId(
			Long accountId, CommerceChannel commerceChannel)
		throws Exception {

		int count = _commerceAccountHelper.countUserCommerceAccounts(
			contextUser.getUserId(), commerceChannel.getGroupId());

		if (count > 1) {
			if (accountId == null) {
				MultivaluedMap<String, String> queryParameters =
					contextUriInfo.getQueryParameters();

				String accountIdString = queryParameters.getFirst("accountId");

				if (accountIdString != null) {
					accountId = GetterUtil.getLong(accountIdString);
				}
				else {
					throw new NoSuchEntryException();
				}
			}
		}
		else {
			long[] commerceAccountIds =
				_commerceAccountHelper.getUserCommerceAccountIds(
					contextUser.getUserId(), commerceChannel.getGroupId());

			if (commerceAccountIds.length == 0) {
				AccountEntry accountEntry =
					_accountEntryLocalService.getGuestAccountEntry(
						contextCompany.getCompanyId());

				commerceAccountIds = new long[] {
					accountEntry.getAccountEntryId()
				};
			}

			return commerceAccountIds[0];
		}

		return accountId;
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionGroupedEntryLocalService
		_cpDefinitionGroupedEntryLocalService;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.LinkedProductDTOConverter)"
	)
	private DTOConverter<CPDefinitionGroupedEntry, LinkedProduct>
		_linkedProductDTOConverter;

}