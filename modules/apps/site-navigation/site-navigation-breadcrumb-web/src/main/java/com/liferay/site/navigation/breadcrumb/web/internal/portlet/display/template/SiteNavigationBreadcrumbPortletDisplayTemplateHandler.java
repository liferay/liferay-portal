/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.breadcrumb.web.internal.portlet.display.template;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.site.navigation.breadcrumb.web.internal.configuration.SiteNavigationBreadcrumbWebTemplateConfiguration;
import com.liferay.site.navigation.breadcrumb.web.internal.constants.SiteNavigationBreadcrumbPortletKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Manuel Navarro
 */
@Component(
	configurationPid = "com.liferay.site.navigation.breadcrumb.web.internal.configuration.SiteNavigationBreadcrumbWebTemplateConfiguration",
	property = "jakarta.portlet.name=" + SiteNavigationBreadcrumbPortletKeys.SITE_NAVIGATION_BREADCRUMB,
	service = TemplateHandler.class
)
public class SiteNavigationBreadcrumbPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return BreadcrumbEntry.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		return HashMapBuilder.<String, Object>put(
			"breadcrumbUtil", BreadcrumbUtil.class
		).build();
	}

	@Override
	public String getDefaultTemplateKey() {
		return _siteNavigationBreadcrumbWebTemplateConfiguration.
			ddmTemplateKeyDefault();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			SiteNavigationBreadcrumbPortletKeys.SITE_NAVIGATION_BREADCRUMB,
			locale);

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return SiteNavigationBreadcrumbPortletKeys.SITE_NAVIGATION_BREADCRUMB;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup breadcrumbUtilTemplateVariableGroup =
			new TemplateVariableGroup(
				"breadcrumb-util", getRestrictedVariables(language));

		breadcrumbUtilTemplateVariableGroup.addVariable(
			"breadcrumb-util", BreadcrumbUtil.class, "breadcrumbUtil");

		templateVariableGroups.put(
			"breadcrumb-util", breadcrumbUtilTemplateVariableGroup);

		TemplateVariableGroup fieldsTemplateVariableGroup =
			templateVariableGroups.get("fields");

		fieldsTemplateVariableGroup.addCollectionVariable(
			"breadcrumb-entries", List.class,
			PortletDisplayTemplateConstants.ENTRIES, "breadcrumb-entry",
			BreadcrumbEntry.class, "curEntry", "getTitle()");
		fieldsTemplateVariableGroup.addVariable(
			"breadcrumb-entry", BreadcrumbEntry.class,
			PortletDisplayTemplateConstants.ENTRY, "getTitle()");

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_siteNavigationBreadcrumbWebTemplateConfiguration =
			ConfigurableUtil.createConfigurable(
				SiteNavigationBreadcrumbWebTemplateConfiguration.class,
				properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/site/navigation/breadcrumb/web/portlet/display" +
			"/template/dependencies/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private volatile SiteNavigationBreadcrumbWebTemplateConfiguration
		_siteNavigationBreadcrumbWebTemplateConfiguration;

}