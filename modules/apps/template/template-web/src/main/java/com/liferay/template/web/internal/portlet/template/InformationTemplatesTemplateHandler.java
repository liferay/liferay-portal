/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.template;

import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.BaseTemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.web.internal.constants.TemplateConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
	service = TemplateHandler.class
)
public class InformationTemplatesTemplateHandler extends BaseTemplateHandler {

	@Override
	public String getClassName() {
		return InfoItemFormProvider.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			TemplatePortletKeys.TEMPLATE, locale);

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return TemplateConstants.RESOURCE_NAME;
	}

	@Override
	public String getTemplatesHelpPath(String language) {
		return "com/liferay/template/web/internal/portlet/template" +
			"/dependencies/template.ftl";
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		return LinkedHashMapBuilder.<String, TemplateVariableGroup>put(
			"general-variables",
			() -> {
				TemplateVariableGroup generalVariablesTemplateVariableGroup =
					new TemplateVariableGroup("general-variables");

				generalVariablesTemplateVariableGroup.addVariable(
					"current-url", String.class, "currentURL");
				generalVariablesTemplateVariableGroup.addVariable(
					"locale", Locale.class, "locale");
				generalVariablesTemplateVariableGroup.addVariable(
					"template-id", null, "template_id");
				generalVariablesTemplateVariableGroup.addVariable(
					"theme-display", ThemeDisplay.class, "themeDisplay");

				return generalVariablesTemplateVariableGroup;
			}
		).put(
			"util",
			() -> {
				TemplateVariableGroup utilTemplateVariableGroup =
					new TemplateVariableGroup("util");

				utilTemplateVariableGroup.addVariable(
					"http-request", HttpServletRequest.class, "request");

				return utilTemplateVariableGroup;
			}
		).build();
	}

	@Override
	public boolean isDisplayTemplateHandler() {
		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}