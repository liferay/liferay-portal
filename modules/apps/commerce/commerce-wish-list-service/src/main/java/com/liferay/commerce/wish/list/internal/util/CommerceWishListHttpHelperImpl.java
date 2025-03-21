/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.internal.util;

import com.liferay.commerce.wish.list.constants.CommerceWishListPortletKeys;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListLocalService;
import com.liferay.commerce.wish.list.util.CommerceWishListHttpHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(service = CommerceWishListHttpHelper.class)
public class CommerceWishListHttpHelperImpl
	implements CommerceWishListHttpHelper {

	@Override
	public PortletURL getCommerceWishListPortletURL(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = null;

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		String portletId =
			CommerceWishListPortletKeys.COMMERCE_WISH_LIST_CONTENT;

		long plid = _portal.getPlidFromPortletId(groupId, portletId);

		if (plid > 0) {
			portletURL = _portletURLFactory.create(
				httpServletRequest, portletId, plid,
				PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = _portletURLFactory.create(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);
		}

		return portletURL;
	}

	@Override
	public CommerceWishList getCurrentCommerceWishList(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		long groupId = _portal.getScopeGroupId(httpServletRequest);

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			user = _userLocalService.getGuestUser(
				_portal.getCompanyId(httpServletRequest));
		}

		String cookieName = _getCookieName(groupId);

		String guestUuid = CookiesManagerUtil.getCookieValue(
			cookieName, httpServletRequest);

		CommerceWishList commerceWishList =
			_commerceWishListLocalService.getDefaultCommerceWishList(
				user.getUserId(), groupId, guestUuid);

		if (commerceWishList == null) {
			return commerceWishList;
		}

		if (user.isGuestUser()) {
			if (Validator.isNull(guestUuid)) {
				Cookie cookie = new Cookie(
					cookieName, commerceWishList.getUuid());

				cookie.setMaxAge(CookiesConstants.MAX_AGE);

				CookiesManagerUtil.addCookie(
					CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
					httpServletRequest, httpServletResponse);
			}
		}
		else {
			if (Validator.isNotNull(guestUuid)) {
				CookiesManagerUtil.deleteCookies(
					CookiesManagerUtil.getDomain(httpServletRequest),
					httpServletRequest, httpServletResponse, cookieName);
			}
		}

		return commerceWishList;
	}

	@Override
	public int getCurrentCommerceWishListItemsCount(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		CommerceWishList commerceWishList = getCurrentCommerceWishList(
			httpServletRequest, httpServletResponse);

		if (commerceWishList == null) {
			return 0;
		}

		return _commerceWishListItemService.getCommerceWishListItemsCount(
			commerceWishList.getCommerceWishListId());
	}

	private String _getCookieName(long groupId) {
		return CommerceWishList.class.getName() + StringPool.POUND + groupId;
	}

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListLocalService _commerceWishListLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private UserLocalService _userLocalService;

}