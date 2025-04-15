/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierService;
import com.liferay.commerce.pricing.type.CommercePriceModifierTypeRegistry;
import com.liferay.commerce.pricing.web.internal.display.context.CommercePriceListDisplayContext;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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
public class CommercePriceModifierPricingClassesScreenNavigationEntry
	extends CommercePriceModifierPricingClassesScreenNavigationCategory
	implements ScreenNavigationEntry<CommercePriceModifier> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(
		User user, CommercePriceModifier commercePriceModifier) {

		if ((commercePriceModifier == null) ||
			!CommercePriceModifierConstants.TARGET_PRODUCT_GROUPS.equals(
				commercePriceModifier.getTarget())) {

			return false;
		}

		boolean hasPermission = false;

		try {
			hasPermission = _commercePriceListModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePriceModifier.getCommercePriceListId(),
				ActionKeys.UPDATE);
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
			CommercePriceListDisplayContext commercePriceListDisplayContext =
				new CommercePriceListDisplayContext(
					_commerceCatalogService, _commerceCurrencyLocalService,
					_commercePriceListModelResourcePermission,
					_commercePriceListService, _commercePriceModifierService,
					_commercePriceModifierTypeRegistry, httpServletRequest);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commercePriceListDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/commerce_price_lists/commerce_price_modifier" +
				"/pricing_classes.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceModifierPricingClassesScreenNavigationEntry.class);

	@Reference
	private CommerceCatalogService _commerceCatalogService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private CommercePriceListService _commercePriceListService;

	@Reference
	private CommercePriceModifierService _commercePriceModifierService;

	@Reference
	private CommercePriceModifierTypeRegistry
		_commercePriceModifierTypeRegistry;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.pricing.web)"
	)
	private ServletContext _servletContext;

}