/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.model.WishList;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.WISH_LISTS,
	service = FDSDataProvider.class
)
public class CommerceWishListFDSDataProvider
	implements FDSDataProvider<WishList> {

	@Override
	public List<WishList> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateFormat = FastDateFormatFactoryUtil.getDate(
			DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return TransformUtil.transform(
			_commerceWishListService.getCommerceWishLists(
				themeDisplay.getScopeGroupId(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), null),
			commerceWishList -> new WishList(
				commerceWishList.getUserName(),
				dateFormat.format(commerceWishList.getCreateDate()),
				_commerceWishListItemService.getCommerceWishListItemsCount(
					commerceWishList.getCommerceWishListId()),
				commerceWishList.getName(),
				commerceWishList.getCommerceWishListId()));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _commerceWishListService.getCommerceWishListsCount(
			themeDisplay.getScopeGroupId());
	}

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListService _commerceWishListService;

}