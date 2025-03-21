/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.friendly.url.provider.FriendlyURLSeparatorProvider;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Gianmarco Brunialti Masera
 */
public class CommerceOrderInfoItemUtil {

	public static CommerceOrder getCommerceOrder(
		CommerceOrderService commerceOrderService,
		HttpServletRequest httpServletRequest) {

		CommerceOrder commerceOrder = null;

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (infoItemReference != null) {
			try {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)
						infoItemReference.getInfoItemIdentifier();

				commerceOrder = commerceOrderService.getCommerceOrder(
					classPKInfoItemIdentifier.getClassPK());
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return null;
			}
		}

		if (commerceOrder == null) {
			Object infoItem = httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

			if (!(infoItem instanceof CommerceOrder)) {
				return null;
			}

			commerceOrder = (CommerceOrder)infoItem;
		}

		return commerceOrder;
	}

	public static String getCommerceOrderFriendlyURL(
		FriendlyURLSeparatorProvider friendlyURLSeparatorProvider,
		HttpServletRequest httpServletRequest) {

		if (friendlyURLSeparatorProvider == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String friendlyURLSeparator =
			friendlyURLSeparatorProvider.getFriendlyURLSeparator(
				themeDisplay.getCompanyId(), CommerceOrder.class.getName());

		if (Validator.isNull(friendlyURLSeparator)) {
			friendlyURLSeparator =
				FriendlyURLResolverConstants.URL_SEPARATOR_COMMERCE_ORDER;
		}

		return _getSiteDefaultURL(themeDisplay) + friendlyURLSeparator;
	}

	private static String _getSiteDefaultURL(ThemeDisplay themeDisplay) {
		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		return HtmlUtil.escape(
			group.getDisplayURL(themeDisplay, layout.isPrivateLayout()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderInfoItemUtil.class);

}