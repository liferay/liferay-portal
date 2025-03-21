/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.theme.contributor.internal.template;

import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.product.navigation.product.menu.helper.ProductNavigationProductMenuHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class ProductMenuTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		if (!_productNavigationProductMenuHelper.isShowProductMenu(
				httpServletRequest)) {

			return;
		}

		String cssClass = GetterUtil.getString(
			contextObjects.get("bodyCssClass"));

		String productMenuState = SessionClicks.get(
			httpServletRequest,
			"com.liferay.product.navigation.product.menu.web_productMenuState",
			"closed");

		if (Objects.equals(productMenuState, "open")) {
			cssClass += " open product-menu-open";
		}

		contextObjects.put("bodyCssClass", cssClass);

		contextObjects.put("liferay_product_menu_state", productMenuState);
	}

	@Reference
	private ProductNavigationProductMenuHelper
		_productNavigationProductMenuHelper;

}