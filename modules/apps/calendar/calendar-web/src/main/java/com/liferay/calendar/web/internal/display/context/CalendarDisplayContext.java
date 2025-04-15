/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.display.context;

import com.liferay.calendar.constants.CalendarActionKeys;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.service.CalendarBookingService;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.CalendarService;
import com.liferay.calendar.util.RecurrenceUtil;
import com.liferay.calendar.util.comparator.CalendarResourceNameComparator;
import com.liferay.calendar.web.internal.search.CalendarResourceDisplayTerms;
import com.liferay.calendar.web.internal.search.CalendarResourceSearch;
import com.liferay.calendar.web.internal.search.CalendarSearchContainer;
import com.liferay.calendar.web.internal.security.permission.resource.CalendarPermission;
import com.liferay.calendar.web.internal.security.permission.resource.CalendarPortletPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.kernel.util.comparator.UserScreenNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletSession;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Adam Brandizzi
 */
public class CalendarDisplayContext {

	public CalendarDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		GroupLocalService groupLocalService,
		CalendarBookingLocalService calendarBookingLocalService,
		CalendarBookingService calendarBookingService,
		CalendarLocalService calendarLocalService,
		CalendarResourceLocalService calendarResourceLocalService,
		CalendarService calendarService) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_groupLocalService = groupLocalService;
		_calendarBookingLocalService = calendarBookingLocalService;
		_calendarBookingService = calendarBookingService;
		_calendarLocalService = calendarLocalService;
		_calendarResourceLocalService = calendarResourceLocalService;
		_calendarService = calendarService;

		_portletSession = renderRequest.getPortletSession();

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_portletId = portletDisplay.getId();
	}

	public List<CalendarBooking> getChildCalendarBookings(
			CalendarBooking calendarBooking)
		throws PortalException {

		Group group = _themeDisplay.getScopeGroup();

		return _calendarBookingService.getChildCalendarBookings(
			calendarBooking.getCalendarBookingId(), group.isStagingGroup());
	}

	public String getClearResultsURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), _renderResponse)
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() {
		if (!_isShowAddResourceButton()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				HttpServletRequest httpServletRequest =
					PortalUtil.getHttpServletRequest(_renderRequest);

				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_calendar_resource.jsp", "redirect",
					PortalUtil.getCurrentURL(httpServletRequest));
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "add-calendar-resource"));
			}
		).build();
	}

	public Calendar getDefaultCalendar(
			List<Calendar> groupCalendars, List<Calendar> userCalendars)
		throws PortalException {

		Calendar defaultCalendar = null;

		for (Calendar groupCalendar : groupCalendars) {
			if (groupCalendar.isDefaultCalendar() &&
				CalendarPermission.contains(
					_themeDisplay.getPermissionChecker(), groupCalendar,
					CalendarActionKeys.MANAGE_BOOKINGS)) {

				defaultCalendar = groupCalendar;
			}
		}

		if (defaultCalendar == null) {
			for (Calendar userCalendar : userCalendars) {
				if (userCalendar.isDefaultCalendar()) {
					defaultCalendar = userCalendar;
				}
			}
		}

		if (defaultCalendar == null) {
			for (Calendar groupCalendar : groupCalendars) {
				if (CalendarPermission.contains(
						_themeDisplay.getPermissionChecker(), groupCalendar,
						CalendarActionKeys.MANAGE_BOOKINGS)) {

					defaultCalendar = groupCalendar;
				}
			}
		}

		if (defaultCalendar == null) {
			for (Calendar groupCalendar : groupCalendars) {
				if (groupCalendar.isDefaultCalendar() &&
					CalendarPermission.contains(
						_themeDisplay.getPermissionChecker(), groupCalendar,
						CalendarActionKeys.VIEW_BOOKING_DETAILS)) {

					defaultCalendar = groupCalendar;
				}
			}
		}

		if (defaultCalendar == null) {
			for (Calendar groupCalendar : groupCalendars) {
				if (CalendarPermission.contains(
						_themeDisplay.getPermissionChecker(), groupCalendar,
						CalendarActionKeys.VIEW_BOOKING_DETAILS)) {

					defaultCalendar = groupCalendar;
				}
			}
		}

		return defaultCalendar;
	}

	public String getEditCalendarBookingRedirectURL(
		HttpServletRequest httpServletRequest, String defaultURL) {

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		String ppid = HttpComponentsUtil.getParameter(
			redirect, "p_p_id", false);

		if (ppid.equals(CalendarPortletKeys.CALENDAR)) {
			return defaultURL;
		}

		return ParamUtil.getString(httpServletRequest, "redirect", defaultURL);
	}

	public List<DropdownItem> getFilterItemsDropdownItems() {
		HttpServletRequest httpServletRequest = _themeDisplay.getRequest();

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterActiveDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "active"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(_getScopeDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "scope"));
			}
		).build();
	}

	public SearchContainer<Group> getGroupSearchContainer()
		throws PortalException {

		if (_groupSearchContainer != null) {
			return _groupSearchContainer;
		}

		_groupSearchContainer = new CalendarSearchContainer(
			_renderRequest, CalendarResourceSearch.DEFAULT_CUR_PARAM + "Groups",
			_getIteratorURL());

		_groupSearchContainer.setId("sites");
		_groupSearchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		_groupSearchContainer.setOrderByComparator(
			new GroupNameComparator(orderByAsc));
		_groupSearchContainer.setOrderByType(getOrderByType());
		_groupSearchContainer.setResultsAndTotal(
			() -> GroupServiceUtil.search(
				_themeDisplay.getCompanyId(), _getClassNameIds(), getKeywords(),
				MapUtil.toLinkedHashMap(new String[] {"site:true:boolean"}),
				_groupSearchContainer.getStart(),
				_groupSearchContainer.getEnd(),
				_groupSearchContainer.getOrderByComparator()),
			GroupServiceUtil.searchCount(
				_themeDisplay.getCompanyId(), getKeywords(), getKeywords(),
				new String[] {"site:true:boolean"}));

		return _groupSearchContainer;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	public Recurrence getLastRecurrence(CalendarBooking calendarBooking)
		throws PortalException {

		CalendarBooking lastCalendarBooking =
			RecurrenceUtil.getLastInstanceCalendarBooking(
				_calendarBookingLocalService.getRecurringCalendarBookings(
					calendarBooking));

		return lastCalendarBooking.getRecurrenceObj();
	}

	public List<NavigationItem> getNavigationItems() {
		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(_renderRequest);

		String tabs1 = ParamUtil.getString(
			httpServletRequest, "tabs1", "calendar");

		return NavigationItemList.of(
			NavigationItemBuilder.setActive(
				tabs1.equals("calendar")
			).setHref(
				_renderResponse.createRenderURL(), "tabs1", "calendar"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "calendar")
			).build(),
			NavigationItemBuilder.setActive(
				tabs1.equals("resources")
			).setHref(
				_renderResponse.createRenderURL(), "tabs1", "resources",
				"scope",
				ParamUtil.getString(
					_renderRequest, "scope",
					String.valueOf(_themeDisplay.getScopeGroupId())),
				"active",
				ParamUtil.getString(
					_renderRequest, "active", Boolean.TRUE.toString())
			).setLabel(
				LanguageUtil.get(httpServletRequest, "resources")
			).build());
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		String orderByCol = ParamUtil.getString(_renderRequest, "orderByCol");

		if (Validator.isNull(orderByCol)) {
			orderByCol = _getPortletPreference("order-by-col", "name");
		}
		else {
			_setPortletPreference("order-by-col", orderByCol);
		}

		_orderByCol = orderByCol;

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		String orderByType = ParamUtil.getString(_renderRequest, "orderByType");

		if (Validator.isNull(orderByType)) {
			orderByType = _getPortletPreference("order-by-type", "asc");
		}
		else {
			_setPortletPreference("order-by-type", orderByType);
		}

		_orderByType = orderByType;

		return _orderByType;
	}

	public List<DropdownItem> getOrderItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(getOrderByCol(), "name"));
				dropdownItem.setHref(getPortletURL(), "orderByCol", "name");
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getRequest(), "name"));
			}
		).build();
	}

	public List<Calendar> getOtherCalendars(User user, long[] calendarIds)
		throws PortalException {

		List<Calendar> otherCalendars = new ArrayList<>();

		for (long calendarId : calendarIds) {
			Calendar calendar = null;

			try {
				calendar = _calendarService.fetchCalendar(calendarId);
			}
			catch (PrincipalException principalException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"No ", ActionKeys.VIEW, " permission for user ",
							user.getUserId()),
						principalException);
				}

				continue;
			}

			if (calendar == null) {
				continue;
			}

			CalendarResource calendarResource = calendar.getCalendarResource();

			if (!calendarResource.isActive()) {
				continue;
			}

			Group scopeGroup = _themeDisplay.getScopeGroup();

			Group calendarGroup = _groupLocalService.getGroup(
				calendar.getGroupId());

			if (scopeGroup.isStagingGroup()) {
				long calendarGroupId = calendarGroup.getGroupId();

				if (calendarGroup.isStagingGroup()) {
					if (scopeGroup.getGroupId() != calendarGroupId) {
						calendar =
							_calendarLocalService.fetchCalendarByUuidAndGroupId(
								calendar.getUuid(),
								calendarGroup.getLiveGroupId());
					}
				}
				else if (scopeGroup.getLiveGroupId() == calendarGroupId) {
					Group stagingGroup = calendarGroup.getStagingGroup();

					calendar =
						_calendarLocalService.fetchCalendarByUuidAndGroupId(
							calendar.getUuid(), stagingGroup.getGroupId());
				}
			}
			else if (calendarGroup.isStagingGroup()) {
				calendar = _calendarLocalService.fetchCalendarByUuidAndGroupId(
					calendar.getUuid(), calendarGroup.getLiveGroupId());
			}

			if (calendar == null) {
				continue;
			}

			otherCalendars.add(calendar);
		}

		return otherCalendars;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setKeywords(
			() -> {
				String keywords = getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setTabs1(
			"resources"
		).setParameter(
			"active", ParamUtil.getString(_renderRequest, "active")
		).setParameter(
			"order-by-col", ParamUtil.getString(_renderRequest, "order-by-col")
		).setParameter(
			"order-by-type",
			ParamUtil.getString(_renderRequest, "order-by-type")
		).setParameter(
			"scope", ParamUtil.getString(_renderRequest, "scope")
		).buildPortletURL();
	}

	public SearchContainer<?> getSearch() {
		CalendarResourceSearch calendarResourceSearch =
			new CalendarResourceSearch(
				_renderRequest, CalendarResourceSearch.DEFAULT_CUR_PARAM,
				getPortletURL());

		calendarResourceSearch.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		calendarResourceSearch.setOrderByComparator(
			CalendarResourceNameComparator.getInstance(orderByAsc));
		calendarResourceSearch.setOrderByType(getOrderByType());

		CalendarResourceDisplayTerms displayTerms =
			new CalendarResourceDisplayTerms(_renderRequest);

		calendarResourceSearch.setResultsAndTotal(
			() -> _calendarResourceLocalService.searchByKeywords(
				_themeDisplay.getCompanyId(),
				new long[] {_themeDisplay.getScopeGroupId()},
				new long[] {
					PortalUtil.getClassNameId(CalendarResource.class.getName())
				},
				getKeywords(), displayTerms.isActive(),
				displayTerms.isAndOperator(), calendarResourceSearch.getStart(),
				calendarResourceSearch.getEnd(),
				calendarResourceSearch.getOrderByComparator()),
			_calendarResourceLocalService.searchCount(
				_themeDisplay.getCompanyId(),
				new long[] {_themeDisplay.getScopeGroupId()},
				new long[] {
					PortalUtil.getClassNameId(CalendarResource.class.getName())
				},
				getKeywords(), displayTerms.isActive()));

		return calendarResourceSearch;
	}

	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	public String getSearchContainerId() {
		return "resource";
	}

	public String getSortingURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public int getTotalItems() {
		SearchContainer<?> searchContainer = getSearch();

		return searchContainer.getTotal();
	}

	public SearchContainer<User> getUserSearchContainer()
		throws PortalException {

		if (_userSearchContainer != null) {
			return _userSearchContainer;
		}

		_userSearchContainer = new CalendarSearchContainer(
			_renderRequest, CalendarResourceSearch.DEFAULT_CUR_PARAM + "Users",
			_getIteratorURL());

		_userSearchContainer.setId("users");
		_userSearchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		_userSearchContainer.setOrderByComparator(
			UserScreenNameComparator.getInstance(orderByAsc));
		_userSearchContainer.setOrderByType(getOrderByType());
		_userSearchContainer.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				_themeDisplay.getCompanyId(), getKeywords(),
				WorkflowConstants.STATUS_ANY, null,
				_userSearchContainer.getStart(), _userSearchContainer.getEnd(),
				_userSearchContainer.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				_themeDisplay.getCompanyId(), getKeywords(),
				WorkflowConstants.STATUS_ANY, null));

		return _userSearchContainer;
	}

	public boolean isDisabledManagementBar() {
		if (_hasResults() || _isSearch()) {
			return false;
		}

		return true;
	}

	private long[] _getClassNameIds() {
		if (_classNameIds != null) {
			return _classNameIds;
		}

		_classNameIds = new long[] {
			PortalUtil.getClassNameId(Group.class),
			PortalUtil.getClassNameId(Organization.class)
		};

		return _classNameIds;
	}

	private List<DropdownItem> _getFilterActiveDropdownItems() {
		CalendarResourceDisplayTerms displayTerms =
			new CalendarResourceDisplayTerms(_renderRequest);

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(displayTerms.isActive());
				dropdownItem.setHref(getPortletURL(), "active", "true");
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getRequest(), "yes"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(!displayTerms.isActive());
				dropdownItem.setHref(getPortletURL(), "active", "false");
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getRequest(), "no"));
			}
		).build();
	}

	private PortletURL _getIteratorURL() {
		if (_iteratorURL != null) {
			return _iteratorURL;
		}

		_iteratorURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setTabs1(
			"resources"
		).buildPortletURL();

		return _iteratorURL;
	}

	private String _getPortletPreference(String name, String defaultValue) {
		if (_themeDisplay.isSignedIn()) {
			PortletPreferences portletPreferences = _getPortletPreferences();

			return portletPreferences.getValue(name, defaultValue);
		}

		return GetterUtil.getString(
			_portletSession.getAttribute(
				_portletId + StringPool.UNDERLINE + name),
			defaultValue);
	}

	private PortletPreferences _getPortletPreferences() {
		if (_portletPreferences != null) {
			return _portletPreferences;
		}

		_portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				_themeDisplay.getCompanyId(), _themeDisplay.getUserId(),
				PortletKeys.PREFS_OWNER_TYPE_USER, _themeDisplay.getPlid(),
				_portletId, StringPool.BLANK);

		return _portletPreferences;
	}

	private List<DropdownItem> _getScopeDropdownItems() {
		CalendarResourceDisplayTerms displayTerms =
			new CalendarResourceDisplayTerms(_renderRequest);

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					displayTerms.getScope() == _themeDisplay.getScopeGroupId());
				dropdownItem.setHref(
					getPortletURL(), "scope", _themeDisplay.getScopeGroupId());
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getRequest(), "current"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					displayTerms.getScope() ==
						_themeDisplay.getCompanyGroupId());
				dropdownItem.setHref(
					getPortletURL(), "scope",
					_themeDisplay.getCompanyGroupId());
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getRequest(), "global"));
			}
		).build();
	}

	private boolean _hasResults() {
		if (getTotalItems() > 0) {
			return true;
		}

		return false;
	}

	private boolean _isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	private boolean _isShowAddResourceButton() {
		return CalendarPortletPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), CalendarActionKeys.ADD_RESOURCE);
	}

	private void _setPortletPreference(String name, String value) {
		if (_themeDisplay.isSignedIn()) {
			PortletPreferences portletPreferences = _getPortletPreferences();

			try {
				portletPreferences.setValue(name, value);

				portletPreferences.store();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
		else {
			_portletSession.setAttribute(
				_portletId + StringPool.UNDERLINE + name, value);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarDisplayContext.class.getName());

	private final CalendarBookingLocalService _calendarBookingLocalService;
	private final CalendarBookingService _calendarBookingService;
	private final CalendarLocalService _calendarLocalService;
	private final CalendarResourceLocalService _calendarResourceLocalService;
	private final CalendarService _calendarService;
	private long[] _classNameIds;
	private final GroupLocalService _groupLocalService;
	private SearchContainer<Group> _groupSearchContainer;
	private PortletURL _iteratorURL;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final String _portletId;
	private PortletPreferences _portletPreferences;
	private final PortletSession _portletSession;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private SearchContainer<User> _userSearchContainer;

}