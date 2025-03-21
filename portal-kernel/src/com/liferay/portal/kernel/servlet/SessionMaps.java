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
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Dante Wang
 */
public class SessionMaps {

	public SessionMaps(Supplier<Map<String, Object>> mapSupplier) {
		_mapSupplier = mapSupplier;
	}

	public void add(
		HttpSession httpSession, String mapKey, String key, Object value) {

		_updateMap(httpSession, mapKey, true, map -> map.put(key, value));
	}

	public void clear(HttpSession httpSession, String mapKey) {
		_updateMap(httpSession, mapKey, false, Map::clear);
	}

	public boolean contains(
		HttpSession httpSession, String mapKey, String key) {

		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return false;
		}

		return map.containsKey(key);
	}

	public Object get(HttpSession httpSession, String mapKey, String key) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return null;
		}

		return map.get(key);
	}

	public boolean isEmpty(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return true;
		}

		return map.isEmpty();
	}

	public Iterator<String> iterator(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return Collections.emptyIterator();
		}

		Set<String> keySet = Collections.unmodifiableSet(map.keySet());

		return keySet.iterator();
	}

	public Set<String> keySet(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(map.keySet());
	}

	public void remove(HttpSession httpSession, String mapKey, String key) {
		_updateMap(httpSession, mapKey, false, map -> map.remove(key));
	}

	public int size(HttpSession httpSession, String mapKey) {
		Map<String, Object> map = _getMap(httpSession, mapKey);

		if (map == null) {
			return 0;
		}

		return map.size();
	}

	private Map<String, Object> _getMap(
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

	private void _updateMap(
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

			map = _mapSupplier.get();
		}

		consumer.accept(map);

		httpSession.setAttribute(mapKey, map);
	}

	private static final Log _log = LogFactoryUtil.getLog(SessionMaps.class);

	private final Supplier<Map<String, Object>> _mapSupplier;

}