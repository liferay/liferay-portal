/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.admin.web.internal.constants.SearchAdminPortletKeys;
import com.liferay.portal.search.admin.web.internal.constants.SearchAdminWebKeys;
import com.liferay.portal.search.admin.web.internal.display.context.SearchAdminDisplayContext;
import com.liferay.portal.search.admin.web.internal.display.context.builder.FieldMappingsDisplayContextBuilder;
import com.liferay.portal.search.admin.web.internal.display.context.builder.IndexActionsDisplayContextBuilder;
import com.liferay.portal.search.admin.web.internal.display.context.builder.SearchAdminDisplayContextBuilder;
import com.liferay.portal.search.admin.web.internal.display.context.builder.SearchEngineDisplayContextBuilder;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.cluster.StatsInformationFactory;
import com.liferay.portal.search.configuration.ReindexConfiguration;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.search.spi.reindexer.IndexReindexerRegistry;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.ReindexConfiguration",
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-search-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Search Administration",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SearchAdminPortletKeys.SEARCH_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SearchAdminPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		SearchAdminDisplayContextBuilder searchAdminDisplayContextBuilder =
			new SearchAdminDisplayContextBuilder(
				_language, _portal, renderRequest, renderResponse);

		searchAdminDisplayContextBuilder.setIndexInformation(
			_indexInformationSnapshot.get());

		List<String> indexReindexerClassNames = ListUtil.fromCollection(
			_indexReindexerRegistry.getIndexReindexerClassNames());

		Collections.sort(indexReindexerClassNames);

		searchAdminDisplayContextBuilder.setIndexReindexerClassNames(
			indexReindexerClassNames);

		SearchAdminDisplayContext searchAdminDisplayContext =
			searchAdminDisplayContextBuilder.build();

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, searchAdminDisplayContext);

		String tab = searchAdminDisplayContext.getSelectedTab();

		if (tab.equals("connections")) {
			SearchEngineDisplayContextBuilder
				searchEngineDisplayContextBuilder =
					new SearchEngineDisplayContextBuilder();

			searchEngineDisplayContextBuilder.setSearchEngineInformation(
				_searchEngineInformationSnapshot.get());

			renderRequest.setAttribute(
				SearchAdminWebKeys.SEARCH_ENGINE_DISPLAY_CONTEXT,
				searchEngineDisplayContextBuilder.build());
		}
		else if (tab.equals("field-mappings")) {
			FieldMappingsDisplayContextBuilder
				fieldMappingsDisplayContextBuilder =
					new FieldMappingsDisplayContextBuilder();

			fieldMappingsDisplayContextBuilder.setCompanyId(
				_portal.getCompanyId(renderRequest));
			fieldMappingsDisplayContextBuilder.setCurrentURL(
				_portal.getCurrentURL(renderRequest));
			fieldMappingsDisplayContextBuilder.setIndexInformation(
				_indexInformationSnapshot.get());
			fieldMappingsDisplayContextBuilder.setNamespace(
				renderResponse.getNamespace());
			fieldMappingsDisplayContextBuilder.setSelectedIndexName(
				ParamUtil.getString(renderRequest, "selectedIndexName"));

			renderRequest.setAttribute(
				SearchAdminWebKeys.FIELD_MAPPINGS_DISPLAY_CONTEXT,
				fieldMappingsDisplayContextBuilder.build());
		}
		else {
			IndexActionsDisplayContextBuilder
				indexActionsDisplayContextBuilder =
					new IndexActionsDisplayContextBuilder(
						_language, _portal, _reindexConfiguration,
						renderRequest, _searchCapabilities);

			indexActionsDisplayContextBuilder.setIndexReindexerClassNames(
				indexReindexerClassNames);
			indexActionsDisplayContextBuilder.setStatsInformationFactory(
				_statsInformationFactorySnapshot.get());

			renderRequest.setAttribute(
				SearchAdminWebKeys.INDEX_ACTIONS_DISPLAY_CONTEXT,
				indexActionsDisplayContextBuilder.build());
		}

		super.render(renderRequest, renderResponse);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_reindexConfiguration = ConfigurableUtil.createConfigurable(
			ReindexConfiguration.class, properties);
	}

	private static final Snapshot<IndexInformation> _indexInformationSnapshot =
		new Snapshot<>(
			SearchAdminPortlet.class, IndexInformation.class, null, true);
	private static final Snapshot<SearchEngineInformation>
		_searchEngineInformationSnapshot = new Snapshot<>(
			SearchAdminPortlet.class, SearchEngineInformation.class, null,
			true);
	private static final Snapshot<StatsInformationFactory>
		_statsInformationFactorySnapshot = new Snapshot<>(
			SearchAdminPortlet.class, StatsInformationFactory.class, null,
			true);

	@Reference
	private IndexReindexerRegistry _indexReindexerRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private volatile ReindexConfiguration _reindexConfiguration;

	@Reference
	private SearchCapabilities _searchCapabilities;

}