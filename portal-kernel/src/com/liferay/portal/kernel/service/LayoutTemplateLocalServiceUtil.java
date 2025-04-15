/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import java.util.List;
import java.util.Set;

/**
 * Provides the local service utility for LayoutTemplate. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutTemplateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutTemplateLocalService
 * @generated
 */
public class LayoutTemplateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutTemplateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static String getContent(
		String layoutTemplateId, boolean standard, String themeId) {

		return getService().getContent(layoutTemplateId, standard, themeId);
	}

	public static String getLangType(
		String layoutTemplateId, boolean standard, String themeId) {

		return getService().getLangType(layoutTemplateId, standard, themeId);
	}

	public static com.liferay.portal.kernel.model.LayoutTemplate
		getLayoutTemplate(
			String layoutTemplateId, boolean standard, String themeId) {

		return getService().getLayoutTemplate(
			layoutTemplateId, standard, themeId);
	}

	public static List<com.liferay.portal.kernel.model.LayoutTemplate>
		getLayoutTemplates() {

		return getService().getLayoutTemplates();
	}

	public static List<com.liferay.portal.kernel.model.LayoutTemplate>
		getLayoutTemplates(String themeId) {

		return getService().getLayoutTemplates(themeId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<com.liferay.portal.kernel.model.LayoutTemplate> init(
		jakarta.servlet.ServletContext servletContext, String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return getService().init(servletContext, xmls, pluginPackage);
	}

	public static List<com.liferay.portal.kernel.model.LayoutTemplate> init(
		String servletContextName, jakarta.servlet.ServletContext servletContext,
		String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return getService().init(
			servletContextName, servletContext, xmls, pluginPackage);
	}

	public static void readLayoutTemplate(
		String servletContextName, jakarta.servlet.ServletContext servletContext,
		Set<com.liferay.portal.kernel.model.LayoutTemplate> layoutTemplates,
		com.liferay.portal.kernel.xml.Element element, boolean standard,
		String themeId,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		getService().readLayoutTemplate(
			servletContextName, servletContext, layoutTemplates, element,
			standard, themeId, pluginPackage);
	}

	public static void uninstallLayoutTemplate(
		String layoutTemplateId, boolean standard) {

		getService().uninstallLayoutTemplate(layoutTemplateId, standard);
	}

	public static void uninstallLayoutTemplates(String themeId) {
		getService().uninstallLayoutTemplates(themeId);
	}

	public static LayoutTemplateLocalService getService() {
		return _service;
	}

	public static void setService(LayoutTemplateLocalService service) {
		_service = service;
	}

	private static volatile LayoutTemplateLocalService _service;

}