/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.product.navigation.control.menu;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tamas Molnar
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.TOOLS,
		"product.navigation.control.menu.entry.order:Integer=300"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class PropagationMessageProductNavigationControlMenuEntry
	extends BaseJSPProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public String getIconJspPath() {
		return "/dynamic_include/propagation_message.jsp";
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {
		
		return false;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.set.prototype.web)"
	)
	private ServletContext _servletContext;

}