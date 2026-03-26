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
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishListItem;
import com.liferay.headless.commerce.delivery.catalog.internal.util.v1_0.AccountUtil;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.WishListItemResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Mahmoud Azzam
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/wish-list-item.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = WishListItemResource.class
)
public class WishListItemResourceImpl extends BaseWishListItemResourceImpl {

	@Override
	public void deleteWishListItem(Long wishListItemId) throws Exception {
		_commerceWishListItemService.deleteCommerceWishListItem(wishListItemId);
	}

	@Override
	public WishListItem getWishListItem(
			Long wishListItemId, Long accountId, String currencyCode)
		throws Exception {

		CommerceWishListItem commerceWishListItem =
			_commerceWishListItemService.getCommerceWishListItem(
				wishListItemId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				commerceWishListItem.getGroupId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException();
		}

		CommerceContext commerceContext = _commerceContextFactory.create(
			AccountUtil.getAccountId(
				contextCompany.getCompanyId(), commerceChannel.getGroupId(),
				contextUser.getUserId(), _accountEntryLocalService,
				_accountEntryService, accountId, _commerceAccountHelper, null),
			commerceChannel.getGroupId(), currencyCode, 0,
			commerceChannel.getCompanyId());

		return _toWishListItem(commerceWishListItem, commerceContext);
	}

	@NestedField(parentClass = WishList.class, value = "wishListItems")
	@Override
	public Page<WishListItem> getWishlistWishListWishListItemsPage(
			@NestedFieldId("id") Long wishListId, Long accountId,
			String currecyCode, Pagination pagination)
		throws Exception {

		CommerceWishList commerceWishList =
			_commerceWishListService.getCommerceWishList(wishListId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				commerceWishList.getGroupId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException();
		}

		CommerceContext commerceContext = _commerceContextFactory.create(
			AccountUtil.getAccountId(
				contextCompany.getCompanyId(), commerceChannel.getGroupId(),
				contextUser.getUserId(), _accountEntryLocalService,
				_accountEntryService, accountId, _commerceAccountHelper, null),
			commerceChannel.getGroupId(), currecyCode, 0,
			commerceChannel.getCompanyId());

		return Page.of(
			transform(
				_commerceWishListItemService.getCommerceWishListItems(
					wishListId, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				commerceWishListItem -> _toWishListItem(
					commerceWishListItem, commerceContext)),
			pagination,
			_commerceWishListItemService.getCommerceWishListItemsCount(
				wishListId));
	}

	@Override
	public WishListItem postWishlistWishListWishListItem(
			Long wishListId, Long accountId, WishListItem wishListItem)
		throws Exception {

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			GetterUtil.getLong(wishListItem.getSkuId()));

		String cpInstanceUuid = StringPool.BLANK;

		if (cpInstance != null) {
			cpInstanceUuid = cpInstance.getCPInstanceUuid();
		}

		CommerceWishList commerceWishList =
			_commerceWishListService.getCommerceWishList(wishListId);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				commerceWishList.getGroupId());

		if (commerceChannel == null) {
			throw new NoSuchChannelException();
		}

		accountId = AccountUtil.getAccountId(
			contextCompany.getCompanyId(), commerceChannel.getGroupId(),
			contextUser.getUserId(), _accountEntryLocalService,
			_accountEntryService, accountId, _commerceAccountHelper, null);

		CommerceWishListItem commerceWishListItem =
			_commerceWishListItemService.addOrUpdateCommerceWishListItem(
				accountId, wishListId, cpInstanceUuid,
				wishListItem.getProductId(), wishListItem.toString());

		return _toWishListItem(
			commerceWishListItem,
			_commerceContextFactory.create(
				accountId, commerceChannel.getGroupId(), null, 0,
				commerceChannel.getCompanyId()));
	}

	private WishListItem _toWishListItem(
			CommerceWishListItem commerceWishListItem,
			CommerceContext commerceContext)
		throws Exception {

		return _wishListItemDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				commerceWishListItem.getCommerceWishListItemId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			commerceContext);
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
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListService _commerceWishListService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.WishListItemDTOConverter)"
	)
	private DTOConverter<CommerceContext, WishListItem>
		_wishListItemDTOConverter;

}