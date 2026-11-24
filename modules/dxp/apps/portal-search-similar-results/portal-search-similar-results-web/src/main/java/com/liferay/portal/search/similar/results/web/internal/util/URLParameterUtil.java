/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class URLParameterUtil {

	public static String[] getFriendlyURLParameters(String urlString) {
		try {
			String[] subpath = StringUtil.split(
				HttpComponentsUtil.getPath(urlString),
				Portal.FRIENDLY_URL_SEPARATOR);

			return StringUtil.split(
				subpath[subpath.length - 1], CharPool.FORWARD_SLASH);
		}
		catch (RuntimeException runtimeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(runtimeException);
			}

			return StringPool.EMPTY_ARRAY;
		}
	}

	public static String getPortletIdParameter(
		String urlString, String parameterName) {

		try {
			Map<String, String[]> parameterMap =
				HttpComponentsUtil.parameterMapFromString(
					HttpComponentsUtil.getQueryString(urlString));

			String[] parameterValues = parameterMap.get(parameterName);

			if (ArrayUtil.isNotEmpty(parameterValues)) {
				return parameterValues[0];
			}

			String ppid = parameterMap.get("p_p_id")[0];

			return parameterMap.get(
				StringBundler.concat(
					StringPool.UNDERLINE, ppid, StringPool.UNDERLINE,
					parameterName))[0];
		}
		catch (RuntimeException runtimeException) {
			if (_log.isDebugEnabled()) {
				_log.debug(runtimeException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		URLParameterUtil.class);

}