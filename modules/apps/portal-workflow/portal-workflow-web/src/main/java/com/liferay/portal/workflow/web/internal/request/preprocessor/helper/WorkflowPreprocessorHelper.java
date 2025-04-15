/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.request.preprocessor.helper;

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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = WorkflowPreprocessorHelper.class)
public class WorkflowPreprocessorHelper {

	public String getMVCPathAttributeName(String namespace) {
		return StringBundler.concat(
			namespace, StringPool.PERIOD,
			MVCRenderConstants.MVC_PATH_REQUEST_ATTRIBUTE_NAME);
	}

	public String getPath(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String mvcPath = portletRequest.getParameter("mvcPath");

		if (mvcPath == null) {
			mvcPath = (String)portletRequest.getAttribute(
				getMVCPathAttributeName(portletResponse.getNamespace()));
		}

		// Check deprecated parameter

		if (mvcPath == null) {
			mvcPath = portletRequest.getParameter("jspPage");
		}

		return mvcPath;
	}

	public void hideDefaultErrorMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			_portal.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	public void hideDefaultSuccessMessage(PortletRequest portletRequest) {
		SessionMessages.add(
			portletRequest,
			_portal.getPortletId(portletRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
	}

	public boolean isSessionErrorException(Throwable throwable) {
		if (_log.isDebugEnabled()) {
			_log.debug(throwable, throwable);
		}

		if (throwable instanceof PortalException) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowPreprocessorHelper.class);

	@Reference
	private Portal _portal;

}