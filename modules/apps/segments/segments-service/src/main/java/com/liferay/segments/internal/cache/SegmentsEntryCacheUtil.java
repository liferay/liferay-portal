/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.cache;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rachael Koestartyo
 */
public class SegmentsEntryCacheUtil {

	public static void clear() {
		_portalCache.removeAll();
	}

	public static void clear(long userId) {
		_portalCache.remove(_generateCacheKey(userId));
	}

	public static long[] getSegmentsEntryIds(String key, long userId) {
		Map<String, long[]> userIdSegmentsEntryIds = _portalCache.get(
			_generateCacheKey(userId));

		if (userIdSegmentsEntryIds == null) {
			return null;
		}

		return userIdSegmentsEntryIds.get(key);
	}

	public static void putSegmentsEntryIds(
		String key, long[] segmentsEntryIds, long userId) {

		Map<String, long[]> userIdSegmentsEntryIds = _portalCache.get(
			_generateCacheKey(userId));

		if (userIdSegmentsEntryIds == null) {
			userIdSegmentsEntryIds = new ConcurrentHashMap<>();

			_portalCache.put(
				_generateCacheKey(userId), userIdSegmentsEntryIds, 1800);
		}

		userIdSegmentsEntryIds.put(key, segmentsEntryIds);
	}

	private static String _generateCacheKey(long userId) {
		return _CACHE_PREFIX + userId;
	}

	private static final String _CACHE_PREFIX = "segments-entry-";

	private static final PortalCache<String, Map<String, long[]>> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.MULTI_VM,
			SegmentsEntryCacheUtil.class.getName());

}