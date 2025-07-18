/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems(
		PatcherProductVersion patcherProductVersion) {

		return DropdownItemListBuilder.add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherProductVersion,
				PatcherActionKeys.EDIT, patcherProductVersion.getUserId()),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_product_versions"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherProductVersionId",
						patcherProductVersion.getPatcherProductVersionId()
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).build();
	}

	public SearchContainer<PatcherProductVersion> getSearchContainer()
		throws Exception {

		if (_patcherProductVersionSearchContainer != null) {
			return _patcherProductVersionSearchContainer;
		}

		SearchContainer<PatcherProductVersion>
			patcherProductVersionSearchContainer = new SearchContainer<>(
				_renderRequest, _getPortletURL(), null,
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

	private String _getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private PortletURL _getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/patcher/index_product_versions"
		).setKeywords(
			_getKeywords()
		).setTabs1(
			"product-versions"
		).buildPortletURL();

		return _portletURL;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private SearchContainer<PatcherProductVersion>
		_patcherProductVersionSearchContainer;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}