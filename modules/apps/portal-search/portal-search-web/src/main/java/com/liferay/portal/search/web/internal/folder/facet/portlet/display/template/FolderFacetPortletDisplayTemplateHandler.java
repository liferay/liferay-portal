/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.folder.facet.portlet.display.template;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.folder.facet.constants.FolderFacetPortletKeys;
import com.liferay.portal.search.web.internal.folder.facet.portlet.FolderFacetPortlet;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = "jakarta.portlet.name=" + FolderFacetPortletKeys.FOLDER_FACET,
	service = TemplateHandler.class
)
public class FolderFacetPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return FolderFacetPortlet.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(
			locale, "x-template",
			_portal.getPortletTitle(
				FolderFacetPortletKeys.FOLDER_FACET, resourceBundle),
			false);
	}

	@Override
	public String getResourceName() {
		return FolderFacetPortletKeys.FOLDER_FACET;
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
			"folder-facet-display-context",
			FolderSearchFacetDisplayContext.class,
			"folderSearchFacetDisplayContext");
		templateVariableGroup.addVariable(
			"term-frequency", Integer.class,
			PortletDisplayTemplateConstants.ENTRY, "getFrequency()");
		templateVariableGroup.addVariable(
			"term-name", String.class, PortletDisplayTemplateConstants.ENTRY,
			"getBucketText()");
		templateVariableGroup.addCollectionVariable(
			"terms", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"term", BucketDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getBucketText()");

		TemplateVariableGroup categoriesServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"category-services", getRestrictedVariables(language));

		categoriesServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		templateVariableGroups.put(
			categoriesServicesTemplateVariableGroup.getLabel(),
			categoriesServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/portal/search/web/internal/folder/facet/portlet" +
			"/display/template/dependencies/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}