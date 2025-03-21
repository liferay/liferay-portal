/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.events;

import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class Action implements LifecycleAction {

	@Override
	public final void processLifecycleEvent(LifecycleEvent lifecycleEvent)
		throws ActionException {

		run(lifecycleEvent.getRequest(), lifecycleEvent.getResponse());
	}

	public abstract void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException;

	public void run(RenderRequest renderRequest, RenderResponse renderResponse)
		throws ActionException {

		try {
			run(
				PortalUtil.getHttpServletRequest(renderRequest),
				PortalUtil.getHttpServletResponse(renderResponse));
		}
		catch (ActionException actionException) {
			throw actionException;
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

}