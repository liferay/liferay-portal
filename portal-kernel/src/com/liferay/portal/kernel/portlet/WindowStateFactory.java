/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.WindowState;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class WindowStateFactory {

	public static WindowState getWindowState(String name) {
		return getWindowState(name, 2);
	}

	public static WindowState getWindowState(
		String name, int portletMajorVersion) {

		if (Validator.isNull(name)) {
			if (portletMajorVersion < 3) {
				return WindowState.NORMAL;
			}

			return WindowState.UNDEFINED;
		}

		WindowState windowState = _windowStates.get(name);

		if (windowState == null) {
			windowState = new WindowState(name);
		}

		return windowState;
	}

	private static final Map<String, WindowState> _windowStates =
		new HashMap<String, WindowState>() {
			{
				try {
					for (Field field : LiferayWindowState.class.getFields()) {
						if (Modifier.isStatic(field.getModifiers()) &&
							(field.getType() == WindowState.class)) {

							WindowState windowState = (WindowState)field.get(
								null);

							put(windowState.toString(), windowState);
						}
					}
				}
				catch (IllegalAccessException illegalAccessException) {
					throw new ExceptionInInitializerError(
						illegalAccessException);
				}
			}
		};

}