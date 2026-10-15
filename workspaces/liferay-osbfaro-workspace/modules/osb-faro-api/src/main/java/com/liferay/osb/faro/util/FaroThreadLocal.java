/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Shinn Lok
 */
public class FaroThreadLocal {

	public static Object getCache() {
		return _cache.get();
	}

	public static FaroRequestAudit getFaroRequestAudit() {
		return _faroRequestAudit.get();
	}

	public static String getOrigin() {
		return _origin.get();
	}

	public static boolean isCacheEnabled() {
		Boolean cacheEnabled = _cacheEnabled.get();

		if (cacheEnabled == null) {
			return true;
		}

		return cacheEnabled;
	}

	public static void setCache(Object cache) {
		_cache.set(cache);
	}

	public static void setCacheEnabled(boolean enabled) {
		_cacheEnabled.set(enabled);
	}

	public static void setFaroRequestAudit(FaroRequestAudit faroRequestAudit) {
		_faroRequestAudit.set(faroRequestAudit);
	}

	public static void setOrigin(String origin) {
		_origin.set(origin);
	}

	private static final ThreadLocal<Object> _cache =
		new CentralizedThreadLocal<>(FaroThreadLocal.class + "._cache");
	private static final ThreadLocal<Boolean> _cacheEnabled =
		new CentralizedThreadLocal<>(FaroThreadLocal.class + "._cacheEnabled");
	private static final ThreadLocal<FaroRequestAudit> _faroRequestAudit =
		new CentralizedThreadLocal<>(
			FaroThreadLocal.class + "._faroRequestAudit");
	private static final ThreadLocal<String> _origin =
		new CentralizedThreadLocal<>(FaroThreadLocal.class + "._origin");

}