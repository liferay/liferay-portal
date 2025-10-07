/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.servlet.taglib;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.analytics.web.internal.configuration.ProductAnalyticsConfiguration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(service = DynamicInclude.class)
public class ProductAnalyticsBottomJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-51356") ||
			LiferayWindowState.isPopUp(httpServletRequest)) {

			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (group.isStagingGroup()) {
			return;
		}

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(themeDisplay.getUser());

		if (!permissionChecker.isGroupAdmin(group.getGroupId())) {
			return;
		}

		try {
			ProductAnalyticsConfiguration productAnalyticsConfiguration =
				_getProductAnalyticsConfiguration(themeDisplay);

			if (!productAnalyticsConfiguration.enabled()) {
				return;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/product_analytics_banner/view.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private ProductAnalyticsConfiguration _getProductAnalyticsConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
			themeDisplay.getServerName());

		if (layoutSet != null) {
			Group group = layoutSet.getGroup();

			return _configurationProvider.getGroupConfiguration(
				ProductAnalyticsConfiguration.class, group.getGroupId());
		}

		return _configurationProvider.getCompanyConfiguration(
			ProductAnalyticsConfiguration.class, themeDisplay.getCompanyId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductAnalyticsBottomJSPDynamicInclude.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.product.analytics.web)"
	)
	private ServletContext _servletContext;

}