/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.spi.history.util;

import com.liferay.change.tracking.spi.constants.CTTimelineKeys;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Cheryl Tang
 */
public class CTTimelineUtil {

	public static void setClassName(
		HttpServletRequest httpServletRequest, Class<?> clazz) {

		setCTTimelineKeys(httpServletRequest, clazz, 0);
	}

	public static void setClassName(
		RenderRequest renderRequest, Class<?> clazz) {

		setCTTimelineKeys(renderRequest, clazz, 0);
	}

	public static void setCTTimelineKeys(
		HttpServletRequest httpServletRequest, Class<?> clazz, long classPK) {

		httpServletRequest = PortalUtil.getOriginalServletRequest(
			httpServletRequest);

		httpServletRequest.setAttribute(
			CTTimelineKeys.CLASS_NAME, clazz.getName());

		if (classPK > 0) {
			httpServletRequest.setAttribute(CTTimelineKeys.CLASS_PK, classPK);
		}
	}

	public static void setCTTimelineKeys(
		RenderRequest renderRequest, Class<?> clazz, long classPK) {

		setCTTimelineKeys(
			PortalUtil.getHttpServletRequest(renderRequest), clazz, classPK);
	}

}