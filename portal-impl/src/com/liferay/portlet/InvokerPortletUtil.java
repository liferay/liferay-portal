/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.internal.PortletSessionImpl;

import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Neil Griffin
 */
public class InvokerPortletUtil {

	public static void clearResponse(
		HttpSession httpSession, long plid, String portletId,
		String languageId) {

		String sesResponseId = encodeResponseKey(plid, portletId, languageId);

		Map<String, InvokerPortletResponse> responses = getResponses(
			httpSession);

		responses.remove(sesResponseId);
	}

	public static void clearResponses(PortletSession session) {
		Map<String, InvokerPortletResponse> responses = getResponses(session);

		responses.clear();
	}

	public static String encodeResponseKey(
		long plid, String portletId, String languageId) {

		return StringBundler.concat(
			StringUtil.toHexString(plid), StringPool.UNDERLINE, portletId,
			StringPool.UNDERLINE, languageId);
	}

	public static Map<String, InvokerPortletResponse> getResponses(
		HttpSession httpSession) {

		Map<String, InvokerPortletResponse> responses =
			(Map<String, InvokerPortletResponse>)httpSession.getAttribute(
				WebKeys.CACHE_PORTLET_RESPONSES);

		if (responses == null) {
			responses = new ConcurrentHashMap<>();

			httpSession.setAttribute(
				WebKeys.CACHE_PORTLET_RESPONSES, responses);
		}

		return responses;
	}

	public static Map<String, InvokerPortletResponse> getResponses(
		PortletSession portletSession) {

		PortletSessionImpl portletSessionImpl =
			(PortletSessionImpl)portletSession;

		return getResponses(portletSessionImpl.getHttpSession());
	}

}