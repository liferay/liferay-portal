/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * @author Leslie Wong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWorkspaceGroupChannelUserSessionsPageResponse {

	public UserSessionBag getUserSessionBag() {
		return _userSessionBag;
	}

	public void setUserSessionBag(UserSessionBag userSessionBag) {
		_userSessionBag = userSessionBag;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UserSession {

		public String getBrowserName() {
			return _browserName;
		}

		public Date getCompleteDate() {
			return _completeDate;
		}

		public Date getCreateDate() {
			return _createDate;
		}

		public String getDeviceType() {
			return _deviceType;
		}

		public List<GetWorkspaceGroupChannelEventsPageResponse.Event>
			getEvents() {

			return _events;
		}

		public Boolean isBecameKnown() {
			return _becameKnown;
		}

		public void setBecameKnown(Boolean becameKnown) {
			_becameKnown = becameKnown;
		}

		public void setBrowserName(String browserName) {
			_browserName = browserName;
		}

		public void setCompleteDate(Date completeDate) {
			_completeDate = completeDate;
		}

		public void setCreateDate(Date createDate) {
			_createDate = createDate;
		}

		public void setDeviceType(String deviceType) {
			_deviceType = deviceType;
		}

		public void setEvents(
			List<GetWorkspaceGroupChannelEventsPageResponse.Event> events) {

			_events = events;
		}

		private Boolean _becameKnown;
		private String _browserName;
		private Date _completeDate;
		private Date _createDate;
		private String _deviceType;
		private List<GetWorkspaceGroupChannelEventsPageResponse.Event> _events;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UserSessionBag {

		public Integer getTotalEvents() {
			return _totalEvents;
		}

		public List<UserSession> getUserSessions() {
			return _userSessions;
		}

		public void setTotalEvents(Integer totalEvents) {
			_totalEvents = totalEvents;
		}

		public void setUserSessions(List<UserSession> userSessions) {
			_userSessions = userSessions;
		}

		private Integer _totalEvents;
		private List<UserSession> _userSessions;

	}

	private UserSessionBag _userSessionBag;

}