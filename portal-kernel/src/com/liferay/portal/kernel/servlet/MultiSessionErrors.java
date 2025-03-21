/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alicia Garcia
 */
public class MultiSessionErrors {

	public static void clear(PortletRequest portletRequest) {
		SessionErrors.clear(portletRequest);

		SessionErrors.clear(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static boolean contains(PortletRequest portletRequest, String key) {
		if (SessionErrors.contains(portletRequest, key) ||
			SessionErrors.contains(
				PortalUtil.getHttpServletRequest(portletRequest), key)) {

			return true;
		}

		return false;
	}

	public static Object get(PortletRequest portletRequest, String key) {
		Object value = SessionErrors.get(portletRequest, key);

		if (value != null) {
			return value;
		}

		return SessionErrors.get(
			PortalUtil.getHttpServletRequest(portletRequest), key);
	}

	public static boolean isEmpty(PortletRequest portletRequest) {
		if (SessionErrors.isEmpty(portletRequest) &&
			_isEmpty(PortalUtil.getHttpServletRequest(portletRequest))) {

			return true;
		}

		return false;
	}

	public static boolean isHideDefaultErrorMessage(
		PortletRequest portletRequest, String portletId) {

		if (MultiSessionMessages.contains(
				portletRequest,
				portletId +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE)) {

			return true;
		}

		for (Class<?> clazz : _CLASSES_HIDE_DEFAULT_ERROR_MESSAGE) {
			if (contains(portletRequest, clazz.getName())) {
				return true;
			}
		}

		return false;
	}

	private static boolean _isEmpty(HttpServletRequest httpServletRequest) {
		return SessionErrors.isEmpty(httpServletRequest);
	}

	private static final Class<?>[] _CLASSES_HIDE_DEFAULT_ERROR_MESSAGE =
		ArrayUtil.append(
			new Class<?>[] {
				InfoFormException.class, NoSuchGroupException.class,
				NoSuchLayoutException.class, UserPasswordException.class
			},
			UserPasswordException.class.getDeclaredClasses());

}