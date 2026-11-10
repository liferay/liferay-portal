/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences;

import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Víctor Galán
 */
public class DetectedAudienceEntryUtil {

	public static String[] getAudienceEntryERCs(
		HttpServletRequest httpServletRequest) {

		String cookieValue = CookiesManagerUtil.getCookieValue(
			"com.liferay.frontend.js.audiences", httpServletRequest);

		if (Validator.isNull(cookieValue)) {
			return null;
		}

		try {
			return JSONUtil.toStringArray(
				JSONFactoryUtil.createJSONArray(
					URLCodec.decodeURL(cookieValue)));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DetectedAudienceEntryUtil.class);

}