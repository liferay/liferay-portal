/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.admin.web.internal.security.permission.resource.LayoutUtilityPageEntryPermission;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRenderer;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRendererRegistryUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class LayoutUtilityPageEntryDisplayContext {

	public LayoutUtilityPageEntryDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAvailableActions(
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (LayoutUtilityPageEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), layoutUtilityPageEntry,
				ActionKeys.DELETE)) {

			availableActions.add("deleteSelectedLayoutUtilityPageEntries");
		}

		Layout draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layoutUtilityPageEntry.getPlid());

		if (!draftLayout.isDraft()) {
			availableActions.add("exportLayoutUtilityPageEntries");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	public SearchContainer<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntrySearchContainer() {

		if (_layoutUtilityPageEntrySearchContainer != null) {
			return _layoutUtilityPageEntrySearchContainer;
		}

		SearchContainer<LayoutUtilityPageEntry>
			layoutUtilityPageEntrySearchContainer = new SearchContainer<>(
				_renderRequest, _getPortletURL(), null,
				"there-are-no-utility-pages");

		layoutUtilityPageEntrySearchContainer.setOrderByCol(getOrderByCol());
		layoutUtilityPageEntrySearchContainer.setOrderByType(getOrderByType());

		String[] types = TransformUtil.transformToArray(
			LayoutUtilityPageEntryViewRendererRegistryUtil.
				getLayoutUtilityPageEntryViewRenderers(),
			LayoutUtilityPageEntryViewRenderer::getType, String.class);

		if (Validator.isNotNull(_getKeywords())) {
			layoutUtilityPageEntrySearchContainer.setResultsAndTotal(
				() ->
					LayoutUtilityPageEntryServiceUtil.
						getLayoutUtilityPageEntries(
							_themeDisplay.getScopeGroupId(), _getKeywords(),
							types,
							layoutUtilityPageEntrySearchContainer.getStart(),
							layoutUtilityPageEntrySearchContainer.getEnd(),
							null),
				LayoutUtilityPageEntryServiceUtil.
					getLayoutUtilityPageEntriesCount(
						_themeDisplay.getScopeGroupId(), _getKeywords(),
						types));
		}
		else {
			layoutUtilityPageEntrySearchContainer.setResultsAndTotal(
				() ->
					LayoutUtilityPageEntryServiceUtil.
						getLayoutUtilityPageEntries(
							_themeDisplay.getScopeGroupId(), types,
							layoutUtilityPageEntrySearchContainer.getStart(),
							layoutUtilityPageEntrySearchContainer.getEnd(),
							null),
				LayoutUtilityPageEntryServiceUtil.
					getLayoutUtilityPageEntriesCount(
						_themeDisplay.getScopeGroupId(), types));
		}

		layoutUtilityPageEntrySearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_layoutUtilityPageEntrySearchContainer =
			layoutUtilityPageEntrySearchContainer;

		return _layoutUtilityPageEntrySearchContainer;
	}

	protected String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM,
			"modified-date");

		return _orderByCol;
	}

	protected String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, "asc");

		return _orderByType;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setTabs1(
			_getTabs1()
		).buildPortletURL();
	}

	private String _getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_renderRequest, "tabs1");

		return _tabs1;
	}

	private String _keywords;
	private SearchContainer<LayoutUtilityPageEntry>
		_layoutUtilityPageEntrySearchContainer;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private String _tabs1;
	private final ThemeDisplay _themeDisplay;

}