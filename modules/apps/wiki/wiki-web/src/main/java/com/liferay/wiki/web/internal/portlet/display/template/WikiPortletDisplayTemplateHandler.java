/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.display.template;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiNodeService;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(
	property = "jakarta.portlet.name=" + WikiPortletKeys.WIKI,
	service = TemplateHandler.class
)
public class WikiPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return WikiPage.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			WikiPortletKeys.WIKI,
			ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass()));

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return WikiPortletKeys.WIKI;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup fieldsTemplateVariableGroup =
			templateVariableGroups.get("fields");

		fieldsTemplateVariableGroup.empty();

		fieldsTemplateVariableGroup.addVariable(
			"asset-entry", AssetEntry.class, "assetEntry");
		fieldsTemplateVariableGroup.addVariable(
			"wiki-page", WikiPage.class, PortletDisplayTemplateConstants.ENTRY);
		fieldsTemplateVariableGroup.addVariable(
			"wiki-page-content", String.class, "formattedContent");

		TemplateVariableGroup wikiServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"wiki-services", getRestrictedVariables(language));

		wikiServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		wikiServicesTemplateVariableGroup.addServiceLocatorVariables(
			WikiPageLocalService.class, WikiPageService.class,
			WikiNodeLocalService.class, WikiNodeService.class);

		templateVariableGroups.put(
			wikiServicesTemplateVariableGroup.getLabel(),
			wikiServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Override
	public boolean isEnabled(long companyId) {
		return FeatureFlagManagerUtil.isEnabled(companyId, "LPD-35013");
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/wiki/web/portlet/display/template/dependencies" +
			"/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.wiki.service)(release.schema.version>=0.0.3))"
	)
	private Release _release;

}