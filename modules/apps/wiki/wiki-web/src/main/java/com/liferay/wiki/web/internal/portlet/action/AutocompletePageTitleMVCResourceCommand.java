/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.wiki.constants.WikiPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/autocomplete_page_title"
	},
	service = MVCResourceCommand.class
)
public class AutocompletePageTitleMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	public void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			JSONArray jsonArray = _getJSONArray(resourceRequest);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(resourceResponse);

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse, jsonArray.toString());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private JSONArray _getJSONArray(ResourceRequest resourceRequest)
		throws PortalException {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		SearchContext searchContext = SearchContextFactory.getInstance(
			httpServletRequest);

		searchContext.setEnd(20);

		String query = ParamUtil.getString(httpServletRequest, "query");

		searchContext.setKeywords(StringUtil.toLowerCase(query));

		long nodeId = ParamUtil.getLong(resourceRequest, "nodeId");

		searchContext.setNodeIds(new long[] {nodeId});

		searchContext.setStart(0);

		Hits hits = _wikiPageSearcher.search(searchContext);

		for (Document document : hits.getDocs()) {
			JSONObject jsonObject = JSONUtil.put(
				"title", document.get(Field.TITLE));

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AutocompletePageTitleMVCResourceCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiPage)")
	private BaseSearcher _wikiPageSearcher;

}