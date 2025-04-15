/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.internal.helper;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.applications.menu.configuration.ApplicationsMenuInstanceConfiguration;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;
import com.liferay.product.navigation.product.menu.helper.ProductNavigationProductMenuHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ProductNavigationProductMenuHelper.class)
public class ProductNavigationProductMenuHelperImpl
	implements ProductNavigationProductMenuHelper {

	@Override
	public boolean isShowProductMenu(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn() ||
			!_productNavigationControlMenuManager.isShowControlMenu(
				httpServletRequest)) {

			return false;
		}

		User user = themeDisplay.getUser();

		if (!themeDisplay.isImpersonated() && !user.isSetupComplete()) {
			return false;
		}

		boolean enableApplicationsMenu = _isEnableApplicationsMenu(
			themeDisplay.getCompanyId());

		if (enableApplicationsMenu && _isApplicationsMenuApp(themeDisplay)) {
			return false;
		}

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (enableApplicationsMenu && scopeGroup.isDepot()) {
			return false;
		}

		List<PanelCategory> childPanelCategories =
			PanelCategoryRegistryUtil.getChildPanelCategories(
				PanelCategoryKeys.ROOT, themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup());

		if (!childPanelCategories.isEmpty()) {
			return true;
		}

		if (!_isEnableApplicationsMenu(themeDisplay.getCompanyId())) {
			childPanelCategories =
				PanelCategoryRegistryUtil.getChildPanelCategories(
					PanelCategoryKeys.APPLICATIONS_MENU,
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroup());

			if (!childPanelCategories.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private boolean _isApplicationsMenuApp(ThemeDisplay themeDisplay) {
		if (Validator.isNull(themeDisplay.getPpid())) {
			return false;
		}

		PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
			_panelAppRegistry);

		if (!panelCategoryHelper.isApplicationsMenuApp(
				themeDisplay.getPpid())) {

			return false;
		}

		Layout layout = themeDisplay.getLayout();

		if ((layout != null) && !layout.isTypeControlPanel()) {
			return false;
		}

		return true;
	}

	private boolean _isEnableApplicationsMenu(long companyId) {
		try {
			ApplicationsMenuInstanceConfiguration
				applicationsMenuInstanceConfiguration =
					_configurationProvider.getCompanyConfiguration(
						ApplicationsMenuInstanceConfiguration.class, companyId);

			if (applicationsMenuInstanceConfiguration.
					enableApplicationsMenu()) {

				return true;
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get applications menu instance configuration",
					configurationException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductNavigationProductMenuHelperImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private ProductNavigationControlMenuManager
		_productNavigationControlMenuManager;

}