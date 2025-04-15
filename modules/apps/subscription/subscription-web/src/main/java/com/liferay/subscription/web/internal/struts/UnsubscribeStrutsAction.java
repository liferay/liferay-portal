/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.subscription.web.internal.struts;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.subscription.web.internal.constants.SubscriptionPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(property = "path=/portal/unsubscribe", service = StrutsAction.class)
public class UnsubscribeStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long userId = ParamUtil.getLong(httpServletRequest, "userId");
		String key = ParamUtil.getString(httpServletRequest, "key");

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			httpServletRequest, SubscriptionPortletKeys.UNSUBSCRIBE,
			PortletRequest.ACTION_PHASE);

		liferayPortletURL.setParameter(
			ActionRequest.ACTION_NAME, "/subscription/unsubscribe");

		liferayPortletURL.setWindowState(WindowState.MAXIMIZED);

		liferayPortletURL.setParameter("userId", String.valueOf(userId));
		liferayPortletURL.setParameter("key", key);

		httpServletResponse.sendRedirect(liferayPortletURL.toString());

		return null;
	}

}