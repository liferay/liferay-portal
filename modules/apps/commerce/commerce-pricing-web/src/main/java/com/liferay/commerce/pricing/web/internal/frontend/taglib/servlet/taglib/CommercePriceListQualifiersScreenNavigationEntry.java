/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelService;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelService;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.web.internal.display.context.CommercePriceListQualifiersDisplayContext;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
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
public class CommercePriceListQualifiersScreenNavigationEntry
	extends CommercePriceListQualifiersScreenNavigationCategory
	implements ScreenNavigationEntry<CommercePriceList> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommercePriceList commercePriceList) {
		if ((commercePriceList == null) ||
			commercePriceList.isCatalogBasePriceList()) {

			return false;
		}

		boolean hasPermission = false;

		try {
			hasPermission = _commercePriceListModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePriceList.getCommercePriceListId(), ActionKeys.UPDATE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return hasPermission;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			CommercePriceListQualifiersDisplayContext
				commercePriceListQualifiersDisplayContext =
					new CommercePriceListQualifiersDisplayContext(
						_commerceCatalogService,
						_commercePriceListAccountRelService,
						_commercePriceListChannelRelService,
						_commercePriceListCommerceAccountGroupRelService,
						_commercePriceListOrderTypeRelService,
						_commercePriceListModelResourcePermission,
						_commercePriceListService, _portal, httpServletRequest);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commercePriceListQualifiersDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/commerce_price_lists/qualifiers.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListQualifiersScreenNavigationEntry.class);

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommercePriceListAccountRelService
		_commercePriceListAccountRelService;

	@Reference
	private CommercePriceListChannelRelService
		_commercePriceListChannelRelService;

	@Reference
	private CommercePriceListCommerceAccountGroupRelService
		_commercePriceListCommerceAccountGroupRelService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private CommercePriceListOrderTypeRelService
		_commercePriceListOrderTypeRelService;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.pricing.web)"
	)
	private ServletContext _servletContext;

}