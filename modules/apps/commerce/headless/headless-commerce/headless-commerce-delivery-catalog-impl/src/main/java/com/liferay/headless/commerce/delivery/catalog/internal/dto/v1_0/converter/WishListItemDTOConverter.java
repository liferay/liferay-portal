/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishListItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mahmoud Azzam
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishListItem"
	},
	service = DTOConverter.class
)
public class WishListItemDTOConverter
	implements DTOConverter<CommerceContext, WishListItem> {

	@Override
	public String getContentType() {
		return WishList.class.getSimpleName();
	}

	@Override
	public WishListItem toDTO(
			DTOConverterContext dtoConverterContext,
			CommerceContext commerceContext)
		throws Exception {

		CommerceWishListItem commerceWishListItem =
			_commerceWishListItemService.getCommerceWishListItem(
				(Long)dtoConverterContext.getId());

		return new WishListItem() {
			{
				setFinalPrice(
					() -> {
						CPInstance cpInstance =
							commerceWishListItem.fetchCPInstance();

						if (cpInstance == null) {
							return null;
						}

						CommerceMoney finalPriceCommerceMoney =
							_commerceProductPriceCalculation.getFinalPrice(
								cpInstance.getCPInstanceId(), BigDecimal.ONE,
								StringPool.BLANK, commerceContext);

						if (finalPriceCommerceMoney.isEmpty()) {
							return null;
						}

						return finalPriceCommerceMoney.format(
							dtoConverterContext.getLocale());
					});
				setFriendlyURL(
					() -> {
						CPDefinition cpDefinition =
							commerceWishListItem.getCPDefinition();

						FriendlyURLEntry friendlyURLEntry =
							_friendlyURLEntryLocalService.
								getMainFriendlyURLEntry(
									_portal.getClassNameId(CProduct.class),
									cpDefinition.getCProductId());

						String productURLSeparator =
							_cpFriendlyURL.getProductURLSeparator(
								cpDefinition.getCompanyId());

						String urlTitle = friendlyURLEntry.getUrlTitle(
							_language.getLanguageId(
								dtoConverterContext.getLocale()));

						return productURLSeparator + urlTitle;
					});
				setIcon(
					() -> {
						CPDefinition cpDefinition =
							commerceWishListItem.getCPDefinition();

						AccountEntry accountEntry =
							commerceContext.getAccountEntry();

						return cpDefinition.getDefaultImageThumbnailSrc(
							accountEntry.getAccountEntryId());
					});
				setId(commerceWishListItem::getCommerceWishListItemId);
				setProductId(commerceWishListItem::getCProductId);
				setProductName(
					() -> {
						CPDefinition cpDefinition =
							commerceWishListItem.getCPDefinition();

						return cpDefinition.getName(
							_language.getLanguageId(
								dtoConverterContext.getLocale()));
					});
				setSkuId(
					() -> {
						CPInstance cpInstance =
							commerceWishListItem.fetchCPInstance();

						if (cpInstance == null) {
							return null;
						}

						return cpInstance.getCPInstanceId();
					});
			}
		};
	}

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}