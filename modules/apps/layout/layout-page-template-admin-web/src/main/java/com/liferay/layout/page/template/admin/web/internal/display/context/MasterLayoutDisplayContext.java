/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class MasterLayoutDisplayContext {

	public MasterLayoutDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public long getLayoutPageTemplateEntryId() {
		if (Validator.isNotNull(_layoutPageTemplateEntryId)) {
			return _layoutPageTemplateEntryId;
		}

		_layoutPageTemplateEntryId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateEntryId");

		return _layoutPageTemplateEntryId;
	}

	public SearchContainer<LayoutPageTemplateEntry>
		getMasterLayoutsSearchContainer() {

		if (_masterLayoutsSearchContainer != null) {
			return _masterLayoutsSearchContainer;
		}

		SearchContainer<LayoutPageTemplateEntry> masterLayoutsSearchContainer =
			new SearchContainer(
				_renderRequest, getPortletURL(), null,
				"there-are-no-master-pages");

		masterLayoutsSearchContainer.setOrderByCol(getOrderByCol());
		masterLayoutsSearchContainer.setOrderByComparator(
			LayoutPageTemplatePortletUtil.
				getLayoutPageTemplateEntryOrderByComparator(
					getOrderByCol(), getOrderByType()));
		masterLayoutsSearchContainer.setOrderByType(getOrderByType());

		if (isSearch()) {
			masterLayoutsSearchContainer.setResultsAndTotal(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntries(
							_themeDisplay.getScopeGroupId(), getKeywords(),
							LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
							masterLayoutsSearchContainer.getStart(),
							masterLayoutsSearchContainer.getEnd(),
							masterLayoutsSearchContainer.
								getOrderByComparator()),
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						_themeDisplay.getScopeGroupId(), getKeywords(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT));
		}
		else {
			List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
				new ArrayList<>();

			int start = masterLayoutsSearchContainer.getStart();
			int end = masterLayoutsSearchContainer.getEnd();

			if (start == 0) {
				end -= 1;
				layoutPageTemplateEntries.add(_addBlankMasterLayout());
			}
			else {
				start -= 1;
			}

			layoutPageTemplateEntries.addAll(
				LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
					_themeDisplay.getScopeGroupId(),
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, start,
					end, masterLayoutsSearchContainer.getOrderByComparator()));

			int layoutPageTemplateEntriesCount =
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						_themeDisplay.getScopeGroupId(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

			masterLayoutsSearchContainer.setResultsAndTotal(
				() -> layoutPageTemplateEntries,
				layoutPageTemplateEntriesCount + 1);
		}

		masterLayoutsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_masterLayoutsSearchContainer = masterLayoutsSearchContainer;

		return _masterLayoutsSearchContainer;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			"master-layout-order-by-col", "create-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			"master-layout-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_master_layouts.jsp"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setKeywords(
			() -> {
				String keywords = getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setTabs1(
			"master-layouts"
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).buildPortletURL();
	}

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	private LayoutPageTemplateEntry _addBlankMasterLayout() {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				createLayoutPageTemplateEntry(0);

		layoutPageTemplateEntry.setName(
			LanguageUtil.get(_httpServletRequest, "blank"));
		layoutPageTemplateEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

		return layoutPageTemplateEntry;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private Long _layoutPageTemplateEntryId;
	private SearchContainer<LayoutPageTemplateEntry>
		_masterLayoutsSearchContainer;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}