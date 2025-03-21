/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.category.web.internal.portlet.display.template;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.category.web.internal.display.context.CPCategoryContentDisplayContext;
import com.liferay.commerce.product.content.category.web.internal.portlet.CPCategoryContentPortlet;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_CATEGORY_CONTENT_WEB,
	service = TemplateHandler.class
)
public class CPCategoryContentPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return CPCategoryContentPortlet.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		StringBundler sb = new StringBundler(3);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		sb.append(
			_portal.getPortletTitle(
				CPPortletKeys.CP_CATEGORY_CONTENT_WEB, resourceBundle));

		sb.append(StringPool.SPACE);
		sb.append(_language.get(locale, "template"));

		return sb.toString();
	}

	@Override
	public String getResourceName() {
		return CPPortletKeys.CP_CATEGORY_CONTENT_WEB;
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
			"cp-category-content-display-context",
			CPCategoryContentDisplayContext.class,
			"cpCategoryContentDisplayContext");
		templateVariableGroup.addVariable(
			"asset-category", AssetCategory.class, "assetCategory");

		TemplateVariableGroup assetCategoryServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"asset-category-services", getRestrictedVariables(language));

		assetCategoryServicesTemplateVariableGroup.setAutocompleteEnabled(
			false);

		assetCategoryServicesTemplateVariableGroup.addServiceLocatorVariables(
			AssetCategoryLocalService.class, AssetCategoryService.class);

		templateVariableGroups.put(
			assetCategoryServicesTemplateVariableGroup.getLabel(),
			assetCategoryServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/commerce/product/content/category/web/internal" +
			"/portlet/display/template/dependencies/portlet-display-" +
				"templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}