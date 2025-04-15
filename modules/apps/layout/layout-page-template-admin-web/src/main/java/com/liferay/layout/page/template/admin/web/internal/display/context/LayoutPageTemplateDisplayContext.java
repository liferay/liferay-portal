/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateCollectionPermission;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplatePermission;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class LayoutPageTemplateDisplayContext {

	public LayoutPageTemplateDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/layout_page_template_admin" +
						"/edit_layout_page_template_collection",
					"redirect", _themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new"));
			}
		).build();
	}

	public List<DropdownItem> getCollectionsDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			() -> LayoutPageTemplateCollectionPermission.contains(
				_themeDisplay.getPermissionChecker(),
				getLayoutPageTemplateCollectionId(), ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.putData("action", "deleteCollections");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).build();
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public LayoutPageTemplateCollection getLayoutPageTemplateCollection()
		throws PortalException {

		if (_layoutPageTemplateCollection != null) {
			return _layoutPageTemplateCollection;
		}

		_layoutPageTemplateCollection =
			LayoutPageTemplateCollectionServiceUtil.
				fetchLayoutPageTemplateCollection(
					getLayoutPageTemplateCollectionId());

		return _layoutPageTemplateCollection;
	}

	public long getLayoutPageTemplateCollectionId() {
		if (Validator.isNotNull(_layoutPageTemplateCollectionId)) {
			return _layoutPageTemplateCollectionId;
		}

		long defaultLayoutPageTemplateCollectionId = 0;

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			getLayoutPageTemplateCollections();

		if (ListUtil.isNotEmpty(layoutPageTemplateCollections)) {
			LayoutPageTemplateCollection layoutPageTemplateCollection =
				layoutPageTemplateCollections.get(0);

			defaultLayoutPageTemplateCollectionId =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();
		}

		long layoutPageTemplateCollectionId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateCollectionId");

		if (layoutPageTemplateCollectionId <= 0) {
			layoutPageTemplateCollectionId =
				defaultLayoutPageTemplateCollectionId;
		}

		_layoutPageTemplateCollectionId = layoutPageTemplateCollectionId;

		return _layoutPageTemplateCollectionId;
	}

	public List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections() {

		if (_layoutPageTemplateCollections != null) {
			return _layoutPageTemplateCollections;
		}

		_layoutPageTemplateCollections =
			LayoutPageTemplateCollectionServiceUtil.
				getLayoutPageTemplateCollections(
					_themeDisplay.getScopeGroupId(),
					LayoutPageTemplateEntryTypeConstants.BASIC);

		return _layoutPageTemplateCollections;
	}

	public SearchContainer<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesSearchContainer() {

		if (_layoutPageTemplateEntriesSearchContainer != null) {
			return _layoutPageTemplateEntriesSearchContainer;
		}

		SearchContainer<LayoutPageTemplateEntry>
			layoutPageTemplateEntriesSearchContainer = new SearchContainer(
				_renderRequest, getPortletURL(), null,
				"there-are-no-page-templates");

		layoutPageTemplateEntriesSearchContainer.setOrderByCol(getOrderByCol());
		layoutPageTemplateEntriesSearchContainer.setOrderByComparator(
			LayoutPageTemplatePortletUtil.
				getLayoutPageTemplateEntryOrderByComparator(
					getOrderByCol(), getOrderByType()));
		layoutPageTemplateEntriesSearchContainer.setOrderByType(
			getOrderByType());

		if (isSearch()) {
			layoutPageTemplateEntriesSearchContainer.setResultsAndTotal(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntries(
							_themeDisplay.getScopeGroupId(),
							getLayoutPageTemplateCollectionId(), getKeywords(),
							layoutPageTemplateEntriesSearchContainer.getStart(),
							layoutPageTemplateEntriesSearchContainer.getEnd(),
							layoutPageTemplateEntriesSearchContainer.
								getOrderByComparator()),
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						_themeDisplay.getScopeGroupId(),
						getLayoutPageTemplateCollectionId(), getKeywords()));
		}
		else {
			layoutPageTemplateEntriesSearchContainer.setResultsAndTotal(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntries(
							_themeDisplay.getScopeGroupId(),
							getLayoutPageTemplateCollectionId(),
							layoutPageTemplateEntriesSearchContainer.getStart(),
							layoutPageTemplateEntriesSearchContainer.getEnd(),
							layoutPageTemplateEntriesSearchContainer.
								getOrderByComparator()),
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						_themeDisplay.getScopeGroupId(),
						getLayoutPageTemplateCollectionId()));
		}

		layoutPageTemplateEntriesSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_layoutPageTemplateEntriesSearchContainer =
			layoutPageTemplateEntriesSearchContainer;

		return _layoutPageTemplateEntriesSearchContainer;
	}

	public LayoutPageTemplateEntry getLayoutPageTemplateEntry()
		throws PortalException {

		if (_layoutPageTemplateEntry != null) {
			return _layoutPageTemplateEntry;
		}

		_layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntry(
				getLayoutPageTemplateEntryId());

		return _layoutPageTemplateEntry;
	}

	public long getLayoutPageTemplateEntryId() {
		if (Validator.isNotNull(_layoutPageTemplateEntryId)) {
			return _layoutPageTemplateEntryId;
		}

		_layoutPageTemplateEntryId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateEntryId");

		return _layoutPageTemplateEntryId;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			"layout-page-template-order-by-col", "create-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			"layout-page-template-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
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
			"page-templates"
		).setParameter(
			"layoutPageTemplateCollectionId",
			() -> {
				long layoutPageTemplateCollectionId =
					getLayoutPageTemplateCollectionId();

				if (layoutPageTemplateCollectionId > 0) {
					return layoutPageTemplateCollectionId;
				}

				return null;
			}
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

	public VerticalNavItemList getVerticalNavItemList() {
		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				getLayoutPageTemplateCollections()) {

			verticalNavItemList.add(
				verticalNavItem -> {
					String name = layoutPageTemplateCollection.getName();

					long layoutPageTemplateCollectionId =
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId();

					verticalNavItem.setActive(
						layoutPageTemplateCollectionId ==
							getLayoutPageTemplateCollectionId());

					verticalNavItem.setHref(
						PortletURLBuilder.createRenderURL(
							_renderResponse
						).setTabs1(
							"page-templates"
						).setParameter(
							"layoutPageTemplateCollectionId",
							layoutPageTemplateCollection.
								getLayoutPageTemplateCollectionId()
						).buildString());
					verticalNavItem.setId(name);
					verticalNavItem.setLabel(name);
				});
		}

		return verticalNavItemList;
	}

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	public boolean isShowAddButton(String actionId) {
		return LayoutPageTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getSiteGroupId(), actionId);
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private LayoutPageTemplateCollection _layoutPageTemplateCollection;
	private Long _layoutPageTemplateCollectionId;
	private List<LayoutPageTemplateCollection> _layoutPageTemplateCollections;
	private SearchContainer<LayoutPageTemplateEntry>
		_layoutPageTemplateEntriesSearchContainer;
	private LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private Long _layoutPageTemplateEntryId;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}