/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.portlet.display.template;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.custom.facet.constants.CustomFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetDisplayContext;
import com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortlet;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	configurationPid = "com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration",
	property = "jakarta.portlet.name=" + CustomFacetPortletKeys.CUSTOM_FACET,
	service = TemplateHandler.class
)
public class CustomFacetPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return CustomFacetPortlet.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(
			locale, "x-template",
			_portal.getPortletTitle(
				CustomFacetPortletKeys.CUSTOM_FACET, resourceBundle),
			false);
	}

	@Override
	public String getResourceName() {
		return CustomFacetPortletKeys.CUSTOM_FACET;
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
			"custom-facet-display-context", CustomFacetDisplayContext.class,
			"customFacetDisplayContext");
		templateVariableGroup.addVariable(
			"term-field-name", String.class,
			PortletDisplayTemplateConstants.ENTRY, "getBucketText()");
		templateVariableGroup.addVariable(
			"term-frequency", Integer.class,
			PortletDisplayTemplateConstants.ENTRY, "getFrequency()");
		templateVariableGroup.addCollectionVariable(
			"terms", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"term", BucketDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getBucketText()");

		TemplateVariableGroup customFacetServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"custom-services", getRestrictedVariables(language));

		customFacetServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		templateVariableGroups.put(
			customFacetServicesTemplateVariableGroup.getLabel(),
			customFacetServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_customFacetPortletInstanceConfiguration =
			ConfigurableUtil.createConfigurable(
				CustomFacetPortletInstanceConfiguration.class, properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/portal/search/web/internal/custom/facet/portlet" +
			"/display/template/dependencies/portlet-display-templates.xml";
	}

	private volatile CustomFacetPortletInstanceConfiguration
		_customFacetPortletInstanceConfiguration;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}