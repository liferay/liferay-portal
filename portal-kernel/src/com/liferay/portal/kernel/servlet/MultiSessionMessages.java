/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 */
public class MultiSessionMessages {

	public static void add(PortletRequest portletRequest, Class<?> clazz) {
		SessionMessages.add(portletRequest, clazz.getName());

		SessionMessages.add(
			PortalUtil.getHttpServletRequest(portletRequest), clazz.getName());
	}

	public static void add(
		PortletRequest portletRequest, Class<?> clazz, Object value) {

		SessionMessages.add(portletRequest, clazz.getName(), value);

		SessionMessages.add(
			PortalUtil.getHttpServletRequest(portletRequest), clazz.getName(),
			value);
	}

	public static void add(PortletRequest portletRequest, String key) {
		SessionMessages.add(portletRequest, key);

		SessionMessages.add(
			PortalUtil.getHttpServletRequest(portletRequest), key);
	}

	public static void add(
		PortletRequest portletRequest, String key, Object value) {

		SessionMessages.add(portletRequest, key, value);

		SessionMessages.add(
			PortalUtil.getHttpServletRequest(portletRequest), key, value);
	}

	public static void clear(PortletRequest portletRequest) {
		SessionMessages.clear(portletRequest);

		SessionMessages.clear(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static boolean contains(
		PortletRequest portletRequest, Class<?> clazz) {

		if (SessionMessages.contains(portletRequest, clazz.getName()) ||
			SessionMessages.contains(
				PortalUtil.getHttpServletRequest(portletRequest),
				clazz.getName())) {

			return true;
		}

		return false;
	}

	public static boolean contains(PortletRequest portletRequest, String key) {
		if (SessionMessages.contains(portletRequest, key) ||
			SessionMessages.contains(
				PortalUtil.getHttpServletRequest(portletRequest), key)) {

			return true;
		}

		return false;
	}

	public static Object get(PortletRequest portletRequest, Class<?> clazz) {
		Object value = SessionMessages.get(portletRequest, clazz.getName());

		if (value != null) {
			return value;
		}

		return SessionMessages.get(
			PortalUtil.getHttpServletRequest(portletRequest), clazz.getName());
	}

	public static Object get(PortletRequest portletRequest, String key) {
		Object value = SessionMessages.get(portletRequest, key);

		if (value != null) {
			return value;
		}

		return SessionMessages.get(
			PortalUtil.getHttpServletRequest(portletRequest), key);
	}

	public static boolean isEmpty(PortletRequest portletRequest) {
		if (SessionMessages.isEmpty(portletRequest) &&
			SessionMessages.isEmpty(
				PortalUtil.getHttpServletRequest(portletRequest))) {

			return true;
		}

		return false;
	}

	public static Iterator<String> iterator(PortletRequest portletRequest) {
		Set<String> set = keySet(portletRequest);

		return set.iterator();
	}

	public static Set<String> keySet(PortletRequest portletRequest) {
		Set<String> set = new HashSet<>();

		set.addAll(SessionMessages.keySet(portletRequest));

		set.addAll(
			SessionMessages.keySet(
				PortalUtil.getHttpServletRequest(portletRequest)));

		return Collections.unmodifiableSet(set);
	}

	public static void print(PortletRequest portletRequest) {
		SessionMessages.print(portletRequest);

		SessionMessages.print(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static int size(PortletRequest portletRequest) {
		int size = SessionMessages.size(portletRequest);

		size += SessionMessages.size(
			PortalUtil.getHttpServletRequest(portletRequest));

		return size;
	}

}