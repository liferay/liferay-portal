/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemLocalService;
import com.liferay.commerce.product.type.virtual.web.internal.display.context.CommerceVirtualOrderItemEditDisplayContext;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceOrderItemVirtualSettingsScreenNavigationEntry
	extends CommerceOrderItemVirtualSettingsScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceOrderItem> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceOrderItem commerceOrderItem) {
		if (commerceOrderItem == null) {
			return false;
		}

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			_commerceVirtualOrderItemLocalService.
				fetchCommerceVirtualOrderItemByCommerceOrderItemId(
					commerceOrderItem.getCommerceOrderItemId());

		if (commerceVirtualOrderItem == null) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RenderRequest renderRequest =
				(RenderRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST);

			CommerceVirtualOrderItemEditDisplayContext
				commerceVirtualOrderItemEditDisplayContext =
					new CommerceVirtualOrderItemEditDisplayContext(
						_commerceOrderService, _commerceOrderItemService,
						_getCommerceVirtualOrderItem(httpServletRequest),
						_dlAppService, _itemSelector, renderRequest);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceVirtualOrderItemEditDisplayContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/commerce_order_item/virtual_settings.jsp");
	}

	private CommerceVirtualOrderItem _getCommerceVirtualOrderItem(
		HttpServletRequest httpServletRequest) {

		CommerceVirtualOrderItem commerceVirtualOrderItem = null;

		long commerceVirtualOrderItemId = ParamUtil.getLong(
			httpServletRequest, "commerceVirtualOrderItemId");

		if (commerceVirtualOrderItemId > 0) {
			commerceVirtualOrderItem =
				_commerceVirtualOrderItemLocalService.
					fetchCommerceVirtualOrderItem(commerceVirtualOrderItemId);
		}

		if (commerceVirtualOrderItem != null) {
			return commerceVirtualOrderItem;
		}

		long commerceOrderItemId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderItemId");

		if (commerceOrderItemId > 0) {
			commerceVirtualOrderItem =
				_commerceVirtualOrderItemLocalService.
					fetchCommerceVirtualOrderItemByCommerceOrderItemId(
						commerceOrderItemId);
		}

		return commerceVirtualOrderItem;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemVirtualSettingsScreenNavigationEntry.class);

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceVirtualOrderItemLocalService
		_commerceVirtualOrderItemLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.type.virtual.web)"
	)
	private ServletContext _servletContext;

}