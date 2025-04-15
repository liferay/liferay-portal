/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletMode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class PortletModeFactory {

	public static PortletMode getPortletMode(String name) {
		return getPortletMode(name, 2);
	}

	public static PortletMode getPortletMode(
		String name, int portletMajorVersion) {

		if (Validator.isNull(name)) {
			if (portletMajorVersion < 3) {
				return PortletMode.VIEW;
			}

			return PortletMode.UNDEFINED;
		}

		PortletMode portletMode = _portletModes.get(name);

		if (portletMode == null) {
			portletMode = new PortletMode(name);
		}

		return portletMode;
	}

	private static final Map<String, PortletMode> _portletModes =
		new HashMap<String, PortletMode>() {
			{
				try {
					for (Field field : LiferayPortletMode.class.getFields()) {
						if (Modifier.isStatic(field.getModifiers()) &&
							(field.getType() == PortletMode.class)) {

							PortletMode portletMode = (PortletMode)field.get(
								null);

							put(portletMode.toString(), portletMode);
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