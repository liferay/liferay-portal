/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationModelConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationScreenConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.search.FieldNames;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryIterator;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/configuration_admin/search_results"
	},
	service = MVCRenderCommand.class
)
public class SearchResultsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			Indexer<ConfigurationModel> indexer =
				_indexerRegistry.nullSafeGetIndexer(ConfigurationModel.class);

			SearchContext searchContext = _getSearchContext(
				renderRequest.getParameter("keywords"),
				renderRequest.getLocale());

			Hits hits = indexer.search(searchContext);

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_ITERATOR,
				new ConfigurationEntryIterator(
					_getConfigurationEntries(
						ConfigurationScopeDisplayContextFactory.create(
							renderRequest),
						hits.getDocs(), renderRequest.getLocale(),
						searchContext)));

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_RETRIEVER,
				_configurationEntryRetriever);

			return "/search_results.jsp";
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private List<ConfigurationEntry> _getConfigurationEntries(
		ConfigurationScopeDisplayContext configurationScopeDisplayContext,
		Document[] documents, Locale locale, SearchContext searchContext) {

		List<ConfigurationEntry> configurationEntries = new ArrayList<>();

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		for (Document document : documents) {
			ConfigurationModel configurationModel = _getConfigurationModel(
				configurationModels, document);

			if ((configurationModel != null) &&
				configurationModel.isGenerateUI()) {

				configurationEntries.add(
					new ConfigurationModelConfigurationEntry(
						configurationModel, locale));
			}
		}

		for (ConfigurationScreen configurationScreen :
				_configurationEntryRetriever.getAllConfigurationScreens()) {

			if (!Objects.equals(
					String.valueOf(configurationScopeDisplayContext.getScope()),
					configurationScreen.getScope()) ||
				!configurationScreen.isVisible()) {

				continue;
			}

			String configurationScreenCategoryKey = StringUtil.toLowerCase(
				_language.get(
					locale, "category." + configurationScreen.getCategoryKey()),
				locale);
			String configurationScreenKey = StringUtil.toLowerCase(
				configurationScreen.getKey(), locale);
			String configurationScreenName = StringUtil.toLowerCase(
				configurationScreen.getName(locale), locale);
			String keywords = StringUtil.toLowerCase(
				searchContext.getKeywords(), locale);

			if (Validator.isNull(keywords) ||
				configurationScreenCategoryKey.contains(keywords) ||
				configurationScreenKey.contains(keywords) ||
				configurationScreenName.contains(keywords)) {

				configurationEntries.add(
					new ConfigurationScreenConfigurationEntry(
						configurationScreen, locale));
			}
		}

		return configurationEntries;
	}

	private ConfigurationModel _getConfigurationModel(
		Map<String, ConfigurationModel> configurationModels,
		Document document) {

		String configurationModelId = document.get(
			FieldNames.CONFIGURATION_MODEL_ID);

		ConfigurationModel configurationModel = configurationModels.get(
			configurationModelId);

		if (configurationModel == null) {
			String configurationModelFactoryId = document.get(
				FieldNames.CONFIGURATION_MODEL_FACTORY_PID);

			configurationModel = configurationModels.get(
				configurationModelFactoryId);
		}

		return configurationModel;
	}

	private SearchContext _getSearchContext(String keywords, Locale locale) {
		SearchContext searchContext = new SearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(true);
		queryConfig.setLocale(locale);
		queryConfig.setScoreEnabled(true);

		searchContext.setAndSearch(false);
		searchContext.setCompanyId(CompanyConstants.SYSTEM);
		searchContext.setLocale(locale);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		return searchContext;
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private Language _language;

}