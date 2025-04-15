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
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
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

		Indexer<ConfigurationModel> indexer =
			_indexerRegistry.nullSafeGetIndexer(ConfigurationModel.class);

		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(false);
		searchContext.setCompanyId(CompanyConstants.SYSTEM);
		searchContext.setLocale(renderRequest.getLocale());

		String keywords = renderRequest.getParameter("keywords");

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(true);
		queryConfig.setLocale(renderRequest.getLocale());
		queryConfig.setScoreEnabled(true);

		try {
			Hits hits = indexer.search(searchContext);

			Document[] documents = hits.getDocs();

			ConfigurationScopeDisplayContext configurationScopeDisplayContext =
				ConfigurationScopeDisplayContextFactory.create(renderRequest);

			Map<String, ConfigurationModel> configurationModels =
				_configurationModelRetriever.getConfigurationModels(
					configurationScopeDisplayContext.getScope(),
					configurationScopeDisplayContext.getScopePK());

			List<ConfigurationEntry> searchResults = new ArrayList<>(
				documents.length);

			for (Document document : documents) {
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

				if ((configurationModel != null) &&
					configurationModel.isGenerateUI()) {

					searchResults.add(
						new ConfigurationModelConfigurationEntry(
							configurationModel, renderRequest.getLocale()));
				}
			}

			ExtendedObjectClassDefinition.Scope scope =
				configurationScopeDisplayContext.getScope();

			for (ConfigurationScreen configurationScreen :
					_configurationEntryRetriever.getAllConfigurationScreens()) {

				if (!Objects.equals(scope, configurationScreen.getScope()) ||
					!configurationScreen.isVisible()) {

					continue;
				}

				String configurationScreenKey = StringUtil.toLowerCase(
					configurationScreen.getKey(), renderRequest.getLocale());
				String configurationScreenName = StringUtil.toLowerCase(
					configurationScreen.getName(renderRequest.getLocale()),
					renderRequest.getLocale());

				String searchReadyKeywords = StringUtil.toLowerCase(
					keywords, renderRequest.getLocale());

				if (Validator.isNull(keywords) ||
					configurationScreenKey.contains(searchReadyKeywords) ||
					configurationScreenName.contains(searchReadyKeywords)) {

					searchResults.add(
						new ConfigurationScreenConfigurationEntry(
							configurationScreen, renderRequest.getLocale()));
				}
			}

			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_ITERATOR,
				new ConfigurationEntryIterator(searchResults));
			renderRequest.setAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_ENTRY_RETRIEVER,
				_configurationEntryRetriever);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return "/search_results.jsp";
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private IndexerRegistry _indexerRegistry;

}