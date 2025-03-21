/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.events;

import com.liferay.on.demand.admin.manager.OnDemandAdminManager;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(property = "key=logout.events.post", service = LifecycleAction.class)
public class OnDemandAdminLogoutPostAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if (_onDemandAdminManager.isOnDemandAdminUser(user)) {
				_userLocalService.deleteUser(user);
			}
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	@Reference
	private OnDemandAdminManager _onDemandAdminManager;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}