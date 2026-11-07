/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

/**
 * @author Adam Brandizzi
 */
public class WorkflowPreprocessorUtil {

	public static String getPath(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String mvcPath = portletRequest.getParameter("mvcPath");

		if (mvcPath == null) {
			mvcPath = (String)portletRequest.getAttribute(
				StringBundler.concat(
					portletResponse.getNamespace(), StringPool.PERIOD,
					MVCRenderConstants.MVC_PATH_REQUEST_ATTRIBUTE_NAME));
		}

		// Check deprecated parameter

		if (mvcPath == null) {
			mvcPath = portletRequest.getParameter("jspPage");
		}

		return mvcPath;
	}

	public static void hideDefaultErrorMessage(
		Portal portal, PortletRequest portletRequest) {

		SessionMessages.add(
			portletRequest,
			portal.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	public static void hideDefaultSuccessMessage(
		Portal portal, PortletRequest portletRequest) {

		SessionMessages.add(
			portletRequest,
			portal.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
	}

	public static boolean isSessionErrorException(Throwable throwable) {
		if (_log.isDebugEnabled()) {
			_log.debug(throwable, throwable);
		}

		if (throwable instanceof PortalException) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowPreprocessorUtil.class);

}