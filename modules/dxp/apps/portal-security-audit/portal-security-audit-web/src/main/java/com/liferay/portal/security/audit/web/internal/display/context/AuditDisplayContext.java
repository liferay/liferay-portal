/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.AuditEvent;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.web.internal.AuditEventManagerUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Mariano Álvaro Sáiz
 */
public class AuditDisplayContext {

	public AuditDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, TimeZone timeZone) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_timeZone = timeZone;

		_servletRequestWrapper =
			(ServletRequestWrapper)
				liferayPortletRequest.getOriginalHttpServletRequest();

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_today = CalendarFactoryUtil.getCalendar(
			timeZone, _themeDisplay.getLocale());

		_today.add(Calendar.MINUTE, 1);

		_yesterday = CalendarFactoryUtil.getCalendar(
			timeZone, _themeDisplay.getLocale());

		_yesterday.add(Calendar.DATE, -1);
	}

	public String getClassName() {
		if (_className != null) {
			return _className;
		}

		_className = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "className", StringPool.BLANK);

		return _className;
	}

	public String getClassPK() {
		if (_classPK != null) {
			return _classPK;
		}

		_classPK = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "classPK", StringPool.BLANK);

		return _classPK;
	}

	public String getClientHost() {
		if (_clientHost != null) {
			return _clientHost;
		}

		_clientHost = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "clientHost", StringPool.BLANK);

		return _clientHost;
	}

	public String getClientIP() {
		if (_clientIP != null) {
			return _clientIP;
		}

		_clientIP = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "clientIP", StringPool.BLANK);

		return _clientIP;
	}

	public int getEndDateAmPm() {
		if (_endDateAmPm != null) {
			return _endDateAmPm;
		}

		_endDateAmPm = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "endDateAmPm", _today.get(Calendar.AM_PM));

		return _endDateAmPm;
	}

	public int getEndDateDay() {
		if (_endDateDay != null) {
			return _endDateDay;
		}

		_endDateDay = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "endDateDay", _today.get(Calendar.DATE));

		return _endDateDay;
	}

	public int getEndDateHour() {
		if (_endDateHour != null) {
			return _endDateHour;
		}

		_endDateHour = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "endDateHour", _today.get(Calendar.HOUR));

		return _endDateHour;
	}

	public int getEndDateMinute() {
		if (_endDateMinute != null) {
			return _endDateMinute;
		}

		_endDateMinute = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "endDateMinute",
			_today.get(Calendar.MINUTE));

		return _endDateMinute;
	}

	public int getEndDateMonth() {
		if (_endDateMonth != null) {
			return _endDateMonth;
		}

		_endDateMonth = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "endDateMonth", _today.get(Calendar.MONTH));

		return _endDateMonth;
	}

	public int getEndDateYear() {
		if (_endDateYear != null) {
			return _endDateYear;
		}

		_endDateYear = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "endDateYear", _today.get(Calendar.YEAR));

		return _endDateYear;
	}

	public String getEventType() {
		if (_eventType != null) {
			return _eventType;
		}

		_eventType = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "eventType", StringPool.BLANK);

		return _eventType;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "groupId", 0);

		return _groupId;
	}

	public SearchContainer<AuditEvent> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		DisplayTerms displayTerms = new DisplayTerms(_liferayPortletRequest);

		_searchContainer = new SearchContainer(
			_liferayPortletRequest, displayTerms, null,
			SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA,
			_getPortletURL(),
			ListUtil.fromArray(
				"user-id", "user-name", "resource-id", "resource-name",
				"resource-action", "client-ip", "create-date"),
			"there-are-no-events");

		int[] range = {QueryUtil.ALL_POS, QueryUtil.ALL_POS};

		if (_paging) {
			range[0] = _searchContainer.getStart();
			range[1] = _searchContainer.getEnd();
		}

		if (displayTerms.isAdvancedSearch()) {
			Date endDate = PortalUtil.getDate(
				getEndDateMonth(), getEndDateDay(), getEndDateYear(),
				(getEndDateAmPm() != Calendar.PM) ? getEndDateHour() :
					getEndDateHour() + 12,
				getEndDateMinute(), _timeZone, null);

			Date startDate = PortalUtil.getDate(
				getStartDateMonth(), getStartDateDay(), getStartDateYear(),
				(getStartDateAmPm() != Calendar.PM) ? getStartDateHour() :
					getStartDateHour() + 12,
				getStartDateMinute(), _timeZone, null);

			_searchContainer.setResultsAndTotal(
				() -> AuditEventManagerUtil.getAuditEvents(
					_themeDisplay.getCompanyId(), getGroupId(), getUserId(),
					getUserName(), startDate, endDate, getEventType(),
					getClassName(), getClassPK(), getClientHost(),
					getClientIP(), getServerName(), getServerPort(), null,
					displayTerms.isAndOperator(), range[0], range[1],
					new AuditEventCreateDateComparator()),
				AuditEventManagerUtil.getAuditEventsCount(
					_themeDisplay.getCompanyId(), getGroupId(), getUserId(),
					getUserName(), startDate, endDate, getEventType(),
					getClassName(), getClassPK(), getClientHost(),
					getClientIP(), getServerName(), getServerPort(), null,
					displayTerms.isAndOperator()));
		}
		else {
			String keywords = displayTerms.getKeywords();

			String number =
				Validator.isNumber(keywords) ? keywords : String.valueOf(0);

			_searchContainer.setResultsAndTotal(
				() -> AuditEventManagerUtil.getAuditEvents(
					_themeDisplay.getCompanyId(), getGroupId(),
					Long.valueOf(number), keywords, null, null, keywords,
					keywords, keywords, keywords, keywords, keywords,
					Integer.valueOf(number), null, false, range[0], range[1],
					new AuditEventCreateDateComparator()),
				AuditEventManagerUtil.getAuditEventsCount(
					_themeDisplay.getCompanyId(), getGroupId(),
					Long.valueOf(number), keywords, null, null, keywords,
					keywords, keywords, keywords, keywords, keywords,
					Integer.valueOf(number), null, false));
		}

		return _searchContainer;
	}

	public String getServerName() {
		if (_serverName != null) {
			return _serverName;
		}

		_serverName = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "serverName", StringPool.BLANK);

		return _serverName;
	}

	public int getServerPort() {
		if (_serverPort != null) {
			return _serverPort;
		}

		_serverPort = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "serverPort", 0);

		return _serverPort;
	}

	public int getStartDateAmPm() {
		if (_startDateAmPm != null) {
			return _startDateAmPm;
		}

		_startDateAmPm = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "startDateAmPm",
			_yesterday.get(Calendar.AM_PM));

		return _startDateAmPm;
	}

	public int getStartDateDay() {
		if (_startDateDay != null) {
			return _startDateDay;
		}

		_startDateDay = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "startDateDay",
			_yesterday.get(Calendar.DATE));

		return _startDateDay;
	}

	public int getStartDateHour() {
		if (_startDateHour != null) {
			return _startDateHour;
		}

		_startDateHour = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "startDateHour",
			_yesterday.get(Calendar.HOUR));

		return _startDateHour;
	}

	public int getStartDateMinute() {
		if (_startDateMinute != null) {
			return _startDateMinute;
		}

		_startDateMinute = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "startDateMinute",
			_yesterday.get(Calendar.MINUTE));

		return _startDateMinute;
	}

	public int getStartDateMonth() {
		if (_startDateMonth != null) {
			return _startDateMonth;
		}

		_startDateMonth = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "startDateMonth",
			_yesterday.get(Calendar.MONTH));

		return _startDateMonth;
	}

	public int getStartDateYear() {
		if (_startDateYear != null) {
			return _startDateYear;
		}

		_startDateYear = _getParamWithOrWithoutNamespace(
			ParamUtil::getInteger, "startDateYear",
			_yesterday.get(Calendar.YEAR));

		return _startDateYear;
	}

	public long getUserId() {
		if (_userId != null) {
			return _userId;
		}

		_userId = _getParamWithOrWithoutNamespace(
			ParamUtil::getLong, "userId", 0L);

		return _userId;
	}

	public String getUserName() {
		if (_userName != null) {
			return _userName;
		}

		_userName = _getParamWithOrWithoutNamespace(
			ParamUtil::getString, "userName", StringPool.BLANK);

		return _userName;
	}

	public void setPaging(boolean paging) {
		_paging = paging;
	}

	@FunctionalInterface
	public interface ParamGetter<V> {

		public V get(
			HttpServletRequest httpServletRequest, String param,
			V defaultValue);

	}

	private <T> T _getParamWithOrWithoutNamespace(
		ParamGetter<T> paramGetter, String param, T defaultValue) {

		return paramGetter.get(
			(HttpServletRequest)_servletRequestWrapper.getRequest(),
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + param,
			paramGetter.get(
				(HttpServletRequest)_servletRequestWrapper.getRequest(), param,
				paramGetter.get(_httpServletRequest, param, defaultValue)));
	}

	private PortletURL _getPortletURL() throws Exception {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.create(
			PortletURLUtil.clone(
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse),
				_liferayPortletResponse)
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "endDateAmPm",
			getEndDateAmPm()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "endDateDay",
			getEndDateDay()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "endDateHour",
			getEndDateHour()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "endDateMinute",
			getEndDateMinute()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "endDateMonth",
			getEndDateMonth()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "endDateYear",
			getEndDateYear()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "startDateAmPm",
			getStartDateAmPm()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "startDateDay",
			getStartDateDay()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "startDateHour",
			getStartDateHour()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "startDateMinute",
			getStartDateMinute()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "startDateMonth",
			getStartDateMonth()
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "startDateYear",
			getStartDateYear()
		).setParameter(
			"className", getClassName()
		).setParameter(
			"classPK", getClassPK()
		).setParameter(
			"clientHost", getClientHost()
		).setParameter(
			"clientIP", getClientIP()
		).setParameter(
			"eventType", getEventType()
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"serverName", getServerName()
		).setParameter(
			"serverPort", getServerPort()
		).setParameter(
			"userId", getUserId()
		).setParameter(
			"userName", getUserName()
		).buildPortletURL();

		return _portletURL;
	}

	private String _className;
	private String _classPK;
	private String _clientHost;
	private String _clientIP;
	private Integer _endDateAmPm;
	private Integer _endDateDay;
	private Integer _endDateHour;
	private Integer _endDateMinute;
	private Integer _endDateMonth;
	private Integer _endDateYear;
	private String _eventType;
	private Integer _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private boolean _paging = true;
	private PortletURL _portletURL;
	private SearchContainer<AuditEvent> _searchContainer;
	private String _serverName;
	private Integer _serverPort;
	private final ServletRequestWrapper _servletRequestWrapper;
	private Integer _startDateAmPm;
	private Integer _startDateDay;
	private Integer _startDateHour;
	private Integer _startDateMinute;
	private Integer _startDateMonth;
	private Integer _startDateYear;
	private final ThemeDisplay _themeDisplay;
	private final TimeZone _timeZone;
	private final Calendar _today;
	private Long _userId;
	private String _userName;
	private final Calendar _yesterday;

}