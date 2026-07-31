/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

/**
 * Eagerly starts the JAX-RS whiteboard so a test that calls an "/o/" endpoint
 * does not race Liferay's request triggered lazy start. The whiteboard is only
 * started when the first request reaches "/o/*", and during that start the
 * endpoint transiently returns a 404 whose HTML body then breaks a JSON read.
 * Triggering the start before the request removes that window. This is called
 * from the HTTP test funnels so only a test that actually calls the whiteboard
 * pays for it.
 *
 * @author Shuyang Zhou
 */
public class JAXRSWhiteboardTestUtil {

	public static void ensureReady() {
		if (_ready) {
			return;
		}

		try {
			boolean ready = SystemBundleUtil.callService(
				"com.liferay.portal.remote.jaxrs.whiteboard.lifecycle." +
					"JAXRSLifecycle",
				jaxRSLifecycle -> {
					if (jaxRSLifecycle == null) {
						return false;
					}

					ReflectionTestUtil.invoke(
						jaxRSLifecycle, "ensureReady", new Class<?>[0]);

					return true;
				});

			if (ready) {
				_ready = true;
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private static volatile boolean _ready;

}