/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.context.SearchContextFactory;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.summary.SummaryBuilderFactory;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(service = SearchDisplayContextFactory.class)
public class SearchDisplayContextFactoryImpl
	implements SearchDisplayContextFactory {

	@Override
	public SearchDisplayContext create(
			RenderRequest renderRequest, RenderResponse renderResponse,
			PortletPreferences portletPreferences)
		throws PortletException {

		return new SearchDisplayContext(
			renderRequest, portletPreferences, portal, language, searcher,
			new IndexSearchPropsValuesImpl(),
			new ClassicPortletURLFactoryImpl(renderRequest, renderResponse),
			summaryBuilderFactory, searchContextFactory,
			searchRequestBuilderFactory, _jsonFactory);
	}

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected SearchContextFactory searchContextFactory;

	@Reference
	protected Searcher searcher;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Reference
	protected SummaryBuilderFactory summaryBuilderFactory;

	@Reference
	private JSONFactory _jsonFactory;

}