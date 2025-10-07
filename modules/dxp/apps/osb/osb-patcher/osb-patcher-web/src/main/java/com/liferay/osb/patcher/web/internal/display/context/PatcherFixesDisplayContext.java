/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
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
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixesDisplayContext {

	public PatcherFixesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems(PatcherFix patcherFix) {
		return DropdownItemListBuilder.add(
			() ->
				PatcherPermission.contains(
					_themeDisplay.getPermissionChecker(), patcherFix,
					PatcherActionKeys.EDIT, patcherFix.getUserId()) &&
				patcherFix.isLatestFix() &&
				(patcherFix.getType() != PatcherFixConstants.TYPE_FIX_PACK),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.EDIT_COMMENTS_FIELD, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "openModal");
				dropdownItem.putData(
					"title",
					LanguageUtil.get(
						_httpServletRequest, "edit-engineer-comments"));
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_comments_field_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "edit-engineer-comments"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "openModal");
				dropdownItem.putData(
					"title",
					LanguageUtil.get(_httpServletRequest, "edit-fix-packs"));
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_fix_pack_fields_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit-fix-packs"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.BUILDS, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "openModal");
				dropdownItem.putData(
					"title",
					LanguageUtil.format(
						_httpServletRequest, "view-builds-for-fix-id-x",
						patcherFix.getPatcherFixId()));
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/view_builds_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("view");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "view-builds"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.FIXES, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "openModal");
				dropdownItem.putData(
					"title",
					LanguageUtil.format(
						_httpServletRequest, "view-fixes-for-fix-id-x",
						patcherFix.getPatcherFixId()));
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/view_fixes_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("view");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "view-fixes"));
			}
		).add(
			() ->
				PatcherPermission.contains(
					_themeDisplay.getPermissionChecker(), patcherFix,
					PatcherActionKeys.EXCLUDE, patcherFix.getUserId()) &&
				(patcherFix.getType() != PatcherFixConstants.TYPE_EXCLUDED) &&
				(patcherFix.getType() != PatcherFixConstants.TYPE_FIX_PACK),
			dropdownItem -> {
				dropdownItem.putData("action", "submitForm");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/exclude_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).buildString());
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "exclude"));
			}
		).build();
	}

	public SearchContainer<PatcherFix> getSearchContainer() throws Exception {
		if (_patcherPatcherFixSearchContainer != null) {
			return _patcherPatcherFixSearchContainer;
		}

		SearchContainer<PatcherFix> patcherPatcherFixSearchContainer =
			new SearchContainer<>(
				_renderRequest, _getPortletURL(), null, "there-are-no-fixes");

		Indexer<PatcherFix> indexer = IndexerRegistryUtil.getIndexer(
			PatcherFix.class);

		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		searchContext.setAttribute(
			"patcherProductVersionId", _getPatcherProducttVersionId());
		searchContext.setAttribute(
			"patcherProjectVersionId", _getPatcherProjectVersionId());
		searchContext.setAttribute("status", _getStatus());
		searchContext.setAttribute("type", _getType());
		searchContext.setEnd(patcherPatcherFixSearchContainer.getEnd());
		searchContext.setGroupIds(null);

		String patcherFixName = ParamUtil.getString(
			_httpServletRequest, "patcherFixName");

		if ((!PatcherUtil.isPatcherTickets(_getKeywords()) ||
			 PatcherUtil.isPatcherProjectVersionName(_getKeywords())) &&
			!PatcherUtil.isPatcherTickets(patcherFixName)) {

			searchContext.setSorts(
				new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true));
		}

		searchContext.setStart(patcherPatcherFixSearchContainer.getStart());

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

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private long _getPatcherProducttVersionId() {
		if (_patcherProductVersionId != null) {
			return _patcherProductVersionId;
		}

		_patcherProductVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProductVersionId");

		return _patcherProductVersionId;
	}

	private long _getPatcherProjectVersionId() {
		if (_patcherProjectVersionId != null) {
			return _patcherProjectVersionId;
		}

		_patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		return _patcherProjectVersionId;
	}

	private PortletURL _getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/patcher/index_fixes"
		).setKeywords(
			_getKeywords()
		).setTabs1(
			"fixes"
		).setParameter(
			"patcherProductVersionId", _getPatcherProducttVersionId()
		).setParameter(
			"patcherProjectVersionId", _getPatcherProjectVersionId()
		).setParameter(
			"status", _getStatus()
		).setParameter(
			"type", _getType()
		).buildPortletURL();

		return _portletURL;
	}

	private int _getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			_httpServletRequest, "status", WorkflowConstants.STATUS_ANY);

		return _status;
	}

	private int _getType() {
		if (_type != null) {
			return _type;
		}

		_type = ParamUtil.getInteger(_httpServletRequest, "type", -1);

		return _type;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private SearchContainer<PatcherFix> _patcherPatcherFixSearchContainer;
	private Long _patcherProductVersionId;
	private Long _patcherProjectVersionId;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Integer _status;
	private final ThemeDisplay _themeDisplay;
	private Integer _type;

}