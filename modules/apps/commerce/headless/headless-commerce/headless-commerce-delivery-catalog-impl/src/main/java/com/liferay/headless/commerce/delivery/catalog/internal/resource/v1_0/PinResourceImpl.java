/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramPinLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Pin;
import com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.PinDTOConverterContext;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.PinResource;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Ivica Cardic
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/pin.properties",
	scope = ServiceScope.PROTOTYPE, service = PinResource.class
)
public class PinResourceImpl extends BasePinResourceImpl {

	@Override
	public Page<Pin>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Long accountId,
				String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					channelExternalReferenceCode,
					contextCompany.getCompanyId());

		CProduct cProduct =
			_cProductLocalService.getCProductByExternalReferenceCode(
				productExternalReferenceCode, contextCompany.getCompanyId());

		return getChannelProductPinsPage(
			commerceChannel.getCommerceChannelId(), cProduct.getCProductId(),
			accountId, search, pagination, sorts);
	}

	@Override
	public Page<Pin> getChannelProductPinsPage(
			Long channelId, Long productId, Long accountId, String search,
			Pagination pagination, Sort[] sorts)
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

		accountId = _getAccountId(accountId, commerceChannel);

		_commerceProductViewPermission.check(
			PermissionThreadLocal.getPermissionChecker(), accountId,
			commerceChannel.getGroupId(), cpDefinition.getCPDefinitionId());

		CommerceContext commerceContext = _commerceContextFactory.create(
			accountId, commerceChannel.getGroupId(), null, 0,
			contextCompany.getCompanyId());

		return Page.of(
			_toPins(
				commerceContext,
				_csDiagramPinLocalService.getCSDiagramPins(
					cpDefinition.getCPDefinitionId(),
					pagination.getStartPosition(),
					pagination.getEndPosition())),
			pagination,
			_csDiagramPinLocalService.getCSDiagramPinsCount(
				cpDefinition.getCPDefinitionId()));
	}

	private Long _getAccountId(Long accountId, CommerceChannel commerceChannel)
		throws Exception {

		if ((accountId != null) && (accountId > 0)) {
			AccountEntry accountEntry = _accountEntryService.fetchAccountEntry(
				accountId);

			if (accountEntry != null) {
				return accountEntry.getAccountEntryId();
			}
		}

		int countUserCommerceAccounts =
			_commerceAccountHelper.countUserCommerceAccounts(
				contextUser.getUserId(), commerceChannel.getGroupId());

		if (countUserCommerceAccounts > 1) {
			if (accountId == null) {
				throw new NoSuchEntryException();
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

	private Pin _toPin(CommerceContext commerceContext, long csDiagramPinId)
		throws Exception {

		return _pinDTOConverter.toDTO(
			new PinDTOConverterContext(
				commerceContext, contextCompany.getCompanyId(), csDiagramPinId,
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<Pin> _toPins(
		CommerceContext commerceContext, List<CSDiagramPin> csDiagramPins) {

		return transform(
			csDiagramPins,
			csDiagramPin -> _toPin(
				commerceContext, csDiagramPin.getCSDiagramPinId()));
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

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CSDiagramPinLocalService _csDiagramPinLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.PinDTOConverter)"
	)
	private DTOConverter<CSDiagramEntry, Pin> _pinDTOConverter;

}