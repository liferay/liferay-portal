/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.product.asset.categories.web.internal.display.context.CategoryCPDisplayLayoutDisplayContext;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDisplayLayoutService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceChannelCategoryDisplayLayoutsScreenNavigationEntry
	extends CommerceChannelCategoryDisplayLayoutsScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceChannel> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceChannel commerceChannel) {
		try {
			if (!GroupPermissionUtil.contains(
					PermissionThreadLocal.getPermissionChecker(),
					commerceChannel.getSiteGroupId(), ActionKeys.ADD_LAYOUT)) {

				return false;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}

		return CommerceChannelConstants.CHANNEL_TYPE_SITE.equals(
			commerceChannel.getType());
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CategoryCPDisplayLayoutDisplayContext
			categoryCPDisplayLayoutDisplayContext =
				new CategoryCPDisplayLayoutDisplayContext(
					_actionHelper, _assetCategoryLocalService,
					httpServletRequest, _commerceChannelLocalService,
					_cpDisplayLayoutService, _groupLocalService, _itemSelector,
					_layoutLocalService);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			categoryCPDisplayLayoutDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/display_layout/view.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceChannelCategoryDisplayLayoutsScreenNavigationEntry.class);

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDisplayLayoutService _cpDisplayLayoutService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.asset.categories.web)"
	)
	private ServletContext _servletContext;

}