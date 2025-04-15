/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.product.navigation.control.menu;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.sites.kernel.util.Sites;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.TOOLS,
		"product.navigation.control.menu.entry.order:Integer=300"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class InformationMessagesProductNavigationControlMenuEntry
	extends BaseJSPProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	public static final String INFORMATION_MESSAGES_LINKED_LAYOUT =
		"INFORMATION_MESSAGES_LINKED_LAYOUT";

	public static final String INFORMATION_MESSAGES_MODIFIED_LAYOUT =
		"INFORMATION_MESSAGES_MODIFIED_LAYOUT";

	@Override
	public String getIconJspPath() {
		return "/dynamic_include/information_messages.jsp";
	}

	public boolean hasUpdateLayoutPermission(ThemeDisplay themeDisplay)
		throws PortalException {

		return LayoutPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			ActionKeys.UPDATE);
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			httpServletRequest.setAttribute(
				INFORMATION_MESSAGES_LINKED_LAYOUT,
				_isLinkedLayout(themeDisplay));
			httpServletRequest.setAttribute(
				INFORMATION_MESSAGES_MODIFIED_LAYOUT,
				_isModifiedLayout(themeDisplay));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return super.includeIcon(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeAssetDisplay() || layout.isTypeControlPanel()) {
			return false;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());
		}

		int layoutType = -1;

		if (layoutPageTemplateEntry != null) {
			layoutType = layoutPageTemplateEntry.getType();
		}

		if ((layoutType == LayoutPageTemplateEntryTypeConstants.BASIC) ||
			(layoutType ==
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return false;
		}

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByPlid(layout.getClassPK());

		if ((layoutUtilityPageEntry != null) ||
			(!_isLinkedLayout(themeDisplay) &&
			 !_isModifiedLayout(themeDisplay))) {

			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private boolean _isLinkedLayout(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		if ((layout instanceof VirtualLayout) || !layout.isLayoutUpdateable() ||
			(layout.isLayoutPrototypeLinkActive() &&
			 !group.hasStagingGroup())) {

			return LayoutPermissionUtil.containsWithoutViewableGroup(
				themeDisplay.getPermissionChecker(), layout, false,
				ActionKeys.UPDATE);
		}

		return false;
	}

	private boolean _isModifiedLayout(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		LayoutSet layoutSet = layout.getLayoutSet();

		if (!layoutSet.isLayoutSetPrototypeLinkActive() ||
			!_sites.isLayoutModifiedSinceLastMerge(layout) ||
			!hasUpdateLayoutPermission(themeDisplay)) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InformationMessagesProductNavigationControlMenuEntry.class);

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.layout.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private Sites _sites;

}