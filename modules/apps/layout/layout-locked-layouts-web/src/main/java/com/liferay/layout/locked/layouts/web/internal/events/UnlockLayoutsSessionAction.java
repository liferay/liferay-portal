/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.events;

import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "key=" + PropsKeys.SERVLET_SESSION_DESTROY_EVENTS,
	service = LifecycleAction.class
)
public class UnlockLayoutsSessionAction extends SessionAction {

	@Override
	public void run(HttpSession httpSession) throws ActionException {
		long userId = GetterUtil.getLong(
			httpSession.getAttribute(WebKeys.USER_ID));

		if (userId <= 0) {
			return;
		}

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return;
		}

		try {
			_layoutLockManager.unlockLayoutsByUserId(
				user.getCompanyId(), user.getUserId());
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	@Reference
	private LayoutLockManager _layoutLockManager;

	@Reference
	private UserLocalService _userLocalService;

}