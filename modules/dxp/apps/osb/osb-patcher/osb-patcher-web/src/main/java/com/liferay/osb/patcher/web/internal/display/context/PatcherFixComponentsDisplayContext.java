/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
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
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixComponentsDisplayContext {

	public PatcherFixComponentsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems(
		PatcherFixComponent patcherFixComponent) {

		return DropdownItemListBuilder.add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFixComponent,
				PatcherActionKeys.EDIT, patcherFixComponent.getUserId()),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_fix_components"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixComponentId",
						patcherFixComponent.getPatcherFixComponentId()
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFixComponent,
				ActionKeys.DELETE, patcherFixComponent.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/delete_fix_components"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixComponentId",
						patcherFixComponent.getPatcherFixComponentId()
					).buildString());
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	public SearchContainer<PatcherFixComponent> getSearchContainer()
		throws Exception {

		if (_patcherFixComponentSearchContainer != null) {
			return _patcherFixComponentSearchContainer;
		}

		SearchContainer<PatcherFixComponent>
			patcherFixComponentSearchContainer = new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-fix-components");

		Indexer<PatcherFixComponent> indexer = IndexerRegistryUtil.getIndexer(
			PatcherFixComponent.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setEnd(patcherFixComponentSearchContainer.getEnd());
		searchContext.setGroupIds(null);
		searchContext.setSorts(new Sort("name_sortable", false));
		searchContext.setStart(patcherFixComponentSearchContainer.getStart());

		Hits hits = indexer.search(searchContext);

		patcherFixComponentSearchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				SearchResultUtil.getSearchResults(
					hits, LocaleUtil.getDefault()),
				searchResult ->
					PatcherFixComponentLocalServiceUtil.
						fetchPatcherFixComponent(searchResult.getClassPK())),
			hits.getLength());

		_patcherFixComponentSearchContainer =
			patcherFixComponentSearchContainer;

		return _patcherFixComponentSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private SearchContainer<PatcherFixComponent>
		_patcherFixComponentSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}