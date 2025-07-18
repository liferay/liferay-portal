/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
public class PatcherProjectVersionsDisplayContext {

	public PatcherProjectVersionsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems(
		PatcherProjectVersion patcherProjectVersion) {

		return DropdownItemListBuilder.add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherProjectVersion,
				PatcherActionKeys.EDIT, patcherProjectVersion.getUserId()),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_project_versions"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherProjectVersionId",
						patcherProjectVersion.getPatcherProjectVersionId()
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherProjectVersion,
				ActionKeys.DELETE, patcherProjectVersion.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/delete_project_versions"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherProjectVersionId",
						patcherProjectVersion.getPatcherProjectVersionId()
					).buildString());
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
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

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public SearchContainer<PatcherProjectVersion> getSearchContainer()
		throws Exception {

		if (_patcherProjectVersionSearchContainer != null) {
			return _patcherProjectVersionSearchContainer;
		}

		SearchContainer<PatcherProjectVersion>
			patcherProjectVersionSearchContainer = new SearchContainer<>(
				_renderRequest, _getPortletURL(), null,
				"there-are-no-project-versions");

		Indexer<PatcherProjectVersion> indexer = IndexerRegistryUtil.getIndexer(
			PatcherProjectVersion.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setAttribute(
			"patcherProductVersionId", getPatcherProductVersionId());
		searchContext.setEnd(patcherProjectVersionSearchContainer.getEnd());
		searchContext.setGroupIds(null);
		searchContext.setSorts(new Sort("name_sortable", false));
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
			"/patcher/index_project_versions"
		).setKeywords(
			_getKeywords()
		).setTabs1(
			"project-versions"
		).setParameter(
			"patcherProductVersionId", getPatcherProductVersionId()
		).buildPortletURL();

		return _portletURL;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private Long _patcherProductVersionId;
	private PatcherProjectVersion _patcherProjectVersion;
	private SearchContainer<PatcherProjectVersion>
		_patcherProjectVersionSearchContainer;
	private PortletURL _portletURL;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}