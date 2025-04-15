/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.display.context.RankingPortletDisplayBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.display.context.RankingPortletDisplayContext;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ResultRankingsPortletKeys.RESULT_RANKINGS,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class ViewResultRankingsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		RankingPortletDisplayContext rankingPortletDisplayContext =
			new RankingPortletDisplayBuilder(
				portal.getHttpServletRequest(renderRequest), language, portal,
				queries, _rankingBuilderFactory, rankingIndexNameBuilder,
				renderRequest, renderResponse, searchEngineAdapter,
				searchEngineInformation, sorts
			).build();

		renderRequest.setAttribute(
			ResultRankingsPortletKeys.RESULT_RANKINGS_DISPLAY_CONTEXT,
			rankingPortletDisplayContext);

		return "/view.jsp";
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected Queries queries;

	@Reference
	protected RankingIndexNameBuilder rankingIndexNameBuilder;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	@Reference
	protected SearchEngineInformation searchEngineInformation;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Reference
	protected Sorts sorts;

	@Reference
	private RankingBuilderFactory _rankingBuilderFactory;

}