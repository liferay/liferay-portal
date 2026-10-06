/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Dante Wang
 */
public class SessionMaps {

	public static void add(
		HttpSession httpSession, String mapKey, String key, Object value) {

		_updateMap(httpSession, mapKey, true, map -> map.put(key, value));
	}

	public static void clear(HttpSession httpSession, String mapKey) {
		_updateMap(httpSession, mapKey, false, Map::clear);
	}

	public static boolean contains(
		HttpSession httpSession, String mapKey, String key) {

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return false;
		}

		return map.containsKey(key);
	}

	public static Object get(
		HttpSession httpSession, String mapKey, String key) {

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return null;
		}

		return map.get(key);
	}

	public static boolean isEmpty(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return true;
		}

		return map.isEmpty();
	}

	public static Iterator<String> iterator(
		HttpSession httpSession, String mapKey) {

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return Collections.emptyIterator();
		}

		Set<String> keySet = Collections.unmodifiableSet(map.keySet());

		return keySet.iterator();
	}

	public static Set<String> keySet(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(map.keySet());
	}

	public static void remove(
		HttpSession httpSession, String mapKey, String key) {

		_updateMap(httpSession, mapKey, false, map -> map.remove(key));
	}

	public static int size(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return 0;
		}

		return map.size();
	}

	private static Map<String, Object> _getMap(
		HttpSession httpSession, String mapKey) {

		if (httpSession == null) {
			return null;
		}

		try {
			return (Map<String, Object>)httpSession.getAttribute(mapKey);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Session is already invalidated, just return a null map

			return null;
		}
	}

	private static void _updateMap(
		HttpSession httpSession, String mapKey, boolean createIfAbsent,
		Consumer<Map<String, Object>> consumer) {

		if (httpSession == null) {
			return;
		}

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			if (!createIfAbsent) {
				return;
			}

			synchronized (SessionMaps.class) {
				map = _getMap(httpSession, mapKey);

				if (map == null) {
					map = new ConcurrentHashMap<>();

					httpSession.setAttribute(mapKey, map);
				}
			}
		}

		consumer.accept(map);

		httpSession.setAttribute(mapKey, map);
	}

	private static final Log _log = LogFactoryUtil.getLog(SessionMaps.class);

}