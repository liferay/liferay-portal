/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.events;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class KBServicePreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			_run(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isLifecycleRender()) {
			return;
		}

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (Validator.isNull(portletId) ||
			!portletId.equals(
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE)) {

			return;
		}

		String request_p_p_auth = ParamUtil.getString(
			httpServletRequest, "p_p_auth");

		if (Validator.isNull(request_p_p_auth)) {
			return;
		}

		String actual_p_p_auth = AuthTokenUtil.getToken(
			httpServletRequest, themeDisplay.getPlid(), portletId);

		if (request_p_p_auth.equals(actual_p_p_auth)) {
			return;
		}

		// A guest user that signs in will cause the original portlet
		// authentication token to become stale. See SessionAuthToken.

		String redirect = _portal.escapeRedirect(themeDisplay.getURLCurrent());

		if (Validator.isNull(redirect)) {
			return;
		}

		redirect = HttpComponentsUtil.setParameter(
			redirect, "p_p_auth", actual_p_p_auth);

		httpServletResponse.sendRedirect(redirect);
	}

	private static final boolean _PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(
				PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED));

	private static final Log _log = LogFactoryUtil.getLog(
		KBServicePreAction.class);

	@Reference
	private Portal _portal;

}