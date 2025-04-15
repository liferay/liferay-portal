/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.notifications.web.internal.constants.NotificationsPortletKeys;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sergio González
 */
public class NotificationsManagementToolbarDisplayContext {

	public NotificationsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletURL currentURLObj) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_currentURLObj = currentURLObj;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			() -> !_isActionRequired(),
			dropdownItem -> {
				dropdownItem.putData("action", "markNotificationsAsRead");
				dropdownItem.setIcon("envelope-open");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "mark-as-read"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			() -> !_isActionRequired(),
			dropdownItem -> {
				dropdownItem.putData("action", "markNotificationsAsUnread");
				dropdownItem.setIcon("envelope-closed");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "mark-as-unread"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteNotifications");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public List<String> getAvailableActions(
		UserNotificationEvent userNotificationEvent,
		UserNotificationFeedEntry userNotificationFeedEntry) {

		List<String> availableActions = new ArrayList<>();

		if ((userNotificationFeedEntry == null) ||
			!userNotificationFeedEntry.isApplicable()) {

			return availableActions;
		}

		if (!userNotificationFeedEntry.isActionable()) {
			availableActions.add("deleteNotifications");
		}

		if (!userNotificationEvent.isActionRequired()) {
			availableActions.add("markNotificationsAsRead");
			availableActions.add("markNotificationsAsUnread");
		}

		return availableActions;
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setParameter(
			"actionRequired", _isActionRequired()
		).buildString();
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			() -> !_isActionRequired(),
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "filter-by"));
			}
		).build();
	}

	public List<LabelItem> getFilterLabelItems() {
		String navigation = _getNavigation();

		return LabelItemListBuilder.add(
			() -> navigation.equals("read") || navigation.equals("unread"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							_currentURLObj, _liferayPortletResponse)
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, navigation));
			}
		).build();
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(true);
				dropdownItem.setHref(getSortingURL());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "date"));
			}
		).build();
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, NotificationsPortletKeys.NOTIFICATIONS,
			"desc");

		return _orderByType;
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(_currentURLObj, _liferayPortletResponse)
		).setParameter(
			SearchContainer.DEFAULT_CUR_PARAM, "0"
		).setParameter(
			"orderByCol", "date"
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildPortletURL();
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		String navigation = _getNavigation();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(navigation.equals("all"));
				dropdownItem.setHref(
					PortletURLUtil.clone(
						_currentURLObj, _liferayPortletResponse),
					SearchContainer.DEFAULT_CUR_PARAM, "0", "navigation",
					"all");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(navigation.equals("unread"));
				dropdownItem.setHref(
					PortletURLUtil.clone(
						_currentURLObj, _liferayPortletResponse),
					SearchContainer.DEFAULT_CUR_PARAM, "0", "navigation",
					"unread");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "unread"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(navigation.equals("read"));
				dropdownItem.setHref(
					PortletURLUtil.clone(
						_currentURLObj, _liferayPortletResponse),
					SearchContainer.DEFAULT_CUR_PARAM, "0", "navigation",
					"read");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "read"));
			}
		).build();
	}

	private String _getNavigation() {
		return ParamUtil.getString(_httpServletRequest, "navigation", "all");
	}

	private boolean _isActionRequired() {
		return ParamUtil.getBoolean(_httpServletRequest, "actionRequired");
	}

	private final PortletURL _currentURLObj;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByType;

}