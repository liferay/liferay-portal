/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.display.template;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
	service = TemplateHandler.class
)
public class AssetPublisherPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return AssetEntry.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		return HashMapBuilder.<String, Object>put(
			"assetPublisherHelper", assetPublisherHelper
		).build();
	}

	@Override
	public String getName(Locale locale) {
		return language.format(
			locale, "x-template",
			portal.getPortletTitle(
				AssetPublisherPortletKeys.ASSET_PUBLISHER, locale),
			false);
	}

	@Override
	public String getResourceName() {
		return AssetPublisherPortletKeys.ASSET_PUBLISHER;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		String[] restrictedVariables = getRestrictedVariables(language);

		TemplateVariableGroup assetPublisherUtilTemplateVariableGroup =
			new TemplateVariableGroup(
				"asset-publisher-util", restrictedVariables);

		assetPublisherUtilTemplateVariableGroup.addVariable(
			"asset-publisher-helper", AssetPublisherHelper.class,
			"assetPublisherHelper");

		templateVariableGroups.put(
			"asset-publisher-util", assetPublisherUtilTemplateVariableGroup);

		TemplateVariableGroup fieldsTemplateVariableGroup =
			templateVariableGroups.get("fields");

		fieldsTemplateVariableGroup.empty();

		fieldsTemplateVariableGroup.addCollectionVariable(
			"asset-entries", List.class,
			PortletDisplayTemplateConstants.ENTRIES, "asset-entry",
			AssetEntry.class, "curEntry", "getTitle(locale)");
		fieldsTemplateVariableGroup.addVariable(
			"asset-entry", AssetEntry.class,
			PortletDisplayTemplateConstants.ENTRY, "getTitle(locale)");

		TemplateVariableGroup assetServicesTemplateVariableGroup =
			new TemplateVariableGroup("asset-services", restrictedVariables);

		assetServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		assetServicesTemplateVariableGroup.addServiceLocatorVariables(
			AssetEntryLocalService.class, AssetEntryService.class,
			AssetVocabularyLocalService.class, AssetVocabularyService.class,
			AssetCategoryLocalService.class, AssetCategoryService.class,
			AssetTagLocalService.class, AssetTagService.class);

		templateVariableGroups.put(
			assetServicesTemplateVariableGroup.getLabel(),
			assetServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/asset/publisher/web/portlet/display/template" +
			"/dependencies/portlet-display-templates.xml";
	}

	@Reference
	protected AssetPublisherHelper assetPublisherHelper;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

}