/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.model;

import java.util.Date;

/**
 * @author Davyson Melo
 */
public class IssueReportFDSEntry {

	public IssueReportFDSEntry(
		Date date, String agentName, String surface, String feedbackType,
		String issueType, String level, String userMessage, String userEmail) {

		_date = date;
		_agentName = agentName;
		_surface = surface;
		_feedbackType = feedbackType;
		_issueType = issueType;
		_level = level;
		_userMessage = userMessage;
		_userEmail = userEmail;
	}

	public String getAgentName() {
		return _agentName;
	}

	public Date getDate() {
		return _date;
	}

	public String getFeedbackType() {
		return _feedbackType;
	}

	public String getIssueType() {
		return _issueType;
	}

	public String getLevel() {
		return _level;
	}

	public String getSurface() {
		return _surface;
	}

	public String getUserEmail() {
		return _userEmail;
	}

	public String getUserMessage() {
		return _userMessage;
	}

	private final String _agentName;
	private final Date _date;
	private final String _feedbackType;
	private final String _issueType;
	private final String _level;
	private final String _surface;
	private final String _userEmail;
	private final String _userMessage;

}