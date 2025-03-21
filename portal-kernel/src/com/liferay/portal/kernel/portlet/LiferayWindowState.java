/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Zsolt Balogh
 */
public class LiferayWindowState extends WindowState {

	public static final WindowState EXCLUSIVE = new WindowState("exclusive");

	public static final WindowState POP_UP = new WindowState("pop_up");

	public static boolean isExclusive(HttpServletRequest httpServletRequest) {
		String state = _getWindowState(httpServletRequest);

		if ((state != null) && state.equals(EXCLUSIVE.toString())) {
			return true;
		}

		return false;
	}

	public static boolean isMaximized(HttpServletRequest httpServletRequest) {
		String state = _getWindowState(httpServletRequest);

		if ((state != null) && state.equals(WindowState.MAXIMIZED.toString())) {
			return true;
		}

		return false;
	}

	public static boolean isPopUp(HttpServletRequest httpServletRequest) {
		String state = _getWindowState(httpServletRequest);

		if ((state != null) && state.equals(POP_UP.toString())) {
			return true;
		}

		return false;
	}

	public static boolean isWindowStatePreserved(
		WindowState oldWindowState, WindowState newWindowState) {

		// Changes to EXCLUSIVE are always preserved

		if ((newWindowState != null) &&
			newWindowState.equals(LiferayWindowState.EXCLUSIVE)) {

			return true;
		}

		// Some window states are automatically preserved

		if ((oldWindowState != null) &&
			oldWindowState.equals(LiferayWindowState.POP_UP)) {

			return false;
		}

		return true;
	}

	public LiferayWindowState(String name) {
		super(name);
	}

	private static String _getWindowState(
		HttpServletRequest httpServletRequest) {

		WindowState windowState = (WindowState)httpServletRequest.getAttribute(
			WebKeys.WINDOW_STATE);

		if (windowState != null) {
			return windowState.toString();
		}

		return httpServletRequest.getParameter("p_p_state");
	}

}