/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Ferrer
 * @author Sergio González
 */
public class ProgressTracker implements Serializable {

	public static final String PERCENT =
		ProgressTracker.class.getName() + "_PERCENT";

	public ProgressTracker(String progressId) {
		_progressId = progressId;

		addProgress(ProgressStatusConstants.PREPARED, 0, StringPool.BLANK);
	}

	public void addProgress(int status, int percent, String message) {
		Tuple tuple = new Tuple(percent, message);

		_progress.put(status, tuple);
	}

	public void finish(HttpServletRequest httpServletRequest) {
		finish(httpServletRequest.getSession());
	}

	public void finish(HttpSession httpSession) {
		httpSession.removeAttribute(PERCENT + _progressId);
	}

	public void finish(PortletRequest portletRequest) {
		finish(portletRequest.getPortletSession());
	}

	public void finish(PortletSession portletSession) {
		portletSession.removeAttribute(
			PERCENT + _progressId, PortletSession.APPLICATION_SCOPE);
	}

	public String getMessage() {
		Tuple tuple = _progress.get(_status);

		return GetterUtil.getString(tuple.getObject(1));
	}

	public int getPercent() {
		return _percent;
	}

	public int getStatus() {
		return _status;
	}

	public void initialize(HttpServletRequest httpServletRequest) {
		initialize(httpServletRequest.getSession());
	}

	public void initialize(HttpSession httpSession) {
		httpSession.setAttribute(PERCENT + _progressId, this);
	}

	public void initialize(PortletRequest portletRequest) {
		initialize(portletRequest.getPortletSession());
	}

	public void initialize(PortletSession portletSession) {
		portletSession.setAttribute(
			PERCENT + _progressId, this, PortletSession.APPLICATION_SCOPE);
	}

	public void setPercent(int percent) {
		_percent = percent;
	}

	public void setStatus(int status) {
		_status = status;

		Tuple tuple = _progress.get(_status);

		_percent = GetterUtil.getInteger(tuple.getObject(0));
	}

	public void start(HttpServletRequest httpServletRequest) {
		start(httpServletRequest.getSession());
	}

	public void start(HttpSession httpSession) {
		initialize(httpSession);

		setPercent(1);
	}

	public void start(PortletRequest portletRequest) {
		start(portletRequest.getPortletSession());
	}

	public void start(PortletSession portletSession) {
		initialize(portletSession);

		setPercent(1);
	}

	private int _percent;
	private final Map<Integer, Tuple> _progress = new HashMap<>();
	private final String _progressId;
	private int _status = ProgressStatusConstants.PREPARED;

}