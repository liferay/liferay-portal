/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.portlet.display.template;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.menu.web.internal.configuration.SiteNavigationMenuWebTemplateConfiguration;
import com.liferay.site.navigation.theme.SiteNavigationMenuNavItem;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juergen Kappler
 */
@Component(
	configurationPid = "com.liferay.site.navigation.menu.web.internal.configuration.SiteNavigationMenuWebTemplateConfiguration",
	property = "jakarta.portlet.name=" + SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
	service = TemplateHandler.class
)
public class SiteNavigationMenuPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return NavItem.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		return HashMapBuilder.<String, Object>put(
			"navItem", SiteNavigationMenuNavItem.class
		).build();
	}

	@Override
	public String getDefaultTemplateKey() {
		return _siteNavigationMenuWebTemplateConfiguration.
			ddmTemplateKeyDefault();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU, locale);

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup templateVariableGroup =
			templateVariableGroups.get("fields");

		templateVariableGroup.empty();

		templateVariableGroup.addVariable(
			"header-type", String.class, "headerType");
		templateVariableGroup.addVariable(
			"included-layouts", String.class, "includedLayouts");
		templateVariableGroup.addVariable(
			"nested-children", String.class, "nestedChildren");
		templateVariableGroup.addVariable(
			"root-layout-level", Integer.class, "rootLayoutLevel");
		templateVariableGroup.addVariable(
			"root-layout-type", String.class, "rootLayoutType");
		templateVariableGroup.addCollectionVariable(
			"navigation-items", List.class,
			PortletDisplayTemplateConstants.ENTRIES, "navigation-item",
			NavItem.class, "navigationEntry", "getName()");

		templateVariableGroups.put(
			"navigation-util", _getUtilTemplateVariableGroup());

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_siteNavigationMenuWebTemplateConfiguration =
			ConfigurableUtil.createConfigurable(
				SiteNavigationMenuWebTemplateConfiguration.class, properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/site/navigation/menu/web/portlet/display/template" +
			"/dependencies/portlet-display-templates.xml";
	}

	private TemplateVariableGroup _getUtilTemplateVariableGroup() {
		TemplateVariableGroup templateVariableGroup = new TemplateVariableGroup(
			"navigation-util");

		templateVariableGroup.addVariable(
			"navigation-item", NavItem.class, "navItem");

		return templateVariableGroup;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private volatile SiteNavigationMenuWebTemplateConfiguration
		_siteNavigationMenuWebTemplateConfiguration;

}