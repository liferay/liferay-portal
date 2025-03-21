/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

/**
 * Provides a wrapper for {@link LayoutTemplateLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutTemplateLocalService
 * @generated
 */
public class LayoutTemplateLocalServiceWrapper
	implements LayoutTemplateLocalService,
			   ServiceWrapper<LayoutTemplateLocalService> {

	public LayoutTemplateLocalServiceWrapper() {
		this(null);
	}

	public LayoutTemplateLocalServiceWrapper(
		LayoutTemplateLocalService layoutTemplateLocalService) {

		_layoutTemplateLocalService = layoutTemplateLocalService;
	}

	@Override
	public java.lang.String getContent(
		java.lang.String layoutTemplateId, boolean standard,
		java.lang.String themeId) {

		return _layoutTemplateLocalService.getContent(
			layoutTemplateId, standard, themeId);
	}

	@Override
	public java.lang.String getLangType(
		java.lang.String layoutTemplateId, boolean standard,
		java.lang.String themeId) {

		return _layoutTemplateLocalService.getLangType(
			layoutTemplateId, standard, themeId);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutTemplate getLayoutTemplate(
		java.lang.String layoutTemplateId, boolean standard,
		java.lang.String themeId) {

		return _layoutTemplateLocalService.getLayoutTemplate(
			layoutTemplateId, standard, themeId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutTemplate>
		getLayoutTemplates() {

		return _layoutTemplateLocalService.getLayoutTemplates();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutTemplate>
		getLayoutTemplates(java.lang.String themeId) {

		return _layoutTemplateLocalService.getLayoutTemplates(themeId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _layoutTemplateLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutTemplate> init(
		jakarta.servlet.ServletContext servletContext, java.lang.String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return _layoutTemplateLocalService.init(
			servletContext, xmls, pluginPackage);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutTemplate> init(
		java.lang.String servletContextName,
		jakarta.servlet.ServletContext servletContext, java.lang.String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return _layoutTemplateLocalService.init(
			servletContextName, servletContext, xmls, pluginPackage);
	}

	@Override
	public void readLayoutTemplate(
		java.lang.String servletContextName,
		jakarta.servlet.ServletContext servletContext,
		java.util.Set<com.liferay.portal.kernel.model.LayoutTemplate>
			layoutTemplates,
		com.liferay.portal.kernel.xml.Element element, boolean standard,
		java.lang.String themeId,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		_layoutTemplateLocalService.readLayoutTemplate(
			servletContextName, servletContext, layoutTemplates, element,
			standard, themeId, pluginPackage);
	}

	@Override
	public void uninstallLayoutTemplate(
		java.lang.String layoutTemplateId, boolean standard) {

		_layoutTemplateLocalService.uninstallLayoutTemplate(
			layoutTemplateId, standard);
	}

	@Override
	public void uninstallLayoutTemplates(java.lang.String themeId) {
		_layoutTemplateLocalService.uninstallLayoutTemplates(themeId);
	}

	@Override
	public LayoutTemplateLocalService getWrappedService() {
		return _layoutTemplateLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutTemplateLocalService layoutTemplateLocalService) {

		_layoutTemplateLocalService = layoutTemplateLocalService;
	}

	private LayoutTemplateLocalService _layoutTemplateLocalService;

}