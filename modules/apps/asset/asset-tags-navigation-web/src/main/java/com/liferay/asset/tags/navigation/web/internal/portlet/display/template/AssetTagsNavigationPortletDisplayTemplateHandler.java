/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.navigation.web.internal.portlet.display.template;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.asset.tags.navigation.constants.AssetTagsNavigationPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(
	property = "jakarta.portlet.name=" + AssetTagsNavigationPortletKeys.ASSET_TAGS_NAVIGATION,
	service = TemplateHandler.class
)
public class AssetTagsNavigationPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return AssetTag.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		return language.format(
			locale, "x-template",
			portal.getPortletTitle(
				AssetTagsNavigationPortletKeys.ASSET_TAGS_NAVIGATION, locale),
			false);
	}

	@Override
	public String getResourceName() {
		return AssetTagsNavigationPortletKeys.ASSET_TAGS_NAVIGATION;
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

		templateVariableGroup.addCollectionVariable(
			"tags", List.class, PortletDisplayTemplateConstants.ENTRIES, "tag",
			AssetTag.class, "curTag", "name");

		TemplateVariableGroup assetServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"tag-services", getRestrictedVariables(language));

		assetServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		assetServicesTemplateVariableGroup.addServiceLocatorVariables(
			AssetTagLocalService.class, AssetTagService.class);

		templateVariableGroups.put(
			assetServicesTemplateVariableGroup.getLabel(),
			assetServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/asset/tags/navigation/web/portlet/display" +
			"/template/dependencies/portlet-display-templates.xml";
	}

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

}