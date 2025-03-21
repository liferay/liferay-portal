/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.portlet.display.template;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.similar.results.web.internal.configuration.SimilarResultsWebTemplateConfiguration;
import com.liferay.portal.search.similar.results.web.internal.constants.SimilarResultsPortletKeys;
import com.liferay.portal.search.similar.results.web.internal.display.context.SimilarResultsDisplayContext;
import com.liferay.portal.search.similar.results.web.internal.display.context.SimilarResultsDocumentDisplayContext;
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
	configurationPid = "com.liferay.portal.search.similar.results.web.internal.configuration.SimilarResultsWebTemplateConfiguration",
	property = "jakarta.portlet.name=" + SimilarResultsPortletKeys.SIMILAR_RESULTS,
	service = TemplateHandler.class
)
public class SimilarResultsPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return SimilarResultsDocumentDisplayContext.class.getName();
	}

	@Override
	public String getDefaultTemplateKey() {
		return _similarResultsWebTemplateConfiguration.
			similarResultsTemplateKeyDefault();
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(
			locale, "x-template",
			_portal.getPortletTitle(
				SimilarResultsPortletKeys.SIMILAR_RESULTS, resourceBundle),
			false);
	}

	@Override
	public String getResourceName() {
		return SimilarResultsPortletKeys.SIMILAR_RESULTS;
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
			"similar-results-display-context",
			SimilarResultsDisplayContext.class, "similarResultsDisplayContext");
		templateVariableGroup.addCollectionVariable(
			"documents", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"document", SimilarResultsDocumentDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getTitle()");
		templateVariableGroup.addVariable(
			"document-entry", SimilarResultsDocumentDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getTitle()");

		TemplateVariableGroup categoriesServicesTemplateVariableGroup =
			new TemplateVariableGroup(
				"category-services", getRestrictedVariables(language));

		categoriesServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		templateVariableGroups.put(
			categoriesServicesTemplateVariableGroup.getLabel(),
			categoriesServicesTemplateVariableGroup);

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_similarResultsWebTemplateConfiguration =
			ConfigurableUtil.createConfigurable(
				SimilarResultsWebTemplateConfiguration.class, properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/search/similar/results/web/portlet/display" +
			"/template/dependencies/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private volatile SimilarResultsWebTemplateConfiguration
		_similarResultsWebTemplateConfiguration;

}