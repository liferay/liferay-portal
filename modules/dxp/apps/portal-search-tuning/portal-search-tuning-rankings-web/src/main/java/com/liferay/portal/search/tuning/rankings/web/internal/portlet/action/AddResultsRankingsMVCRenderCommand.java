/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.display.context.AddRankingDisplayContext;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ResultRankingsPortletKeys.RESULT_RANKINGS,
		"mvc.command.name=/result_rankings/add_results_rankings"
	},
	service = MVCRenderCommand.class
)
public class AddResultsRankingsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			AddRankingDisplayContext.class.getName(),
			new AddRankingDisplayContext(
				_itemSelector, renderRequest, renderResponse));

		return "/add_results_rankings.jsp";
	}

	@Reference
	private ItemSelector _itemSelector;

}