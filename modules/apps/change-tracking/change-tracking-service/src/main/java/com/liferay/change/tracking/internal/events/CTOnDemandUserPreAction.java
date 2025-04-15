/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.events;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class CTOnDemandUserPreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			_run(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private boolean _isPublicationsPortletRequest(
		HttpServletRequest httpServletRequest) {

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (Validator.isNull(portletId)) {
			portletId = HttpComponentsUtil.getParameter(
				httpServletRequest.getHeader(WebKeys.REFERER), "p_p_id", false);
		}

		if (Objects.equals(portletId, CTPortletKeys.PUBLICATIONS)) {
			return true;
		}

		long previewCTCollectionId = GetterUtil.getLong(
			HttpComponentsUtil.getParameter(
				httpServletRequest.getHeader(WebKeys.REFERER),
				"previewCTCollectionId", false),
			-1);

		if (previewCTCollectionId >= 0) {
			return true;
		}

		return false;
	}

	private void _run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || !user.isOnDemandUser()) {
			return;
		}

		if (!_isPublicationsPortletRequest(httpServletRequest)) {
			AuthenticatedSessionManagerUtil.logout(
				httpServletRequest, httpServletResponse);

			httpServletRequest.setAttribute(WebKeys.LOGOUT, Boolean.TRUE);

			httpServletResponse.sendRedirect(
				_portal.getCurrentURL(httpServletRequest));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTOnDemandUserPreAction.class);

	@Reference
	private Portal _portal;

}