/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.context.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.Context;
import com.liferay.segments.context.contributor.RequestContextContributor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"request.context.contributor.key=" + SegmentsAsahRequestContextContributor.KEY_SEGMENTS_ANONYMOUS_USER_ID,
		"request.context.contributor.type=id"
	},
	service = RequestContextContributor.class
)
public class SegmentsAsahRequestContextContributor
	implements RequestContextContributor {

	public static final String KEY_SEGMENTS_ANONYMOUS_USER_ID =
		"segmentsAnonymousUserId";

	@Override
	public void contribute(
		Context context, HttpServletRequest httpServletRequest) {

		String segmentsAnonymousUserId = _getSegmentsAnonymousUserId(
			httpServletRequest);

		httpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_ANONYMOUS_USER_ID,
			segmentsAnonymousUserId);

		context.put(KEY_SEGMENTS_ANONYMOUS_USER_ID, segmentsAnonymousUserId);
	}

	private String _getSegmentsAnonymousUserId(
		HttpServletRequest httpServletRequest) {

		Cookie[] cookies = httpServletRequest.getCookies();

		if (ArrayUtil.isEmpty(cookies)) {
			return StringPool.BLANK;
		}

		for (Cookie cookie : cookies) {
			if (Objects.equals(
					cookie.getName(), _AC_CLIENT_USER_ID_COOKIE_NAME)) {

				return cookie.getValue();
			}
		}

		return StringPool.BLANK;
	}

	private static final String _AC_CLIENT_USER_ID_COOKIE_NAME =
		"ac_client_user_id";

}