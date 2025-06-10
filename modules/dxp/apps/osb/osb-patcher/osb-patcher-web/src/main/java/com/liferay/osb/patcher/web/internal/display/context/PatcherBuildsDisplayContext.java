/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class PatcherBuildsDisplayContext {

	public PatcherBuildsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<PatcherBuild> getSearchContainer() throws Exception {
		if (_patcherBuildSearchContainer != null) {
			return _patcherBuildSearchContainer;
		}

		SearchContainer<PatcherBuild> patcherBuildSearchContainer =
			new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-builds");

		Indexer<PatcherBuild> indexer = IndexerRegistryUtil.getIndexer(
			PatcherBuild.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		boolean advancedSearch = ParamUtil.getBoolean(
			_httpServletRequest, "advancedSearch");

		if (!advancedSearch) {
			searchContext.setAttribute("buildsIndexSearch", Boolean.TRUE);
		}

		searchContext.setEnd(patcherBuildSearchContainer.getEnd());
		searchContext.setGroupIds(null);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		String patcherBuildName = ParamUtil.getString(
			_httpServletRequest, "patcherBuildName");

		if ((!PatcherUtil.isPatcherTickets(keywords) ||
			 PatcherUtil.isPatcherProjectVersionName(keywords)) &&
			!PatcherUtil.isPatcherTickets(patcherBuildName)) {

			searchContext.setSorts(
				new Sort("statusDate", Sort.LONG_TYPE, true));
		}

		searchContext.setStart(patcherBuildSearchContainer.getStart());

		Hits hits = indexer.search(searchContext);

		patcherBuildSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult -> PatcherBuildLocalServiceUtil.fetchPatcherBuild(
					searchResult.getClassPK())),
			hits.getLength());

		_patcherBuildSearchContainer = patcherBuildSearchContainer;

		return _patcherBuildSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private SearchContainer<PatcherBuild> _patcherBuildSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}