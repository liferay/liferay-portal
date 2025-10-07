/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
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
public class PatcherViewFixesDisplayContext {

	public PatcherViewFixesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public PatcherFix getPatcherFix() {
		if (_patcherFix != null) {
			return _patcherFix;
		}

		long patcherFixId = ParamUtil.getLong(
			_httpServletRequest, "patcherFixId");

		_patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(patcherFixId);

		return _patcherFix;
	}

	public SearchContainer<PatcherFix> getSearchContainer() throws Exception {
		if (_patcherPatcherFixSearchContainer != null) {
			return _patcherPatcherFixSearchContainer;
		}

		SearchContainer<PatcherFix> patcherPatcherFixSearchContainer =
			new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null, null);

		Indexer<PatcherFix> indexer = IndexerRegistryUtil.getIndexer(
			PatcherFix.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		PatcherFix patcherFix = getPatcherFix();

		searchContext.setAttribute("key", patcherFix.getKey());

		searchContext.setAttribute("viewSearch", Boolean.TRUE);
		searchContext.setGroupIds(null);
		searchContext.setSorts(new Sort("keyVersion", true));

		Hits hits = indexer.search(searchContext);

		patcherPatcherFixSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult -> PatcherFixLocalServiceUtil.fetchPatcherFix(
					searchResult.getClassPK())),
			hits.getLength());

		_patcherPatcherFixSearchContainer = patcherPatcherFixSearchContainer;

		return _patcherPatcherFixSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private PatcherFix _patcherFix;
	private SearchContainer<PatcherFix> _patcherPatcherFixSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}