/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.navigation.web.internal.portlet.display.template;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.asset.categories.navigation.web.internal.display.context.CPAssetCategoriesNavigationDisplayContext;
import com.liferay.commerce.product.asset.categories.navigation.web.internal.portlet.CPAssetCategoriesNavigationPortlet;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_ASSET_CATEGORIES_NAVIGATION,
	service = TemplateHandler.class
)
public class CPAssetCategoriesNavigationPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return CPAssetCategoriesNavigationPortlet.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		StringBundler sb = new StringBundler(3);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		sb.append(
			_portal.getPortletTitle(
				CPPortletKeys.CP_ASSET_CATEGORIES_NAVIGATION, resourceBundle));

		sb.append(StringPool.SPACE);
		sb.append(_language.get(locale, "template"));

		return sb.toString();
	}

	@Override
	public String getResourceName() {
		return CPPortletKeys.CP_ASSET_CATEGORIES_NAVIGATION;
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
			"cp-asset-categories-navigation-display-context",
			CPAssetCategoriesNavigationDisplayContext.class,
			"cpAssetCategoriesNavigationDisplayContext");
		templateVariableGroup.addCollectionVariable(
			"asset-categories", List.class,
			PortletDisplayTemplateConstants.ENTRIES, "assetCategory",
			AssetCategory.class, "curAssetCategory", "title");

		TemplateVariableGroup assetCategoriesServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"asset-categories-services", getRestrictedVariables(language));

		assetCategoriesServicesTemplateVariableGroup.setAutocompleteEnabled(
			false);

		assetCategoriesServicesTemplateVariableGroup.addServiceLocatorVariables(
			AssetCategoryLocalService.class, AssetCategoryService.class);

		templateVariableGroups.put(
			assetCategoriesServicesTemplateVariableGroup.getLabel(),
			assetCategoriesServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}