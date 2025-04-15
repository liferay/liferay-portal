/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class RenderParametersPool {

	public static Map<String, Map<String, String[]>> clear(
		HttpServletRequest httpServletRequest, long plid) {

		if (plid <= 0) {
			return null;
		}

		Map<Long, Map<String, Map<String, String[]>>> pool =
			_getRenderParametersPool(httpServletRequest, false);

		if (pool == null) {
			return null;
		}

		return pool.remove(plid);
	}

	public static Map<String, String[]> clear(
		HttpServletRequest httpServletRequest, long plid, String portletId) {

		Map<String, Map<String, String[]>> plidPool = get(
			httpServletRequest, plid);

		if (plidPool == null) {
			return null;
		}

		return plidPool.remove(portletId);
	}

	public static Map<String, Map<String, String[]>> get(
		HttpServletRequest httpServletRequest, long plid) {

		if (plid <= 0) {
			return null;
		}

		Map<Long, Map<String, Map<String, String[]>>> pool =
			_getRenderParametersPool(httpServletRequest, false);

		if (pool == null) {
			return null;
		}

		return pool.get(plid);
	}

	public static Map<String, String[]> get(
		HttpServletRequest httpServletRequest, long plid, String portletId) {

		Map<String, Map<String, String[]>> plidPool = get(
			httpServletRequest, plid);

		if (plidPool == null) {
			return null;
		}

		return plidPool.get(portletId);
	}

	public static Map<String, Map<String, String[]>> getOrCreate(
		HttpServletRequest httpServletRequest, long plid) {

		if (plid <= 0) {
			return new ConcurrentHashMap<>();
		}

		Map<Long, Map<String, Map<String, String[]>>> pool =
			_getRenderParametersPool(httpServletRequest, true);

		return pool.computeIfAbsent(plid, key -> new ConcurrentHashMap<>());
	}

	public static Map<String, String[]> getOrCreate(
		HttpServletRequest httpServletRequest, long plid, String portletId) {

		Map<String, Map<String, String[]>> plidPool = getOrCreate(
			httpServletRequest, plid);

		return plidPool.computeIfAbsent(portletId, key -> new HashMap<>());
	}

	public static void put(
		HttpServletRequest httpServletRequest, long plid, String portletId,
		Map<String, String[]> params) {

		if (params.isEmpty()) {
			return;
		}

		Map<String, Map<String, String[]>> plidPool = getOrCreate(
			httpServletRequest, plid);

		plidPool.put(portletId, params);
	}

	private static Map<Long, Map<String, Map<String, String[]>>>
		_getRenderParametersPool(
			HttpServletRequest httpServletRequest, boolean createIfAbsent) {

		HttpSession httpSession = httpServletRequest.getSession();

		HttpSession portalHttpSession = PortalSessionContext.get(
			httpSession.getId());

		if (portalHttpSession != null) {
			httpSession = portalHttpSession;
		}

		Map<Long, Map<String, Map<String, String[]>>> renderParametersPool =
			(Map<Long, Map<String, Map<String, String[]>>>)
				httpSession.getAttribute(WebKeys.PORTLET_RENDER_PARAMETERS);

		if (createIfAbsent && (renderParametersPool == null)) {
			renderParametersPool = new ConcurrentHashMap<>();

			httpSession.setAttribute(
				WebKeys.PORTLET_RENDER_PARAMETERS, renderParametersPool);
		}

		return renderParametersPool;
	}

}