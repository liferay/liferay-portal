/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.action.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Guilherme Camacho
 */
public class ObjectActionThreadLocal {

	public static void addObjectEntryId(
		long objectActionId, long objectEntryId) {

		Map<Long, Set<Long>> objectEntryIdsMap = getObjectEntryIdsMap();

		Set<Long> objectEntryIds = objectEntryIdsMap.get(objectActionId);

		if (objectEntryIds == null) {
			objectEntryIds = new HashSet<>();

			objectEntryIdsMap.put(objectActionId, objectEntryIds);
		}

		objectEntryIds.add(objectEntryId);
	}

	public static void clearObjectEntryIdsMap() {
		Map<Long, Set<Long>> objectEntryIdsMap = getObjectEntryIdsMap();

		objectEntryIdsMap.clear();
	}

	public static HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest.get();
	}

	public static Map<Long, Set<Long>> getObjectEntryIdsMap() {
		return _objectEntryIdsMap.get();
	}

	public static boolean isClearObjectEntryIdsMap() {
		return _clearObjectEntryIdsMap.get();
	}

	public static boolean isSkipObjectActionExecution() {
		return _skipObjectActionExecution.get();
	}

	public static void setClearObjectEntryIdsMap(
		boolean clearObjectEntryIdsMap) {

		_clearObjectEntryIdsMap.set(clearObjectEntryIdsMap);
	}

	public static void setHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest.set(httpServletRequest);
	}

	public static void setSkipObjectActionExecution(
		boolean skipObjectActionExecution) {

		_skipObjectActionExecution.set(skipObjectActionExecution);
	}

	private static final ThreadLocal<Boolean> _clearObjectEntryIdsMap =
		new CentralizedThreadLocal<>(
			ObjectActionThreadLocal.class + "._clearObjectEntryIdsMap",
			() -> true);
	private static final ThreadLocal<HttpServletRequest> _httpServletRequest =
		new CentralizedThreadLocal<>(
			ObjectActionThreadLocal.class + "._httpServletRequest", () -> null);
	private static final ThreadLocal<Map<Long, Set<Long>>> _objectEntryIdsMap =
		new CentralizedThreadLocal<>(
			ObjectActionThreadLocal.class.getName() + "._objectEntryIdsMap",
			HashMap::new);
	private static final ThreadLocal<Boolean> _skipObjectActionExecution =
		new CentralizedThreadLocal<>(
			ObjectActionThreadLocal.class + "._skipObjectActionExecution",
			() -> false);

}