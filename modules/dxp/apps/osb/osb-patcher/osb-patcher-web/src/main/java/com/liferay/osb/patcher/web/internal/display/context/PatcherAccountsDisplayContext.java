/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Field;
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
public class PatcherAccountsDisplayContext {

	public PatcherAccountsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<PatcherAccount> getSearchContainer()
		throws Exception {

		if (_patcherAccountSearchContainer != null) {
			return _patcherAccountSearchContainer;
		}

		SearchContainer<PatcherAccount> patcherAccountSearchContainer =
			new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-accounts");

		Indexer<PatcherAccount> indexer = IndexerRegistryUtil.getIndexer(
			PatcherAccount.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setEnd(patcherAccountSearchContainer.getEnd());
		searchContext.setGroupIds(null);
		searchContext.setSorts(
			new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true));
		searchContext.setStart(patcherAccountSearchContainer.getStart());

		Hits hits = indexer.search(searchContext);

		patcherAccountSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult ->
					PatcherAccountLocalServiceUtil.fetchPatcherAccount(
						searchResult.getClassPK())),
			hits.getLength());

		_patcherAccountSearchContainer = patcherAccountSearchContainer;

		return _patcherAccountSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private SearchContainer<PatcherAccount> _patcherAccountSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}