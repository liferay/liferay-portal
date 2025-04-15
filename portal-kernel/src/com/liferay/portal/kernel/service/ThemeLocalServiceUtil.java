/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import java.util.List;

/**
 * Provides the local service utility for Theme. This utility wraps
 * <code>com.liferay.portal.service.impl.ThemeLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ThemeLocalService
 * @generated
 */
public class ThemeLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.ThemeLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.portal.kernel.model.ColorScheme fetchColorScheme(
		long companyId, String themeId, String colorSchemeId) {

		return getService().fetchColorScheme(companyId, themeId, colorSchemeId);
	}

	public static com.liferay.portal.kernel.model.PortletDecorator
		fetchPortletDecorator(
			long companyId, String themeId, String colorSchemeId) {

		return getService().fetchPortletDecorator(
			companyId, themeId, colorSchemeId);
	}

	public static com.liferay.portal.kernel.model.Theme fetchTheme(
		long companyId, String themeId) {

		return getService().fetchTheme(companyId, themeId);
	}

	public static com.liferay.portal.kernel.model.ColorScheme getColorScheme(
		long companyId, String themeId, String colorSchemeId) {

		return getService().getColorScheme(companyId, themeId, colorSchemeId);
	}

	public static List<com.liferay.portal.kernel.model.Theme>
		getControlPanelThemes(long companyId, long userId) {

		return getService().getControlPanelThemes(companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<com.liferay.portal.kernel.model.Theme> getPageThemes(
		long companyId, long groupId, long userId) {

		return getService().getPageThemes(companyId, groupId, userId);
	}

	public static com.liferay.portal.kernel.model.PortletDecorator
		getPortletDecorator(
			long companyId, String themeId, String portletDecoratorId) {

		return getService().getPortletDecorator(
			companyId, themeId, portletDecoratorId);
	}

	public static com.liferay.portal.kernel.model.Theme getTheme(
		long companyId, String themeId) {

		return getService().getTheme(companyId, themeId);
	}

	public static List<com.liferay.portal.kernel.model.Theme> getThemes(
		long companyId) {

		return getService().getThemes(companyId);
	}

	public static List<com.liferay.portal.kernel.model.Theme> getWARThemes() {
		return getService().getWARThemes();
	}

	public static List<com.liferay.portal.kernel.model.Theme> init(
		jakarta.servlet.ServletContext servletContext, String themesPath,
		boolean loadFromServletContext, String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return getService().init(
			servletContext, themesPath, loadFromServletContext, xmls,
			pluginPackage);
	}

	public static List<com.liferay.portal.kernel.model.Theme> init(
		String servletContextName, jakarta.servlet.ServletContext servletContext,
		String themesPath, boolean loadFromServletContext, String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return getService().init(
			servletContextName, servletContext, themesPath,
			loadFromServletContext, xmls, pluginPackage);
	}

	public static void uninstallThemes(
		List<com.liferay.portal.kernel.model.Theme> themes) {

		getService().uninstallThemes(themes);
	}

	public static ThemeLocalService getService() {
		return _service;
	}

	public static void setService(ThemeLocalService service) {
		_service = service;
	}

	private static volatile ThemeLocalService _service;

}