/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
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

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class PatcherProductVersionsDisplayContext {

	public PatcherProductVersionsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<PatcherProductVersion> getSearchContainer()
		throws Exception {

		if (_patcherProductVersionSearchContainer != null) {
			return _patcherProductVersionSearchContainer;
		}

		SearchContainer<PatcherProductVersion>
			patcherProductVersionSearchContainer = new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-product-versions");

		Indexer<PatcherProductVersion> indexer = IndexerRegistryUtil.getIndexer(
			PatcherProductVersion.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setEnd(patcherProductVersionSearchContainer.getEnd());
		searchContext.setGroupIds(null);
		searchContext.setSorts(new Sort("name_sortable", false));
		searchContext.setStart(patcherProductVersionSearchContainer.getStart());

		Hits hits = indexer.search(searchContext);

		patcherProductVersionSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult ->
					PatcherProductVersionLocalServiceUtil.
						fetchPatcherProductVersion(searchResult.getClassPK())),
			hits.getLength());

		_patcherProductVersionSearchContainer =
			patcherProductVersionSearchContainer;

		return _patcherProductVersionSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private SearchContainer<PatcherProductVersion>
		_patcherProductVersionSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}