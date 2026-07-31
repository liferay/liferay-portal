/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.internal.manager;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuCategory;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuCategoryRegistry;
import com.liferay.product.navigation.control.menu.util.ProductNavigationControlMenuEntryRegistry;
import com.liferay.site.configuration.MenuAccessConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ProductNavigationControlMenuManager.class)
public class ProductNavigationControlMenuManagerImpl
	implements ProductNavigationControlMenuManager {

	@Override
	public boolean isShowControlMenu(HttpServletRequest httpServletRequest) {
		if (Objects.equals(
				Constants.PREVIEW,
				ParamUtil.getString(
					httpServletRequest, "p_l_mode", Constants.VIEW)) ||
			_isLockedLayoutView(httpServletRequest) ||
			_isGuestUser(httpServletRequest)) {

			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean hasRelevantProductNavigationControlMenuEntries =
			_hasRelevantProductNavigationControlMenuEntries(httpServletRequest);

		Group group = themeDisplay.getScopeGroup();
		Layout layout = themeDisplay.getLayout();

		if ((!group.isCMS() && !group.isSite() &&
			 !Objects.equals(GroupConstants.DSR, group.getGroupKey())) ||
			layout.isDraftLayout() || layout.isTypeControlPanel()) {

			return hasRelevantProductNavigationControlMenuEntries;
		}

		try {
			MenuAccessConfiguration menuAccessConfiguration =
				_configurationProvider.getGroupConfiguration(
					MenuAccessConfiguration.class, group.getCompanyId(),
					group.getGroupId());

			if ((menuAccessConfiguration != null) &&
				menuAccessConfiguration.showControlMenuByRole()) {

				String[] accessToControlMenuRoleIds =
					menuAccessConfiguration.accessToControlMenuRoleIds();

				User user = themeDisplay.getUser();

				for (Role role : user.getAllRoles()) {
					if (ArrayUtil.contains(
							accessToControlMenuRoleIds,
							String.valueOf(role.getRoleId()))) {

						return hasRelevantProductNavigationControlMenuEntries;
					}
				}

				return false;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return hasRelevantProductNavigationControlMenuEntries;
	}

	private boolean _hasRelevantProductNavigationControlMenuEntries(
		HttpServletRequest httpServletRequest) {

		List<ProductNavigationControlMenuCategory>
			productNavigationControlMenuCategories =
				_productNavigationControlMenuCategoryRegistry.
					getProductNavigationControlMenuCategories(
						ProductNavigationControlMenuCategoryKeys.ROOT);

		for (ProductNavigationControlMenuCategory
				productNavigationControlMenuCategory :
					productNavigationControlMenuCategories) {

			List<ProductNavigationControlMenuEntry>
				productNavigationControlMenuEntries =
					_productNavigationControlMenuEntryRegistry.
						getProductNavigationControlMenuEntries(
							productNavigationControlMenuCategory,
							httpServletRequest);

			if (productNavigationControlMenuEntries.isEmpty()) {
				continue;
			}

			for (ProductNavigationControlMenuEntry
					productNavigationControlMenuEntry :
						productNavigationControlMenuEntries) {

				if (productNavigationControlMenuEntry.isRelevant(
						httpServletRequest)) {

					return true;
				}
			}
		}

		return false;
	}

	private boolean _isGuestUser(HttpServletRequest httpServletRequest) {
		try {
			User user = _portal.getUser(httpServletRequest);

			if (user == null) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				user = themeDisplay.getUser();
			}

			if ((user == null) || user.isGuestUser()) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to determine if user is guest user", exception);
			}
		}

		return false;
	}

	private boolean _isLockedLayoutView(HttpServletRequest httpServletRequest) {
		String mvcRenderCommandName =
			_portal.getPortletNamespace(LayoutAdminPortletKeys.GROUP_PAGES) +
				"mvcRenderCommandName";

		return Objects.equals(
			ParamUtil.getString(httpServletRequest, mvcRenderCommandName),
			"/layout_admin/locked_layout");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductNavigationControlMenuManagerImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private ProductNavigationControlMenuCategoryRegistry
		_productNavigationControlMenuCategoryRegistry;

	@Reference
	private ProductNavigationControlMenuEntryRegistry
		_productNavigationControlMenuEntryRegistry;

}