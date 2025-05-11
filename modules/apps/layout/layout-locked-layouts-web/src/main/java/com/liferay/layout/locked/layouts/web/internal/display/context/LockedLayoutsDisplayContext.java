/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.model.LockedLayout;
import com.liferay.layout.model.LockedLayoutType;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

/**
 * @author Lourdes Fernández Besada
 */
public class LockedLayoutsDisplayContext {

	public LockedLayoutsDisplayContext(
		Language language, LayoutLocalService layoutLocalService,
		LayoutLockManager layoutLockManager,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, Portal portal) {

		_language = language;
		_layoutLocalService = layoutLocalService;
		_layoutLockManager = layoutLockManager;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getLastAutoSave(LockedLayout lockedLayout) {
		Date lastAutoSaveDate = lockedLayout.getLastAutoSaveDate();

		return _language.format(
			_themeDisplay.getLocale(), "x-ago",
			_language.getTimeDescription(
				_themeDisplay.getLocale(),
				System.currentTimeMillis() - lastAutoSaveDate.getTime(), true));
	}

	public String getLayoutType(LockedLayout lockedLayout) {
		LockedLayoutType lockedLayoutType = lockedLayout.getLockedLayoutType();

		if (lockedLayoutType == null) {
			return StringPool.BLANK;
		}

		return _language.get(
			_themeDisplay.getLocale(), lockedLayoutType.getValue());
	}

	public String getLayoutURL(LockedLayout lockedLayout)
		throws PortalException {

		if (lockedLayout.getLockedLayoutType() ==
				LockedLayoutType.CONTENT_PAGE_TEMPLATE) {

			return PortletURLBuilder.create(
				_getLayoutPageTemplatesPortletURL()
			).setTabs1(
				"page-templates"
			).buildString();
		}

		if (lockedLayout.getLockedLayoutType() ==
				LockedLayoutType.DISPLAY_PAGE_TEMPLATE) {

			return PortletURLBuilder.create(
				_getLayoutPageTemplatesPortletURL()
			).setTabs1(
				"display-page-templates"
			).buildString();
		}

		if (lockedLayout.getLockedLayoutType() ==
				LockedLayoutType.MASTER_PAGE) {

			return PortletURLBuilder.create(
				_getLayoutPageTemplatesPortletURL()
			).setTabs1(
				"master-layouts"
			).buildString();
		}

		Layout layout = _layoutLocalService.fetchLayout(lockedLayout.getPlid());

		return _portal.getLayoutFullURL(layout, _themeDisplay);
	}

	public List<DropdownItem> getLockedLayoutDropdownItems(
		LockedLayout lockedLayout) {

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					dropdownItem -> {
						dropdownItem.putData("action", "unlockLockedLayout");
						dropdownItem.putData(
							"unlockLockedLayoutURL",
							PortletURLBuilder.createActionURL(
								_liferayPortletResponse
							).setActionName(
								() -> {
									if (FeatureFlagManagerUtil.isEnabled(
											_themeDisplay.getCompanyId(),
											"LPD-11003")) {

										return "/locked_items/unlock_layouts";
									}

									return "/layout_locked_layouts" +
										"/unlock_layouts";
								}
							).setRedirect(
								_themeDisplay.getURLCurrent()
							).setParameter(
								"plid", lockedLayout.getPlid()
							).buildString());
						dropdownItem.setIcon("unlock");
						dropdownItem.setLabel(
							_language.get(_themeDisplay.getLocale(), "unlock"));
					}
				).build())
		).build();
	}

	public LockedLayoutOrder getLockedLayoutOrder() {
		if (_lockedLayoutOrder != null) {
			return _lockedLayoutOrder;
		}

		_lockedLayoutOrder = new LockedLayoutOrder(
			Objects.equals(getOrderByType(), "desc"), _themeDisplay.getLocale(),
			LockedLayoutOrder.LockedLayoutOrderType.create(getOrderByCol()));

		return _lockedLayoutOrder;
	}

	public LockedLayoutType getLockedLayoutType() {
		if (_lockedLayoutType != null) {
			return _lockedLayoutType;
		}

		_lockedLayoutType = LockedLayoutType.create(
			ParamUtil.getString(_liferayPortletRequest, "type"));

		return _lockedLayoutType;
	}

	public String getName(LockedLayout lockedLayout) {
		return lockedLayout.getName();
	}

	public String getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_liferayPortletRequest, "orderByCol",
			LockedLayoutOrder.LockedLayoutOrderType.LAST_AUTOSAVE.getValue());

		return _orderByCol;
	}

	public String getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_liferayPortletRequest, "orderByType", "desc");

		return _orderByType;
	}

	public SearchContainer<LockedLayout> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<LockedLayout> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, _liferayPortletResponse.createRenderURL(),
			null, "no-locked-pages-were-found");

		searchContainer.setResultsAndTotal(_getFilteredLockedLayouts());

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_liferayPortletResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public boolean hasLockedLayouts() {
		return ListUtil.isNotEmpty(_getLockedLayouts());
	}

	public static class LockedLayoutOrder {

		public LockedLayoutOrder(
			boolean ascending, Locale locale,
			LockedLayoutOrderType lockedLayoutOrderType) {

			_ascending = ascending;
			_locale = locale;
			_lockedLayoutOrderType = lockedLayoutOrderType;
		}

		public Locale getLocale() {
			return _locale;
		}

		public LockedLayoutOrderType getLockedLayoutOrderType() {
			return _lockedLayoutOrderType;
		}

		public boolean isAscending() {
			return _ascending;
		}

		public enum LockedLayoutOrderType {

			LAST_AUTOSAVE("last-autosave"), NAME("name"), USER("user");

			public static LockedLayoutOrderType create(String value) {
				if (Validator.isNull(value)) {
					return null;
				}

				for (LockedLayoutOrderType lockedLayoutType :
						LockedLayoutOrderType.values()) {

					if (Objects.equals(lockedLayoutType.getValue(), value)) {
						return lockedLayoutType;
					}
				}

				return null;
			}

			public String getValue() {
				return _value;
			}

			private LockedLayoutOrderType(String value) {
				_value = value;
			}

			private final String _value;

		}

		private final boolean _ascending;
		private final Locale _locale;
		private final LockedLayoutOrderType _lockedLayoutOrderType;

	}

	private List<LockedLayout> _getFilteredLockedLayouts() {
		if (_filteredLockedLayouts != null) {
			return _filteredLockedLayouts;
		}

		if (Validator.isNull(_getKeywords())) {
			_filteredLockedLayouts = _getLockedLayouts();

			return _filteredLockedLayouts;
		}

		_filteredLockedLayouts = ListUtil.filter(
			_getLockedLayouts(),
			lockedLayout -> _hasKeywords(_getKeywords(), lockedLayout));

		return _filteredLockedLayouts;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = StringUtil.toLowerCase(
			ParamUtil.getString(_liferayPortletRequest, "keywords"));

		return _keywords;
	}

	private PortletURL _getLayoutPageTemplatesPortletURL() {
		if (_layoutPageTemplatesPortletURL != null) {
			return _layoutPageTemplatesPortletURL;
		}

		_layoutPageTemplatesPortletURL = _portal.getControlPanelPortletURL(
			_portal.getHttpServletRequest(_liferayPortletRequest),
			_themeDisplay.getScopeGroup(),
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES, 0, 0,
			PortletRequest.RENDER_PHASE);

		return _layoutPageTemplatesPortletURL;
	}

	private List<LockedLayout> _getLockedLayouts() {
		if (_lockedLayouts != null) {
			return _lockedLayouts;
		}

		List<LockedLayout> lockedLayouts = _layoutLockManager.getLockedLayouts(
			_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId(),
			_themeDisplay.getLocale());

		if (ListUtil.isEmpty(lockedLayouts)) {
			_lockedLayouts = lockedLayouts;

			return _lockedLayouts;
		}

		LockedLayoutType lockedLayoutType = getLockedLayoutType();

		if (lockedLayoutType != null) {
			lockedLayouts = ListUtil.filter(
				lockedLayouts,
				lockedLayout ->
					lockedLayout.getLockedLayoutType() == lockedLayoutType);
		}

		String orderByCol = getOrderByCol();
		String orderByType = getOrderByType();

		Comparator<LockedLayout> lockedLayoutComparator = null;

		if (Objects.equals(
				orderByCol,
				LockedLayoutOrder.LockedLayoutOrderType.LAST_AUTOSAVE.
					getValue())) {

			lockedLayoutComparator = Comparator.comparing(
				lockedLayout -> lockedLayout.getLastAutoSaveDate());
		}
		else if (Objects.equals(
					orderByCol,
					LockedLayoutOrder.LockedLayoutOrderType.NAME.getValue())) {

			lockedLayoutComparator = Comparator.comparing(
				LockedLayout::getName,
				CollatorUtil.getInstance(_themeDisplay.getLocale()));
		}
		else {
			lockedLayoutComparator = Comparator.comparing(
				LockedLayout::getUserName,
				CollatorUtil.getInstance(_themeDisplay.getLocale()));
		}

		if (Objects.equals(orderByType, "desc")) {
			lockedLayoutComparator = lockedLayoutComparator.reversed();
		}

		lockedLayouts.sort(lockedLayoutComparator);

		_lockedLayouts = lockedLayouts;

		return _lockedLayouts;
	}

	private boolean _hasKeywords(String keywords, LockedLayout lockedLayout) {
		if (StringUtil.contains(
				StringUtil.toLowerCase(lockedLayout.getUserName()), keywords,
				StringPool.BLANK) ||
			StringUtil.contains(
				StringUtil.toLowerCase(getName(lockedLayout)), keywords,
				StringPool.BLANK)) {

			return true;
		}

		return false;
	}

	private List<LockedLayout> _filteredLockedLayouts;
	private String _keywords;
	private final Language _language;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutLockManager _layoutLockManager;
	private PortletURL _layoutPageTemplatesPortletURL;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private LockedLayoutOrder _lockedLayoutOrder;
	private List<LockedLayout> _lockedLayouts;
	private LockedLayoutType _lockedLayoutType;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;
	private SearchContainer<LockedLayout> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}