/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletSession;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Raymond Augé
 */
public class SessionMessages {

	public static final String KEY_SUFFIX_CLOSE_REDIRECT = ".closeRedirect";

	public static final String KEY_SUFFIX_CLOSE_REFRESH_PORTLET =
		".closeRefreshPortlet";

	public static final String KEY_SUFFIX_DELETE_SUCCESS_DATA =
		".deleteSuccessData";

	public static final String KEY_SUFFIX_FORCE_SEND_REDIRECT =
		".forceSendRedirect";

	public static final String KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE =
		".hideDefaultErrorMessage";

	public static final String KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE =
		".hideDefaultSuccessMessage";

	public static final String KEY_SUFFIX_PORTLET_NOT_AJAXABLE =
		".portletNotAjaxable";

	public static final String KEY_SUFFIX_REFRESH_PORTLET = ".refreshPortlet";

	public static final String KEY_SUFFIX_REFRESH_PORTLET_DATA =
		".refreshPortletData";

	public static final String KEY_SUFFIX_UPDATED_CONFIGURATION =
		".updatedConfiguration";

	public static final String KEY_SUFFIX_UPDATED_PREFERENCES =
		".updatedPreferences";

	public static void add(
		HttpServletRequest httpServletRequest, Class<?> clazz) {

		add(_getPortalHttpSession(httpServletRequest), clazz.getName());
	}

	public static void add(
		HttpServletRequest httpServletRequest, Class<?> clazz, Object value) {

		add(_getPortalHttpSession(httpServletRequest), clazz.getName(), value);
	}

	public static void add(HttpServletRequest httpServletRequest, String key) {
		add(_getPortalHttpSession(httpServletRequest), key);
	}

	public static void add(
		HttpServletRequest httpServletRequest, String key, Object value) {

		add(_getPortalHttpSession(httpServletRequest), key, value);
	}

	public static void add(HttpSession httpSession, Class<?> clazz) {
		add(httpSession, clazz.getName());
	}

	public static void add(
		HttpSession httpSession, Class<?> clazz, Object value) {

		add(httpSession, clazz.getName(), value);
	}

	public static void add(HttpSession httpSession, String key) {
		if (_log.isDebugEnabled()) {
			_log.debug("Adding key " + key);
		}

		_sessionMaps.add(httpSession, _CLASS_NAME, key, key);
	}

	public static void add(HttpSession httpSession, String key, Object value) {
		if (_log.isDebugEnabled()) {
			Exception exception = null;

			if (value instanceof Exception) {
				exception = (Exception)value;
			}

			_log.debug(
				StringBundler.concat("Adding key ", key, " with value ", value),
				exception);
		}

		_sessionMaps.add(httpSession, _CLASS_NAME, key, value);
	}

	public static void add(PortletRequest portletRequest, Class<?> clazz) {
		add(portletRequest, clazz.getName());
	}

	public static void add(
		PortletRequest portletRequest, Class<?> clazz, Object value) {

		add(portletRequest, clazz.getName(), value);
	}

	public static void add(PortletRequest portletRequest, String key) {
		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Adding key ", key, " to portlet ",
					portletRequest.getWindowID()));
		}

		_sessionMaps.add(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest), key,
			key);
	}

	public static void add(
		PortletRequest portletRequest, String key, Object value) {

		if (_log.isDebugEnabled()) {
			Exception exception = null;

			if (value instanceof Exception) {
				exception = (Exception)value;
			}

			_log.debug(
				StringBundler.concat(
					"Adding key ", key, " to portlet ",
					portletRequest.getWindowID(), " with value ", value),
				exception);
		}

		_sessionMaps.add(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest), key,
			value);
	}

	public static void clear(HttpServletRequest httpServletRequest) {
		clear(_getPortalHttpSession(httpServletRequest));
	}

	public static void clear(HttpSession httpSession) {
		_sessionMaps.clear(httpSession, _CLASS_NAME);
	}

	public static void clear(PortletRequest portletRequest) {
		_sessionMaps.clear(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest));
	}

	public static boolean contains(
		HttpServletRequest httpServletRequest, Class<?> clazz) {

		return contains(
			_getPortalHttpSession(httpServletRequest), clazz.getName());
	}

	public static boolean contains(
		HttpServletRequest httpServletRequest, Class<?>[] classes) {

		return contains(_getPortalHttpSession(httpServletRequest), classes);
	}

	public static boolean contains(
		HttpServletRequest httpServletRequest, String key) {

		return contains(_getPortalHttpSession(httpServletRequest), key);
	}

	public static boolean contains(HttpSession httpSession, Class<?> clazz) {
		return contains(httpSession, clazz.getName());
	}

	public static boolean contains(
		HttpSession httpSession, Class<?>[] classes) {

		for (Class<?> clazz : classes) {
			if (contains(httpSession, clazz.getName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean contains(HttpSession httpSession, String key) {
		return _sessionMaps.contains(httpSession, _CLASS_NAME, key);
	}

	public static boolean contains(
		PortletRequest portletRequest, Class<?> clazz) {

		return contains(portletRequest, clazz.getName());
	}

	public static boolean contains(
		PortletRequest portletRequest, Class<?>[] classes) {

		for (Class<?> clazz : classes) {
			if (contains(portletRequest, clazz.getName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean contains(PortletRequest portletRequest, String key) {
		return _sessionMaps.contains(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest),
			key);
	}

	public static Object get(
		HttpServletRequest httpServletRequest, Class<?> clazz) {

		return get(_getPortalHttpSession(httpServletRequest), clazz.getName());
	}

	public static Object get(
		HttpServletRequest httpServletRequest, String key) {

		return get(_getPortalHttpSession(httpServletRequest), key);
	}

	public static Object get(HttpSession httpSession, Class<?> clazz) {
		return get(httpSession, clazz.getName());
	}

	public static Object get(HttpSession httpSession, String key) {
		return _sessionMaps.get(httpSession, _CLASS_NAME, key);
	}

	public static Object get(PortletRequest portletRequest, Class<?> clazz) {
		return get(portletRequest, clazz.getName());
	}

	public static Object get(PortletRequest portletRequest, String key) {
		return _sessionMaps.get(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest),
			key);
	}

	public static boolean isEmpty(HttpServletRequest httpServletRequest) {
		return isEmpty(_getPortalHttpSession(httpServletRequest));
	}

	public static boolean isEmpty(HttpSession httpSession) {
		return _sessionMaps.isEmpty(httpSession, _CLASS_NAME);
	}

	public static boolean isEmpty(PortletRequest portletRequest) {
		return _sessionMaps.isEmpty(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest));
	}

	public static Iterator<String> iterator(
		HttpServletRequest httpServletRequest) {

		return iterator(_getPortalHttpSession(httpServletRequest));
	}

	public static Iterator<String> iterator(HttpSession httpSession) {
		return _sessionMaps.iterator(httpSession, _CLASS_NAME);
	}

	public static Iterator<String> iterator(PortletRequest portletRequest) {
		return _sessionMaps.iterator(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest));
	}

	public static Set<String> keySet(HttpServletRequest httpServletRequest) {
		return keySet(_getPortalHttpSession(httpServletRequest));
	}

	public static Set<String> keySet(HttpSession httpSession) {
		return _sessionMaps.keySet(httpSession, _CLASS_NAME);
	}

	public static Set<String> keySet(PortletRequest portletRequest) {
		return _sessionMaps.keySet(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest));
	}

	public static void print(HttpServletRequest httpServletRequest) {
		print(_getPortalHttpSession(httpServletRequest));
	}

	public static void print(HttpSession httpSession) {
		Iterator<String> iterator = iterator(httpSession);

		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}

	public static void print(PortletRequest portletRequest) {
		Iterator<String> iterator = iterator(portletRequest);

		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}

	public static void remove(
		HttpServletRequest httpServletRequest, Class<?> clazz) {

		_sessionMaps.remove(
			_getPortalHttpSession(httpServletRequest), _CLASS_NAME,
			clazz.getName());
	}

	public static void remove(
		HttpServletRequest httpServletRequest, String key) {

		_sessionMaps.remove(
			_getPortalHttpSession(httpServletRequest), _CLASS_NAME, key);
	}

	public static int size(HttpServletRequest httpServletRequest) {
		return size(_getPortalHttpSession(httpServletRequest));
	}

	public static int size(HttpSession httpSession) {
		return _sessionMaps.size(httpSession, _CLASS_NAME);
	}

	public static int size(PortletRequest portletRequest) {
		return _sessionMaps.size(
			_getPortalHttpSession(portletRequest), _getKey(portletRequest));
	}

	private static String _getKey(PortletRequest portletRequest) {
		StringBundler sb = new StringBundler(6);

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(portletRequest);

		sb.append(LiferayPortletSession.PORTLET_SCOPE_NAMESPACE);
		sb.append(liferayPortletRequest.getPortletName());
		sb.append(LiferayPortletSession.LAYOUT_SEPARATOR);
		sb.append(liferayPortletRequest.getPlid());
		sb.append(StringPool.QUESTION);
		sb.append(_CLASS_NAME);

		return sb.toString();
	}

	private static HttpSession _getPortalHttpSession(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		return originalHttpServletRequest.getSession();
	}

	private static HttpSession _getPortalHttpSession(
		PortletRequest portletRequest) {

		return _getPortalHttpSession(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	private static final String _CLASS_NAME = SessionMessages.class.getName();

	private static final Log _log = LogFactoryUtil.getLog(
		SessionMessages.class);

	private static final SessionMaps _sessionMaps = new SessionMaps(
		SessionMessagesMap::new);

	private static class SessionMessagesMap extends HashMap<String, Object> {

		@Override
		public boolean containsKey(Object key) {
			return super.containsKey(_transformKey((String)key));
		}

		@Override
		public Object get(Object key) {
			return super.get(_transformKey((String)key));
		}

		@Override
		public Object put(String key, Object value) {
			return super.put(_transformKey(key), value);
		}

		private String _transformKey(String key) {
			if (Objects.equals(key, "request_processed")) {
				key = "requestProcessed";
			}

			return key;
		}

	}

}