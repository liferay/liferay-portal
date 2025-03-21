/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.events;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;

import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class InvokerSessionAction extends SessionAction {

	public InvokerSessionAction(SessionAction sessionAction) {
		this(sessionAction, Thread.currentThread().getContextClassLoader());
	}

	public InvokerSessionAction(
		SessionAction sessionAction, ClassLoader classLoader) {

		_sessionAction = sessionAction;
		_classLoader = classLoader;
	}

	@Override
	public void run(HttpSession httpSession) throws ActionException {
		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_classLoader)) {

			_sessionAction.run(httpSession);
		}
	}

	private final ClassLoader _classLoader;
	private final SessionAction _sessionAction;

}