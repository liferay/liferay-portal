/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.search.Field;
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

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherProjectVersionsDisplayContext {

	public PatcherProjectVersionsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public JSONArray getDXP70AndNewerPatcherProductVersionIdsJSONArray() {
		JSONArray patcherProductVersionIdsJSONArray =
			JSONFactoryUtil.createJSONArray();

		List<PatcherProductVersion> patcherProductVersions =
			PatcherProductVersionUtil.getPatcherProductVersions(
				PatcherProductVersionConstants.
					TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30);

		for (PatcherProductVersion patcherProductVersion :
				patcherProductVersions) {

			patcherProductVersionIdsJSONArray.put(
				patcherProductVersion.getPatcherProductVersionId());
		}

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				PatcherProductVersionConstants.
					LABEL_PRODUCT_VERSION_QUARTERLY_RELEASES);

		if (patcherProductVersion != null) {
			patcherProductVersionIdsJSONArray.put(
				patcherProductVersion.getPatcherProductVersionId());
		}

		return patcherProductVersionIdsJSONArray;
	}

	public JSONArray getMarketplaceReleasePatcherProductVersionIdsJSONArray()
		throws Exception {

		return JSONFactoryUtil.createJSONArray(
			JSONFactoryUtil.looseSerializeDeep(
				PatcherProductVersionUtil.
					getMarketplaceReleasePatcherProductVersionIds()));
	}

	public long getPatcherProductVersionId() {
		if (_patcherProductVersionId != null) {
			return _patcherProductVersionId;
		}

		_patcherProductVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProductVersionId");

		return _patcherProductVersionId;
	}

	public PatcherProjectVersion getPatcherProjectVersion() {
		if (_patcherProjectVersion != null) {
			return _patcherProjectVersion;
		}

		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		_patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(
				patcherProjectVersionId);

		return _patcherProjectVersion;
	}

	public SearchContainer<PatcherProjectVersion> getSearchContainer()
		throws Exception {

		if (_patcherProjectVersionSearchContainer != null) {
			return _patcherProjectVersionSearchContainer;
		}

		SearchContainer<PatcherProjectVersion>
			patcherProjectVersionSearchContainer = new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-project-versions");

		Indexer<PatcherProjectVersion> indexer = IndexerRegistryUtil.getIndexer(
			PatcherProjectVersion.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setEnd(patcherProjectVersionSearchContainer.getEnd());
		searchContext.setGroupIds(null);
		searchContext.setSorts(
			new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true));
		searchContext.setStart(patcherProjectVersionSearchContainer.getStart());

		Hits hits = indexer.search(searchContext);

		patcherProjectVersionSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult ->
					PatcherProjectVersionLocalServiceUtil.
						fetchPatcherProjectVersion(searchResult.getClassPK())),
			hits.getLength());

		_patcherProjectVersionSearchContainer =
			patcherProjectVersionSearchContainer;

		return _patcherProjectVersionSearchContainer;
	}

	public boolean isNameDisabled() {
		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X);

		if (patcherProductVersion == null) {
			return false;
		}

		PatcherProjectVersion patcherProjectVersion =
			getPatcherProjectVersion();

		if (patcherProjectVersion.getPatcherProductVersionId() !=
				patcherProductVersion.getPatcherProductVersionId()) {

			return true;
		}

		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private Long _patcherProductVersionId;
	private PatcherProjectVersion _patcherProjectVersion;
	private SearchContainer<PatcherProjectVersion>
		_patcherProjectVersionSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}