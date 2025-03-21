/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet.display.template;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsWebTemplateConfiguration;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletDisplayContext;
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
	configurationPid = "com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsWebTemplateConfiguration",
	property = "jakarta.portlet.name=" + SearchResultsPortletKeys.SEARCH_RESULTS,
	service = TemplateHandler.class
)
public class SearchResultsPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return SearchResultSummaryDisplayContext.class.getName();
	}

	@Override
	public String getDefaultTemplateKey() {
		return _searchResultsWebTemplateConfiguration.
			searchResultsTemplateKeyDefault();
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(
			locale, "x-template",
			_portal.getPortletTitle(
				SearchResultsPortletKeys.SEARCH_RESULTS, resourceBundle),
			false);
	}

	@Override
	public String getResourceName() {
		return SearchResultsPortletKeys.SEARCH_RESULTS;
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
			"content", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getContent()");
		templateVariableGroup.addVariable(
			"created-by-user-name", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getCreatorUserName()");
		templateVariableGroup.addVariable(
			"created-by-user-portrait", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY,
			"getCreatorUserPortraitURLString()");
		templateVariableGroup.addVariable(
			"creation-date", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getCreationDateString()");
		templateVariableGroup.addCollectionVariable(
			"documents", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"document", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getHighlightedTitle()");
		templateVariableGroup.addVariable(
			"download-file-size", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY,
			"getAssetRendererDownloadSize()");
		templateVariableGroup.addVariable(
			"download-file-url", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY,
			"getAssetRendererURLDownload()");
		templateVariableGroup.addVariable(
			"modified-by-user-name", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getModifiedByUserName()");
		templateVariableGroup.addVariable(
			"modified-by-user-portrait",
			SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY,
			"getModifiedByUserPortraitURLString()");
		templateVariableGroup.addVariable(
			"modified-date", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getModifiedDateString()");
		templateVariableGroup.addVariable(
			"publish-date", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getPublishedDateString()");
		templateVariableGroup.addVariable(
			"search-container", SearchContainer.class, "searchContainer");
		templateVariableGroup.addVariable(
			"search-results-display-context",
			SearchResultsPortletDisplayContext.class,
			"searchResultsPortletDisplayContext");
		templateVariableGroup.addVariable(
			"title", SearchResultSummaryDisplayContext.class,
			PortletDisplayTemplateConstants.ENTRY, "getHighlightedTitle()");

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
		_searchResultsWebTemplateConfiguration =
			ConfigurableUtil.createConfigurable(
				SearchResultsWebTemplateConfiguration.class, properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/portal/search/web/internal/search/results/web" +
			"/portlet/display/template/dependencies/portlet-display-" +
				"templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private volatile SearchResultsWebTemplateConfiguration
		_searchResultsWebTemplateConfiguration;

}