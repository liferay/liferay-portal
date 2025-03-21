/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.servlet.HttpSessionWrapper;

import jakarta.servlet.http.HttpSession;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletServletSession extends HttpSessionWrapper {

	public PortletServletSession(
		HttpSession httpSession, LiferayPortletRequest liferayPortletRequest) {

		super(httpSession);

		_liferayPortletRequestReference = new WeakReference<>(
			liferayPortletRequest);
	}

	@Override
	public void invalidate() {
		super.invalidate();

		LiferayPortletRequest liferayPortletRequest =
			_liferayPortletRequestReference.get();

		if (liferayPortletRequest != null) {
			liferayPortletRequest.invalidateSession();
		}
	}

	private final Reference<LiferayPortletRequest>
		_liferayPortletRequestReference;

}