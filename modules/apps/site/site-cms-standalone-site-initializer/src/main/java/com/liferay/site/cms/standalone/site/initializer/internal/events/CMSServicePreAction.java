/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez Álvarez
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class CMSServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) || !themeDisplay.isSignedIn() ||
			!StringUtil.endsWith(
				httpServletRequest.getRequestURI(),
				Portal.PATH_PORTAL_LAYOUT)) {

			return;
		}

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (!scopeGroup.isGuest()) {
			return;
		}

		try {
			httpServletResponse.sendRedirect(
				_portal.getPathContext() + "/web/cms");
		}
		catch (IOException ioException) {
			throw new ActionException(ioException);
		}
	}

	@Reference
	private Portal _portal;

}